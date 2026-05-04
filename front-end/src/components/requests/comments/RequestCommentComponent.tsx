import LockIcon from "@mui/icons-material/Lock";
import LockOpenIcon from "@mui/icons-material/LockOpen";
import VisibilityOffIcon from "@mui/icons-material/VisibilityOff";
import { Avatar, Box, Grid, Typography } from "@mui/material";
import dayjs from "dayjs";

import { useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";
import { PiiActionCode } from "../../../common/piiAnalysis/PiiAction";
import { PiiActionWarningPill, WARNING_ACTIONS } from "../../../common/piiAnalysis/PiiActionWarningPill";
import { PermissionGroup } from "../../../PermissionGroup";
import ProtectedComponent from "../../../ProtectedComponent";
import { helperTexts } from "../../Project/create-project/HelperText";
import { CommentModifyAction } from "../actions/comments/CommentModifyAction";

import { RequestComment } from "./RequestComments";

const getHelperTextForAction = (action?: PiiActionCode | null) => {
  if (!action) {
    return <></>;
  }
  if (WARNING_ACTIONS.includes(action)) {
    return helperTexts.commentPiiRequiresReview();
  }
  if (action == PiiActionCode.PII_NOT_SUITABLE) {
    return helperTexts.commentPiiNotAcceptable();
  }
};

export const RequestCommentComponent = ({
  requestComment,
  updateRequest,
}: {
  requestComment: RequestComment;
  updateRequest: () => void;
}) => {
  const { activeReferenceTypes } = useReferenceTypes();
  const deleteReasons = activeReferenceTypes.DeletionReason;
  const suppressedReasons = activeReferenceTypes.SuppressionReason;
  const piiAction = requestComment.piiMetadata?.action?.code;

  return (
    <Box
      sx={{
        borderRadius: "5px",
        backgroundColor: "rgb(235, 250, 250)",
        marginBottom: "17px",
        padding: "13px",
      }}
    >
      <Grid container>
        <Grid item>
          <Avatar sx={{ marginRight: "13px", width: 56, height: 56 }}>{requestComment.uploadedByName[0]}</Avatar>
        </Grid>
        <Grid item xs sx={{ textAlign: "left" }}>
          <Grid container>
            <Grid item xs={4}>
              <Typography variant="body2" sx={{ fontWeight: "bold" }}>
                {requestComment.uploadedByName}
              </Typography>
              {requestComment.uploadedByEmail !== "Anonymous" && (
                <Typography variant="body2" sx={{ fontWeight: "bold" }}>
                  {requestComment.uploadedByEmail}
                </Typography>
              )}
              <Typography variant="body2" title={dayjs.utc(requestComment.uploadedDate).format("DD/MM/YYYY HH:mm")}>
                {dayjs.utc(requestComment.uploadedDate).fromNow()}
              </Typography>
            </Grid>
            <Grid item xs={8}>
              <Box
                sx={{
                  display: "flex",
                  justifyContent: "flex-end",
                  alignItems: "center",
                }}
              >
                {requestComment.anonymous && (
                  <Box
                    className={"document-status anonymous"}
                    sx={{
                      padding: "3px 8px",
                    }}
                  >
                    Anonymous&nbsp;
                    <VisibilityOffIcon fontSize="small" />
                  </Box>
                )}
                <Box
                  className={`document-status ${requestComment.publicAccess ? "public-status" : "private-status"}`}
                  sx={{
                    padding: "3px 8px",
                    marginRight: "9px",
                  }}
                >
                  {requestComment.publicAccess ? "Public" : "Private"}&nbsp;
                  {requestComment.publicAccess ? <LockOpenIcon fontSize="small" /> : <LockIcon fontSize="small" />}
                </Box>
                <Box
                  sx={{
                    padding: "3px 8px",
                    marginRight: "9px",
                    marginLeft: "-9px",
                  }}
                >
                  <PiiActionWarningPill action={piiAction} helperText={getHelperTextForAction(piiAction)} />
                </Box>
                {!requestComment.deleted && (
                  <ProtectedComponent roles={[PermissionGroup.GS_ADMIN, PermissionGroup.ARM_TEAM]}>
                    <CommentModifyAction comment={requestComment} updateRequest={updateRequest} />
                  </ProtectedComponent>
                )}
              </Box>
            </Grid>
          </Grid>
        </Grid>
        <Grid item xs={12}>
          <Box
            sx={{
              margin: "6px",
              marginTop: "13px",
              textAlign: "left",
            }}
          >
            <Typography variant="body1">
              {requestComment.submittedComment}
              {requestComment.deleted && (
                <span>
                  &nbsp;—&nbsp;Reason for deletion:&nbsp;
                  {deleteReasons.find((reason) => reason.id === requestComment.deletedReasonId)?.description ??
                    "Reason not found"}
                </span>
              )}
              {!requestComment.deleted && requestComment.suppressed && (
                <span>
                  &nbsp;—&nbsp;Reason for suppression:&nbsp;
                  {suppressedReasons.find((reason) => reason.id === requestComment.suppressionReasonId)?.description ??
                    "Reason not found"}
                </span>
              )}
            </Typography>
          </Box>
        </Grid>
      </Grid>
    </Box>
  );
};
