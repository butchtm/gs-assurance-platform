import { Button, Typography } from "@mui/material";
import dayjs from "dayjs";
import { useFormikContext } from "formik";

import { getReferenceValueDescriptionById, useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { CreateProjectForm } from "../../../../common/project/Project";
import { useFormikStepContext } from "../../../Reusable/Formik/FormikStepper";

export const ReviewStepCreditingPeriods = () => {
  const { referenceMethodologies } = useReferenceTypes();
  const { values } = useFormikContext<CreateProjectForm>();
  const { setStep } = useFormikStepContext();

  return (
    <>
      <Typography variant="body2" gutterBottom align="left">
        <strong>Crediting Periods</strong>
        <Button className="edit-button" onClick={() => setStep(2)}>
          Edit
        </Button>
      </Typography>
      {values.creditingPeriods.map((period) => (
        <div key={period.periodId}>
          <Typography variant="body2" gutterBottom align="left" style={{ wordBreak: "break-word", fontSize: "0.8rem" }}>
            {period.periodId}: {dayjs.utc(period.startDate).format("DD/MM/YYYY")} -{" "}
            {dayjs.utc(period.endDate).format("DD/MM/YYYY")}
          </Typography>
          {period.methodologies.length > 0 && (
            <Typography variant="body2" gutterBottom align="left">
              <strong>Methodologies</strong>
            </Typography>
          )}
          {period.methodologies?.map((methodology) => (
            <Typography
              key={methodology.id}
              variant="body2"
              gutterBottom
              align="left"
              style={{
                wordBreak: "break-word",
                fontSize: "0.8rem",
                marginLeft: "1rem",
              }}
            >
              {getReferenceValueDescriptionById(methodology.id, referenceMethodologies)}
              {" - "}
              {methodology.version}
            </Typography>
          ))}
        </div>
      ))}
    </>
  );
};
