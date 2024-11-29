import data_preprocessing
import joblib
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import r2_score, mean_squared_error
from sklearn.preprocessing import LabelEncoder
import logging

logging.basicConfig(level=logging.INFO, format='%(asctime)s %(levelname)s:%(message)s')

def train_models():
    try:
        df = data_preprocessing.load_data()

        logging.info(f"Dataframe shape: {df.shape}")
        logging.info(f"First 5 rows:\n{df.head()}")

        if df.shape[0] < 2:
            logging.error("Not enough data to train the models. At least two samples are required.")
            return

        feature_columns = [
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
            'allocated_bandwidth',
            'allocated_prbs'
        ]

        y_throughput = df['throughput']
        y_latency = df['latency']

        categorical_features = ['mobility_pattern', 'user_activity', 'handover_event']

        df_encoded = df.copy()
        label_encoders = {}
        for col in categorical_features:
            if df_encoded[col].dtype == 'object' or df_encoded[col].dtype == 'bool':
                le = LabelEncoder()
                df_encoded[col] = le.fit_transform(df_encoded[col])
                label_encoders[col] = le

        X = df_encoded[feature_columns]

        if X.isnull().values.any() or y_throughput.isnull().values.any() or y_latency.isnull().values.any():
            logging.warning("Data contains missing values. Handling missing data.")
            combined = pd.concat([X, y_throughput, y_latency], axis=1).dropna()
            X = combined[feature_columns]
            y_throughput = combined['throughput']
            y_latency = combined['latency']
            logging.info(f"Dataframe shape after dropping missing values: {X.shape[0]} samples")
            if X.shape[0] < 2:
                logging.error("Not enough data after handling missing values.")
                return

        X_train_t, X_test_t, y_train_t, y_test_t = train_test_split(
            X, y_throughput, test_size=0.2, random_state=42)
        logging.info(f"Throughput Model - Training set size: {X_train_t.shape[0]} samples")
        logging.info(f"Throughput Model - Testing set size: {X_test_t.shape[0]} samples")

        throughput_model = RandomForestRegressor(n_estimators=100, random_state=42)
        throughput_model.fit(X_train_t, y_train_t)
        logging.info("Throughput model training completed.")

        y_pred_t = throughput_model.predict(X_test_t)
        score_t = r2_score(y_test_t, y_pred_t)
        mse_t = mean_squared_error(y_test_t, y_pred_t)
        logging.info(f"Throughput Model R^2 Score: {score_t}")
        logging.info(f"Throughput Model Mean Squared Error: {mse_t}")

        X_train_l, X_test_l, y_train_l, y_test_l = train_test_split(
            X, y_latency, test_size=0.2, random_state=42)
        logging.info(f"Latency Model - Training set size: {X_train_l.shape[0]} samples")
        logging.info(f"Latency Model - Testing set size: {X_test_l.shape[0]} samples")

        latency_model = RandomForestRegressor(n_estimators=100, random_state=42)
        latency_model.fit(X_train_l, y_train_l)
        logging.info("Latency model training completed.")


        #Hey Amhed Here !!!!
        #can you please check the model evaluation metrics R2 says its accurate but the meanSquare score is
        #very innacurate
        y_pred_l = latency_model.predict(X_test_l)
        score_l = r2_score(y_test_l, y_pred_l)
        mse_l = mean_squared_error(y_test_l, y_pred_l)
        logging.info(f"Latency Model R^2 Score: {score_l}")
        logging.info(f"Latency Model Mean Squared Error: {mse_l}")

        joblib.dump({
            'throughput_model': throughput_model,
            'latency_model': latency_model,
            'label_encoders': label_encoders,
            'feature_columns': feature_columns,
            'categorical_features': categorical_features
        }, 'network_qos_models.joblib')
        logging.info("Trained models and label encoders saved as 'network_qos_models.joblib'.")

    except Exception as e:
        logging.error(f"An error occurred during model training: {e}")
        raise

if __name__ == "__main__":
    train_models()
