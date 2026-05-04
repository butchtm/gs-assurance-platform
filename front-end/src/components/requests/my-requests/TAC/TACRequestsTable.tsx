import { CircularProgress, Table, TableCell, TableContainer, TableHead, TableRow } from "@mui/material";
import dayjs from "dayjs";
import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import Pagination from "../../../Reusable/Pagination";
import {
  getLiveConsultationsForRequest,
  getRequestsInConsultationByCompetencies,
  getRequestsInConsultationWithInfo,
  TacRequestsResponse,
} from "../../Requests";

interface TACRequestsTableProps {
  filterByCompetencies: boolean;
}

const TACRequestsTable: React.FC<TACRequestsTableProps> = ({ filterByCompetencies }) => {
  const [requests, setRequests] = useState<TacRequestsResponse[]>();
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [dateSort, setDateSort] = useState<string>("last_request_status_change_date,desc");

  function fetchData() {
    if (filterByCompetencies) {
      return getRequestsInConsultationByCompetencies(currentPage, dateSort);
    } else {
      return getRequestsInConsultationWithInfo(currentPage, dateSort);
    }
  }

  async function fetchRequests() {
    try {
      setLoading(true);
      const requestsResponse = await fetchData();
      setRequests(requestsResponse.data);
      setTotalPages(requestsResponse.totalPages);

      fetchAndUpdateConsultationInfo(requestsResponse.data);
      setLoading(false);
    } catch (error) {
      console.error("Error fetching requests:", error);
    }
  }

  async function fetchAndUpdateConsultationInfo(requestsToFetch: TacRequestsResponse[]) {
    const updatedRequests = await Promise.all(
      requestsToFetch.map(async (request) => {
        try {
          const liveConsultationInfo = await getLiveConsultationsForRequest(request.id);
          const tacConsultationInfo = Array.isArray(liveConsultationInfo)
            ? liveConsultationInfo.find((info) => info.type.userType === "TAC")
            : null;
          return {
            ...request,
            currentConsultationStartDate: tacConsultationInfo?.startDate,
            currentConsultationEndDate: tacConsultationInfo?.endDate,
          };
        } catch (error) {
          console.error(`Failed to fetch data for requestId ${request.id}`, error);
          return request;
        }
      }),
    );
    setRequests(updatedRequests);
  }

  useEffect(() => {
    fetchRequests();
  }, []);

  useEffect(() => {
    if (requests) {
      fetchRequests();
    }
  }, [currentPage]);

  useEffect(() => {
    if (requests) {
      if (currentPage != 1) {
        setCurrentPage(1);
      } else {
        fetchRequests();
      }
    }
  }, [filterByCompetencies, dateSort]);

  const onClickSorting = (field: string) => {
    if (dateSort && dateSort == `${field},desc`) {
      setDateSort(`${field},asc`);
    } else {
      setDateSort(`${field},desc`);
    }
  };

  let content;

  if (loading) {
    content = <CircularProgress color="primary" size={75} />;
  } else if (requests && requests.length > 0) {
    content = (
      <TableContainer>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell className="row-text gsid-col">GSID</TableCell>
              <TableCell className="row-text">Project Name</TableCell>
              <TableCell className="row-text">Request Name</TableCell>
              <TableCell className="row-text">Request Status</TableCell>
              <TableCell className="row-text">Assigned VVB</TableCell>
              <TableCell className="row-text">Consultation Start Date</TableCell>
              <TableCell className="row-text">Consultation End Date</TableCell>
              <TableCell
                className="row-text clickable"
                onClick={() => onClickSorting("last_request_status_change_date")}
              >
                Last Submitted Date
              </TableCell>
            </TableRow>
          </TableHead>
          <tbody>
            {requests.map((request) => (
              <TableRow key={request.id}>
                <TableCell className="row-text">
                  <Link to={`/projects/${request.projectGsid}`}>{request.projectGsid}</Link>
                </TableCell>
                <TableCell className="row-text">{request.projectName}</TableCell>
                <TableCell className="row-text">
                  <Link to={`/projects/${request.projectGsid}/requests?expand=${request.id}`}>
                    {request.requestName || ""}
                  </Link>
                </TableCell>
                <TableCell className="row-text">{request.requestStatus.description}</TableCell>
                <TableCell className="row-text">{request.vvbAccountName || "None Assigned"}</TableCell>
                <TableCell className="row-text">
                  {request.currentConsultationStartDate &&
                    dayjs.utc(request.currentConsultationStartDate).format("DD/MM/YYYY")}
                </TableCell>
                <TableCell className="row-text">
                  {request.currentConsultationEndDate &&
                    dayjs.utc(request.currentConsultationEndDate).format("DD/MM/YYYY")}
                </TableCell>
                <TableCell>
                  {request.lastRequestStatusChangeDate &&
                    dayjs.utc(request.lastRequestStatusChangeDate).format("DD/MM/YYYY")}
                </TableCell>
              </TableRow>
            ))}
          </tbody>
        </Table>
        <Pagination
          total={totalPages}
          current={currentPage}
          onChange={(newPage) => {
            setCurrentPage(newPage);
            window.scrollTo(0, 0);
          }}
        />
      </TableContainer>
    );
  } else {
    content = <p>No Requests found</p>;
  }

  return <div className="request-table">{content}</div>;
};

export default TACRequestsTable;
