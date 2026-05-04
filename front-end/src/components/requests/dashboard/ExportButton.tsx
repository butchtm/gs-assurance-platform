import { Button } from "@mui/material";
import EmailIcon from "@mui/icons-material/Email";

import ProtectedComponent from "../../../ProtectedComponent";
import { PermissionGroup } from "../../../PermissionGroup";

export const ExportButton = ({ onClick, userEmail }: { onClick: () => void; userEmail?: string }) => {
  if (!userEmail) {
    return <></>;
  }

  return (
    <ProtectedComponent roles={[PermissionGroup.GS_ADMIN, PermissionGroup.ARM_TEAM]}>
      <Button data-testid="export-button" variant="contained" onClick={onClick} endIcon={<EmailIcon />}>
        Export
      </Button>
    </ProtectedComponent>
  );
};
