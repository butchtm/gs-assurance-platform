from typing import Any
from locust.clients import HttpSession

from src.client.consultation_client import ConsultationClient


class ConsultationService:
    def __init__(self, client: HttpSession, cookies: Any):
        self.consultation_client = ConsultationClient(client, cookies)

    def end_consultation(self, request_id: int):
        self.consultation_client.end_consultation(request_id)


