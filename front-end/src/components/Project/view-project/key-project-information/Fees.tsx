import React, { useEffect, useState } from "react";
import { Grid } from "@mui/material";
import { Layers } from "@mui/icons-material";
import dayjs from "dayjs";

import { CreditingPeriod, MonitoringPeriod, ReferenceType } from "../../../../common/project/Project";
import InformationBlock from "../../../Reusable/Card/InformationBlock";
import InformationCard from "../../../Reusable/Card/InformationCard";
import { isNullOrUndefined } from "../../../../common/request/GSRequest";
import { useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";

interface FeesProps {
  feeSchedule?: ReferenceType;
  creditingPeriods?: CreditingPeriod[];
}

const Fees: React.FC<FeesProps> = ({ feeSchedule, creditingPeriods }): JSX.Element => {
  const { activeReferenceTypes } = useReferenceTypes();
  const [latestFeeSchedule, setLatestFeeSchedule] = useState<ReferenceType | null>(null);

  useEffect(() => {
    if (!creditingPeriods) {
      return;
    }
    setLatestFeeSchedule(getLatestFeeSchedule(creditingPeriods) ?? null);
  }, [creditingPeriods, feeSchedule]);

  const getLatestFeeSchedule = (creditingPeriods: CreditingPeriod[]) => {
    const monitoringPeriodsWithFeeSchedules = creditingPeriods
      .flatMap(({ monitoringPeriods }) => monitoringPeriods)
      .filter((period) => period !== undefined)
      .filter((period) => !isNullOrUndefined(period?.feeSchedule))
      .sort(sortDescendingEndDate);
    if (monitoringPeriodsWithFeeSchedules.length > 0) {
      const latestWithMonitoringPeriod = monitoringPeriodsWithFeeSchedules[0];
      if (latestWithMonitoringPeriod && !feeScheduleMatchesProject(latestWithMonitoringPeriod, feeSchedule)) {
        return getFeeScheduleRefType(latestWithMonitoringPeriod?.feeSchedule);
      }
    }
    return null;
  };

  const sortDescendingEndDate = (a?: MonitoringPeriod, b?: MonitoringPeriod) => {
    const aDate = dayjs.utc(a?.endDate).startOf("day");
    const bDate = dayjs.utc(b?.endDate).startOf("day");
    if (aDate.isSame(bDate)) {
      return 0;
    }
    return bDate.isBefore(aDate) ? -1 : 1;
  };

  const feeScheduleMatchesProject = (monitoringPeriod: MonitoringPeriod, feeSchedule?: ReferenceType) => {
    return monitoringPeriod.feeSchedule == feeSchedule?.id;
  };

  const getFeeScheduleRefType = (feeScheduleId: number | null) => {
    return activeReferenceTypes.FeeSchedule.find((x) => x.id == feeScheduleId);
  };

  return (
    <InformationCard title="Fees">
      <Grid container spacing={2}>
        <Grid item xs={latestFeeSchedule ? 6 : 12}>
          <InformationBlock
            icon={<Layers sx={{ fontSize: "13px" }} />}
            title={latestFeeSchedule ? "Project Fee Schedule" : "Fee Schedule"}
          >
            {feeSchedule?.description || "Not Set"}
          </InformationBlock>
        </Grid>
        {latestFeeSchedule && (
          <Grid item xs={6}>
            <InformationBlock icon={<Layers sx={{ fontSize: "13px" }} />} title="Latest Fee Schedule">
              {latestFeeSchedule.description || "Not Set"}
            </InformationBlock>
          </Grid>
        )}
      </Grid>
    </InformationCard>
  );
};

export default Fees;
