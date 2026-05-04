import { Box } from "@mui/material";
import { useContext } from "react";

import { GSDocument } from "../../../../common/document/GSDocument";
import { GSRequest } from "../../../../common/request/GSRequest";
import { DocumentDownloadAction } from "../../actions/documents/DocumentDownloadAction";
import { DocumentModifyAction } from "../../actions/documents/DocumentModifyAction";
import { DocumentDeleteAction } from "../../actions/documents/DocumentDeleteAction";
import { AuthContext } from "../../../Auth/Login/AuthProvider";

export const DocumentActions = ({
  document,
  request,
  onDocumentModify,
}: {
  document: GSDocument;
  request: GSRequest;
  onDocumentModify: () => void;
}) => {
  const authContext = useContext(AuthContext);
  if (authContext.isSupportUser()) {
    return <></>;
  }

  return (
    <Box sx={{ margin: "auto 0" }}>
      <DocumentDownloadAction document={document} />
      <DocumentModifyAction request={request} document={document} onDocumentModify={onDocumentModify} />
      <DocumentDeleteAction request={request} document={document} onDocumentDelete={onDocumentModify} />
    </Box>
  );
};
