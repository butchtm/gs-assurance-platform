from typing import Any
from locust.clients import HttpSession
from requests.cookies import cookiejar_from_dict
from src.models.request_create_update import RequestCreateUpdate


class RequestClient:
    def __init__(self, client: HttpSession, cookies: Any):
        self.client = client
        self.cookies = cookies


    def get_requests(self, page_size: int):
        cookiejar_from_dict(self.cookies, self.client.cookies)
        return self.client.get(f"/api/requests?size={page_size}", name="get_requests")

    def get_request(self, request_id):
        cookiejar_from_dict(self.cookies, self.client.cookies)
        return self.client.get(f"/api/requests/{request_id}", name="get_request")

    def create_request(self, project_id: int, request_create: RequestCreateUpdate):
        cookiejar_from_dict(self.cookies, self.client.cookies)
        return self.client.post(f"/api/projects/{project_id}/requests",
                                name="create_request",
                                json=request_create.to_dict()).json()

    def update_request(self, project_id: int, request_update: RequestCreateUpdate):
        cookiejar_from_dict(self.cookies, self.client.cookies)
        return self.client.put(f"/api/projects/{project_id}/requests",
                               name="update_request",
                               json=request_update.to_dict()).json()
