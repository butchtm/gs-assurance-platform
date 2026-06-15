import os

try:
    from dotenv import load_dotenv
    env_path = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(__file__))), '.env')
    load_dotenv(env_path)
except ImportError:
    pass  # python-dotenv not installed, rely on system environment

class Config:
    """Configuration settings for connecting to PostgreSQL databases."""
    
    assurance_local_conn = {
        "host": os.getenv("LOCAL_ASSURANCE_DB_HOST"),
        "port": os.getenv("LOCAL_ASSURANCE_DB_PORT"),
        "dbname": os.getenv("LOCAL_ASSURANCE_DB_NAME"),
        "user": os.getenv("LOCAL_ASSURANCE_DB_USER"),
        "password": os.getenv("LOCAL_ASSURANCE_DB_PASSWORD"),
    }

    assurance_dev_conn = {
        "host": os.getenv("DEV_ASSURANCE_DB_HOST"),
        "port": os.getenv("DEV_ASSURANCE_DB_PORT"),
        "dbname": os.getenv("DEV_ASSURANCE_DB_NAME"),
        "user": os.getenv("DEV_ASSURANCE_DB_USER"),
        "password": os.getenv("DEV_ASSURANCE_DB_PASSWORD"),
    }

    assurance_prod_conn = {
        "host": os.getenv("PROD_ASSURANCE_DB_HOST"),
        "port": os.getenv("PROD_ASSURANCE_DB_PORT") or "5432",
        "dbname": os.getenv("PROD_ASSURANCE_DB_NAME"),
        "user": os.getenv("PROD_ASSURANCE_DB_USER"),
        "password": os.getenv("PROD_ASSURANCE_DB_PASSWORD"),
    }

    assurance_staging_conn = {
        "host": os.getenv("STAGING_ASSURANCE_DB_HOST"),
        "port": os.getenv("STAGING_ASSURANCE_DB_PORT") or "5432",
        "dbname": os.getenv("STAGING_ASSURANCE_DB_NAME"),
        "user": os.getenv("STAGING_ASSURANCE_DB_USER"),
        "password": os.getenv("STAGING_ASSURANCE_DB_PASSWORD"),
    }

    def __init__(self):
        source_env = os.getenv('DUMP_AND_RESTORE_SOURCE')  # expected values: local, dev, staging, prod
        target_env = os.getenv('DUMP_AND_RESTORE_TARGET')  # expected values: local, dev, staging, prod

        if source_env:
            self.source_db = self._build_from_assurance_conn(source_env, var_name='DUMP_AND_RESTORE_SOURCE')
        else:
            print("Warning: DUMP_AND_RESTORE_SOURCE not set")

        if target_env:
            self.target_db = self._build_from_assurance_conn(target_env, var_name='DUMP_AND_RESTORE_TARGET')
        else:
            print("Warning: DUMP_AND_RESTORE_TARGET not set")

    
        self.exclude_tables = []

    def _build_from_assurance_conn(self, env_key: str, var_name: str):
        """Return a normalized connection dict for the given environment key.

        env_key: one of local|dev|staging|prod (case-insensitive)
        var_name: the name of the triggering environment variable (for clearer errors)
        """
        mapping = {
            'local': self.assurance_local_conn,
            'dev': self.assurance_dev_conn,
            'staging': self.assurance_staging_conn,
            'prod': self.assurance_prod_conn,
        }
        key = env_key.lower().strip()
        conn = mapping.get(key)
        if conn is None:
            raise ValueError(
                f"Unsupported value '{env_key}' for {var_name}. Expected one of: " + ", ".join(mapping.keys())
            )

        normalized = {
            'host': conn.get('host'),
            'port': conn.get('port'),
            'database': conn.get('dbname') or conn.get('database'),
            'user': conn.get('user'),
            'password': conn.get('password'),
        }
        print(f"Using {var_name} connection: {normalized['user']}@{normalized['host']}:{normalized['port']}/{normalized['database']}")
        return normalized