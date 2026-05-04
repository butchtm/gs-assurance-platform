import CloseIcon from "@mui/icons-material/Close";
import {
  Box,
  Button,
  Divider,
  FormControl,
  FormControlLabel,
  Grid,
  IconButton,
  InputLabel,
  MenuItem,
  Modal,
  Select,
  SelectChangeEvent,
  Switch,
  TextField,
  Typography,
} from "@mui/material";
import dayjs from "dayjs";
import { ChangeEvent, useEffect, useState } from "react";
import { toast } from "react-toastify";

import { ReferenceType, useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";
import {
  canPiiActionBeMadePublic,
  getActionOptionByCode,
  getSupportedActionOptions,
  PiiActionCode,
} from "../../../common/piiAnalysis/PiiAction";
import { PiiActionDropdown, PiiActionOption } from "../../../common/piiAnalysis/PiiActionDropdown";
import { publicSwitchDisabledPiiHelperText } from "../../../common/piiAnalysis/PiiAnalysis";
import CustomTooltipText, { CustomTooltip } from "../../Reusable/FormFields/CustomTooltipText";

import { RequestCommentConfirmDeletion } from "./RequestCommentConfirmDeletion";
import { RequestComment, RequestCommentUpdate, updateRequestComment } from "./RequestComments";

const COMMENT_PII_ACTIONS = ["UNDER_INVESTIGATION", "ACCEPTED", "PII_NOT_SUITABLE", "NO_PII_FOUND"];

const RequestCommentModifyModal = ({
  comment,
  isOpen,
  onClose,
}: {
  comment: RequestComment;
  isOpen: boolean;
  onClose: () => void;
}) => {
  const { activeReferenceTypes } = useReferenceTypes();
  const [isPublic, setIsPublic] = useState<boolean>(comment.publicAccess);
  const suppressedReasons: ReferenceType[] = activeReferenceTypes.SuppressionReason;
  const [actionType, setActionType] = useState<string>();
  const [anonymous, setAnonymous] = useState<boolean>(comment.anonymous ?? false);
  const [addressed, setAddressed] = useState<boolean>(comment.addressed ?? false);
  const [openDialog, setOpenDialog] = useState(false);
  const [supressed, setSupressed] = useState<boolean>(comment.suppressed ?? false);
  const [suppressedReasonId, setSuppressedReasonId] = useState<string>(
    comment.suppressionReasonId ? comment.suppressionReasonId.toString() : "",
  );

  const supportedActionOptions = getSupportedActionOptions(COMMENT_PII_ACTIONS, activeReferenceTypes);

  const preselectedPiiAction = getActionOptionByCode(supportedActionOptions, comment?.piiMetadata?.action?.code);

  const [selectedPiiAction, setSelectedPiiAction] = useState<PiiActionOption | null>(preselectedPiiAction);
  const [publicCommentToggleDisabled, setPublicCommentToggleDisabled] = useState<boolean>(true);

  const handleSubmit = async () => {
    if (comment) {
      const payload: RequestCommentUpdate = {
        id: comment.id,
        publicAccess: isPublic,
        anonymous: anonymous,
        addressed: addressed,
        suppressed: supressed,
        piiAction: selectedPiiAction?.value,
      };

      if (supressed) {
        payload["suppressionReasonId"] = Number(suppressedReasonId);
      }
      try {
        await updateRequestComment(payload);
      } catch {
        toast.error("Error making comment");
      }

      toast.success("Commented Successfully");
    } else {
      toast.error("No Platform Account Linked");
    }
    onClose();
  };

  const handleClickDelete = () => {
    setActionType("delete");
    setOpenDialog(true);
  };

  const handleSupressed = (event: SelectChangeEvent<string>) => {
    setSuppressedReasonId(event.target.value);
  };

  const handleClose = () => {
    setOpenDialog(false);
    onClose();
  };

  const handleSupressedToggle = (event: ChangeEvent<HTMLInputElement>) => {
    setSupressed(event.target.checked);
  };

  const handleAnonymousToggle = (event: ChangeEvent<HTMLInputElement>) => {
    setAnonymous(event.target.checked);
  };

  const handlePublicToggle = (event: ChangeEvent<HTMLInputElement>) => {
    setIsPublic(event.target.checked);
  };

  const handleAddressedToggle = (event: ChangeEvent<HTMLInputElement>) => {
    setAddressed(event.target.checked);
  };

  useEffect(() => {
    const currentActionDisabled = !canPiiActionBeMadePublic(comment?.piiMetadata?.action?.code);
    const selectedActionDisabled = selectedPiiAction
      ? !canPiiActionBeMadePublic(selectedPiiAction.code as PiiActionCode)
      : false;

    const toggleDisabled = selectedPiiAction ? selectedActionDisabled : currentActionDisabled;

    setPublicCommentToggleDisabled(toggleDisabled);
  }, [comment?.piiMetadata?.action?.code, selectedPiiAction?.code]);

  return (
    <>
      <Modal open={isOpen} onClose={onClose}>
        <Box
          sx={{
            position: "absolute",
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
            bgcolor: "background.paper",
            boxShadow: 24,
            p: 4,
            borderRadius: 2,
            width: "60%",
          }}
        >
          <IconButton onClick={onClose} style={{ float: "right", marginBottom: "2rem" }}>
            <CloseIcon data-testid="modal-close-icon" />
          </IconButton>
          <Typography variant="h6" component="h2" sx={{ mb: 2 }}>
            Moderate Comment
          </Typography>
          <TextField
            fullWidth
            label="Raised By"
            variant="outlined"
            value={comment.uploadedByName}
            InputProps={{
              readOnly: true,
            }}
            inputProps={{ "data-testid": "raised-by-input" }}
            sx={{ mb: 2 }}
          />
          {comment.accountType && (
            <TextField
              fullWidth
              label="Account Type"
              variant="outlined"
              value={comment.accountType?.description}
              InputProps={{
                readOnly: true,
              }}
              inputProps={{ "data-testid": "account-type-input" }}
              sx={{ mb: 2 }}
            />
          )}
          <TextField
            fullWidth
            label="Raised on"
            variant="outlined"
            value={dayjs.utc(comment.uploadedDate).format("DD/MM/YYYY")}
            InputProps={{
              readOnly: true,
            }}
            inputProps={{ "data-testid": "raised-on-input" }}
            sx={{ mb: 2 }}
          />
          <TextField
            fullWidth
            label="Comment"
            variant="outlined"
            multiline
            rows={4}
            InputProps={{
              readOnly: true,
            }}
            inputProps={{ "data-testid": "comment-input" }}
            value={comment.submittedComment}
            sx={{ mb: 2 }}
          />
          <Grid container>
            <Grid item xs={6}>
              <Box>
                <Box sx={{ fontWeight: "bold" }}>Public Comment:</Box>
                <FormControlLabel
                  control={
                    <>
                      <Switch checked={isPublic} disabled={publicCommentToggleDisabled} onChange={handlePublicToggle} />
                      {publicCommentToggleDisabled && (
                        <CustomTooltipText text={publicSwitchDisabledPiiHelperText} type={CustomTooltip.HELP} />
                      )}
                    </>
                  }
                  label={isPublic ? "Yes" : "No"}
                  data-testid="public-comment-label"
                />
              </Box>
              <Box>
                <Box sx={{ fontWeight: "bold" }}>Comment Anonymously:</Box>
                <FormControlLabel
                  control={<Switch checked={anonymous} onChange={handleAnonymousToggle} />}
                  label={anonymous ? "Yes" : "No"}
                  data-testid="anonymous-comment-label"
                />
              </Box>
              <Box>
                <Box sx={{ fontWeight: "bold" }}>Addressed:</Box>
                <FormControlLabel
                  control={<Switch checked={addressed} onChange={handleAddressedToggle} />}
                  label={addressed ? "Yes" : "No"}
                  data-testid="addressed-comment-label"
                />
              </Box>
              <Box>
                <Box sx={{ fontWeight: "bold" }}>Supressed:</Box>
                <FormControlLabel
                  control={<Switch checked={supressed} onChange={handleSupressedToggle} />}
                  label={supressed ? "Yes" : "No"}
                  data-testid="supressed-comment-label"
                />
              </Box>
              {supressed && (
                <FormControl sx={{ width: "100%", marginTop: "1rem" }}>
                  <InputLabel id="reason-label">Supressed Reason</InputLabel>
                  <Select
                    value={suppressedReasonId}
                    label={"Supressed Reason"}
                    onChange={handleSupressed}
                    data-testid="suppressed-reason-select"
                  >
                    {suppressedReasons.map((reason) => (
                      <MenuItem
                        key={"reason-" + reason.id}
                        value={reason.id}
                        data-testid={`suppressed-reason-${reason.id}`}
                      >
                        {reason.description}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              )}
            </Grid>
            <Divider sx={{ my: 2 }} />
            <Grid item xs={6}>
              <Typography variant="h6" sx={{ mb: 1 }}>
                Comment Deletion
              </Typography>
              <Typography variant="body2" color="textSecondary" sx={{ mb: 2 }}>
                {"Permanently delete this comment. You can't undo this action."}
              </Typography>
              <Button
                variant="contained"
                data-testid="delete-comment"
                color="error"
                onClick={handleClickDelete}
                sx={{ mb: 2 }}
              >
                Delete
              </Button>
              {comment?.piiMetadata?.piiPresent && (
                <>
                  <Typography variant="h6" sx={{ mb: 1 }}>
                    PII Actions
                  </Typography>
                  <Box>
                    <Typography component="span" variant="body1" sx={{ fontWeight: "bold", mr: 2 }}>
                      Current Action:
                    </Typography>
                    <Typography component="span" variant="body1">
                      {preselectedPiiAction?.label ?? "No Action Taken"}
                    </Typography>
                  </Box>
                  <PiiActionDropdown
                    supportedActions={supportedActionOptions}
                    selectedAction={selectedPiiAction}
                    onActionChange={setSelectedPiiAction}
                  />
                </>
              )}
            </Grid>
          </Grid>
          <Button
            variant="contained"
            data-testid="update-comment"
            onClick={handleSubmit}
            sx={{ marginTop: "2rem", backgroundColor: (theme) => theme.palette.primary.main }}
          >
            Update Comment
          </Button>
        </Box>
      </Modal>
      {actionType && <RequestCommentConfirmDeletion open={openDialog} onClose={handleClose} commentId={comment.id} />}
    </>
  );
};

export default RequestCommentModifyModal;
