import CloseIcon from "@mui/icons-material/Close";
import {
  Button,
  Checkbox,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  FormControlLabel,
  IconButton,
  TextField,
  Typography,
} from "@mui/material";
import React, { useState } from "react";

import { ConfirmDeleteDialog } from "../ConfirmDeleteDialog";
import {
  createMethodology,
  deleteMethodology,
  EditableMethodologyValues,
  MethodologyReferenceType,
  updateMethodology,
} from "../ReferenceTypes";

export const CreateEditMethodologyModal: React.FC<{
  open: boolean;
  onClose: (submitted: boolean, created?: boolean) => void;
  methodology?: MethodologyReferenceType;
}> = ({ open, onClose, methodology }) => {
  const [description, setDescription] = useState<string>(methodology?.description ?? "");
  const [active, setActive] = useState<boolean>(methodology?.active ?? false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState<boolean>(false);

  const handleCreate = async () => {
    const values: EditableMethodologyValues = {
      description: description,
      active: active,
    };
    await createMethodology(values);
    onClose(true, true);
  };

  const handleEdit = async () => {
    const values: EditableMethodologyValues = {
      description: description,
      active: active,
      id: methodology?.id,
    };
    await updateMethodology(values);
    onClose(true);
  };

  const handleDelete = async () => {
    if (methodology?.id) {
      await deleteMethodology(methodology.id);
      onClose(true);
    }
  };

  return (
    <Dialog open={open} onClose={() => onClose(false)}>
      <DialogTitle variant="h6" sx={{ display: "flex", justifyContent: "space-between" }}>
        {methodology ? "Edit" : "Create"} Methodology
        <IconButton
          onClick={() => onClose(false)}
          style={{
            float: "right",
          }}
        >
          <CloseIcon />
        </IconButton>
      </DialogTitle>
      <DialogContent sx={{ width: 600, mt: 1, overflow: "visible" }}>
        <FormControl fullWidth>
          <TextField
            multiline
            label="Description"
            rows={4}
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            required={true}
            data-testid="methodology-description-input"
          />
          <FormControlLabel
            control={<Checkbox checked={active} onChange={(e) => setActive(e.target.checked)} />}
            label="Active"
            data-testid="methodology-active-checkbox"
          />
        </FormControl>
      </DialogContent>
      <DialogActions sx={{ justifyContent: "space-between" }}>
        <Button
          variant="outlined"
          onClick={() => setDeleteDialogOpen(true)}
          disabled={!methodology || methodology.inUse}
        >
          Delete
        </Button>
        {methodology?.inUse && <Typography variant="body2">Methodologies in use cannot be deleted</Typography>}
        <Button
          sx={{ float: "right" }}
          variant="contained"
          onClick={methodology ? handleEdit : handleCreate}
          disabled={!description}
        >
          {methodology ? "Edit" : "Create"}
        </Button>
      </DialogActions>
      {deleteDialogOpen && (
        <ConfirmDeleteDialog
          isOpen={deleteDialogOpen}
          onClose={() => setDeleteDialogOpen(false)}
          onConfirm={handleDelete}
          referenceType="methodology"
        />
      )}
    </Dialog>
  );
};
