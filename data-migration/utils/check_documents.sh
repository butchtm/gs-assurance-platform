#!/usr/bin/env python3

import pyodbc
import os
from azure.storage.blob import ContainerClient
import re

# Get the db document details
connection_string = f'DRIVER={{ODBC Driver 18 for SQL Server}};SERVER={os.environ["DBSVR"]};DATABASE={os.environ["DBNAME"]};UID={os.environ["DBUSER"]};PWD={os.environ["DBPWD"]}'
conn = pyodbc.connect(connection_string)
cursor = conn.cursor()
cursor.execute("""
SELECT
  ContainerName,
  FileName,
  FileSize,
  Hash
FROM
  dbo.Documents
ORDER BY
  ContainerName,
  FileName;
""")
documents = cursor.fetchall()
db_docs = {}
for document in documents:
  doc_path = f'{document.ContainerName}/{document.FileName}'
  doc_size = document.FileSize
  if doc_path in db_docs:
    print(f'DB_DUPE_DOC|{doc_path}|{doc_size}')
    continue
  db_docs[doc_path] = {'size': doc_size, 'hash': document.Hash}

# Get the container document details
container_client = ContainerClient(
  account_url = os.environ['STORAGE_ACCOUNT_URL'],
  credential = os.environ['SAS_TOKEN'],
  container_name = os.environ['STORAGE_ACCOUNT_CONTAINER']
)
blobs = container_client.list_blobs(name_starts_with = os.environ['PROJECT_DOCUMENT_PREFIX'])
prefix_re = re.compile(f'^{os.environ["PROJECT_DOCUMENT_PREFIX"]}')
container_docs = {}
for blob in blobs:
  full_path = blob['name']
  rel_path = prefix_re.sub('', full_path)
  blob_size = blob['size']
  if rel_path in container_docs:
    print(f'SA_DUPE_DOC|{rel_path}|{blob_size}')
    continue
  container_docs[rel_path] = {'size': blob_size}

# Check DB docs against Container Docs
for db_doc, db_doc_detail in db_docs.items():
  db_doc_size = db_doc_detail['size']
  if db_doc not in container_docs:
    print(f'DB_DOC_NOT_IN_SA|{db_doc}|{db_doc_size}||')
    continue
  sa_doc_detail = container_docs[db_doc]
  sa_doc_size = sa_doc_detail['size'] 
  if db_doc_size != sa_doc_size:
    print(f'DB_SA_DOC_SIZE_DIFFERENCE|{db_doc}|{db_doc_size}|{sa_doc_size}')
    continue
  print(f'DB_SA_MATCH|{db_doc}|{db_doc_size}|{sa_doc_size}')

# Check for unreferenced Container Docs
for sa_doc, sa_doc_detail in container_docs.items():
  if sa_doc not in db_docs:
    print(f'SA_DOC_NOT_IN_DB|{sa_doc}||{sa_doc_detail["size"]}')
