import OpenInNewIcon from "@mui/icons-material/OpenInNew";
import {
  CircularProgress,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from "@mui/material";
import dayjs from "dayjs";
import React, { useEffect, useState } from "react";

import Pagination from "../../../Reusable/Pagination";
import { formatName } from "../../../User/User";
import { AssignmentModal } from "../../assignment/AssignmentModal";
import { AssignmentSummaryModal } from "../../assignment/AssignmentSummaryModal";
import { DashboardFilters } from "../../CommonRequestFilters";
import { AssignedRequestFilters, AssignedRequestsResponse, getAssignedRequests } from "../../Requests";
import { HighRiskPill } from "../../reusable/HighRiskPill";
import { RequestDeadline } from "../../reusable/RequestDeadline";
import { SortedTooltipTableHeader } from "../SortedTooltipTableHeader";
import { UnreadMessagesPill } from "../../reusable/UnreadMessagesPill";

interface AssignedRequestsProps {
  filters: DashboardFilters;
  openSummaryModal: boolean;
  handleSummaryModal: () => void;
}

const getSortingHandler =
  (currentSort: string, sortKey: string, setSort: React.Dispatch<React.SetStateAction<string>>) => () => {
    if (currentSort === `${sortKey},desc`) setSort(`${sortKey},asc`);
    else setSort(`${sortKey},desc`);
  };

const ARMTeamRequestsTable: React.FC<AssignedRequestsProps> = ({ filters, openSummaryModal, handleSummaryModal }) => {
  const [requests, setRequests] = useState<AssignedRequestsResponse[]>([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [dateSort, setDateSort] = useState<string>("lastRequestStatusChangeDate,desc");
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [isAssignmentModalOpen, setIsAssignmentModalOpen] = useState(false);
  const [assignmentModalRequest, setAssignmentModalRequest] = useState<AssignedRequestsResponse>();

  const handleOpenAssignmentModal = (request: AssignedRequestsResponse) => {
    setAssignmentModalRequest(request);
    setIsAssignmentModalOpen(true);
  };

  const handleCloseAssignmentModal = () => {
    setIsAssignmentModalOpen(false);
  };

  const fetchRequests = async () => {
    setIsLoading(true);
    try {
      const params: AssignedRequestFilters = {
        requestStatusIds: filters.requestStatusIds,
        projectSearch: filters.projectSearch,
        projectAccountIds: filters.projectAccountIds,
        reviewerAccountIds: filters.reviewerAccountIds,
        vvbAccountIds: filters.vvbAccountIds,
        requestTypeIds: filters.requestTypeIds,
      };
      const response = await getAssignedRequests(params, currentPage, dateSort);
      if (response) {
        setRequests(response.content);
        setTotalPages(response.totalPages);
      }
    } catch (error) {
      console.error("Error fetching requests:", error);
      setRequests([]);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchRequests();
  }, [currentPage, dateSort, filters]);

  useEffect(() => {
    if (requests.length === 0 || currentPage > totalPages) {
      setCurrentPage(1);
    } else {
      fetchRequests();
    }
  }, [filters]);

  const handleSortDueDate = getSortingHandler(dateSort, "latestActivityPlannedEndDate", setDateSort);
  const handleSortSubmissionDate = getSortingHandler(dateSort, "assignmentCreatedDate", setDateSort);
  const handleSortRoREndDate = getSortingHandler(dateSort, "currentRoundEndDate", setDateSort);
  const handleSortLastStatusChangeDate = getSortingHandler(dateSort, "lastRequestStatusChangeDate", setDateSort);

  if (isLoading) {
    return <CircularProgress />;
  }

  return (
    <>
      <div className="request-table">
        {requests && requests.length > 0 ? (
          <>
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell className="row-text gsid-col">GSID</TableCell>
                    <TableCell className="row-text">Project Name</TableCell>
                    <TableCell className="row-text">Developer</TableCell>
                    <TableCell className="row-text">Request</TableCell>
                    <TableCell className="row-text">Batch Size</TableCell>
                    <TableCell className="row-text">Status</TableCell>
                    <TableCell className="row-text">Round</TableCell>
                    <TableCell className="row-text clickable" onClick={handleSortRoREndDate}>
                      Round of Review End Date
                    </TableCell>
                    <TableCell className="row-text clickable" onClick={handleSortSubmissionDate}>
                      Assigned
                    </TableCell>
                    <TableCell className="row-text gsid-col">Reviewer</TableCell>
                    <TableCell className="row-text gsid-col">VVB</TableCell>
                    <TableCell className="row-text">Activity</TableCell>
                    <TableCell className="row-text">
                      <SortedTooltipTableHeader
                        title="Days Remaining"
                        helperText="Remaining business days before deadline"
                        onSort={handleSortDueDate}
                      />
                    </TableCell>
                    <TableCell className="row-text">Responsibility</TableCell>
                    <TableCell className="row-text">Last Submitted</TableCell>
                    <TableCell className="row-text clickable" onClick={handleSortLastStatusChangeDate}>
                      Last Submitted Date
                    </TableCell>
                    <TableCell className="row-text">Invoice Status</TableCell>
                    <TableCell className="row-text">Assignments</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {requests.map((request) => (
                    <TableRow key={request.requestId}>
                      <TableCell>
                        <Typography variant="body1" className="row-text">
                          <a href={`/projects/${request.gsid}`} target="_blank" rel="noopener noreferrer">
                            {request.gsid}
                          </a>
                        </Typography>
                        {request.projectRisk && <HighRiskPill />}
                        {!!request.unreadMessages && <UnreadMessagesPill unreadMessages={request.unreadMessages} />}
                      </TableCell>
                      <TableCell sx={{ wordWrap: "break-word", whiteSpace: "normal" }}>
                        <Typography variant="body1" className="row-text project-name">
                          {request.projectName}
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Typography variant="body1" className="row-text">
                          {request.projectAccountName}
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Typography variant="body1" className="row-text">
                          <a
                            href={`/projects/${request.gsid}/requests?expand=${request.requestId}`}
                            target="_blank"
                            rel="noopener noreferrer"
                          >
                            {request.requestName}
                          </a>
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Typography variant="body1" className="row-text">
                          {request.batchSize && request.batchSize > 1 ? request.batchSize : ""}
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Typography variant="body1" className="row-text">
                          {request.requestStatus}
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Typography variant="body1" className="row-text">
                          {request.currentRoundIndex}
                        </Typography>
                      </TableCell>
                      <TableCell>
                        {request.currentRoundEndDate && request.currentRoundIndex != null && (
                          <RequestDeadline date={request.currentRoundEndDate} />
                        )}
                      </TableCell>
                      <TableCell>
                        <Typography variant="body1" className="row-text">
                          {dayjs.utc(request.assignmentCreatedDate).format("DD/MM/YYYY")}
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Typography variant="body1" className="row-text">
                          {request.reviewerAccountName}
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Typography variant="body1" className="row-text">
                          {request.vvbAccountName}
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Typography variant="body1" className="row-text">
                          {request.latestActivityName}
                        </Typography>
                      </TableCell>
                      <TableCell>
                        {request.latestActivityPlannedEndDate && (
                          <RequestDeadline date={request.latestActivityPlannedEndDate} />
                        )}
                      </TableCell>
                      <TableCell>
                        <Typography variant="body1" className="row-text">
                          {request.latestActivityResponsibleAccountName}
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Typography variant="body1" className="row-text">
                          {formatName(request.submittedByGivenName, request.submittedByFamilyName)}
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Typography variant="body1" className="row-text">
                          {request.lastRequestStatusChangeDate &&
                            dayjs.utc(request.lastRequestStatusChangeDate).format("DD/MM/YYYY")}
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Typography variant="body1" className="row-text">
                          {request.invoiceStatus}
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <IconButton title="View Assignments" onClick={() => handleOpenAssignmentModal(request)}>
                          <OpenInNewIcon />
                        </IconButton>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
            <Pagination
              total={totalPages}
              current={currentPage}
              onChange={(newPage) => {
                setCurrentPage(newPage);
                window.scrollTo(0, 0);
              }}
            />
          </>
        ) : (
          <p>No Requests found.</p>
        )}
      </div>
      {isAssignmentModalOpen && assignmentModalRequest && (
        <AssignmentModal
          readonlyOnOpen
          isOpen={isAssignmentModalOpen}
          onClose={handleCloseAssignmentModal}
          handleOpenSummaryModal={handleSummaryModal}
          requestId={assignmentModalRequest.requestId}
          requestProjectName={assignmentModalRequest.projectName}
          requestTypeId={assignmentModalRequest.requestTypeId}
          requestTypeDescription={assignmentModalRequest.requestTypeDescription}
          requestStatusId={assignmentModalRequest.requestStatusId}
          requestProjectDeveloperName={assignmentModalRequest.projectAccountName}
        />
      )}
      {openSummaryModal && <AssignmentSummaryModal isOpen={openSummaryModal} onClose={handleSummaryModal} />}
    </>
  );
};

export default ARMTeamRequestsTable;
