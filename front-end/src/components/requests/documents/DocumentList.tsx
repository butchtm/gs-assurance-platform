import { CircularProgress, Box } from "@mui/material";
import { useEffect, useState } from "react";

import { DocumentCategoryType, GSDocument } from "../../../common/document/GSDocument";
import { GSDocumentScrap } from "../../../common/document/GSDocumentScrap";
import { GSRequest } from "../../../common/request/GSRequest";
import { getAllDocumentsForRequestByCategory } from "../Requests";
import { getAllRequestForms, RequestForm } from "../../Forms/Forms";

import { Document } from "./document/Document";
import { MissingDocument } from "./document/MissingDocument";
import { DocumentListActions } from "./DocumentListActions";
import { Form } from "./document/Form";

interface DocumentAndHistory {
  document: GSDocument;
  history?: GSDocument[];
}

export const DocumentList = ({
  projectId,
  request,
  onDocumentModify,
  documentCategoryType,
  requiredDocuments,
  refreshDocuments,
}: {
  projectId: number;
  request: GSRequest;
  onDocumentModify: () => void;
  documentCategoryType: DocumentCategoryType;
  requiredDocuments: GSDocumentScrap[];
  refreshDocuments: boolean;
}) => {
  const [documentsWithHistory, setDocumentsWithHistory] = useState<DocumentAndHistory[]>([]);
  const [requestForms, setRequestForms] = useState<RequestForm[]>([]);
  const [missingDocuments, setMissingDocuments] = useState<GSDocumentScrap[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const getRequestDocumentsAndForms = async () => {
      const docsResponse = await getAllDocumentsForRequestByCategory(projectId, request.id, documentCategoryType.id);
      const formsResponse = await getAllRequestForms(request.id);
      const documentsWithHistory = buildDocumentHistory(docsResponse);
      setDocumentsWithHistory(documentsWithHistory);
      setRequestForms(formsResponse);
      setMissingDocuments(getMissingDocuments(documentsWithHistory, formsResponse));
      setIsLoading(false);
    };

    getRequestDocumentsAndForms();
  }, [refreshDocuments]);

  const getMissingDocuments = (documentsWithHistory: DocumentAndHistory[], requestForms: RequestForm[]) => {
    return requiredDocuments.filter(
      (requiredDocument) =>
        !documentsWithHistory.some((document) => document.document.documentTypeId == requiredDocument.documentTypeId) &&
        !requestForms.some((form) => form.type.id == requiredDocument.formType?.id),
    );
  };

  const toDocumentTypeIds = (documents: GSDocument[]) => {
    if (requiredDocuments && requiredDocuments.length > 0) {
      return [...new Set(requiredDocuments.map((requiredDocument) => requiredDocument.documentTypeId))];
    }
    return [...new Set(documents.map((document) => document.documentTypeId))];
  };

  const buildDocumentHistory = (documents: GSDocument[]): DocumentAndHistory[] => {
    const documentsWithHistory: DocumentAndHistory[] = [];
    const documentTypesIds = toDocumentTypeIds(documents);
    for (const documentTypeId of documentTypesIds) {
      const documentsOfType = documents.filter((x) => x.documentTypeId === documentTypeId).sort(sortByLatest);
      const latestVersion = documentsOfType.shift();
      if (latestVersion) {
        documentsWithHistory.push({
          document: latestVersion,
          history: documentsOfType,
        });
      }
    }
    return documentsWithHistory;
  };

  const sortByLatest = (a: GSDocument, b: GSDocument) =>
    new Date(b.modifiedTimestamp).getTime() - new Date(a.modifiedTimestamp).getTime();

  return (
    <>
      <Box sx={{ marginBottom: "18px", display: "flex", justifyContent: "center", gap: "10px" }}>
        <DocumentListActions
          request={request}
          documentCategoryType={documentCategoryType}
          disabled={documentsWithHistory.length == 0}
        />
      </Box>
      {isLoading && <CircularProgress />}
      {documentsWithHistory.map((document) => (
        <Document
          key={document.document.id}
          document={document.document}
          history={document.history}
          request={request}
          onDocumentModify={onDocumentModify}
        />
      ))}
      {requestForms.map((form) => (
        <Form
          key={form.id}
          form={form}
          requestId={request.id}
          requestStatus={request.requestStatus}
          isBatchRequest={!!request.batchId}
          gsid={request.project.gsid}
        />
      ))}
      {missingDocuments.map((missingDocument) => (
        <MissingDocument
          key={missingDocument.documentTypeId}
          missingDocument={missingDocument}
          gsid={request.project.gsid}
          requestId={request.id}
          requestStatus={request.requestStatus}
        />
      ))}
    </>
  );
};
