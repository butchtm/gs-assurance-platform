import { Article, CalendarToday, Description, Numbers } from "@mui/icons-material";
import { Grid } from "@mui/material";
import dayjs from "dayjs";

import { GSRequest } from "../../../common/request/GSRequest";
import { RequestField } from "../reusable/RequestField";

export const StatusDropdownRequestPanel = (props: {
  request: GSRequest;
  dashboardFormat: boolean;
  documentStatus: string;
}) => {
  return (
    <Grid container item xs={12} sm={12} md={12} lg={props.dashboardFormat ? 12 : 6}>
      <RequestField name="Request ID" value={props.request.id} icon={<Numbers />} />
      <RequestField name="Request Status" value={props.request.requestStatus.description} icon={<Article />} />
      <RequestField
        name="Documents Status"
        value={props.documentStatus ? props.documentStatus : ""}
        icon={<Description />}
      />
      <RequestField name="Request Type" value={props.request.requestType.description} icon={<Article />} />
      {props.request.startDate && (
        <RequestField
          name="Request Start Date"
          value={dayjs.utc(props.request.startDate).format("DD/MM/YYYY")}
          icon={<CalendarToday />}
        />
      )}
      {props.request.endDate && (
        <RequestField
          name="Request End Date"
          value={dayjs.utc(props.request.endDate).format("DD/MM/YYYY")}
          icon={<CalendarToday />}
        />
      )}
    </Grid>
  );
};
