import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { IconButton, Table, TableBody, TableCell, TableHead, TableRow } from "@mui/material";
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Helmet } from "react-helmet-async";

import FullScreenMessage from "../../../../common/full-screen-message/FullScreenMessage";
import Pagination from "../../../Reusable/Pagination";
import { getPublicConsultation, ProjectInConsultationSummary } from "../PublicConsultation";

import PublicConsultationExpandableRow from "./PublicConsultationExpandableRow";
import PublicConsultationRow from "./PublicConsultationRow";

const expansionTracker = new Map<string, boolean>();

const PublicConsultationList: React.FC = () => {
  const navigate = useNavigate();

  const [projects, setProjects] = useState<ProjectInConsultationSummary[]>([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState<number>(0);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [allExpanded, setAllExpanded] = useState<boolean>(true);

  useEffect(() => {
    const fetchProjects = async () => {
      setIsLoading(true);
      try {
        const projectsData = await getPublicConsultation(currentPage);
        if (projectsData) {
          setProjects(projectsData.data);
          setTotalPages(projectsData.totalPages);
          for (const project of projectsData.data) {
            if (project.requests.length > 1) {
              expansionTracker.set(project.gsid, true);
            }
          }
        }
      } catch (error) {
        console.error("Error fetching project in consultation details:", error);
      }
      setIsLoading(false);
    };
    fetchProjects();
  }, [currentPage]);

  const updateExpansionTracker = (gsid: string, updatedExpanded: boolean) => {
    expansionTracker.set(gsid, updatedExpanded);

    const expansionTrackerValues = Array.from(expansionTracker.values());
    const allExpandedRows = expansionTrackerValues.filter((x) => x);
    if (allExpandedRows.length === expansionTrackerValues.length) {
      setAllExpanded(true);
    } else if (allExpandedRows.length === 0) {
      setAllExpanded(false);
    }
  };

  const handleAllExpandClick = () => {
    setAllExpanded(!allExpanded);
    Array.from(expansionTracker.keys()).forEach((gsid) => {
      expansionTracker.set(gsid, !allExpanded);
    });
  };

  const openProject = (gsid: string, requestId: number): void => {
    window.scrollTo(0, 0);
    navigate(`/public-consultations/request/${requestId}`, {
      state: {
        gsid: gsid,
        requestId: requestId,
      },
    });
  };

  if (isLoading) {
    return <></>;
  }

  if (projects.length === 0) {
    return <FullScreenMessage headline={"No projects currently in Stakeholder Consultation"} />;
  }

  return (
    <>
      <Helmet>
        <title>Public Consultations</title>
      </Helmet>
      <div style={{ margin: "20px" }}>
        <Table sx={{ whiteSpace: "normal", wordWrap: "break-word" }}>
          <TableHead>
            <TableRow className="table-headings">
              <TableCell className="row-text">GSID</TableCell>
              <TableCell className="row-text project-name-col">Project Name</TableCell>
              <TableCell className="row-text">
                <IconButton
                  sx={(theme) => ({
                    transform: !allExpanded ? "rotate(0deg)" : "rotate(180deg)",
                    marginLeft: "auto",
                    transition: theme.transitions.create("transform", {
                      duration: theme.transitions.duration.shortest,
                    }),
                  })}
                  onClick={handleAllExpandClick}
                  aria-label="Expand all"
                  data-testid="consultation-expand-all-button"
                >
                  <ExpandMoreIcon data-testid="consultation-expand-all-row" />
                </IconButton>
              </TableCell>
              <TableCell className="row-text">Request</TableCell>
              <TableCell className="row-text status-col">Status</TableCell>
              <TableCell className="row-text">Methodology</TableCell>
              <TableCell className="row-text">Country</TableCell>
              <TableCell className="row-text">Start Date</TableCell>
              <TableCell className="row-text">End Date</TableCell>
              <TableCell className="row-text">Batch</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {projects.map((project) =>
              project.requests.length > 1 ? (
                <PublicConsultationExpandableRow
                  key={project.gsid}
                  project={project}
                  allExpanded={allExpanded}
                  onClick={openProject}
                  updateExpansionTracker={updateExpansionTracker}
                />
              ) : (
                <PublicConsultationRow
                  key={project.gsid}
                  project={project}
                  request={project.requests[0]}
                  singleRequest={true}
                  onClick={openProject}
                />
              ),
            )}
          </TableBody>
        </Table>
      </div>
      <Pagination
        total={totalPages}
        current={currentPage}
        onChange={(newPage) => {
          setCurrentPage(newPage);
          window.scrollTo(0, 0);
        }}
      />
    </>
  );
};

export default PublicConsultationList;
