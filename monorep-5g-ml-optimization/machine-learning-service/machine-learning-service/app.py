from flask import Flask, request, jsonify
from optimizer import optimize_resources
import joblib
import pandas as pd

app = Flask(__name__)

model_data = joblib.load('network_qos_models.joblib')
label_encoders = model_data['label_encoders']
feature_columns = model_data['feature_columns']
categorical_features = model_data['categorical_features']

@app.route('/optimize', methods=['POST'])
def optimize():
    data = request.get_json()

    if not isinstance(data, list):
        return jsonify({'error': 'Input data should be a list of session features'}), 400

    session_features_list = data

    required_features = [
        'signal_strength',
        'snr',
        'rsrp',
        'rsrq',
        'cqi',
        'mcs',
        'bler',
        'prb_utilization',
        'ue_speed',
        'interference_level',
        'handover_event',
        'user_distance',
        'mobility_pattern',
        'user_activity',
        'session_id'
    ]

    for session_features in session_features_list:
        for feature in required_features:
            if feature not in session_features:
                return jsonify({'error': f'Missing feature: {feature}'}), 400

    try:
        allocations = optimize_resources(session_features_list)
    except Exception as e:
        return jsonify({'error': str(e)}), 500

    return jsonify({'allocations': allocations})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
