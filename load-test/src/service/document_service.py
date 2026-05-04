from typing import Any
from src.client.document_client import DocumentClient
from locust.clients import HttpSession


class DocumentService:

    def __init__(self, client: HttpSession, cookies: Any):
        self.document_client = DocumentClient(client, cookies)

    def upload_document(self, project_id, request_id):
        return self.document_client.upload_document(project_id, request_id)

    def download_document(self, document_id):
        return self.document_client.download_document(document_id)