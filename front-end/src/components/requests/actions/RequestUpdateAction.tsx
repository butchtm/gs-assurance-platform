import { Update } from "@mui/icons-material";
import { Button } from "@mui/material";
import { useNavigate, useSearchParams } from "react-router-dom";

import { GSRequest } from "../../../common/request/GSRequest";
import { BatchedProjectRequests } from "../Requests";
import "../RequestsView.scss";

export const RequestUpdateAction = (props: { request: GSRequest; requestBatches?: BatchedProjectRequests }) => {
  const [, setSearchParams] = useSearchParams();
  const navigate = useNavigate();

  const storeRequestIdBeforeNavigatingAway = () => {
    setSearchParams({ expand: props.request.id.toString() });
  };

  const onClickUpdateRequest = () => {
    storeRequestIdBeforeNavigatingAway();
    navigate("/update-request", {
      state: {
        request: props.request,
        requestBatches: props.requestBatches,
      },
    });
  };

  return (
    <Button onClick={onClickUpdateRequest} variant="outlined" className="action-button">
      <Update />
      <span className="action-text">Update Request</span>
    </Button>
  );
};
