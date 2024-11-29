import pandas as pd
from database_config import get_engine
import logging

logging.basicConfig(level=logging.INFO, format='%(asctime)s %(levelname)s:%(message)s')

def load_data():
    engine = get_engine()
    query = """
    SELECT
        latency,
        throughput,
        signal_strength,
        snr,
        rsrp,
        rsrq,
        cqi,
        mcs,
        bler,
        prb_utilization,
        ue_speed,
        interference_level,
        handover_event,
        user_distance,
        mobility_pattern,
        user_activity,
        allocated_bandwidth,
        allocated_prbs
    FROM network_metrics
    """
    try:
        with engine.connect() as connection:
            df = pd.read_sql(query, connection)
        logging.info("Data loaded successfully.")
        return df
    except Exception as e:
        logging.error(f"Error loading data: {e}")
        raise
