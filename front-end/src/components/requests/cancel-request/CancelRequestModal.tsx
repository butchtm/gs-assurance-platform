import {
  Box,
  Button,
  FormControl,
  InputLabel,
  MenuItem,
  Modal,
  Select,
  SelectChangeEvent,
  TextField,
  Typography,
} from "@mui/material";
import { useState } from "react";

import { Options, useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";
import { CreateUpdateRequestPayload, GSRequest } from "../../../common/request/GSRequest";
import { updateRequest } from "../Requests";

import { CancelRequestConfirmationDialog } from "./CancelRequestConfirmationDialog";

export const CancelRequestModal = (props: {
  request: GSRequest;
  onClose: () => void;
  isOpen: boolean;
  updateRequestRefresh: () => void;
}) => {
  const [openCancelConfirmDialog, setOpenCancelConfirmDialog] = useState(false);
  const [status, setStatus] = useState<string>("");
  const [reasonId, setReasonId] = useState<string>("");
  const [otherReason, setOtherReason] = useState<string>("");
  const { activeReferenceTypes } = useReferenceTypes();

  const otherOptionId = activeReferenceTypes.RequestCancellationReason.find((x) => x.code == "OTHER")?.id.toString();

  const reasonOptions: Options[] = activeReferenceTypes.RequestCancellationReason.map((x) => ({
    label: x.description,
    value: x.id.toString(),
  }));

  const getStatusIdBySelection = (selection: string): number | null => {
    const statusCode = selection === "Cancelled" ? "CANCELLED" : "OMITTED";
    const status = activeReferenceTypes.RequestStatus.find((requestStatus) => requestStatus.code === statusCode);
    return status?.id || null;
  };

  const performCancelRequest = async () => {
    const requestStatusId = getStatusIdBySelection(status);
    if (!requestStatusId) {
      return;
    }

    let requestToSubmit: Partial<CreateUpdateRequestPayload> = {
      id: props.request.id,
      requestStatusId: requestStatusId,
      manuallyAdded: true,
      cancellationReasonId: reasonId,
    };
    if (otherReason) {
      requestToSubmit = {
        ...requestToSubmit,
        otherCancellationReason: otherReason,
      };
    }

    await updateRequest(requestToSubmit, props.request.project.id);

    props.updateRequestRefresh();
    props.onClose();
  };

  return (
    <>
      <Modal open={props.isOpen} onClose={props.onClose}>
        <div
          className="cancel-request-container"
          style={{ backgroundColor: "white", width: 600, padding: 20, borderRadius: 8 }}
        >
          <Typography variant="h6" component="h2" sx={{ mt: 2, mb: 1 }}>
            Close Request
          </Typography>
          <Typography variant="body2" sx={{ mb: 3, color: "text.secondary" }}>
            Select the closing status and reason for this request. This action will permanently close the request.
          </Typography>
          <Box>
            <FormControl fullWidth sx={{ mb: 2 }}>
              <InputLabel id="status-label">Request Status</InputLabel>
              <Select
                labelId="status-label"
                value={status}
                label="Request Status"
                onChange={(event: SelectChangeEvent) => setStatus(event.target.value)}
                data-testid="cancel-request-status-select"
              >
                <MenuItem value="Cancelled" data-testid="cancelled-option">
                  Cancelled
                </MenuItem>
                <MenuItem value="Omitted" data-testid="omitted-option">
                  Omitted
                </MenuItem>
              </Select>
            </FormControl>
            <FormControl fullWidth sx={{ mb: 2 }}>
              <InputLabel id="reason-label">Reason For Request Closure</InputLabel>
              <Select
                labelId="reason-label"
                value={reasonId}
                label="Reason for Request Closure"
                onChange={(event: SelectChangeEvent) => setReasonId(event.target.value)}
                data-testid="cancel-request-reason-select"
              >
                {reasonOptions.map((option) => (
                  <MenuItem key={option.value} value={option.value} data-testid={`reason-option-${option.value}`}>
                    {option.label}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
            {reasonId == otherOptionId && (
              <FormControl fullWidth sx={{ mb: 2 }}>
                <TextField
                  label="Other Reason"
                  type="text"
                  value={otherReason}
                  onChange={(event) => setOtherReason(event.target.value)}
                  fullWidth
                  data-testid="cancel-request-other-reason"
                  multiline
                  rows={4}
                />
              </FormControl>
            )}
            <Box sx={{ display: "flex", justifyContent: "flex-end", gap: 1 }}>
              <Button variant="outlined" onClick={props.onClose}>
                Cancel
              </Button>
              <Button
                variant="contained"
                color="error"
                type="submit"
                onClick={() => setOpenCancelConfirmDialog(true)}
                disabled={!status || !reasonId || (reasonId == otherOptionId && !otherReason)}
                data-testid="close-request-button"
              >
                {status ? `Mark as ${status}` : "Close Request"}
              </Button>
            </Box>
          </Box>
        </div>
      </Modal>
      <CancelRequestConfirmationDialog
        isOpen={openCancelConfirmDialog}
        onClose={() => setOpenCancelConfirmDialog(false)}
        onConfirm={() => {
          setOpenCancelConfirmDialog(false);
          performCancelRequest();
        }}
        selectedStatus={status ?? ""}
      />
    </>
  );
};
