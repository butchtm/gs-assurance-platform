import { Tab } from "@mui/material";
import React, { useState } from "react";
import { TabContext, TabList, TabPanel } from "@mui/lab";

import "./ProjectDocuments.css";
import { PublicStakeholderComment, PublicStakeholderDocument } from "../PublicConsultation/PublicConsultation";
import { TabLabel } from "../../Reusable/Tab/TabLabel";
import AddPublicConsultationComment from "../PublicConsultation/request-comments/AddPublicConsultationComment";

import { PublicStakeholderCommentsList } from "./PublicStakeholderCommentsList";
import { PublicDocumentsList } from "./PublicDocumentList";

enum RequestInformationTabs {
  Documents,
  Comments,
}

interface PublicRequestTabsProps {
  documents?: PublicStakeholderDocument[];
  comments?: PublicStakeholderComment[];
  isPublicConsultationPage: boolean;
  requestId: number;
}

export const PublicRequestTabs: React.FC<PublicRequestTabsProps> = ({
  documents,
  comments,
  isPublicConsultationPage,
  requestId,
}) => {
  const [selectedTabType, setSelectedTabType] = useState<RequestInformationTabs>(RequestInformationTabs.Documents);

  return (
    <TabContext value={String(selectedTabType)}>
      <TabList onChange={(event, newTabType) => setSelectedTabType(newTabType)}>
        <Tab
          label={<TabLabel text="Documents" count={documents?.length ?? 0} />}
          value={String(RequestInformationTabs.Documents)}
        />
        <Tab
          label={<TabLabel text="Comments" count={comments?.length ?? 0} />}
          value={String(RequestInformationTabs.Comments)}
        />
      </TabList>
      <TabPanel value={String(RequestInformationTabs.Documents)} sx={{ padding: "5px" }}>
        <PublicDocumentsList documents={documents} />
      </TabPanel>
      <TabPanel value={String(RequestInformationTabs.Comments)} sx={{ padding: "5px" }}>
        <PublicStakeholderCommentsList comments={comments} />
        {isPublicConsultationPage && <AddPublicConsultationComment requestId={requestId} />}
      </TabPanel>
    </TabContext>
  );
};
