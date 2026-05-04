import "./ProjectDocuments.css";
import { toast } from "react-toastify";
import dayjs from "dayjs";
import DownloadRoundedIcon from "@mui/icons-material/DownloadRounded";
import { Typography, Table, TableHead, TableRow, TableCell, TableBody, IconButton } from "@mui/material";

import { PublicStakeholderDocument } from "../PublicConsultation/PublicConsultation";
import { downloadPublicDocument } from "../../../common/document/ProjectDocuments";

interface PublicDocumentsListProps {
  documents?: PublicStakeholderDocument[];
}

export const PublicDocumentsList: React.FC<PublicDocumentsListProps> = ({ documents }: PublicDocumentsListProps) => {
  const handleDownload = async (documentId: string, documentName: string) => {
    const downloadToast = toast.info(`Downloading "${documentName}"...`, {
      autoClose: false,
      closeButton: false,
      draggable: false,
      closeOnClick: false,
    });

    try {
      await downloadPublicDocument(documentId, documentName);

      toast.update(downloadToast, {
        render: `"${documentName}" has started downloading.`,
        type: toast.TYPE.SUCCESS,
        autoClose: 3000,
        closeButton: true,
      });
    } catch (error) {
      console.error("Failed to download document:", error);
      toast.update(downloadToast, {
        render: `Failed to download "${documentName}". Please try again.`,
        type: toast.TYPE.ERROR,
        autoClose: 5000,
        closeButton: true,
      });
    }
  };

  if (!documents?.length) {
    return (
      <Typography variant="body1" sx={{ textAlign: "left", padding: "10px 0 0 5px" }}>
        No Public Documents Found
      </Typography>
    );
  }
  return (
    <Table>
      <TableHead>
        <TableRow>
          <TableCell className="row-text">Document Name</TableCell>
          <TableCell className="row-text">Uploaded On</TableCell>
          <TableCell className="row-text">Download</TableCell>
        </TableRow>
      </TableHead>
      <TableBody>
        {documents.map((document) => (
          <TableRow key={document.id}>
            <TableCell className="row-text">{document.displayCategoryType + " (" + document.filename + ")"}</TableCell>
            <TableCell className="row-text">{dayjs.utc(document.uploadedTimestamp).format("DD/MM/YYYY")}</TableCell>
            <TableCell className="row-text">
              {document.isMissing ? (
                "Not available to download"
              ) : (
                <IconButton
                  aria-label="Download"
                  data-testid="download-button"
                  onClick={() => handleDownload(document.id, document.filename)}
                >
                  <DownloadRoundedIcon />
                </IconButton>
              )}
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
};
