import React from "react";

import { GSRequest } from "../../../../common/request/GSRequest";
import { GSDocument } from "../../../../common/document/GSDocument";
import { helperTexts } from "../../../Project/create-project/HelperText";
import { OK_ACTIONS, PiiActionWarningPill, WARNING_ACTIONS } from "../../../../common/piiAnalysis/PiiActionWarningPill";
import { DocumentReviewPiiAction } from "../../actions/documents/DocumentReviewPiiAction";
import { PiiActionCode } from "../../../../common/piiAnalysis/PiiAction";

import { DocumentPiiScanPill } from "./DocumentPiiScanPill";

const getHelperTextForAction = (action?: PiiActionCode | null) => {
  if (!action) {
    return <></>;
  }
  if (WARNING_ACTIONS.includes(action)) {
    return helperTexts.documentPiiRequiresReview();
  }
  if (action == PiiActionCode.PII_NOT_SUITABLE) {
    return helperTexts.documentPiiNotAcceptable();
  }
};

export const DocumentPiiWarning: React.FC<{
  request: GSRequest;
  document: GSDocument;
  onDocumentModify: () => void;
}> = ({ request, document, onDocumentModify }) => {
  const scanStatus = document.piiScanStatus;
  const currentAction = document.piiAction;
  const helperTextForAction = getHelperTextForAction(currentAction);
  return (
    <>
      {!currentAction && scanStatus && <DocumentPiiScanPill scanStatus={scanStatus} />}
      {currentAction && !OK_ACTIONS.includes(currentAction) && (
        <>
          <PiiActionWarningPill action={currentAction} helperText={helperTextForAction} />
          <DocumentReviewPiiAction request={request} document={document} onDocumentModify={onDocumentModify} />
        </>
      )}
    </>
  );
};
