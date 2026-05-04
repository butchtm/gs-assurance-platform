import { Warning } from "@mui/icons-material";
import {
  Box,
  Button,
  Checkbox,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  FormControlLabel,
  FormGroup,
  Grid,
  TextField,
  Tooltip,
  Typography,
  useMediaQuery,
  useTheme,
} from "@mui/material";
import { AxiosError } from "axios";
import React, { useEffect, useState } from "react";
import { toast } from "react-toastify";

import {
  analyseText,
  checkPiiToolTip,
  mapResultToColour,
  piiWithColourHelperText,
  removeDuplicatePii,
  submitCommentToolTip,
  TextAnalysis,
  TextAnalysisResponse,
} from "../../../common/piiAnalysis/PiiAnalysis";
import { GSRequest } from "../../../common/request/GSRequest";
import { Account } from "../../PlatformAccounts/PlatformAccountTypes";
import CustomTooltipText, { CustomTooltip } from "../../Reusable/FormFields/CustomTooltipText";

import { createRequestComment } from "./RequestComments";

const RequestCommentModal = ({
  request,
  account,
  isOpen,
  onClose,
}: {
  request: GSRequest;
  account: Account;
  isOpen: boolean;
  onClose: () => void;
}) => {
  const theme = useTheme();
  const fullScreen = useMediaQuery(theme.breakpoints.down("sm"));

  const [isAnonymous, setIsAnonymous] = useState<boolean>(false);
  const [comment, setComment] = useState<string>("");
  const [shouldShowPii, setShouldShowPii] = useState<boolean>(false);
  const [hasCheckedPii, setHasCheckedPii] = useState<boolean>(false);
  const [toAnalyse, setToAnalyse] = useState<TextAnalysis>();
  const [potentialPii, setPotentialPii] = useState<TextAnalysisResponse>();
  const [understandsPii, setUnderstandsPii] = useState<boolean>(false);
  const [canSubmit, setCanSubmit] = useState<boolean>(false);

  const analyseFreeTextFields = async () => {
    if (!comment) {
      return;
    }
    if (toAnalyse != null) {
      return await analyseText(toAnalyse);
    }
  };

  useEffect(() => {
    setHasCheckedPii(false);
    setUnderstandsPii(false);
    setCanSubmit(false);
    const fieldsToAnalyse = [];
    if (comment != "") {
      fieldsToAnalyse.push({ field: "comment", text: comment });
    }

    const textToAnalyse: TextAnalysis = {
      fieldsToAnalyse: fieldsToAnalyse,
    };
    setToAnalyse(textToAnalyse);
  }, [comment]);

  useEffect(() => {
    if (!understandsPii) {
      setCanSubmit(false);
    }
    if (hasCheckedPii) {
      if (!potentialPii?.probablePii.length) {
        setCanSubmit(true);
      }
      if (potentialPii?.probablePii.length && understandsPii) {
        setCanSubmit(true);
      }
    }
  }, [potentialPii, understandsPii]);

  const handleCheckForPii = async () => {
    try {
      const response = await analyseFreeTextFields();
      if (response) {
        setPotentialPii(response);
        setShouldShowPii(true);
        setHasCheckedPii(true);
      }
    } catch (error) {
      console.error("Failed to fetch Pii check response:", error);
      throw error;
    }
  };

  const handleSubmit = async () => {
    try {
      await createRequestComment({
        requestId: request.id,
        comment: comment,
        accountId: account.id,
        publicAccess: false,
        anonymous: isAnonymous,
        textAnalysisResults: potentialPii?.probablePii.length ? potentialPii : null,
      });
      toast.success("Commented Successfully");
    } catch (error) {
      if (error instanceof AxiosError && error.response?.data) {
        const apiError = error.response.data;
        toast.error(apiError.detail || "Error adding Comment.");
      } else {
        toast.error("An unexpected error occurred.");
      }
    }

    onClose();
  };

  const onIsAnonymousChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setIsAnonymous(event.target.checked);
  };

  const onCommentChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setComment(event.target.value);
  };

  return (
    <Dialog fullWidth fullScreen={fullScreen} maxWidth={shouldShowPii ? "md" : "sm"} open={isOpen} onClose={onClose}>
      <DialogTitle sx={{ pb: 0 }}>Add Comment</DialogTitle>
      <DialogContent
        sx={{
          pt: 1,
          px: 3,
          pb: 2,
          maxHeight: "70vh",
          overflowY: "auto",
        }}
      >
        <Grid container sx={{ marginTop: "15px", minWidth: "480px" }} spacing={1}>
          <DialogContentText sx={{ mb: 1 }}>
            <strong>Account Name:</strong> {account.name}
            <br />
            <strong>Account Type:</strong> {account.accountType.description}
          </DialogContentText>
          <Grid container item xs={12} spacing={1}>
            <Grid item xs={shouldShowPii ? 6 : 12}>
              <TextField
                autoFocus
                required
                fullWidth
                label="Comment"
                multiline
                minRows={potentialPii?.probablePii.length ? 8 : 4}
                value={comment}
                onChange={onCommentChange}
                inputProps={{ "data-testid": "comment-input" }}
              />
            </Grid>
            {shouldShowPii && (
              <Grid container item xs={6}>
                <Box sx={{ border: 1, borderRadius: 1, height: "100%", width: "100%" }}>
                  <Grid item xs={12}>
                    <Typography variant="body1" sx={{ position: "relative", top: 6, left: 8, mb: 1 }}>
                      {potentialPii?.probablePii.length ? "Identified Potential PII" : "No PII identified"}
                    </Typography>
                  </Grid>
                  <Grid container item xs={12} sx={{ maxHeight: "184px", overflowY: "auto" }}>
                    {potentialPii &&
                      removeDuplicatePii(potentialPii.probablePii).map((pii) => (
                        <Grid
                          key={`${pii.entity}-${pii.probability}`}
                          item
                          container
                          xs={12}
                          m={1}
                          sx={{ alignItems: "center" }}
                        >
                          <Grid item xs={1}>
                            <Warning sx={{ color: mapResultToColour(pii.probability) }} />
                          </Grid>
                          <Grid item xs={11}>
                            <Typography
                              variant="body2"
                              gutterBottom
                              align="left"
                              color={mapResultToColour(pii.probability)}
                            >
                              {pii.identifiedString}
                            </Typography>
                            <Typography variant="body2" gutterBottom align="left">
                              {pii.entity}
                            </Typography>
                          </Grid>
                        </Grid>
                      ))}
                  </Grid>
                </Box>
              </Grid>
            )}
          </Grid>
          <Grid container item xs={12}>
            <Grid item xs={6}>
              <FormGroup>
                <FormControlLabel
                  control={
                    <Checkbox data-testid="comment-anonymously" checked={isAnonymous} onChange={onIsAnonymousChange} />
                  }
                  label="Comment anonymously"
                  data-testid="comment-anonymously-label"
                />
              </FormGroup>
            </Grid>
            {!!potentialPii?.probablePii.length && (
              <Grid item xs={6}>
                <FormControlLabel
                  control={<Checkbox checked={understandsPii} onChange={(e) => setUnderstandsPii(e.target.checked)} />}
                  label="I understand that this comment contains PII"
                  data-testid="understands-pii-checkbox"
                />
                <Typography variant="body2" sx={{ marginTop: "10px" }}>
                  The information submitted will be subject to review
                </Typography>
              </Grid>
            )}
          </Grid>
        </Grid>
      </DialogContent>
      <DialogActions>
        <CustomTooltipText text={piiWithColourHelperText} type={CustomTooltip.WARNING} />
        <Button onClick={onClose} data-testid="modal-cancel-button">
          Cancel
        </Button>
        <Tooltip title={checkPiiToolTip(!!comment)}>
          <span>
            <Button onClick={handleCheckForPii} disabled={!comment}>
              Check For Pii
            </Button>
          </span>
        </Tooltip>
        <Tooltip title={submitCommentToolTip(hasCheckedPii, !!potentialPii?.probablePii.length, understandsPii)}>
          <span>
            <Button onClick={handleSubmit} disabled={!canSubmit} type="submit" data-testid="modal-submit-button">
              Submit Comment
            </Button>
          </span>
        </Tooltip>
      </DialogActions>
    </Dialog>
  );
};

export default RequestCommentModal;
