from dataclasses import dataclass
from typing import List, Optional
from datetime import datetime
from dataclass_wizard import JSONWizard


@dataclass
class TransitionProjectDetails(JSONWizard):
    project_link_in_other_scheme: Optional[str] = None
    crediting_period_start_date_other_schemes: Optional[datetime] = None
    crediting_period_end_date_other_schemes: Optional[datetime] = None
    transition_approval_procedure_id: Optional[int] = None
    transition_from_other_scheme_id: Optional[int] = None
    transition_pathway_id: Optional[int] = None


@dataclass
class ProjectCreditingPeriodDTO(JSONWizard):
    period_id: int
    start_date: str
    end_date: str



@dataclass
class ProjectCreate(JSONWizard):
    name: str
    project_classification_id: int
    account_id: int
    project_status_id: int
    description: Optional[str] = None
    master_project_id: Optional[int] = None
    parent_project_id: Optional[int] = None
    size_id: Optional[int] = None
    current_standard_id: Optional[int] = None
    fee_schedule_id: Optional[int] = None
    registration_type_id: Optional[int] = None
    carbon_stream_id: Optional[int] = None
    project_asset_ids: Optional[List[int]] = None
    location_ids: Optional[List[int]] = None
    start_date: Optional[datetime] = None
    estimated_annual_units: Optional[int] = None
    type_ids: Optional[List[int]] = None
    methodology_ids: Optional[List[int]] = None
    sustainable_development_goal_ids: Optional[List[int]] = None
    notes: Optional[str] = None
    certified_design_date: Optional[datetime] = None
    certified_project_date: Optional[datetime] = None
    last_design_certification_renewal_date: Optional[datetime] = None
    last_performance_review_date: Optional[datetime] = None
    transition_project: Optional[TransitionProjectDetails] = None
    crediting_periods: Optional[List[ProjectCreditingPeriodDTO]] = None


