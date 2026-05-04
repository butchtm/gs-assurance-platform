import EditIcon from "@mui/icons-material/Edit";
import { IconButton } from "@mui/material";
import { useContext, useState } from "react";

import { GSDocument } from "../../../../common/document/GSDocument";
import { GSRequest, hasClosedStatus } from "../../../../common/request/GSRequest";
import { PermissionGroup } from "../../../../PermissionGroup";
import ProtectedComponent from "../../../../ProtectedComponent";
import { AuthContext } from "../../../Auth/Login/AuthProvider";
import UploadDocumentModal from "../../documents/upload-document-modal/UploadRequestDocumentModal";
import "../../RequestsView.scss";

export const DocumentModifyAction = (props: {
  request: GSRequest;
  document: GSDocument;
  onDocumentModify: () => void;
}) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const authContext = useContext(AuthContext);

  const canEditDocument = () => {
    return authContext.isArmUser() || authContext.isAdminUser() || !hasClosedStatus(props.request);
  };

  const handleOpenModal = () => {
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    props.onDocumentModify();
    setIsModalOpen(false);
  };

  return (
    <ProtectedComponent
      roles={[
        PermissionGroup.GS_ADMIN,
        PermissionGroup.ARM_TEAM,
        PermissionGroup.VVB,
        PermissionGroup.GS_REVIEWER,
        PermissionGroup.GS_REGISTRY,
      ]}
    >
      {canEditDocument() && (
        <IconButton aria-label="Modify document" onClick={handleOpenModal}>
          <EditIcon />
        </IconButton>
      )}

      {isModalOpen && (
        <UploadDocumentModal
          isOpen={isModalOpen}
          projectId={props.request.project?.id || 0}
          request={props.request}
          onClose={handleCloseModal}
          edit={true}
          document={props.document}
        />
      )}
    </ProtectedComponent>
  );
};
