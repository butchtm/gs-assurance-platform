import { Description } from "@mui/icons-material";
import { Grid, Typography } from "@mui/material";
import React from "react";

import CertificationMilestones from "../../../Project/view-project/key-project-information/CertificationMilestones";
import CreditingPeriods from "../../../Project/view-project/key-project-information/CreditingPeriods";
import HeadlineInformation from "../../../Project/view-project/key-project-information/HeadlineInformation";
import ProjectCharacteristics from "../../../Project/view-project/key-project-information/ProjectCharacteristics";
import ProjectImpact from "../../../Project/view-project/key-project-information/ProjectImpact";
import InformationBlock from "../../../Reusable/Card/InformationBlock";
import InformationCard from "../../../Reusable/Card/InformationCard";
import { ProjectInConsultation } from "../PublicConsultation";

interface PublicConsultationProjectDetailsProps {
  project: ProjectInConsultation;
}

const PublicConsultationProjectDetails: React.FC<PublicConsultationProjectDetailsProps> = ({
  project,
}): JSX.Element => {
  return (
    <>
      <Typography variant="h5" sx={{ textAlign: "left" }}>
        Project
      </Typography>
      <Grid container spacing={2} sx={{ paddingTop: "10px" }}>
        <Grid item xs={12} md={7}>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <HeadlineInformation
                gsid={project.gsid}
                masterProjectGsid={project.masterProjectGsid}
                classification={project.projectClassification}
                countries={project.projectLocations}
              />
            </Grid>
            <Grid item xs={12} md={12}>
              <ProjectCharacteristics
                types={project.projectTypes}
                size={project.projectSize}
                currentStandardVersion={project.currentStandardVersion}
                creditingPeriods={project.projectCreditingPeriods}
              />
            </Grid>
            <Grid item xs={12} md={12}>
              <CertificationMilestones
                status={project.projectStatus}
                registrationType={project.registrationType}
                startDate={project.projectStartDate}
                listingDate={project.listingDate}
                designCertificationDate={project.designCertificationDate}
              />
            </Grid>
          </Grid>
        </Grid>
        <Grid item xs={12} md={5}>
          <ProjectImpact
            assets={project.projectAssets}
            carbonStream={project.carbonStream}
            estimatedAnnualUnits={project.estimatedAnnualUnits}
            sustainableDevelopmentGoals={project.projectSustainabilityGoalsTypes}
          />
          <InformationCard>
            <InformationBlock icon={<Description sx={{ fontSize: "13px" }} />} title="Project Description">
              {project.projectDescription || "Not Set"}
            </InformationBlock>
          </InformationCard>
          <CreditingPeriods creditingPeriods={project.projectCreditingPeriods} project={project} />
        </Grid>
      </Grid>
    </>
  );
};

export default PublicConsultationProjectDetails;
