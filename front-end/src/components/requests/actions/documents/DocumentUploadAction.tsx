import UploadIcon from "@mui/icons-material/CloudUpload";
import { Button } from "@mui/material";
import { useContext, useState } from "react";

import { GSRequest, hasClosedStatus } from "../../../../common/request/GSRequest";
import { AuthContext } from "../../../Auth/Login/AuthProvider";
import "../../RequestsView.scss";
import { RequiredDocumentsResponse } from "../../Requests";
import UploadDocumentModal from "../../documents/upload-document-modal/UploadRequestDocumentModal";

export const DocumentUploadAction = ({
  request,
  onDocumentUpload,
  disabled,
  requiredDocuments,
}: {
  request: GSRequest;
  onDocumentUpload: () => void;
  disabled: boolean;
  requiredDocuments?: RequiredDocumentsResponse;
}) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const authContext = useContext(AuthContext);

  const canUploadDocument = () => {
    return authContext.isArmUser() || authContext.isAdminUser() || !hasClosedStatus(request);
  };

  const handleOpenModal = () => {
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    onDocumentUpload();
    setIsModalOpen(false);
  };

  return (
    <>
      {canUploadDocument() && (
        <Button disabled={disabled} onClick={handleOpenModal} variant="outlined" className="action-button">
          <UploadIcon />
          <span className="action-text">Upload Document</span>
        </Button>
      )}

      {isModalOpen && (
        <UploadDocumentModal
          isOpen={isModalOpen}
          projectId={request.project?.id || 0}
          request={request}
          onClose={handleCloseModal}
          edit={false}
          document={null}
          requiredDocuments={requiredDocuments}
        />
      )}
    </>
  );
};
