from dataclasses import dataclass
from dataclass_wizard import JSONWizard

@dataclass
class CommentCreateDTO(JSONWizard):
    request_id: int
    comment: str
    users_name: str
    users_email: str
    account_id: int | None
    anonymous: bool
