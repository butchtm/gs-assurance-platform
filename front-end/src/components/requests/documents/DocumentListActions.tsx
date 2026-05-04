import DownloadIcon from "@mui/icons-material/Download";
import { Button } from "@mui/material";
import { useContext, useState } from "react";

import { DocumentCategoryType } from "../../../common/document/GSDocument";
import { GSRequest } from "../../../common/request/GSRequest";
import { AuthContext } from "../../Auth/Login/AuthProvider";

import "../RequestsView.scss";
import { DownloadAllDocumentsModal } from "./DownloadAllDocumentsModal";

export const DocumentListActions = ({
  request,
  documentCategoryType,
  disabled,
}: {
  request: GSRequest;
  documentCategoryType: DocumentCategoryType;
  disabled?: boolean;
}) => {
  const [isDownloadAllModalOpen, setIsDownloadAllModalOpen] = useState(false);
  const authContext = useContext(AuthContext);

  if (authContext.isSupportUser()) {
    return <></>;
  }

  return (
    <>
      <Button onClick={() => setIsDownloadAllModalOpen(true)} disabled={disabled}>
        <DownloadIcon />
        <span className="action-text">Download All {documentCategoryType.description}</span>
      </Button>
      {isDownloadAllModalOpen && (
        <DownloadAllDocumentsModal
          isOpen={isDownloadAllModalOpen}
          projectId={request.project.id}
          requestId={request.id}
          displayCategoryId={documentCategoryType.id}
          onClose={() => setIsDownloadAllModalOpen(false)}
        />
      )}
    </>
  );
};
