import DownloadRoundedIcon from "@mui/icons-material/DownloadRounded";
import { IconButton } from "@mui/material";
import { toast } from "react-toastify";

import { GSDocument } from "../../../../common/document/GSDocument";
import { downloadDocument } from "../../../../common/document/ProjectDocuments";
import "../../RequestsView.scss";

export const DocumentDownloadAction = (props: { document: GSDocument }) => {
  const handleDownload = async (documentId: string, documentName: string) => {
    const downloadToast = toast.info(`Downloading "${documentName}"...`, {
      autoClose: false,
      closeButton: false,
      draggable: false,
      closeOnClick: false,
    });

    try {
      await downloadDocument(documentId, documentName);

      toast.update(downloadToast, {
        render: `"${documentName}" has started downloading.`,
        type: toast.TYPE.SUCCESS,
        autoClose: 3000,
        closeButton: true,
      });
    } catch (error) {
      toast.update(downloadToast, {
        render: `Failed to download "${documentName}". Please try again.`,
        type: toast.TYPE.ERROR,
        autoClose: 5000,
        closeButton: true,
      });
      console.error("Failed to download document:", error);
    }
  };

  if (props.document.isMissing) {
    return <>Not available for download</>;
  }

  return (
    <IconButton
      aria-label="Download document"
      onClick={() => handleDownload(props.document.id, props.document.fileName)}
    >
      <DownloadRoundedIcon />
    </IconButton>
  );
};
