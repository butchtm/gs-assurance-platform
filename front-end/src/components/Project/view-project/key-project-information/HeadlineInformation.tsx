import React from "react";
import { Grid } from "@mui/material";
import { AccountTree, Language, Numbers } from "@mui/icons-material";

import InformationCard from "../../../Reusable/Card/InformationCard";
import InformationBlock from "../../../Reusable/Card/InformationBlock";

interface HeadlineInformationProps {
  gsid?: string;
  masterProjectGsid?: string;
  classification?: string;
  countries?: string[];
}

const HeadlineInformation: React.FC<HeadlineInformationProps> = ({
  gsid,
  masterProjectGsid,
  classification,
  countries,
}): JSX.Element => {
  return (
    <InformationCard>
      <Grid container spacing={2}>
        <Grid item xs={3}>
          <InformationBlock icon={<Numbers sx={{ fontSize: "13px" }} />} title="GSID">
            {gsid}
          </InformationBlock>
        </Grid>
        <Grid item xs={3}>
          <InformationBlock icon={<Numbers sx={{ fontSize: "13px" }} />} title="PoA GSID">
            {masterProjectGsid || "N/A"}
          </InformationBlock>
        </Grid>
        <Grid item xs={3}>
          <InformationBlock icon={<AccountTree sx={{ fontSize: "13px" }} />} title="Project Class">
            {classification || "Not Set"}
          </InformationBlock>
        </Grid>
        <Grid item xs={3}>
          <InformationBlock icon={<Language sx={{ fontSize: "13px" }} />} title="Country">
            {countries?.length ? countries && countries.join(", ") : "Not Set"}
          </InformationBlock>
        </Grid>
      </Grid>
    </InformationCard>
  );
};

export default HeadlineInformation;
