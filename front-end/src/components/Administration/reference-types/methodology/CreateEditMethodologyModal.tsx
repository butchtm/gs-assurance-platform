import { useState } from "react";
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
import { useFormik } from "formik";

import { ConfirmDeleteDialog } from "../ConfirmDeleteDialog";
import {
  createMethodology,
  deleteMethodology,
  EditableMethodologyValues,
  MethodologyReferenceType,
  updateMethodology,
} from "../ReferenceTypes";

interface CreateEditMethodologyModalProps {
  open: boolean;
  onClose: (submitted: boolean, created?: boolean) => void;
  methodology?: MethodologyReferenceType;
}

export const CreateEditMethodologyModal = ({ open, onClose, methodology }: CreateEditMethodologyModalProps) => {
  const isEditMode = Boolean(methodology);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState<boolean>(false);

  const formik = useFormik({
    initialValues: {
      description: methodology?.description ?? "",
      active: methodology?.active ?? false,
      paa: methodology?.paa ?? !isEditMode,
    },
    onSubmit: async (values: EditableMethodologyValues) => {
      if (isEditMode) {
        await updateMethodology({ ...values, id: methodology?.id });
      } else {
        await createMethodology(values);
      }
      onClose(true, !isEditMode);
    },
  });

  const handleDelete = async () => {
    if (methodology?.id) {
      await deleteMethodology(methodology.id);
      onClose(true);
    }
  };

  return (
    <Dialog open={open} onClose={() => onClose(false)}>
      <form onSubmit={formik.handleSubmit}>
        <DialogTitle variant="h6" sx={{ display: "flex", justifyContent: "space-between" }}>
          {isEditMode ? "Edit" : "Create"} Methodology
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
              name="description"
              multiline
              label="Description"
              rows={4}
              value={formik.values.description}
              onChange={formik.handleChange}
              required={true}
              data-testid="methodology-description-input"
            />
            <FormControlLabel
              control={<Checkbox checked={formik.values.active} onChange={formik.handleChange} name="active" />}
              label="Active"
              data-testid="methodology-active-checkbox"
            />
            <FormControlLabel
              control={<Checkbox checked={formik.values.paa} onChange={formik.handleChange} name="paa" />}
              label="PAA (Paris Agreement Alignment)"
              data-testid="methodology-paa-checkbox"
            />
          </FormControl>
        </DialogContent>
        <DialogActions sx={{ justifyContent: "space-between" }}>
          <Button
            variant="outlined"
            onClick={() => setDeleteDialogOpen(true)}
            disabled={!methodology || methodology.inUse}
            data-testid="methodology-delete-button"
          >
            Delete
          </Button>
          {methodology?.inUse && <Typography variant="body2">Methodologies in use cannot be deleted</Typography>}
          <Button type="submit" sx={{ float: "right" }} variant="contained" disabled={!formik.values.description}>
            {isEditMode ? "Edit" : "Create"}
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
      </form>
    </Dialog>
  );
};
