import { Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from "@mui/material";
import React, { useEffect, useState } from "react";

import { apiClient } from "../../components/APIClient/Client";
import { getGitTag, getEnvironment } from "../env/env";

interface GitDescribeResponse {
  git: {
    commit: {
      id: {
        describe: string;
      };
    };
  };
}

export const ApplicationVersions: React.FC<{ isOpen: boolean; onClose: () => void }> = ({ isOpen, onClose }) => {
  const [uiVersion, setUiVersion] = useState<string>();
  const [apiVersion, setApiVersion] = useState<string>();
  const [environment, setEnvironment] = useState<string>();

  const getApiVersion = async () => {
    try {
      const response: GitDescribeResponse = await apiClient.makeRequest("/actuator/info", "GET");
      setApiVersion(response.git.commit.id.describe);
    } catch {
      console.error("Error fetching version");
    }
  };

  useEffect(() => {
    if (isOpen) {
      getApiVersion();
      setUiVersion(getGitTag());
      setEnvironment(getEnvironment());
    }
  });

  return (
    <Dialog open={isOpen}>
      <DialogTitle>Application Versions</DialogTitle>
      <DialogContent>
        <DialogContentText>
          <b>UI Version:</b> {uiVersion ?? "Not available"}
        </DialogContentText>
        <DialogContentText>
          <b>API Version:</b> {apiVersion ?? "Not available"}
        </DialogContentText>
        <DialogContentText>
          <b>Environment:</b> {environment ?? "Not available"}
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button variant="outlined" onClick={onClose}>
          Close
        </Button>
      </DialogActions>
    </Dialog>
  );
};
