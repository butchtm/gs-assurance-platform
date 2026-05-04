import React from "react";
import { Grid } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers";
import dayjs, { Dayjs } from "dayjs";
import { useFormikContext } from "formik";

import { UpdateProjectPayload } from "../../../../common/project/Project";
import { ReferenceType } from "../../../../common/data-types/DataReferenceTypes";

export const DateFields: React.FC<{
  projectStatus: ReferenceType | undefined;
}> = ({ projectStatus }) => {
  const { values, setFieldValue } = useFormikContext<UpdateProjectPayload>();

  const LISTED_STATUSES = ["LISTED", "DESIGN_CERTIFIED", "CERTIFIED_PROJECT", "DE_CERTIFIED"];
  const DESIGN_CERTIFIED_STATUSES = ["DESIGN_CERTIFIED", "CERTIFIED_PROJECT", "DE_CERTIFIED"];
  const CERTIFIED_PROJECT_STATUSES = ["CERTIFIED_PROJECT", "DE_CERTIFIED"];

  const canEditDate = (statuses: string[], projectStatus: string) => {
    return statuses.includes(projectStatus);
  };

  const handleListingDateChange = (date: Dayjs | null) => {
    if (date) {
      setFieldValue("listingDate", date.toISOString());
      setFieldValue("listingDateManualSet", true);
    } else {
      setFieldValue("listingDate", null);
      setFieldValue("listingDateManualSet", false);
    }
  };

  return (
    <>
      {projectStatus?.code && (
        <Grid container spacing={4} sx={{ paddingBottom: 4 }}>
          {canEditDate(LISTED_STATUSES, projectStatus.code) && (
            <Grid item xs={12} md={6}>
              <DatePicker
                label="Listing Date"
                value={values.listingDate ? dayjs.utc(values.listingDate) : null}
                minDate={dayjs.utc(new Date(2000, 0, 1))}
                onChange={(date) => handleListingDateChange(date)}
                slotProps={{ textField: { fullWidth: true } }}
                format="DD/MM/YYYY"
                timezone="UTC"
              />
            </Grid>
          )}
          {canEditDate(DESIGN_CERTIFIED_STATUSES, projectStatus.code) && (
            <Grid item xs={12} md={6}>
              <DatePicker
                label="Design Certification Date"
                value={values.designCertificationDate ? dayjs.utc(values.designCertificationDate) : null}
                minDate={dayjs.utc(new Date(2000, 0, 1))}
                onChange={(date) => setFieldValue("designCertificationDate", date?.toISOString())}
                slotProps={{ textField: { fullWidth: true } }}
                format="DD/MM/YYYY"
                timezone="UTC"
              />
            </Grid>
          )}
          {canEditDate(CERTIFIED_PROJECT_STATUSES, projectStatus.code) && (
            <Grid item xs={12} md={6}>
              <DatePicker
                label="Certified Project Date"
                value={values.certifiedProjectDate ? dayjs.utc(values.certifiedProjectDate) : null}
                minDate={dayjs.utc(new Date(2000, 0, 1))}
                onChange={(date) => setFieldValue("certifiedProjectDate", date?.toISOString())}
                slotProps={{ textField: { fullWidth: true } }}
                format="DD/MM/YYYY"
                timezone="UTC"
              />
            </Grid>
          )}
        </Grid>
      )}
      <Grid container spacing={4} sx={{ paddingBottom: 4 }}>
        <Grid item xs={12} md={6}>
          <DatePicker
            label="Last Design Certificate Renewal Date"
            value={
              values.lastDesignCertificationRenewalDate ? dayjs.utc(values.lastDesignCertificationRenewalDate) : null
            }
            minDate={dayjs.utc(new Date(2000, 0, 1))}
            onChange={(date) => setFieldValue("lastDesignCertificationRenewalDate", date?.toISOString())}
            slotProps={{ textField: { fullWidth: true } }}
            format="DD/MM/YYYY"
            timezone="UTC"
          />
        </Grid>
        <Grid item xs={12} md={6}>
          <DatePicker
            label="Last Performance Review Date"
            value={values.lastPerformanceReviewDate ? dayjs.utc(values.lastPerformanceReviewDate) : null}
            minDate={dayjs.utc(new Date(2000, 0, 1))}
            onChange={(date) => setFieldValue("lastPerformanceReviewDate", date?.toISOString())}
            slotProps={{ textField: { fullWidth: true } }}
            format="DD/MM/YYYY"
            timezone="UTC"
          />
        </Grid>
      </Grid>
    </>
  );
};
