from typing import Any
from locust.clients import HttpSession
from requests.cookies import cookiejar_from_dict

class ConsultationClient:
    def __init__(self, client: HttpSession, cookies: Any):
        self.client = client
        self.cookies = cookies

    def end_consultation(self, request_id: int):
        cookiejar_from_dict(self.cookies, self.client.cookies)
        self.client.put(f"/api/requests/{request_id}/live-stakeholder-consultations/completions",
                        name = "end_consultation")

