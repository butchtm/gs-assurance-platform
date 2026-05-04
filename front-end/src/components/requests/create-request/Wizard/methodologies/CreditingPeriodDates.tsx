import { Typography } from "@mui/material";
import dayjs from "dayjs";
import { useFormikContext } from "formik";
import React, { useEffect, useState } from "react";

import { CreditingPeriod } from "../../../../../common/project/Project";
import { CreateRequestForm } from "../Request";

export const CreditingPeriodDates: React.FC<{
  creditingPeriodId: number | null;
  creditingPeriods: CreditingPeriod[];
}> = ({ creditingPeriodId, creditingPeriods }) => {
  const { values } = useFormikContext<CreateRequestForm>();
  const [creditingPeriodDates, setCreditingPeriodDates] = useState<string>();

  useEffect(() => {
    const creditingPeriod = creditingPeriods.find((x) => x.periodId == creditingPeriodId);
    if (creditingPeriod) {
      setCreditingPeriodDates(
        `${dayjs.utc(creditingPeriod.startDate).format("DD/MM/YYYY")} to ${dayjs
          .utc(creditingPeriod.endDate)
          .format("DD/MM/YYYY")}`,
      );
    } else if (values.newCreditingPeriodStartDate && values.newCreditingPeriodEndDate) {
      setCreditingPeriodDates(
        `${dayjs.utc(values.newCreditingPeriodStartDate).format("DD/MM/YYYY")} to ${dayjs
          .utc(values.newCreditingPeriodEndDate)
          .format("DD/MM/YYYY")}`,
      );
    }
  });
  return (
    <>
      <Typography variant="h6">{creditingPeriodDates}</Typography>
      {creditingPeriodId && <Typography variant="h6">Crediting Period {creditingPeriodId}</Typography>}
    </>
  );
};
