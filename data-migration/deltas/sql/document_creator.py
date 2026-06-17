import logging
import pandas as pd
import psycopg2

def run_document_update_sql(data: pd.DataFrame, cursor):
    documents_update = data['documentsToInsert'].copy()
    documents_update.drop(['ReviewsID'], axis=1, inplace=True)
    if not documents_update.empty:
        try:
            logging.info(f"Running document upsert - {len(documents_update.index)} rows to upsert.")
            for _, row in documents_update.iterrows():
                execute_document_sql(row, cursor)
        except Exception as e:
            logging.exception(f"Failed to update documents {e}")
            raise


def run_base_document_request_sql(data: pd.DataFrame, cursor):
    base_document = data['documentsToInsert'].copy()
    base_document = base_document[['ReviewsID', 'ID']]
    if not base_document.empty:
        try:
            logging.info(f"Running document request upsert - {len(base_document.index)} rows to upsert.")
            for _, row in base_document.iterrows():
                execute_request_document_sql(row, cursor)

        except Exception as e:
            logging.exception(f"Failed to update request documents {e}")
            raise

def run_request_document_all_sql(data: pd.DataFrame, cursor):
    request_document = data

    request_document_rename = request_document.rename(columns={
        'DocumentsID': 'ID',
    })
    
    try:
        logging.info(f"Running document request upsert - {len(request_document_rename.index)} rows to upsert.")
        for _, row in request_document_rename.iterrows():
            execute_request_document_sql(row, cursor)

    except Exception as e:
        logging.exception(f"Failed to update request documents {e}")
        raise

def execute_request_document_sql(row, conn):
    try:
        query = """
        INSERT INTO gs.request_document 
        (request_id, document_id)  
        VALUES 
        (%(request_id)s, %(document_id)s)
        ON CONFLICT (request_id, document_id)  
        DO NOTHING;"""
        values = {'request_id': row['ReviewsID'],
                  'document_id': row['ID']}
        conn.execute(query, values)
    except psycopg2.DatabaseError as e:
        logging.error(f"Error inserting/updating gs.request_document: {e}")
        raise


def execute_document_sql(row, conn):
    try:
        query = """
        INSERT INTO gs.document 
        (id, container_name, file_name, file_size, hash_value, access, category, document_type_id, content_type, 
        uploaded_timestamp, modified_timestamp, is_latest)  
        VALUES (%(id)s, %(container_name)s, %(file_name)s, %(file_size)s, %(hash_value)s, %(access)s, 
        %(category)s, %(document_type_id)s, %(content_type)s, %(uploaded_timestamp)s, %(modified_timestamp)s, %(is_latest)s) 
        ON CONFLICT (id)  
        DO UPDATE SET  
        container_name=%(container_name)s, 
        file_name=%(file_name)s, 
        file_size=%(file_size)s, 
        hash_value=%(hash_value)s, 
        access=%(access)s, 
        category=%(category)s, 
        document_type_id=%(document_type_id)s, \
        content_type=%(content_type)s, \
        document_hash=NULL, \
        uploaded_timestamp=%(uploaded_timestamp)s, \
        modified_timestamp=%(modified_timestamp)s, \
        is_latest=%(is_latest)s;"""
        values = {'id': row['ID'],
                  'container_name': row['ContainerName'],
                  'file_name': row['FileName'],
                  'file_size': row['FileSize'],
                  'hash_value': row['Hash'].encode("UTF-8"),
                  'access': row['Access'],
                  'category': row['Category'],
                  'content_type': row['ContentType'],
                  'document_type_id': row['DocumentType'],
                  'uploaded_timestamp': row['CreatedDate'],
                  'modified_timestamp': row['LastModifiedDate'],
                  'is_latest': row['IsLatest']}
        conn.execute(query, values)
    except psycopg2.DatabaseError as e:
        logging.error(f"Error inserting/updating gs.document: {e}")
        raise