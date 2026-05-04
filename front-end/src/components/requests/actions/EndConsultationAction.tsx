import { Cancel } from "@mui/icons-material";
import { Button } from "@mui/material";

import { GSRequest } from "../../../common/request/GSRequest";
import { skipStakeholderConsultation } from "../Requests";
import "../RequestsView.scss";

export const EndConsultationAction = (props: { request: GSRequest; updateRequest: () => void }) => {
  const endStakeholderConsultation = async () => {
    const response = await skipStakeholderConsultation(props.request.id);
    if (response !== false) {
      props.updateRequest();
    }
  };

  return (
    <Button onClick={endStakeholderConsultation} variant="outlined" className="action-button">
      <Cancel />
      <span className="action-text">End stakeholder consultation</span>
    </Button>
  );
};
