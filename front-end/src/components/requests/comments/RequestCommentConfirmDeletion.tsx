import {
  Autocomplete,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  FormControl,
  TextField,
} from "@mui/material";
import { useState } from "react";
import { toast } from "react-toastify";

import { useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";

import { deleteRequestComment } from "./RequestComments";

export const RequestCommentConfirmDeletion = ({
  open,
  onClose,
  commentId,
}: {
  open: boolean;
  onClose: () => void;
  commentId: number;
}) => {
  const { activeReferenceTypes } = useReferenceTypes();
  const [selectedReason, setSelectedReason] = useState<string>("");

  const handleAction = async () => {
    if (!selectedReason) {
      toast.error("Please select a valid reason.");
      return;
    }
    try {
      await deleteRequestComment(commentId, Number(selectedReason));
      toast.success("Comment deleted successfully.");
    } catch (_error) {
      const errorMessage = "Error deleting comment.";
      toast.error(errorMessage);
    }
    onClose();
  };

  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>Confirm Deletion</DialogTitle>
      <DialogContent>
        <DialogContentText>Are you sure you want to delete this comment? This cannot be undone.</DialogContentText>
      </DialogContent>
      <FormControl sx={{ width: "80%", margin: "auto" }}>
        <Autocomplete
          data-testid="select-delete-reason"
          options={activeReferenceTypes.DeletionReason}
          getOptionLabel={(option) => option.description}
          value={activeReferenceTypes.DeletionReason.find((reason) => reason.id.toString() == selectedReason) || null}
          onChange={(_, newReason) => {
            if (newReason) {
              setSelectedReason(newReason.id.toString());
            }
          }}
          renderInput={(params) => (
            <TextField
              {...params}
              label="Delete Reason"
              variant="outlined"
              required={true}
              inputProps={{
                ...params.inputProps,
                "data-testid": "delete-reason-input",
              }}
            />
          )}
        />
      </FormControl>
      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>
        <Button onClick={handleAction} color="error" autoFocus>
          Delete
        </Button>
      </DialogActions>
    </Dialog>
  );
};
