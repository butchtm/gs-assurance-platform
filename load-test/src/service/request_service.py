from typing import Any
from locust.clients import HttpSession

from src.client.request_client import RequestClient
from src.models.request_create_update import RequestCreateUpdate


class RequestService:

    def __init__(self, client: HttpSession, cookies: Any):
        self.request_client = RequestClient(client, cookies)

    def get_all_requests_page(self):
        self.request_client.get_requests(25)

    def get_request(self, request_id):
        self.request_client.get_request(request_id)

    def create_request(self, project_id):
        return self.request_client.create_request(project_id,
            RequestCreateUpdate(
                request_type_id=1002,
                crediting_period=1,
                emission_reduction_amount=123
            )
        )

    def update_request(self, project_id, request_id):
        self.request_client.update_request(project_id,
            RequestCreateUpdate(
                id=request_id,
                crediting_period=2,
                emission_reduction_amount=456
            )
        )

    def submit_request_audit(self, project_id, request_id):
        return self.request_client.update_request(project_id,RequestCreateUpdate(
            id=request_id,
            request_status_id=11
        )
    )

    def submit_request_completeness_check_with_consultation(self, project_id, request_id):
        return self.request_client.update_request(project_id, RequestCreateUpdate(
            id=request_id,
            request_status_id=9,
            start_stakeholder_consultation=True
        )
    )

    def submit_request_in_assignment(self, project_id, request_id):
        return self.request_client.update_request(project_id, RequestCreateUpdate(
            id=request_id,
            request_status_id=12
        )
    )

    def submit_request_in_progress(self, project_id, request_id):
        return self.request_client.update_request(project_id, RequestCreateUpdate(
            id=request_id,
            request_status_id=10
        )
    )

    def submit_request_round_completion(self, project_id, request_id):
        return self.request_client.update_request(project_id, RequestCreateUpdate(
            id=request_id,
            request_status_id=13
        )
    )

    def submit_request_approved(self, project_id, request_id):
        self.request_client.update_request(project_id,
            RequestCreateUpdate(
                id=request_id,
                request_status_id=4
            )
        )
