import {
  CircularProgress,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from "@mui/material";
import dayjs from "dayjs";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import Pagination from "../../../Reusable/Pagination";
import { DashboardFilters } from "../../CommonRequestFilters";
import { AssignedRequestsResponse, getAssignedRequests } from "../../Requests";
import { RequestDeadline } from "../../reusable/RequestDeadline";
import { UnreadMessagesPill } from "../../reusable/UnreadMessagesPill";

const VVBRequestsTable = ({ filters }: { filters: DashboardFilters }) => {
  const [requestAssignments, setRequestAssignments] = useState<AssignedRequestsResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [dateSort, setDateSort] = useState<string>("lastRequestStatusChangeDate,desc");

  useEffect(() => {
    const fetchRequests = async () => {
      try {
        const requestsData = await getAssignedRequests(filters, currentPage, dateSort);
        setTotalPages(requestsData.totalPages);
        setRequestAssignments(requestsData.content);
        setIsLoading(false);
      } catch (error) {
        console.error("Error fetching requests:", error);
      }
    };
    fetchRequests();
  }, [currentPage, filters, dateSort]);

  const onClickSorting = (field: string) => {
    if (dateSort && dateSort == `${field},desc`) {
      setDateSort(`${field},asc`);
    } else {
      setDateSort(`${field},desc`);
    }
  };

  if (isLoading) {
    return (
      <div className="request-table">
        <CircularProgress />
      </div>
    );
  }

  if (requestAssignments.length === 0) {
    return (
      <div className="request-table">
        <Typography>No Assigned Requests found.</Typography>
      </div>
    );
  }

  return (
    <div className="request-table">
      <TableContainer>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell className="row-text gsid-col">GSID</TableCell>
              <TableCell className="row-text">Project</TableCell>
              <TableCell className="row-text">Request Name</TableCell>
              <TableCell className="row-text">Request Status</TableCell>
              <TableCell className="row-text">Project Developer</TableCell>
              <TableCell className="row-text clickable" onClick={() => onClickSorting("assignmentCreatedDate")}>
                Assigned Date
              </TableCell>
              <TableCell className="row-text clickable" onClick={() => onClickSorting("lastRequestStatusChangeDate")}>
                Last Submitted Date
              </TableCell>
              <TableCell className="row-text">Country</TableCell>
              <TableCell className="row-text">RoR Count</TableCell>
              <TableCell className="row-text clickable" onClick={() => onClickSorting("currentRoundEndDate")}>
                Round of Review End Date
              </TableCell>
              <TableCell className="row-text">Project Type</TableCell>
              <TableCell className="row-text">Carbon Stream</TableCell>
              <TableCell className="row-text">Assignment Status</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {requestAssignments.map((request) => (
              <TableRow key={`${request.requestId}-${request.assignmentId}`}>
                <TableCell>
                  <Typography variant="body1" className="row-text">
                    <Link to={`/projects/${request.gsid}`}>{request.gsid}</Link>
                  </Typography>
                  {!!request.unreadMessages && <UnreadMessagesPill unreadMessages={request.unreadMessages} />}
                </TableCell>
                <TableCell>
                  <Typography variant="body1" className="row-text">
                    {request.projectName}
                  </Typography>
                </TableCell>
                <TableCell>
                  <Typography variant="body1" className="row-text">
                    <Link to={`/projects/${request.gsid}/requests?expand=${request.requestId}`}>
                      {request.requestName}
                    </Link>
                  </Typography>
                </TableCell>
                <TableCell>
                  <Typography variant="body1" className="row-text">
                    {request.requestStatus}
                  </Typography>
                </TableCell>
                <TableCell>
                  <Typography variant="body1" className="row-text">
                    {request.projectAccountName}
                  </Typography>
                </TableCell>
                <TableCell>
                  <Typography variant="body1" className="row-text">
                    {dayjs.utc(request.assignmentCreatedDate).format("DD/MM/YYYY")}
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
                    {request.projectLocations.map((location) => location.description).join(", ")}
                  </Typography>
                </TableCell>
                <TableCell>
                  <Typography variant="body1" className="row-text">
                    {request.currentRoundIndex != null ? request.currentRoundIndex : 0}
                  </Typography>
                </TableCell>
                <TableCell>
                  {request.currentRoundEndDate && request.currentRoundIndex != null && (
                    <RequestDeadline date={request.currentRoundEndDate} />
                  )}
                </TableCell>
                <TableCell>
                  <Typography variant="body1" className="row-text">
                    {request.projectTypes.map((type) => type.description).join(", ")}
                  </Typography>
                </TableCell>
                <TableCell>
                  <Typography variant="body1" className="row-text">
                    {request.projectCarbonStream != null ? request.projectCarbonStream.description : ""}
                  </Typography>
                </TableCell>
                <TableCell>
                  <Typography variant="body1" className="row-text">
                    {request.assignmentStatus}
                  </Typography>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
        <div style={{ margin: "auto", padding: "16px" }}>
          <Pagination
            total={totalPages}
            current={currentPage}
            onChange={(newPage) => {
              setCurrentPage(newPage);
              window.scrollTo(0, 0);
            }}
          />
        </div>
      </TableContainer>
    </div>
  );
};

export default VVBRequestsTable;
