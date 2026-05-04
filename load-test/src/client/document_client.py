import logging
import random
import string
from random import randint
from typing import Any
from requests.cookies import cookiejar_from_dict
from locust.clients import HttpSession


def create_random_name():
    return ''.join(random.choices(string.ascii_lowercase, k=10)).join('.txt')

def choose_document_size():
    random_int = randint(0, 9)
    if  random_int < 6:
        file_choice = "small_document.txt"  # 1MB
    # elif random_int < 9:
    else:
        file_choice = "large_document.txt"  # 10MB
    # else:
    #     file_choice = "very_large_document.txt"  # 500MB
    logging.info("Uploading file: " + file_choice)
    return "src/resources/" + file_choice

class DocumentClient:
    def __init__(self, client: HttpSession, cookies: Any):
        self.client = client
        self.cookies = cookies

    def upload_document(self, project_id: int, request_id: int):
        file_path = choose_document_size()
        name = create_random_name()
        file = {'file': (name, open(file_path),'multipart/form-data')}
        return self.client.post(f"/api/projects/{project_id}/requests/{request_id}/document-types/1000/documents",
                                name="upload document",
                                cookies=self.cookies,
                                files=file).json()

    def download_document(self, document_id: str):
        logging.getLogger("urllib3").setLevel(logging.ERROR)
        cookiejar_from_dict(self.cookies, self.client.cookies)
        self.client.get(f"/api/documents/{document_id}/download",
                        name="download document",
                        stream=True)
