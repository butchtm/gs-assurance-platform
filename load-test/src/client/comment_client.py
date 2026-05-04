from typing import Any
from locust.clients import HttpSession
from requests.cookies import cookiejar_from_dict
from src.models.comment_create import CommentCreateDTO


class CommentClient:
    def __init__(self, client: HttpSession, cookies: Any):
        self.client = client
        self.cookies = cookies

    def post_comment(self, comment: CommentCreateDTO):
        cookiejar_from_dict(self.cookies, self.client.cookies)
        self.client.post("/api/public/comments",
                         name="add_comment",
                         json=comment.to_dict())
