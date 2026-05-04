import datetime
from typing import Any
from locust.clients import HttpSession

from src.client.project_client import ProjectClient
from src.models.project_create import ProjectCreate, ProjectCreditingPeriodDTO


class ProjectService:

    def __init__(self, client: HttpSession, cookies: Any):
        self.project_client = ProjectClient(client, cookies)

    def get_projects_page(self):
        self.project_client.get_projects(1, 100)

    def get_project_detail(self, project_id):
        self.project_client.get_project_detail(project_id)

    def create_project(self):
        return self.project_client.create_project(
            ProjectCreate(
                name="Test",
                project_classification_id=6,
                account_id=6,
                project_status_id=1,
                description="Created by Locust",
                crediting_periods=[
                    ProjectCreditingPeriodDTO(
                        1,
                        datetime.datetime.now().strftime("%Y-%m-%dT%H:%M:%SZ"),
                        datetime.datetime.now().strftime("%Y-%m-%dT%H:%M:%SZ"))
                ]
            )
        )
