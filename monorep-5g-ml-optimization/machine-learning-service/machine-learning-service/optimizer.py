import random
import numpy as np
import pandas as pd
from deap import base, creator, tools, algorithms
import joblib

# Load the models and encoders
model_data = joblib.load('network_qos_models.joblib')
throughput_model = model_data['throughput_model']
latency_model = model_data['latency_model']
label_encoders = model_data['label_encoders']
feature_columns = model_data['feature_columns']
categorical_features = model_data['categorical_features']

TOTAL_AVAILABLE_BANDWIDTH = 20.0
TOTAL_AVAILABLE_PRBs = 100.0

def get_required_throughput(user_activity):
    return {
        'VoIP': 0.1,
        'Gaming': 1.0,
        'Streaming': 5.0,
        'WebBrowsing': 0.5
    }.get(user_activity, 1.0)

def get_max_latency(user_activity):
    return {
        'VoIP': 50,
        'Gaming': 30,
        'Streaming': 200,
        'WebBrowsing': 300
    }.get(user_activity, 100)

creator.create("FitnessMin", base.Fitness, weights=(-1.0,))
creator.create("Individual", list, fitness=creator.FitnessMin)

def optimize_resources(session_features_list):
    num_sessions = len(session_features_list)
    alpha = 1.0
    beta = 1.0

    processed_session_features_list = []
    for features in session_features_list:
        features = features.copy()
        for col in categorical_features:
            if col in label_encoders:
                le = label_encoders[col]
                features[col] = le.transform([features[col]])[0]
        processed_session_features_list.append(features)

    toolbox = base.Toolbox()

    def alloc_bandwidth():
        return random.uniform(0.1, 5.0)  # float

    def alloc_prbs():
        return random.randint(1, 25)     # int

    toolbox.register("attr_float", alloc_bandwidth)
    toolbox.register("attr_int", alloc_prbs)
    toolbox.register("individual", tools.initCycle, creator.Individual,
                     (toolbox.attr_float, toolbox.attr_int), n=num_sessions)
    toolbox.register("population", tools.initRepeat, list, toolbox.individual)

    def evaluate(individual):
        total_resource_usage = 0
        penalty = 0

        total_allocated_bandwidth = 0
        total_allocated_prbs = 0

        input_features_list = []
        for i in range(num_sessions):
            allocated_bandwidth = individual[i * 2]
            allocated_prbs = individual[i * 2 + 1]

            features = processed_session_features_list[i].copy()
            features['allocated_bandwidth'] = allocated_bandwidth
            features['allocated_prbs'] = allocated_prbs

            input_features_list.append(features)

            total_resource_usage += alpha * allocated_bandwidth + beta * allocated_prbs
            total_allocated_bandwidth += allocated_bandwidth
            total_allocated_prbs += allocated_prbs

        input_df = pd.DataFrame(input_features_list)

        predicted_throughput = throughput_model.predict(input_df[feature_columns])
        predicted_latency = latency_model.predict(input_df[feature_columns])

        for i in range(num_sessions):
            features = input_features_list[i]
            required_throughput = get_required_throughput(features['user_activity'])
            max_latency = get_max_latency(features['user_activity'])

            if predicted_throughput[i] < required_throughput:
                penalty += 1e5 * (required_throughput - predicted_throughput[i])
            if predicted_latency[i] > max_latency:
                penalty += 1e5 * (predicted_latency[i] - max_latency)

        if total_allocated_bandwidth > TOTAL_AVAILABLE_BANDWIDTH:
            penalty += 1e6 * (total_allocated_bandwidth - TOTAL_AVAILABLE_BANDWIDTH)
        if total_allocated_prbs > TOTAL_AVAILABLE_PRBs:
            penalty += 1e6 * (total_allocated_prbs - TOTAL_AVAILABLE_PRBs)

        return (total_resource_usage + penalty,)

    def custom_mutation(individual, indpb):
        for i in range(len(individual)):
            if i % 2 == 0:
                if random.random() < indpb:
                    individual[i] += random.gauss(0, 0.5)
                    if individual[i] < 0.1:
                        individual[i] = 0.1
                    elif individual[i] > 5.0:
                        individual[i] = 5.0
            else:
                if random.random() < indpb:
                    individual[i] += random.randint(-2, 2)
                    if individual[i] < 1:
                        individual[i] = 1
                    elif individual[i] > 25:
                        individual[i] = 25
                    individual[i] = int(round(individual[i]))
        return (individual,)

    toolbox.register("evaluate", evaluate)
    toolbox.register("mate", tools.cxUniform, indpb=0.5)
    toolbox.register("mutate", custom_mutation, indpb=0.2)
    toolbox.register("select", tools.selTournament, tournsize=3)
    toolbox.register("map", map)

    population = toolbox.population(n=30)
    ngen = 20
    cxpb = 0.7
    mutpb = 0.3

    algorithms.eaSimple(population, toolbox, cxpb, mutpb, ngen, verbose=False)

    best_individual = tools.selBest(population, k=1)[0]

    allocations = []
    for i in range(num_sessions):
        allocated_bandwidth = best_individual[i * 2]
        allocated_prbs = best_individual[i * 2 + 1]
        allocations.append({
            'session_id': session_features_list[i]['session_id'],
            'allocated_bandwidth': allocated_bandwidth,
            'allocated_prbs': allocated_prbs
        })

    return allocations
