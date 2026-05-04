from dataclasses import dataclass
from dataclass_wizard import JSONWizard

@dataclass
class AssignmentCreateUpdate(JSONWizard):
    request_id: int
    account_id: int
    assignment_type_id: int
    assignment_status_id: int
