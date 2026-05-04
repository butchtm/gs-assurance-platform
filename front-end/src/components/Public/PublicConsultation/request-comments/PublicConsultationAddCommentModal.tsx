import { Warning } from "@mui/icons-material";
import CloseIcon from "@mui/icons-material/Close";
import {
  Box,
  Button,
  Checkbox,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControlLabel,
  Grid,
  IconButton,
  Switch,
  TextField,
  Tooltip,
  Typography,
  useMediaQuery,
  useTheme,
} from "@mui/material";
import { isAxiosError } from "axios";
import React, { useEffect, useState } from "react";
import { toast } from "react-toastify";

import {
  checkPiiToolTip,
  mapResultToColour,
  piiWithColourHelperText,
  publicAnalyseText,
  removeDuplicatePii,
  submitCommentToolTip,
  TextAnalysis,
  TextAnalysisResponse,
} from "../../../../common/piiAnalysis/PiiAnalysis";
import { publicCreateRequestComment, RequestCommentCreate } from "../../../requests/comments/RequestComments";
import {
  default as CustomHelperText,
  CustomTooltip,
  default as CustomTooltipText,
} from "../../../Reusable/FormFields/CustomTooltipText";
import { ValidEmailTextField } from "../../../Reusable/FormFields/ValidEmailTextField";

interface PublicConsultationAddCommentModalProps {
  requestId: number;
  isOpen: boolean;
  onClose: () => void;
}

const PublicConsultationAddCommentModal: React.FC<PublicConsultationAddCommentModalProps> = ({
  requestId,
  isOpen,
  onClose,
}) => {
  const theme = useTheme();
  const fullScreen = useMediaQuery(theme.breakpoints.down("sm"));

  const [anonymous, setAnonymous] = useState<boolean>(false);
  const [comment, setComment] = useState<string>("");
  const [name, setName] = useState<string>("");
  const [email, setEmail] = useState<string>("");
  const [emailIsValid, setEmailIsValid] = useState<boolean>(true);
  const [shouldShowPii, setShouldShowPii] = useState<boolean>(false);
  const [hasCheckedPii, setHasCheckedPii] = useState<boolean>(false);
  const [toAnalyse, setToAnalyse] = useState<TextAnalysis>();
  const [potentialPii, setPotentialPii] = useState<TextAnalysisResponse>();
  const [understandsPii, setUnderstandsPii] = useState<boolean>(false);
  const [canSubmit, setCanSubmit] = useState<boolean>(false);

  const checkValidFields = () => {
    if (!name) {
      return false;
    }

    if (!emailIsValid) {
      return false;
    }

    if (!comment) {
      return false;
    }
    return true;
  };

  const analyseFreeTextFields = async () => {
    if (!checkValidFields()) {
      return;
    }
    if (toAnalyse != null) {
      return await publicAnalyseText(toAnalyse);
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
  }, [name, email, comment]);

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
      if (isAxiosError(error)) {
        if (error.response?.status === 429) {
          const timeOutSeconds: number = error.response?.headers["x-rate-limit-retry-after-seconds"];
          const timeOutMinutes = Math.ceil(timeOutSeconds / 60);
          toast.error(`Too many requests, please try again in ${timeOutMinutes} minutes`);
        }
      }
      console.error("Failed to fetch Pii check response:", error);
      throw error;
    }
  };

  const handleSubmit = async () => {
    checkValidFields();

    const payload: RequestCommentCreate = {
      requestId: requestId,
      comment: comment,
      usersEmail: email,
      usersName: name,
      publicAccess: false,
      anonymous: anonymous,
      textAnalysisResults: potentialPii?.probablePii.length ? potentialPii : null,
    };

    try {
      await publicCreateRequestComment(payload);
      toast.success("Comment added successfully, it will show publicly upon review.");
      onClose();
    } catch {
      toast.error("Error making comment");
    }
  };

  const handleToggle = (event: React.ChangeEvent<HTMLInputElement>) => {
    setAnonymous(event.target.checked);
  };

  return (
    <Dialog
      open={isOpen}
      onClose={onClose}
      fullScreen={fullScreen}
      maxWidth={shouldShowPii ? "md" : "sm"}
      fullWidth
      aria-labelledby="add-comment-dialog-title"
    >
      <DialogTitle id="add-comment-dialog-title" sx={{ m: 0, p: 2 }}>
        Add Comment
        <IconButton
          aria-label="close"
          onClick={onClose}
          sx={{
            position: "absolute",
            right: 8,
            top: 8,
            color: (theme) => theme.palette.grey[500],
          }}
          size="large"
        >
          <CloseIcon data-testid="modal-close-icon" />
        </IconButton>
      </DialogTitle>

      <DialogContent
        dividers
        sx={{
          pt: 1,
          px: 3,
          pb: 2,
          maxHeight: "70vh",
          overflowY: "auto",
        }}
      >
        <Grid container>
          <Grid container item xs={12} spacing={1}>
            <Grid item xs={shouldShowPii ? 6 : 12}>
              <TextField
                fullWidth
                label="Name"
                variant="outlined"
                value={name}
                onChange={(e) => setName(e.target.value)}
                sx={{ mb: 1 }}
                inputProps={{ "data-testid": "comment-name-input" }}
              />

              <ValidEmailTextField
                fullWidth
                label="Email"
                variant="outlined"
                value={email}
                onEmailAddressChange={(emailAddress, isValid) => {
                  setEmail(emailAddress);
                  setEmailIsValid(isValid);
                }}
                sx={{ mb: 1 }}
              />
              <TextField
                fullWidth
                label="Comment"
                variant="outlined"
                multiline
                inputProps={{ "data-testid": "CommentTextField" }}
                rows={6}
                value={comment}
                onChange={(e) => setComment(e.target.value)}
              />
            </Grid>
            {shouldShowPii && (
              <Grid container item xs={6} sx={{ maxHeight: "330px" }}>
                <Box sx={{ border: 1, borderRadius: 1, height: "100%", width: "100%" }}>
                  <Grid item xs={12}>
                    <Typography variant="body1" sx={{ position: "relative", top: 6, left: 8, mb: 1 }}>
                      {potentialPii?.probablePii.length ? "Identified Potential PII" : "No PII identified"}
                    </Typography>
                  </Grid>
                  <Grid container item xs={12} sx={{ maxHeight: "262px", overflowY: "auto" }}>
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
          <Grid container item columnSpacing={2}>
            <Grid item xs={shouldShowPii ? 6 : 12}>
              <Box sx={{ display: "flex", alignItems: "center", mb: 1 }}>
                <Box component={"span"}>Comment Anonymously</Box>
                <CustomHelperText text="Your data will be anonymised publicly" type={CustomTooltip.HELP} />
              </Box>
              <FormControlLabel
                control={<Switch checked={anonymous} onChange={handleToggle} />}
                label={anonymous ? "Yes" : "No"}
                data-testid="comment-anonymously"
              />
            </Grid>
            {shouldShowPii && !!potentialPii?.probablePii.length && (
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

      <DialogActions sx={{ px: 3, pb: 2 }}>
        <CustomTooltipText text={piiWithColourHelperText} type={CustomTooltip.WARNING} />
        <Tooltip title={checkPiiToolTip(checkValidFields())}>
          <span>
            <Button
              variant="contained"
              onClick={handleCheckForPii}
              sx={{ backgroundColor: "white", borderColor: (theme) => theme.palette.primary.main }}
              disabled={!checkValidFields()}
            >
              Check For Pii
            </Button>
          </span>
        </Tooltip>
        <Tooltip title={submitCommentToolTip(hasCheckedPii, !!potentialPii?.probablePii.length, understandsPii)}>
          <span>
            <Button
              variant="contained"
              onClick={handleSubmit}
              sx={{ backgroundColor: (theme) => theme.palette.primary.main }}
              disabled={!canSubmit}
            >
              Submit Comment
            </Button>
          </span>
        </Tooltip>
      </DialogActions>
    </Dialog>
  );
};

export default PublicConsultationAddCommentModal;
