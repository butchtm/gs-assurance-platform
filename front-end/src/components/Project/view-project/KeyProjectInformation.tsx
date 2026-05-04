import { Description } from "@mui/icons-material";
import { Grid } from "@mui/material";
import { useContext, useEffect, useState } from "react";

import { fieldIncludesPii, lowProbability, piiForReviewHelperText } from "../../../common/piiAnalysis/PiiAnalysis";
import { Project } from "../../../common/project/Project";
import { AuthContext } from "../../Auth/Login/AuthProvider";
import { getRequestWithId } from "../../requests/Requests";
import InformationBlock from "../../Reusable/Card/InformationBlock";
import InformationCard from "../../Reusable/Card/InformationCard";
import CustomTooltipText, { CustomTooltip } from "../../Reusable/FormFields/CustomTooltipText";

import CertificationMilestones from "./key-project-information/CertificationMilestones";
import CreditingPeriods from "./key-project-information/CreditingPeriods";
import Fees from "./key-project-information/Fees";
import HeadlineInformation from "./key-project-information/HeadlineInformation";
import ProjectCharacteristics from "./key-project-information/ProjectCharacteristics";
import ProjectImpact from "./key-project-information/ProjectImpact";
import Resources from "./key-project-information/Resources";
import ViewProjectBundleModal from "./ViewProjectBundleModal";

export const KeyProjectInformation = ({ project }: { project: Partial<Project> }): JSX.Element => {
  const [currentRequestName, setCurrentRequestName] = useState("");
  const [bundleModalOpen, setBundleModalOpen] = useState<boolean>(false);
  const authContext = useContext(AuthContext);
  const canSeeProjectNotes =
    authContext.isAdminUser() ||
    authContext.isArmUser() ||
    (authContext.isProjectDeveloperUser() && project.accountId && authContext.isUserOwnerOf(project.accountId));

  const openBundleModal = () => {
    setBundleModalOpen(true);
  };

  const closeBundleModal = () => {
    setBundleModalOpen(false);
  };

  useEffect(() => {
    const fetchCurrentRequest = async (): Promise<void> => {
      if (project.id && project.currentRequestId) {
        const currentRequest = await getRequestWithId(project.currentRequestId);
        if (currentRequest) {
          setCurrentRequestName(currentRequest?.reviewName);
        }
      }
    };

    fetchCurrentRequest();
  }, [project.currentRequestId]);

  return (
    <>
      <Grid container spacing={2}>
        <Grid item xs={12} md={7}>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <HeadlineInformation
                gsid={project.gsid}
                masterProjectGsid={project.masterProject?.gsid}
                classification={project.projectClassification?.description}
                countries={project.countries?.map((x) => x.name)}
              />
            </Grid>
            <Grid item xs={12} md={12}>
              <ProjectCharacteristics
                types={project.projectTypes?.map((x) => x.description)}
                size={project.projectSize?.description}
                currentStandardVersion={project.currentStandardVersion?.description}
                creditingPeriods={project.creditingPeriods}
                bundleName={project.bundle?.name}
                openBundleModal={openBundleModal}
              />
            </Grid>
            <Grid item xs={12} md={12}>
              <CertificationMilestones
                status={project.projectStatus?.description}
                currentRequestName={currentRequestName}
                registrationType={project.registrationType?.description}
                startDate={project.projectStartDate}
                listingDate={project.listingDate}
                designCertificationDate={project.designCertificationDate}
              />
            </Grid>
            <Grid item xs={6} md={6}>
              <Fees feeSchedule={project.feeSchedule} creditingPeriods={project.creditingPeriods} />
            </Grid>
            <Grid item xs={6} md={6} style={{ height: "100%" }}>
              <Resources registryUrl={project.registryUrl} />
            </Grid>
          </Grid>
        </Grid>
        <Grid item xs={12} md={5}>
          <ProjectImpact
            assets={project.projectAssets?.map((x) => x.description)}
            carbonStream={project.carbonStream?.description}
            estimatedAnnualUnits={project.estimatedAnnualUnits}
            sustainableDevelopmentGoals={project.sustainableDevelopmentGoals}
          />
          <InformationCard>
            <InformationBlock
              icon={<Description sx={{ fontSize: "13px" }} />}
              title="Project Description"
              tooltip={
                fieldIncludesPii(project as Project, "description") ? (
                  <CustomTooltipText
                    text={piiForReviewHelperText}
                    type={CustomTooltip.WARNING}
                    styleOverrides={{ color: lowProbability }}
                    altText="Personally Identifiable Information Tooltip"
                  />
                ) : null
              }
            >
              {project.description == "" ? "Not Set" : project.description}
            </InformationBlock>
          </InformationCard>
          {canSeeProjectNotes && (
            <InformationCard>
              <InformationBlock
                icon={<Description sx={{ fontSize: "13px" }} />}
                title="Project Notes"
                tooltip={
                  fieldIncludesPii(project as Project, "notes") ? (
                    <CustomTooltipText
                      text={piiForReviewHelperText}
                      type={CustomTooltip.WARNING}
                      styleOverrides={{ color: lowProbability }}
                      altText="Personally Identifiable Information Tooltip"
                    />
                  ) : null
                }
              >
                {project.notes == "" ? "Not Set" : project.notes}
              </InformationBlock>
            </InformationCard>
          )}
          <CreditingPeriods creditingPeriods={project.creditingPeriods} project={project} />
        </Grid>
      </Grid>
      {project.bundle && bundleModalOpen && (
        <ViewProjectBundleModal
          projectBundleId={project.bundle?.id}
          bundleName={project.bundle.name}
          isOpen={bundleModalOpen}
          onClose={closeBundleModal}
        />
      )}
    </>
  );
};
