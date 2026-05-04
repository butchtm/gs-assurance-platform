import TabContext from "@mui/lab/TabContext";
import TabList from "@mui/lab/TabList";
import TabPanel from "@mui/lab/TabPanel";
import { Tab } from "@mui/material";
import { useState } from "react";

import { TabLabel } from "../../../components/Reusable/Tab/TabLabel";
import { DocumentCategoryType, GSDocument } from "../../../common/document/GSDocument";
import { GSRequest } from "../../../common/request/GSRequest";
import { RequiredDocumentsResponse } from "../Requests";

import "../RequestsView.scss";
import { DocumentList } from "./DocumentList";
import { DocumentListPaginatedWithoutHistoryOrRequirements } from "./DocumentListPaginatedWithoutHistory";

const DocTabLabel = ({
  documentType,
  documentCount,
}: {
  documentType: DocumentCategoryType;
  documentCount: number;
}) => {
  return <TabLabel text={documentType.description} count={documentCount} />;
};

export const DocumentLists = ({
  request,
  projectId,
  updateDocuments,
  requestTypeRequiredDocuments,
  requestDocuments,
  refreshDocuments,
}: {
  request: GSRequest;
  projectId: number;
  updateDocuments: () => void;
  requestTypeRequiredDocuments: RequiredDocumentsResponse;
  requestDocuments: GSDocument[];
  refreshDocuments: boolean;
}) => {
  const [selectedTabType, setSelectedTabType] = useState<DocumentCategoryType>(DocumentCategoryType.REQUIRED);

  const handleTabChange = (event: React.SyntheticEvent, newTabType: DocumentCategoryType) => {
    setSelectedTabType(newTabType);
  };

  const getDocumentCount = (docType: DocumentCategoryType) => {
    return requestDocuments.filter((doc) => doc.documentCategoryType == docType.id).length;
  };

  return (
    <TabContext value={selectedTabType.toString()}>
      <TabList onChange={handleTabChange}>
        <Tab
          label={
            <DocTabLabel
              documentType={DocumentCategoryType.REQUIRED}
              documentCount={getDocumentCount(DocumentCategoryType.REQUIRED)}
            />
          }
          value={DocumentCategoryType.REQUIRED.toString()}
          data-testid="required-documents"
        />
        <Tab
          label={
            <DocTabLabel
              documentType={DocumentCategoryType.SUPPORT}
              documentCount={getDocumentCount(DocumentCategoryType.SUPPORT)}
            />
          }
          value={DocumentCategoryType.SUPPORT.toString()}
        />
        <Tab
          label={
            <DocTabLabel
              documentType={DocumentCategoryType.OTHER}
              documentCount={getDocumentCount(DocumentCategoryType.OTHER)}
            />
          }
          value={DocumentCategoryType.OTHER.toString()}
        />
      </TabList>
      <TabPanel value={DocumentCategoryType.REQUIRED.toString()}>
        <DocumentList
          projectId={projectId}
          request={request}
          onDocumentModify={updateDocuments}
          documentCategoryType={DocumentCategoryType.REQUIRED}
          requiredDocuments={requestTypeRequiredDocuments.REQUIRED ?? []}
          refreshDocuments={refreshDocuments}
        />
      </TabPanel>
      <TabPanel value={DocumentCategoryType.SUPPORT.toString()}>
        <DocumentList
          projectId={projectId}
          request={request}
          onDocumentModify={updateDocuments}
          documentCategoryType={DocumentCategoryType.SUPPORT}
          requiredDocuments={requestTypeRequiredDocuments.SUPPORTING ?? []}
          refreshDocuments={refreshDocuments}
        />
      </TabPanel>
      <TabPanel value={DocumentCategoryType.OTHER.toString()}>
        <DocumentListPaginatedWithoutHistoryOrRequirements
          projectId={projectId}
          request={request}
          onDocumentModify={updateDocuments}
          documentCategoryType={DocumentCategoryType.OTHER}
          refreshDocuments={refreshDocuments}
        />
      </TabPanel>
    </TabContext>
  );
};
