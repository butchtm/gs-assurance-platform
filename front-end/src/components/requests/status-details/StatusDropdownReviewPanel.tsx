import { CalendarToday, Numbers } from "@mui/icons-material";
import { Grid } from "@mui/material";
import dayjs from "dayjs";

import { Assignment } from "../../../components/requests/assignment/Assignment";
import { GSRequest, RequestRound } from "../../../common/request/GSRequest";
import { RequestField } from "../reusable/RequestField";

import { AuditAssignmentField } from "./AuditAssignmentField";

export const StatusDropdownReviewPanel = ({
  request,
  currentRoundOfReview,
  assignments,
}: {
  request: GSRequest;
  currentRoundOfReview?: RequestRound;
  assignments: Assignment[];
}) => {
  return (
    <Grid container>
      <RequestField name="Round of Review Count" value={request.currentRoundIndex ?? 0} icon={<Numbers />} />
      {currentRoundOfReview?.endDate && (
        <RequestField
          name="Current Round of Review End Date"
          value={dayjs.utc(currentRoundOfReview?.endDate).format("DD/MM/YYYY")}
          icon={<CalendarToday />}
        />
      )}
      <AuditAssignmentField assignments={assignments} />
    </Grid>
  );
};
