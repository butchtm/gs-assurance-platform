import { Box, Button, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { useContext } from "react";

import { ReferenceType, useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { GSDocumentScrap } from "../../../../common/document/GSDocumentScrap";
import { isFormEnabled } from "../../../../common/env/env";
import { createForm } from "../../../Forms/Forms";
import { AuthContext } from "../../../Auth/Login/AuthProvider";

export const MissingDocument = ({
  missingDocument,
  gsid,
  requestId,
  requestStatus,
}: {
  missingDocument: GSDocumentScrap;
  gsid: string;
  requestId: number;
  requestStatus: ReferenceType;
}) => {
  const { allReferenceTypes } = useReferenceTypes();
  const navigate = useNavigate();
  const authContext = useContext(AuthContext);
  const canStartForm =
    authContext.isAdminUser() ||
    authContext.isArmUser() ||
    (authContext.isProjectDeveloperUser() && requestStatus.code === "IN_PREPARATION");

  const getResponsibilityText = (): string => {
    const documentType = allReferenceTypes.DocumentType.find(
      (docType) => docType.id === missingDocument.documentTypeId,
    );
    switch (documentType?.code) {
      case "CDM_VALIDATION_REPORT":
      case "GOLD_STANDARD_VALIDATION_REPORT":
      case "CDM_VERIFICATION_REPORT":
      case "GS4GG_VERIFICATION_REPORT":
      case "GOLD_STANDARD_VERIFICATION_REPORT":
      case "VALIDATION_REPORT_VVB_STATEMENT":
      case "VALIDATION_REPORT_OR_VERIFICATION_REPORT":
      case "VALIDATION_REPORT":
      case "VERIFICATION_REPORT":
      case "OTHER_STANDARD_VALIDATION_REPORT":
      case "OTHER_STANDARD_VERIFICATION_REPORT":
        return "This document should be uploaded by the VVB.";

      case "REVIEW_FEEDBACK_FORM":
        return "To be uploaded by Gold Standard.";

      default:
        return "This document should be uploaded by the Project Developer.";
    }
  };

  const beginForm = async () => {
    const response = await createForm(requestId, missingDocument.formType?.id);
    if (response) {
      navigate(`/projects/${gsid}/requests/${requestId}/form/${response.id}`, {
        state: {
          formType: missingDocument.formType,
          requestId: requestId,
          form: response,
        },
      });
    }
  };

  return (
    <Box
      key={missingDocument.documentTypeId}
      sx={{
        marginBottom: "10px",
        padding: 2,
        borderRadius: "5px",
        backgroundColor: "var(--color-surface-neutral)",
        textAlign: "left",
        display: "flex",
        justifyContent: "space-between",
      }}
    >
      <Box>
        <Typography variant="body1">
          <b>Not Uploaded</b> - <span>{missingDocument.documentTypeDescription}</span>
        </Typography>
        <Typography variant="body2" color="textSecondary" sx={{ marginTop: "10px" }}>
          {getResponsibilityText()}
        </Typography>
      </Box>
      {missingDocument.formType !== null && isFormEnabled() && canStartForm && (
        <Button
          variant="outlined"
          sx={{ height: "fit-content", margin: "auto 0", backgroundColor: "white" }}
          onClick={beginForm}
          data-testid={`start-form-${missingDocument.formType?.id}`}
        >
          Start
        </Button>
      )}
    </Box>
  );
};
