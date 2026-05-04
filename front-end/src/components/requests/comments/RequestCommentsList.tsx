import { Box, Typography } from "@mui/material";

import { RequestConsultation } from "../../../common/request/GSRequest";

import { RequestCommentComponent } from "./RequestCommentComponent";
import { RequestComment } from "./RequestComments";
import { RequestCommentsListConsultationInfo } from "./RequestCommentsListConsultationInfo";

export const RequestCommentsList = ({
  requestComments,
  consultations,
  refreshComments,
}: {
  requestComments: RequestComment[];
  consultations?: RequestConsultation[];
  refreshComments: () => void;
}) => {
  const renderComments = (): JSX.Element | JSX.Element[] => {
    if (requestComments.length == 0) {
      return <Typography sx={{ margin: "10px" }}>No comments</Typography>;
    }
    return requestComments.map((requestComment) => (
      <RequestCommentComponent
        key={requestComment.id}
        requestComment={requestComment}
        updateRequest={refreshComments}
      />
    ));
  };

  return (
    <Box sx={{ margin: "10px" }}>
      <RequestCommentsListConsultationInfo consultations={consultations} />
      {renderComments()}
    </Box>
  );
};
