import { CalendarToday, Checklist, Layers, Numbers } from "@mui/icons-material";
import { Grid } from "@mui/material";
import dayjs, { Dayjs } from "dayjs";
import React from "react";

import InformationBlock from "../../../Reusable/Card/InformationBlock";
import InformationCard from "../../../Reusable/Card/InformationCard";

interface CertificationMilestonesProps {
  status?: string;
  currentRequestName?: string;
  registrationType?: string;
  startDate?: string | Date;
  listingDate?: string | Date;
  designCertificationDate?: string | Dayjs;
}

const CertificationMilestones: React.FC<CertificationMilestonesProps> = ({
  status,
  currentRequestName,
  registrationType,
  startDate,
  listingDate,
  designCertificationDate,
}): JSX.Element => {
  return (
    <InformationCard title="Certification Milestones">
      <Grid container spacing={2}>
        <Grid item xs={4}>
          <InformationBlock icon={<Checklist sx={{ fontSize: "13px" }} />} title="Project Status">
            {status || "N/A"}
          </InformationBlock>
        </Grid>
        {currentRequestName && (
          <Grid item xs={4}>
            <InformationBlock icon={<Numbers sx={{ fontSize: "13px" }} />} title="Current Request">
              {currentRequestName || "Not Set"}
            </InformationBlock>
          </Grid>
        )}
        <Grid item xs={4}>
          <InformationBlock icon={<Layers sx={{ fontSize: "13px" }} />} title="Registration Type">
            {registrationType || "Not Set"}
          </InformationBlock>
        </Grid>
        <Grid item xs={4}>
          <InformationBlock icon={<CalendarToday sx={{ fontSize: "13px" }} />} title="Project Start Date">
            {startDate ? dayjs.utc(startDate).format("DD/MM/YYYY") : "Not Set"}
          </InformationBlock>
        </Grid>
        <Grid item xs={4}>
          <InformationBlock icon={<CalendarToday sx={{ fontSize: "13px" }} />} title="Listing Date">
            {listingDate ? dayjs.utc(listingDate).format("DD/MM/YYYY") : "Not Set"}
          </InformationBlock>
        </Grid>
        <Grid item xs={4}>
          <InformationBlock icon={<CalendarToday sx={{ fontSize: "13px" }} />} title="Design Certification Date">
            {designCertificationDate ? dayjs.utc(designCertificationDate).format("DD/MM/YYYY") : "Not Set"}
          </InformationBlock>
        </Grid>
      </Grid>
    </InformationCard>
  );
};

export default CertificationMilestones;
