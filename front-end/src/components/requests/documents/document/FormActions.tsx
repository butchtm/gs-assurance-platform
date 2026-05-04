import { Box, Button } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { useContext } from "react";

import { RequestForm } from "../../../Forms/Forms";
import { ReferenceType, useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { AuthContext } from "../../../Auth/Login/AuthProvider";

export const FormActions = ({
  form,
  requestId,
  requestStatus,
  isBatchRequest,
  gsid,
}: {
  form: RequestForm;
  requestId: number;
  requestStatus: ReferenceType;
  isBatchRequest?: boolean;
  gsid: string;
}) => {
  const navigate = useNavigate();
  const { activeReferenceTypes } = useReferenceTypes();
  const authContext = useContext(AuthContext);

  const submittedFormType = activeReferenceTypes.RequestFormStatus.find((x) => x.code == "SUBMITTED");

  const canResume =
    (authContext.isAdminUser() || authContext.isArmUser() || authContext.isProjectDeveloperUser()) &&
    !!submittedFormType &&
    form.status.id !== submittedFormType.id;

  const canEditSubmitted =
    (authContext.isAdminUser() ||
      authContext.isArmUser() ||
      (authContext.isProjectDeveloperUser() && requestStatus.code == "IN_PREPARATION")) &&
    !!submittedFormType &&
    form.status.id === submittedFormType.id;

  const navigateToForm = (readonly?: boolean) => {
    navigate(`/projects/${gsid}/requests/${requestId}/form/${form.id}`, {
      state: {
        formType: form.type,
        requestId: requestId,
        form: form,
        readonly: readonly,
      },
    });
  };

  return (
    <Box sx={{ display: "flex" }}>
      {canResume && (
        <Button
          variant="outlined"
          sx={{ height: "fit-content", margin: "auto 0", backgroundColor: "white" }}
          onClick={() => navigateToForm()}
          data-testid={`resume-form-${form.type.id}`}
          disabled={isBatchRequest}
        >
          Resume
        </Button>
      )}
      {canEditSubmitted ? (
        <Button
          variant="outlined"
          sx={{ height: "fit-content", margin: "auto 0", backgroundColor: "white" }}
          onClick={() => navigateToForm()}
          data-testid={`edit-form-${form.type.id}`}
          disabled={isBatchRequest}
        >
          Edit
        </Button>
      ) : (
        <Button
          variant="outlined"
          sx={{ height: "fit-content", margin: "auto 0", backgroundColor: "white" }}
          onClick={() => navigateToForm(true)}
          data-testid={`view-form-${form.type.id}`}
        >
          View
        </Button>
      )}
    </Box>
  );
};
