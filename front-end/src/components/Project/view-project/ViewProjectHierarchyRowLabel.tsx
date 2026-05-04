import { Box, Button, Typography } from "@mui/material";
import React from "react";
import { useNavigate } from "react-router-dom";

import { getReferenceValueDescriptionById, useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";
import { ProjectTree } from "../Project";

interface ViewProjectHierarchyRowLabelProps {
  projectTree: ProjectTree;
  isBundlePresent: boolean;
  returnToProjectInfoCallback: () => void;
}

const ViewProjectHierarchyRowLabel: React.FC<ViewProjectHierarchyRowLabelProps> = ({
  projectTree,
  isBundlePresent,
  returnToProjectInfoCallback,
}): JSX.Element => {
  const { allReferenceTypes } = useReferenceTypes();
  const navigate = useNavigate();

  const handleViewClick = (clickedProjectId: string) => {
    navigate(`/projects/${clickedProjectId}`);
    returnToProjectInfoCallback();
    window.scrollTo(0, 0);
  };
  return (
    <Box display="flex" justifyContent="space-between" alignItems="center">
      <Typography variant="body1" sx={{ flexBasis: isBundlePresent ? "15%" : "20%" }}>
        {projectTree.gsid}
      </Typography>
      <Typography variant="body1" sx={{ flexBasis: isBundlePresent ? "35%" : "20%" }}>
        {projectTree.name}
      </Typography>
      <Typography variant="body1" sx={{ flexBasis: isBundlePresent ? "10%" : "35%" }}>
        {getReferenceValueDescriptionById(projectTree.classificationId, allReferenceTypes.ProjectClassification)}
      </Typography>
      {isBundlePresent && (
        <Typography variant="body1" sx={{ flexBasis: "25%" }}>
          {projectTree.bundle?.name}
        </Typography>
      )}
      <Button sx={{ flexBasis: "10%" }} onClick={() => handleViewClick(projectTree.gsid)}>
        View
      </Button>
    </Box>
  );
};

export default ViewProjectHierarchyRowLabel;
