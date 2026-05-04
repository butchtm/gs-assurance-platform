from typing import Any
from locust.clients import HttpSession

from src.client.comment_client import CommentClient
from src.models.comment_create import CommentCreateDTO


class CommentService:
    def __init__(self, client: HttpSession, cookies: Any):
        self.comment_client = CommentClient(client, cookies)

    def post_comment(self, request_id: int):
        self.comment_client.post_comment(CommentCreateDTO(
            request_id=request_id,
            comment="this is a test comment",
            users_name="username",
            users_email="email@email.com",
            account_id=None,
            anonymous=True
        )
    )


