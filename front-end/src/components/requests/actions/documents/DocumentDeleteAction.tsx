import DeleteIcon from "@mui/icons-material/Delete";
import { IconButton } from "@mui/material";
import { useContext, useState } from "react";

import { GSDocument } from "../../../../common/document/GSDocument";
import { GSRequest } from "../../../../common/request/GSRequest";
import { PermissionGroup } from "../../../../PermissionGroup";
import ProtectedComponent from "../../../../ProtectedComponent";
import { AuthContext } from "../../../Auth/Login/AuthProvider";
import "../../RequestsView.scss";
import DeleteDocumentDialog from "../../documents/delete-document-dialog/DeleteDocumentDialog";

export const DocumentDeleteAction = (props: {
  request: GSRequest;
  document: GSDocument;
  onDocumentDelete: () => void;
}) => {
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const authContext = useContext(AuthContext);

  const handleOpenDialog = () => {
    setIsDialogOpen(true);
  };

  const handleCloseDialog = () => {
    props.onDocumentDelete();
    setIsDialogOpen(false);
  };

  return (
    <ProtectedComponent roles={[PermissionGroup.GS_ADMIN]}>
      {authContext.isAdminUser() && (
        <IconButton aria-label="Delete document" onClick={handleOpenDialog}>
          <DeleteIcon />
        </IconButton>
      )}

      {isDialogOpen && (
        <DeleteDocumentDialog
          isOpen={isDialogOpen}
          request={props.request}
          onClose={handleCloseDialog}
          document={props.document}
        />
      )}
    </ProtectedComponent>
  );
};
