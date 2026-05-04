from typing import Any
from locust.clients import HttpSession

from src.client.assignment_client import AssignmentClient
from src.models.assignment_create_update import AssignmentCreateUpdate


class AssignmentService:

    def __init__(self, client: HttpSession, cookies: Any):
        self.assignment_client = AssignmentClient(client, cookies)

    def create_audit_assignment(self, request_id):
        return self.assignment_client.create_assignment(AssignmentCreateUpdate(
            request_id=request_id,
            account_id=1, # ARMTeam
            assignment_type_id=0,
            assignment_status_id=0
        ))

    def create_reviewer_assignment(self, request_id):
        return self.assignment_client.create_assignment(AssignmentCreateUpdate(
            request_id=request_id,
            account_id=1, # ARMTeam
            assignment_type_id=1,
            assignment_status_id=0
        ))

    def create_completeness_check_assignment(self, request_id):
        return self.assignment_client.create_assignment(AssignmentCreateUpdate(
            request_id=request_id,
            account_id=1, # ARMTeam
            assignment_type_id=4,
            assignment_status_id=1 # ARMTeam always auto accepts
        ))

    def accept_audit_assignment(self, assignment_id, request_id):
        self.assignment_client.accept_assignment(AssignmentCreateUpdate(
            request_id=request_id,
            account_id=1, # ARMTeam
            assignment_type_id=0,
            assignment_status_id=1 # ARMTeam always auto accepts
        ), assignment_id)


    def accept_reviewer_assignment(self, assignment_id, request_id):
        self.assignment_client.accept_assignment(AssignmentCreateUpdate(
            request_id=request_id,
            account_id=1, # ARMTeam
            assignment_type_id=1,
            assignment_status_id=1 # ARMTeam always auto accepts
        ), assignment_id)