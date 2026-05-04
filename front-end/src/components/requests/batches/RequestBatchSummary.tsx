import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { Typography } from "@mui/material";
import { TreeItem, TreeView } from "@mui/x-tree-view";
import React, { useState } from "react";

import { BatchedProjectRequest, BatchedProjectRequests } from "../Requests";
import { RequestSectionHeader } from "../reusable/RequestSectionHeader";

import { RequestBatchSummaryLabel } from "./RequestBatchSummaryLabel";

interface RequestBatchSummaryProps {
  projectId: number | null;
  batchRequests?: BatchedProjectRequests;
}

export const RequestBatchSummary: React.FC<RequestBatchSummaryProps> = ({ projectId, batchRequests }) => {
  const [expanded, setExpanded] = useState<string[]>([]);

  const onNodeToggle = (event: React.SyntheticEvent, nodeIds: string[]) => {
    setExpanded(nodeIds);
  };

  const renderTree = (batchedProjectRequest: BatchedProjectRequest) => {
    return (
      <TreeItem
        key={batchedProjectRequest.id}
        nodeId={batchedProjectRequest.id.toString()}
        label={<RequestBatchSummaryLabel batchedProjectRequest={batchedProjectRequest} />}
      ></TreeItem>
    );
  };

  return (
    <>
      <RequestSectionHeader name="Request Batches" />
      {batchRequests ? (
        <TreeView
          defaultCollapseIcon={<ExpandMoreIcon />}
          defaultExpandIcon={<ChevronRightIcon />}
          expanded={expanded}
          onNodeToggle={onNodeToggle}
          sx={{ margin: "1rem" }}
          selected={(projectId ?? "").toString()}
        >
          <TreeItem
            key={batchRequests.parent.id}
            nodeId={batchRequests.parent.id.toString()}
            label={<RequestBatchSummaryLabel batchedProjectRequest={batchRequests.parent} />}
          >
            {batchRequests.children.map(renderTree)}
          </TreeItem>
        </TreeView>
      ) : (
        <Typography>Loading...</Typography>
      )}
    </>
  );
};
