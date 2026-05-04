from typing import Any
from locust.clients import HttpSession
from requests.cookies import cookiejar_from_dict
from locust.clients import HttpSession

from src.models.assignment_create_update import AssignmentCreateUpdate


class AssignmentClient:
    def __init__(self, client: HttpSession, cookies: Any):
        self.client = client
        self.cookies = cookies

    def create_assignment(self, create_assignment: AssignmentCreateUpdate):
        cookiejar_from_dict(self.cookies, self.client.cookies)
        return self.client.post("/api/assignments",
                                name="create_assignment",
                                json=create_assignment.to_dict()).json()['id']

    def accept_assignment(self, update_assignment: AssignmentCreateUpdate, assignment_id: int):
        cookiejar_from_dict(self.cookies, self.client.cookies)
        self.client.put(f"/api/assignments/{assignment_id}",
                        name="accept_assignment",
                        json=update_assignment.to_dict())


