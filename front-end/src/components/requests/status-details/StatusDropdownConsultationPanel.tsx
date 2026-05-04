import { CalendarToday } from "@mui/icons-material";
import { Grid } from "@mui/material";
import dayjs from "dayjs";

import { RequestConsultation } from "../../../common/request/GSRequest";
import { RequestField } from "../reusable/RequestField";

export const StatusDropdownConsultationPanel = (props: { consultations: RequestConsultation[] | null }) => {
  const privateConsultation = props.consultations?.find((info) => info.type.userType === "TAC");
  const startDate = privateConsultation?.startDate ?? null;
  const endDate = privateConsultation?.endDate ?? null;

  return (
    <Grid container>
      <RequestField
        name="Consultation Start Date"
        value={startDate ? dayjs.utc(startDate).format("DD/MM/YYYY") : ""}
        icon={<CalendarToday />}
      />
      <RequestField
        name="Consultation End Date"
        value={endDate ? dayjs.utc(endDate).format("DD/MM/YYYY") : ""}
        icon={<CalendarToday />}
      />
    </Grid>
  );
};
