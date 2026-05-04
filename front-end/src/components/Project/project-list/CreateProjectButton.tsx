import { Button } from "@mui/material";
import { useContext } from "react";
import { useNavigate } from "react-router-dom";

import { AuthContext } from "../../Auth/Login/AuthProvider";

export const CreateProjectButton = () => {
  const authContext = useContext(AuthContext);
  const navigate = useNavigate();

  if (!authContext.isAdminUser() && !authContext.isArmUser() && !authContext.isProjectDeveloperUser()) {
    return <></>;
  }

  return (
    <Button key="Create" onClick={() => navigate("/create-new-project")}>
      Create
    </Button>
  );
};
