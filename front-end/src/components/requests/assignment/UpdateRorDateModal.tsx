import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Grid, Box, Typography } from "@mui/material";
import { DatePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import dayjs, { Dayjs } from "dayjs";
import { useState } from "react";
import { toast } from "react-toastify";

import { useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";
import { updateRoundOfReview } from "../Requests";

interface UpdateRorDateModalProps {
  isOpen: boolean;
  onClose: () => void;
  requestId: number;
  requestProjectName: string;
  requestCurrentRoundIndex: number;
  defaultRequestCurrentRoundStartDate?: string;
  defaultRequestCurrentRoundEndDate?: string;
  currentRorStatus?: number;
  updateRequest: () => void;
}

export const UpdateRorDateModal = ({
  isOpen,
  onClose,
  requestId,
  requestProjectName,
  requestCurrentRoundIndex,
  defaultRequestCurrentRoundStartDate,
  defaultRequestCurrentRoundEndDate,
  currentRorStatus,
  updateRequest,
}: UpdateRorDateModalProps) => {
  const [requestCurrentRoundStartDate, setRequestCurrentRoundStartDate] = useState<Dayjs | null>(
    defaultRequestCurrentRoundStartDate ? dayjs.utc(defaultRequestCurrentRoundStartDate) : null,
  );
  const [requestCurrentRoundEndDate, setRequestCurrentRoundEndDate] = useState<Dayjs | null>(
    defaultRequestCurrentRoundEndDate ? dayjs.utc(defaultRequestCurrentRoundEndDate) : null,
  );
  const [loading, setLoading] = useState(false);

  const { activeReferenceTypes } = useReferenceTypes();

  const getRoundOfReviewStatus = () => {
    const roundOfReviewStatuses = activeReferenceTypes.RequestRoundStatus;

    if (roundOfReviewStatuses) {
      const openStatus = roundOfReviewStatuses.find((status) => status.code === "OPEN");
      if (openStatus) {
        return openStatus.id;
      }
    }

    return undefined;
  };

  const getStatusDescription = (statusId?: number) => {
    if (!statusId || !activeReferenceTypes.RequestRoundStatus) {
      return "Unknown";
    }

    const status = activeReferenceTypes.RequestRoundStatus.find((s) => s.id === statusId);
    return status?.description || "Unknown";
  };

  const isRorClosed = () => {
    if (!currentRorStatus || !activeReferenceTypes.RequestRoundStatus) {
      return false;
    }

    const closedStatus = activeReferenceTypes.RequestRoundStatus.find((status) => status.code === "CLOSED");
    return currentRorStatus === closedStatus?.id;
  };

  const handleSave = async () => {
    if (!requestCurrentRoundStartDate?.isValid() || !requestCurrentRoundEndDate?.isValid()) {
      toast.error("Invalid round of review date(s)");
      return;
    }

    const roundOfReviewStatus = getRoundOfReviewStatus();
    if (roundOfReviewStatus === undefined) {
      toast.error("Unable to determine OPEN status for round of review");
      return;
    }

    setLoading(true);

    try {
      await updateRoundOfReview(
        requestId,
        {
          startDate: requestCurrentRoundStartDate.toISOString(),
          endDate: requestCurrentRoundEndDate.toISOString(),
          statusId: roundOfReviewStatus,
        },
        requestCurrentRoundIndex,
      );

      toast.success("Round of review dates updated");
      updateRequest();
      onClose();
    } catch (error) {
      console.error("Error updating round of review:", error);
      toast.error("Error updating round of review dates");
    } finally {
      setLoading(false);
    }
  };

  const getStartDateHelperText = (disabled: boolean) => {
    if (disabled) {
      return "Cannot edit dates - RoR is closed";
    }
    return requestCurrentRoundStartDate?.isValid() ? "" : "Invalid date";
  };

  const getEndDateHelperText = (disabled: boolean) => {
    if (disabled) {
      return "Cannot edit dates - RoR is closed";
    }
    return requestCurrentRoundEndDate?.isValid() ? "" : "Invalid date";
  };

  const renderDatePickers = () => {
    const disabled = isRorClosed();

    return (
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <Grid container spacing={1} sx={{ marginTop: "2em", marginBottom: "2em" }}>
          <Grid item xs={6}>
            <DatePicker
              label="Round of Review Start Date"
              value={requestCurrentRoundStartDate}
              onChange={setRequestCurrentRoundStartDate}
              disabled={disabled}
              slotProps={{
                textField: {
                  fullWidth: true,
                  required: true,
                  helperText: getStartDateHelperText(disabled),
                },
              }}
              format="DD/MM/YYYY"
              timezone="UTC"
            />
          </Grid>
          <Grid item xs={6}>
            <DatePicker
              label="Round of Review End Date"
              value={requestCurrentRoundEndDate}
              onChange={setRequestCurrentRoundEndDate}
              disabled={disabled}
              slotProps={{
                textField: {
                  fullWidth: true,
                  required: true,
                  helperText: getEndDateHelperText(disabled),
                },
              }}
              format="DD/MM/YYYY"
              timezone="UTC"
            />
          </Grid>
        </Grid>
      </LocalizationProvider>
    );
  };

  const renderReadOnlyDates = () => {
    return (
      <Box sx={{ marginTop: "2em", marginBottom: "2em" }}>
        <div>Start: {requestCurrentRoundStartDate?.format("MM/DD/YYYY")}</div>
        <div>End: {requestCurrentRoundEndDate?.format("MM/DD/YYYY")}</div>
      </Box>
    );
  };

  return (
    <Dialog maxWidth="md" open={isOpen} onClose={onClose}>
      <DialogTitle>Update Round of Review Dates - {requestProjectName}</DialogTitle>

      <DialogContent sx={{ minWidth: "500px" }}>
        <Box sx={{ marginTop: "8px" }}>
          <Typography variant="body1">Round of Review: {requestCurrentRoundIndex}</Typography>
          <Typography variant="body2" color="textSecondary">
            Status: {getStatusDescription(currentRorStatus)}
          </Typography>
        </Box>

        {isRorClosed() ? (
          <>
            <Typography variant="body2" color="error" sx={{ marginTop: "1em" }}>
              This Round of Review is closed. Dates cannot be modified.
            </Typography>
            {renderReadOnlyDates()}
          </>
        ) : (
          renderDatePickers()
        )}
      </DialogContent>

      <DialogActions>
        <Button onClick={onClose} disabled={loading}>
          {isRorClosed() ? "Close" : "Cancel"}
        </Button>
        {!isRorClosed() && (
          <Button
            onClick={handleSave}
            variant="contained"
            disabled={loading || !requestCurrentRoundStartDate?.isValid() || !requestCurrentRoundEndDate?.isValid()}
          >
            {loading ? "Saving..." : "Save"}
          </Button>
        )}
      </DialogActions>
    </Dialog>
  );
};
