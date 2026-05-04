import { Edit } from "@mui/icons-material";
import TabContext from "@mui/lab/TabContext";
import TabList from "@mui/lab/TabList";
import TabPanel from "@mui/lab/TabPanel";
import { Box, CircularProgress, IconButton, Tab, ThemeProvider } from "@mui/material";
import React, { useContext, useEffect, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import { toast } from "react-toastify";

import FullScreenMessage from "../../../common/full-screen-message/FullScreenMessage";
import { fieldIncludesPii, piiForReviewHelperText } from "../../../common/piiAnalysis/PiiAnalysis";
import { Project, ProjectOverview } from "../../../common/project/Project";
import { theme } from "../../../theme";
import { AuthContext } from "../../Auth/Login/AuthProvider";
import CustomTooltipText, { CustomTooltip } from "../../Reusable/FormFields/CustomTooltipText";
import { RequestsView } from "../../requests/RequestsView";
import { getProject, getProjectByGsid, getProjectTree, ProjectTree } from "../Project";

import { KeyProjectInformation } from "./KeyProjectInformation";
import TransitioningProjectInformation from "./TransitioningProjectInformation";
import ViewProjectHierarchyTree from "./ViewProjectHierarchyTree";
import { RiskManagementTab } from "./risk/RiskManagementTab";
import { RiskModal } from "./risk/RiskModal";
import { RiskWarningBanner } from "./risk/RiskWarningBanner";

interface ViewProjectProps {
  project?: ProjectOverview;
}

const enum ProjectTab {
  PROJECT_INFORMATION = "information",
  TRANSITION_SCHEME = "transition-schemes",
  REQUESTS = "requests",
  PROJECT_VPAS = "vpas",
  RISK = "risk",
}

const ViewProject: React.FC<ViewProjectProps> = () => {
  const { projectId, activeTab } = useParams();
  const [selectedTabType, setSelectedTabType] = useState(activeTab ?? ProjectTab.PROJECT_INFORMATION);
  const [specificRequest, setSpecificRequest] = useState<number>();
  const [project, setProject] = useState<Project>();
  const [isProjectLoading, setIsProjectLoading] = useState<boolean>(true);
  const [projectTree, setProjectTree] = useState<ProjectTree>();
  const [refreshProject, setRefreshProject] = useState<boolean>();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const authContext = useContext(AuthContext);
  const [isRiskModalOpen, setIsRiskModalOpen] = useState<boolean>(false);

  const isArmOrAdmin = authContext.isAdminUser() || authContext.isArmUser();

  const updateRefreshProject = () => {
    setRefreshProject((prev) => !prev);
  };

  const canBeEdited = () => {
    if (isArmOrAdmin) {
      return true;
    }
    if (!project) {
      return false;
    }
    const userOwnsProject = authContext.isProjectDeveloperUser() && authContext.isUserOwnerOf(project.accountId);
    return userOwnsProject && project.projectStatus.code == "NEW";
  };

  useEffect(() => {
    const requestId = searchParams.get("expand");
    if (requestId) {
      setSpecificRequest(Number(requestId));
    }
  }, [searchParams]);

  useEffect(() => {
    const fetchProjectById = async (): Promise<void> => {
      if (!projectId) return;
      try {
        if (projectId.toUpperCase().startsWith("GS")) {
          setProject(await getProjectByGsid(projectId.toUpperCase()));
        } else {
          setProject(await getProject(Number(projectId)));
        }

        setIsProjectLoading(false);
      } catch {
        toast.error(`Failed to fetch project with ID: ${projectId}`);
        setIsProjectLoading(false);
      }
    };

    fetchProjectById();
  }, [projectId, refreshProject]);

  useEffect(() => {
    const fetchProjectTreeGet = async () => {
      if (project) {
        const projectForrest = await getProjectTree(project.id);
        setProjectTree(projectForrest);
      }
    };

    fetchProjectTreeGet();
  }, [project]);

  const shouldDisplayTransitionInformation = () => {
    const transitionDetail = project?.transitionDetail;
    if (!transitionDetail) {
      return false;
    }
    return transitionDetail?.transitionFromOtherScheme?.code != "NONE";
  };

  const handleTabChange = (event: React.SyntheticEvent, newTabType: ProjectTab) => {
    navigate(`/projects/${projectId}/${newTabType}`);
    setSelectedTabType(newTabType);
  };

  const showProjectVpas = project && !["STANDALONE_PROJECT", "NONE"].includes(project?.projectClassification.code);

  if (!project && !isProjectLoading) {
    return <FullScreenMessage headline="No Project Found" />;
  }

  if (!project) {
    return (
      <ThemeProvider theme={theme}>
        <CircularProgress />
      </ThemeProvider>
    );
  }

  return (
    <div className="view-container">
      <div className="title">
        {project.gsid} - {project.name}
        {canBeEdited() && (
          <IconButton
            aria-label="Edit Project"
            data-testid="edit-project"
            onClick={() => navigate("/update-project", { state: { projectId: project.id } })}
            sx={{ color: "white", ml: "5px" }}
          >
            <Edit />
          </IconButton>
        )}
        {fieldIncludesPii(project, "title") && (
          <CustomTooltipText
            text={piiForReviewHelperText}
            type={CustomTooltip.WARNING}
            styleOverrides={{ color: "var(--color-pii-tooltip-warning)" }}
            altText="Personally Identifiable Information Tooltip"
          />
        )}
      </div>
      {isArmOrAdmin && project.currentRiskSummary?.risk && (
        <RiskWarningBanner risk={project.currentRiskSummary} openRiskModal={() => setIsRiskModalOpen(true)} />
      )}
      <TabContext value={selectedTabType}>
        <Box sx={{ marginTop: 1 }}>
          <TabList onChange={handleTabChange}>
            <Tab label="Project Information" value={ProjectTab.PROJECT_INFORMATION} />
            {shouldDisplayTransitionInformation() && (
              <Tab label="Transition Scheme" value={ProjectTab.TRANSITION_SCHEME} />
            )}
            <Tab label="Requests" value={ProjectTab.REQUESTS} />
            {showProjectVpas && <Tab label="Project VPAs" value={ProjectTab.PROJECT_VPAS} />}
            {isArmOrAdmin && <Tab label="Risk" value={ProjectTab.RISK} />}
          </TabList>
        </Box>
        <TabPanel value={ProjectTab.PROJECT_INFORMATION}>
          <KeyProjectInformation project={project} />
        </TabPanel>
        {shouldDisplayTransitionInformation() && (
          <TabPanel value={ProjectTab.TRANSITION_SCHEME}>
            <TransitioningProjectInformation transitionDetail={project.transitionDetail} />
          </TabPanel>
        )}
        <TabPanel value={ProjectTab.REQUESTS}>
          <RequestsView project={project} specificRequest={specificRequest} refreshProject={updateRefreshProject} />
        </TabPanel>
        {showProjectVpas && (
          <TabPanel value={ProjectTab.PROJECT_VPAS}>
            {projectTree ? (
              <ViewProjectHierarchyTree
                project={project}
                returnToProjectInfoCallback={() => setSelectedTabType(ProjectTab.PROJECT_INFORMATION)}
                projectTree={projectTree}
              />
            ) : (
              <>No Related Projects</>
            )}
          </TabPanel>
        )}
        {isArmOrAdmin && (
          <TabPanel value={ProjectTab.RISK}>
            <RiskManagementTab
              projectId={project.id}
              openRiskModal={() => setIsRiskModalOpen(true)}
              refreshProject={refreshProject}
            />
          </TabPanel>
        )}
      </TabContext>
      {isRiskModalOpen && (
        <RiskModal
          isOpen={isRiskModalOpen}
          onClose={(updated: boolean) => {
            setIsRiskModalOpen(false);
            if (updated) {
              updateRefreshProject();
            }
          }}
          project={{
            id: project.id,
            name: project.name,
            gsid: project.gsid,
          }}
          currentRisk={project.currentRiskSummary}
        />
      )}
    </div>
  );
};

export default ViewProject;
