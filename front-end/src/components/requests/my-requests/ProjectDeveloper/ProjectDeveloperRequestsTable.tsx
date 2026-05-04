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
import React, { useEffect, useState } from "react";

import { ReferenceType } from "../../../../common/data-types/DataReferenceTypes";
import Pagination from "../../../Reusable/Pagination";
import { DashboardFilters } from "../../CommonRequestFilters";
import { getProjectDeveloperRequests, ProjectDeveloperRequestsResponse } from "../../Requests";
import { RequestDeadline } from "../../reusable/RequestDeadline";
import { UnreadMessagesPill } from "../../reusable/UnreadMessagesPill";

interface ProjectDeveloperRequestsTableProps {
  filters: DashboardFilters;
}

interface TableHeaderProps {
  onClickSorting: (field: string) => void;
}

const TableHeader: React.FC<TableHeaderProps> = ({ onClickSorting }) => (
  <TableHead>
    <TableRow>
      <TableCell className="row-text gsid-col">GSID</TableCell>
      <TableCell className="row-text">Project Name</TableCell>
      <TableCell className="row-text">Request Name</TableCell>
      <TableCell className="row-text gsid-col">VVB</TableCell>
      <TableCell className="row-text">Batch</TableCell>
      <TableCell className="row-text">RoR Count</TableCell>
      <TableCell className="row-text">RoR End Date</TableCell>
      <TableCell className="row-text">Status</TableCell>
      <TableCell className="row-text">Project Type</TableCell>
      <TableCell className="row-text gsid-col">Country</TableCell>
      <TableCell className="row-text">Carbon Stream</TableCell>
      <TableCell className="row-text">Document Status</TableCell>
      <TableCell className="row-text">Invoice Status</TableCell>
      <TableCell className="row-text clickable" onClick={() => onClickSorting("lastRequestStatusChangeDate")}>
        Last Submitted Date
      </TableCell>
    </TableRow>
  </TableHead>
);

const TableRowComponent: React.FC<{ request: ProjectDeveloperRequestsResponse }> = ({ request }) => {
  return (
    <TableRow key={request.id}>
      <TableCell>
        <Typography className="row-text" variant="body1">
          <a href={`/projects/${request.gsid}`} target="_blank" rel="noopener noreferrer">
            {request.gsid}
          </a>
        </Typography>
        {!!request.unreadMessages && <UnreadMessagesPill unreadMessages={request.unreadMessages} />}
      </TableCell>
      <TableCell sx={{ wordWrap: "break-word", whiteSpace: "normal" }}>
        <Typography variant="body1" className="row-text project-name">
          {request.projectName}
        </Typography>
      </TableCell>
      <TableCell>
        <Typography variant="body1" className="row-text">
          <a href={`/projects/${request.gsid}/requests?expand=${request.id}`} target="_blank" rel="noopener noreferrer">
            {request.requestName}
          </a>
        </Typography>
      </TableCell>
      <TableCell>
        <Typography variant="body1" className="row-text">
          {request.vvbAccountName}
        </Typography>
      </TableCell>
      <TableCell>
        <Typography variant="body1" className="row-text">
          {request.batched ? "Yes" : "No"}
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
          {request.currentRequestStatus.description}
        </Typography>
      </TableCell>
      <TableCell>
        <Typography variant="body1" className="row-text">
          {request.projectTypes.map((type: ReferenceType) => type.description).join(", ") || ""}
        </Typography>
      </TableCell>
      <TableCell>
        <Typography variant="body1" className="row-text">
          {request.projectLocations.map((type: ReferenceType) => type.description).join(", ") || ""}
        </Typography>
      </TableCell>
      <TableCell>
        <Typography variant="body1" className="row-text">
          {request.carbonStream?.description}
        </Typography>
      </TableCell>
      <TableCell>
        <Typography variant="body1" className="row-text">
          {request.requiredDocumentStatus.provided}/{request.requiredDocumentStatus.required}
        </Typography>
      </TableCell>
      <TableCell>
        <Typography variant="body1" className="row-text">
          {request?.invoiceStatus?.description || ""}
        </Typography>
      </TableCell>
      <TableCell className="row-text">
        <Typography variant="body1" className="row-text">
          {request.lastRequestStatusChangeDate && dayjs.utc(request.lastRequestStatusChangeDate).format("DD/MM/YYYY")}
        </Typography>
      </TableCell>
    </TableRow>
  );
};

const ProjectDeveloperRequestsTable: React.FC<ProjectDeveloperRequestsTableProps> = ({ filters }) => {
  const [requests, setRequests] = useState<ProjectDeveloperRequestsResponse[]>();
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [dateSort, setDateSort] = useState<string>("lastRequestStatusChangeDate,desc");

  useEffect(() => {
    const fetchRequests = async () => {
      try {
        const params = {
          requestStatusIds: filters.requestStatusIds,
          projectSearch: filters.projectSearch,
        };
        const response = await getProjectDeveloperRequests(params, currentPage, dateSort);
        if (response) {
          setRequests(response.content);
          setTotalPages(response.totalPages);
          setIsLoading(false);
        }
      } catch (error) {
        console.error("Error fetching requests:", error);
      }
    };
    fetchRequests();
  }, [currentPage, filters, dateSort]);

  const handleSortClick = (field: string) => {
    if (dateSort && dateSort === `${field},desc`) {
      setDateSort(`${field},asc`);
    } else {
      setDateSort(`${field},desc`);
    }
  };

  return isLoading ? (
    <CircularProgress />
  ) : (
    <div className="request-table">
      {requests && requests.length > 0 ? (
        <>
          <TableContainer>
            <Table>
              <TableHeader onClickSorting={handleSortClick} />
              <TableBody>
                {requests.map((request) => (
                  <TableRowComponent key={request.id} request={request} />
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
        <p>No requests found</p>
      )}
    </div>
  );
};

export default ProjectDeveloperRequestsTable;
