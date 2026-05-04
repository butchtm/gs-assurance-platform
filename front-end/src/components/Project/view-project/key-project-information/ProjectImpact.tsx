import { AccountTree, Inventory, Numbers } from "@mui/icons-material";
import { Grid } from "@mui/material";
import React from "react";

import { ReferenceType } from "../../../../common/data-types/DataReferenceTypes";
import InformationBlock from "../../../Reusable/Card/InformationBlock";
import InformationCard from "../../../Reusable/Card/InformationCard";
import NO_PRODUCT from "../media/no-product.svg";
import SVG_1 from "../media/sdg/sdg-1.svg";
import SVG_10 from "../media/sdg/sdg-10.svg";
import SVG_11 from "../media/sdg/sdg-11.svg";
import SVG_12 from "../media/sdg/sdg-12.svg";
import SVG_13 from "../media/sdg/sdg-13.svg";
import SVG_14 from "../media/sdg/sdg-14.svg";
import SVG_15 from "../media/sdg/sdg-15.svg";
import SVG_16 from "../media/sdg/sdg-16.svg";
import SVG_17 from "../media/sdg/sdg-17.svg";
import SVG_2 from "../media/sdg/sdg-2.svg";
import SVG_3 from "../media/sdg/sdg-3.svg";
import SVG_4 from "../media/sdg/sdg-4.svg";
import SVG_5 from "../media/sdg/sdg-5.svg";
import SVG_6 from "../media/sdg/sdg-6.svg";
import SVG_7 from "../media/sdg/sdg-7.svg";
import SVG_8 from "../media/sdg/sdg-8.svg";
import SVG_9 from "../media/sdg/sdg-9.svg";
import VER from "../media/ver.svg";

const CarbonStream = ({ carbonStream }: { carbonStream?: string }): JSX.Element => {
  if (carbonStream) {
    if (carbonStream === "VER") {
      return (
        <div style={{ textAlign: "center" }}>
          <img src={VER} alt="VER" style={{ width: "30px", height: "30px" }} />
          <div>VER</div>
        </div>
      );
    }
    return <>{carbonStream}</>;
  }
  return (
    <div style={{ textAlign: "center" }}>
      <img src={NO_PRODUCT} alt="None" style={{ width: "50px", height: "50px" }} />
      <div>None</div>
    </div>
  );
};

interface ProjectImpactProps {
  assets?: string[];
  carbonStream?: string;
  estimatedAnnualUnits?: number;
  sustainableDevelopmentGoals?: null | ReferenceType[];
}

const ProjectImpact: React.FC<ProjectImpactProps> = ({
  assets,
  carbonStream,
  estimatedAnnualUnits,
  sustainableDevelopmentGoals,
}): JSX.Element => {
  const sdgImages: { [key: number]: string } = {
    1: SVG_1,
    2: SVG_2,
    3: SVG_3,
    4: SVG_4,
    5: SVG_5,
    6: SVG_6,
    7: SVG_7,
    8: SVG_8,
    9: SVG_9,
    10: SVG_10,
    11: SVG_11,
    12: SVG_12,
    13: SVG_13,
    14: SVG_14,
    15: SVG_15,
    16: SVG_16,
    17: SVG_17,
  };

  return (
    <InformationCard title="Project Impact">
      <Grid container spacing={2}>
        <Grid item xs={4}>
          <InformationBlock icon={<Inventory sx={{ fontSize: "13px" }} />} title="Certified Product">
            {assets && assets.length > 0 ? (
              <ul>
                {assets.map((asset) => (
                  <li key={asset}>{asset}</li>
                ))}
              </ul>
            ) : (
              <>
                <img src={NO_PRODUCT} alt="NONE" style={{ width: "30px", height: "30px" }} />
                <div>None</div>
              </>
            )}
          </InformationBlock>
        </Grid>
        <Grid item xs={4}>
          <InformationBlock icon={<AccountTree sx={{ fontSize: "13px" }} />} title="Carbon Stream">
            <CarbonStream carbonStream={carbonStream} />
          </InformationBlock>
        </Grid>
        <Grid item xs={4}>
          <InformationBlock
            icon={<Numbers sx={{ fontSize: "13px" }} />}
            title="Estimated Annual Emission Reduction/Removal"
          >
            {estimatedAnnualUnits?.toLocaleString() || "Not Set"}
          </InformationBlock>
        </Grid>
        <Grid item xs={4}>
          <InformationBlock icon={<AccountTree sx={{ fontSize: "13px" }} />} title="SDG Impacts">
            {sustainableDevelopmentGoals && sustainableDevelopmentGoals.length > 0
              ? sustainableDevelopmentGoals.map((sdg) => {
                  const SdgImage = sdgImages[sdg.id];
                  return SdgImage ? (
                    <img
                      key={sdg.id}
                      src={SdgImage}
                      alt={sdg.code}
                      title={sdg.description}
                      style={{ width: "50px", height: "50px" }}
                    />
                  ) : null;
                })
              : "Not Available"}
          </InformationBlock>
        </Grid>
      </Grid>
    </InformationCard>
  );
};

export default ProjectImpact;
