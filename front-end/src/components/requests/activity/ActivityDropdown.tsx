import { CalendarToday, Checklist, Person } from "@mui/icons-material";
import { Grid } from "@mui/material";
import dayjs from "dayjs";
import React, { useContext, useEffect, useState } from "react";

import { RequestActivity } from "../../../common/request/GSRequest";
import { AuthContext } from "../../Auth/Login/AuthProvider";
import { RequestField } from "../reusable/RequestField";
import { RequestSectionHeader } from "../reusable/RequestSectionHeader";

export const ActivityDropdown: React.FC<{ requestActivity: RequestActivity }> = ({ requestActivity }) => {
  const authContext = useContext(AuthContext);
  const [canViewResponsibility, setCanViewResponsibility] = useState<boolean>(false);

  useEffect(() => {
    const isAssigned = authContext.isUserOwnerOf(requestActivity.account?.id);
    setCanViewResponsibility(
      authContext.isArmUser() ||
        authContext.isAdminUser() ||
        authContext.isSupportUser() ||
        (authContext.isReviewerUser() && isAssigned),
    );
  }, [requestActivity]);

  return (
    <>
      <RequestSectionHeader name="Activity" />
      <Grid
        container
        sx={{
          border: "2px solid var(--color-accent-teal-border)",
          backgroundColor: "var(--color-surface-accent-teal-tint)",
          borderRadius: "5px",
        }}
      >
        <RequestField name="Activity" value={requestActivity.name} icon={<Checklist />} />
        <RequestField
          name="Deadline"
          value={dayjs.utc(requestActivity.dueDate).format("DD/MM/YYYY")}
          icon={<CalendarToday />}
        />
        {canViewResponsibility && (
          <RequestField name="Responsibility" value={requestActivity.account.name} icon={<Person />} />
        )}
      </Grid>
    </>
  );
};
