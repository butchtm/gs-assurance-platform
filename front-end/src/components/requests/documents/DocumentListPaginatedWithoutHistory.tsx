import { CircularProgress, Box } from "@mui/material";
import { useEffect, useState } from "react";

import { DocumentCategoryType, GSDocument } from "../../../common/document/GSDocument";
import { GSRequest } from "../../../common/request/GSRequest";
import Pagination from "../../Reusable/Pagination";
import { getDocumentsForRequestByCategory } from "../Requests";

import { Document } from "./document/Document";
import { DocumentListActions } from "./DocumentListActions";

type DocumentListPaginatedProps = {
  projectId: number;
  request: GSRequest;
  onDocumentModify: () => void;
  documentCategoryType: DocumentCategoryType;
  refreshDocuments: boolean;
};

export const DocumentListPaginatedWithoutHistoryOrRequirements = ({
  projectId,
  request,
  onDocumentModify,
  documentCategoryType,
  refreshDocuments,
}: DocumentListPaginatedProps) => {
  const [documents, setDocuments] = useState<GSDocument[]>([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const getRequestDocuments = async () => {
      const response = await getDocumentsForRequestByCategory(
        projectId,
        request.id,
        documentCategoryType.id,
        currentPage,
        25,
      );
      setDocuments(response.content);
      setTotalPages(response.totalPages);
      setIsLoading(false);
    };

    getRequestDocuments();
  }, [currentPage, refreshDocuments]);

  return (
    <>
      <Box sx={{ marginBottom: "18px", display: "flex", justifyContent: "center", gap: "10px" }}>
        <DocumentListActions
          request={request}
          documentCategoryType={documentCategoryType}
          disabled={documents.length == 0}
        />
      </Box>
      {isLoading && <CircularProgress />}
      {documents.map((document) => (
        <Document key={document.id} document={document} request={request} onDocumentModify={onDocumentModify} />
      ))}
      {totalPages > 0 && (
        <Pagination
          total={totalPages}
          current={currentPage}
          onChange={(newPage) => {
            setCurrentPage(newPage);
          }}
        />
      )}
    </>
  );
};
