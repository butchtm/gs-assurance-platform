import { Button } from "@mui/material";
import { toast } from "react-toastify";

import { PermissionGroup } from "../../PermissionGroup";
import ProtectedComponent from "../../ProtectedComponent";

import { submitPiiAction } from "./PiiAction";

interface PiiAction {
  value: number;
  label: string;
  code?: string;
}

interface PiiActionSubmitProps {
  projectId?: number;
  requestId?: number;
  documentId?: string;
  commentId?: number;
  selectedAction: PiiAction | null;
  onSubmit?: () => void;
}

export const PiiActionSubmit: React.FC<PiiActionSubmitProps> = ({
  projectId,
  requestId,
  documentId,
  commentId,
  selectedAction,
  onSubmit,
}: PiiActionSubmitProps) => {
  const handleActionSubmit = async () => {
    const actionResult = await submitPiiAction(selectedAction, projectId, requestId, commentId, documentId);
    if (actionResult.success) {
      toast.success("PII action submitted successfully.");
      if (typeof onSubmit === "function") {
        onSubmit();
      }
    } else {
      toast.error("Failed to submit PII action. Please try again.");
    }
  };

  return (
    <ProtectedComponent roles={[PermissionGroup.ARM_TEAM, PermissionGroup.GS_ADMIN]}>
      <Button type="submit" variant="contained" onClick={handleActionSubmit}>
        Submit
      </Button>
    </ProtectedComponent>
  );
};
