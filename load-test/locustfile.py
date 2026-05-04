import os
import random
import logging
from time import sleep

import greenlet
from dotenv import load_dotenv
from locust import HttpUser, task, events, tag, between
from src.access_token import AuthenticationCookiesGenerator
from src.service.assignment_service import AssignmentService
from src.service.comment_service import CommentService
from src.service.consultation_service import ConsultationService
from src.service.document_service import DocumentService
from src.service.project_service import ProjectService
from src.service.request_service import RequestService

load_dotenv()

document_ids = []
request_ids = {}
assignment_ids = {}
project_ids = {}
local = not os.getenv("LOCUST_HOST").startswith('https')
logging.basicConfig(level=logging.INFO)

@events.init_command_line_parser.add_listener
def _(parser):
    parser.add_argument("--locust-host",
                        required=True,
                        env_var="LOCUST_HOST",
                        default="http://localhost:8080",
                        help="Host of backend")
    parser.add_argument("--assurance-username",
                        required=True,
                        env_var="ASSURANCE_USERNAME",
                        default="Admin",
                        help="Username for Assurance login")
    parser.add_argument("--assurance-password",
                        required=True,
                        is_secret=True,
                        env_var="ASSURANCE_PASSWORD",
                        help="Password for Assurance login")


@events.test_start.add_listener
def _(environment, **kw):
    environment.cookies = AuthenticationCookiesGenerator().get_token(
        environment.parsed_options.locust_host,
        environment.parsed_options.assurance_username,
        environment.parsed_options.assurance_password
    )

def add_document_id_to_list(document_id: str):
    logging.info("Adding doc id: " + document_id + " for user: " + str(greenlet.getcurrent().minimal_ident))
    global document_ids
    document_ids.append(document_id)

def add_request_id_to_list(request_id: int, status: int):
    logging.info("Adding request id: " + str(request_id) + " for user: " + str(greenlet.getcurrent().minimal_ident))
    global request_ids
    request_ids[request_id] = status

def add_assignment_id_to_list(request_id: int, assignment_id: int):
    logging.info("Adding assignment_id: " + assignment_id + " to request id: "
                 + str(request_id) + " for user: " + str(greenlet.getcurrent().minimal_ident))
    global assignment_ids
    assignment_ids[request_id] = assignment_id

def add_project_id_to_list(project_id: int, request_id: int):
    logging.info("Adding project id: " + str(project_id) + " for user: " + str(greenlet.getcurrent().minimal_ident))
    global project_ids
    project_ids[request_id] = project_id


class GoldStandardUser(HttpUser):

    def __init__(self, parent):
        super(GoldStandardUser, self).__init__(parent)
        self.assignment_service = None
        self.document_service = None
        self.request_service = None
        self.project_service = None
        self.consultation_service = None
        self.comment_service = None


    def on_start(self):
        self.project_service = ProjectService(self.client, self.environment.cookies)
        self.request_service = RequestService(self.client, self.environment.cookies)
        self.document_service = DocumentService(self.client, self.environment.cookies)
        self.assignment_service = AssignmentService(self.client, self.environment.cookies)
        self.consultation_service = ConsultationService(self.client, self.environment.cookies)
        self.comment_service = CommentService(self.client, self.environment.cookies)


    @tag('CreateProjectAndRequest')
    @task(2)
    def task_create_project_and_request(self):
        if len(project_ids) < 50:
            logging.info("Creating project and request for user: " + str(greenlet.getcurrent().minimal_ident))
            # PD creates project and request
            project_id = self.project_service.create_project()
            request_id = self.request_service.create_request(project_id)
            add_project_id_to_list(project_id, request_id)
            add_request_id_to_list(request_id, 0)
            sleep(10)


    @tag('RequestPDActions')
    @task(2)
    def task_pd_actions(self):
        global request_ids
        if not request_ids:
            logging.info("No requests to get for user: " + str(greenlet.getcurrent().minimal_ident))
            return
        request_id = random.choice(list(request_ids.keys()))
        if request_ids[request_id] == 0:
            logging.info("Starting in preparation for user: " + str(greenlet.getcurrent().minimal_ident))
            request_ids.pop(request_id)
            project_id = project_ids[request_id]
            self.request_service.submit_request_audit(project_id, request_id)
            audit_assignment_id = self.assignment_service.create_audit_assignment(request_id)
            global assignment_ids
            assignment_ids[request_id] = audit_assignment_id
            add_request_id_to_list(request_id, 1)
            sleep(10)


    @tag('RequestVVBActions')
    @task(2)
    def task_vvb_actions(self):
        global request_ids
        if not request_ids:
            logging.info("No requests to get for user: " + str(greenlet.getcurrent().minimal_ident))
            return
        request_id = random.choice(list(request_ids.keys()))
        if request_ids[request_id] == 1:
            logging.info("Starting in audit for user: " + str(greenlet.getcurrent().minimal_ident))
            request_ids.pop(request_id)
            project_id = project_ids[request_id]
            audit_assignment_id = assignment_ids[request_id]
            # VVB accepts assignment submits the request to completeness check and starts consultation
            self.assignment_service.accept_audit_assignment(audit_assignment_id, request_id)
            self.request_service.submit_request_completeness_check_with_consultation(project_id, request_id)
            self.assignment_service.create_completeness_check_assignment(request_id)
            add_request_id_to_list(request_id, 2)
            sleep(10)


    @tag('RequestArmAssignmentActions')
    @task(2)
    def task_arm_in_assignment(self):
        global request_ids
        if not request_ids:
            logging.info("No requests to get for user: " + str(greenlet.getcurrent().minimal_ident))
            return
        request_id = random.choice(list(request_ids.keys()))
        if request_ids[request_id] == 2:
            logging.info("Starting in assignment for user: " + str(greenlet.getcurrent().minimal_ident))
            request_ids.pop(request_id)
            project_id = project_ids[request_id]
            # Arm Team progresses to in assignment and creates reviewer assignment
            self.request_service.submit_request_in_assignment(project_id, request_id)
            reviewer_assignment_id = self.assignment_service.create_reviewer_assignment(request_id)
            global assignment_ids
            assignment_ids[request_id] =  reviewer_assignment_id
            add_request_id_to_list(request_id, 3)
            sleep(10)


    @tag('RequestReviewerActions')
    @task(2)
    def task_reviewer_actions(self):
        global request_ids
        if not request_ids:
            logging.info("No requests to get for user: " + str(greenlet.getcurrent().minimal_ident))
            return
        request_id = random.choice(list(request_ids.keys()))
        if request_ids[request_id] == 3:
            logging.info("Starting review for user: " + str(greenlet.getcurrent().minimal_ident))
            request_ids.pop(request_id)
            reviewer_assignment_id = assignment_ids[request_id]
            # Reviewer accepts assignment
            self.assignment_service.accept_reviewer_assignment(reviewer_assignment_id, request_id)
            add_request_id_to_list(request_id, 4)
            sleep(10)


    @tag('RequestArmInProgressActions')
    @task(2)
    def task_arm_in_progress(self):
        global request_ids
        if not request_ids:
            logging.info("No requests to get for user: " + str(greenlet.getcurrent().minimal_ident))
            return
        request_id = random.choice(list(request_ids.keys()))
        if request_ids[request_id] == 4:
            logging.info("Starting in progress for user: " + str(greenlet.getcurrent().minimal_ident))
            request_ids.pop(request_id)
            project_id = project_ids[request_id]
            # Arm Team progresses to in progress
            self.request_service.submit_request_in_progress(project_id, request_id)
            add_request_id_to_list(request_id, 5)
            sleep(10)


    @tag('RequestArmCompleteRequest')
    @task(2)
    def task_complete_request(self):
        if not request_ids:
            logging.info("No requests to get for user: " + str(greenlet.getcurrent().minimal_ident))
            return
        request_id = random.choice(list(request_ids.keys()))
        if request_ids[request_id] == 5:
            logging.info("Starting complete request for user: " + str(greenlet.getcurrent().minimal_ident))
            request_ids.pop(request_id)
            project_id = project_ids[request_id]
            # Arm Team progresses to in round completion
            self.request_service.submit_request_round_completion(project_id, request_id)
            # End consultation
            self.consultation_service.end_consultation(request_id)
            # Arm Team progresses approves request
            self.request_service.submit_request_approved(project_id, request_id)
            add_request_id_to_list(request_id, 6)
            sleep(10)


    @tag('UpdateRequest')
    @task(2)
    def task_update_request(self):
        if not request_ids:
            logging.info("No requests to get for user: " + str(greenlet.getcurrent().minimal_ident))
            return
        logging.info("Starting update request for user: " + str(greenlet.getcurrent().minimal_ident))
        request_id = random.choice(list(request_ids.keys()))
        project_id = project_ids[request_id]
        self.request_service.update_request(project_id, request_id)
        sleep(10)


    @tag('GetProjects')
    @task(3)
    def task_get_projects(self):
        logging.info("Starting GetProjects for user: " + str(greenlet.getcurrent().minimal_ident))
        self.project_service.get_projects_page()
        sleep(10)


    @tag('GetProjectDetail')
    @task(3)
    def task_get_project(self):
        logging.info("Starting GetProjectDetail for user: " + str(greenlet.getcurrent().minimal_ident))
        if not project_ids:
            logging.info("No projects to get for user: " + str(greenlet.getcurrent().minimal_ident))
            return
        project_id = random.choice(list(project_ids.values()))
        self.project_service.get_project_detail(project_id)
        sleep(10)


    @tag('GetRequests')
    @task(3)
    def task_get_requests(self):
        logging.info("Starting GetRequests for user: " + str(greenlet.getcurrent().minimal_ident))
        self.request_service.get_all_requests_page()
        sleep(10)


    @tag('GetRequestDetail')
    @task(3)
    def task_get_request(self):
        logging.info("Starting GetRequest for user: " + str(greenlet.getcurrent().minimal_ident))
        if not request_ids:
            logging.info("No requests to get for user: " + str(greenlet.getcurrent().minimal_ident))
            return
        request_id = random.choice(list(request_ids.keys()))
        self.request_service.get_request(request_id)
        sleep(10)


    @tag('SubmitComment')
    @task(1)
    def task_submit_comment(self):
        logging.info("Starting SubmitComment for user: " + str(greenlet.getcurrent().minimal_ident))
        if not request_ids:
            logging.info("No requests to post comments to for user: " + str(greenlet.getcurrent().minimal_ident))
            return
        request_id = random.choice(list(request_ids.keys()))
        logging.info("Posting comment to: " + str(request_id) + " for user: " + str(greenlet.getcurrent().minimal_ident))
        self.comment_service.post_comment(request_id)
        sleep(10)


    @tag('UploadDocument')
    @task(1)
    def task_upload_document(self):
        if not request_ids:
            logging.info("No requests to get for user: " + str(greenlet.getcurrent().minimal_ident))
            return
        logging.info("Starting upload document for user: " + str(greenlet.getcurrent().minimal_ident))
        request_id = random.choice(list(request_ids.keys()))
        project_id = project_ids[request_id]
        add_document_id_to_list(self.document_service.upload_document(project_id, request_id))
        sleep(10)


    @tag('DownloadDocument')
    @task(2)
    def task_download_document(self):
        logging.info("Starting DownloadDocument for user: " + str(greenlet.getcurrent().minimal_ident))
        if local:
            logging.info("Not downloading documents as running local" + " for user: " + str(greenlet.getcurrent().minimal_ident))
            return
        if not document_ids:
            logging.info("No documents to Download for user: " + str(greenlet.getcurrent().minimal_ident))
            return
        document_id = random.choice(document_ids)
        logging.info("Downloading: " + document_id + " for user: " + str(greenlet.getcurrent().minimal_ident))
        self.document_service.download_document(document_id)
        sleep(10)
