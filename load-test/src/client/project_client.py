from requests.cookies import cookiejar_from_dict
from typing import Any
from locust.clients import HttpSession
from src.models.project_create import ProjectCreate


class ProjectClient:
    def __init__(self, client: HttpSession, cookies: Any):
        self.client = client
        self.cookies = cookies


    def get_projects(self, page: int, page_size: int):
        cookiejar_from_dict(self.cookies, self.client.cookies)
        return self.client.get(f"/api/projects?page={page}&size={page_size}", name="get_projects")


    def get_project_detail(self, project_id):
        cookiejar_from_dict(self.cookies, self.client.cookies)
        return self.client.get(f"/api/projects/{project_id}", name="get_project_detail")

    def create_project(self, project_create: ProjectCreate):
        cookiejar_from_dict(self.cookies, self.client.cookies)
        return self.client.post("/api/projects",
                                name="create_project",
                                json=project_create.to_dict()).json()

