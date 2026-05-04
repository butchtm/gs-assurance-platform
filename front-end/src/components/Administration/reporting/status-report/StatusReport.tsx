import { AccountTree } from "@mui/icons-material";
import DownloadIcon from "@mui/icons-material/Download";
import { Box, Button, Grid } from "@mui/material";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import dayjs, { Dayjs } from "dayjs";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";

import { FilterableSortableTable } from "../../../../common/filterable-sortable-table/FilterableSortableTable";
import InformationBlock from "../../../Reusable/Card/InformationBlock";

import { StatCard } from "./StatCard";
import {
  StatusReportProjectGroup,
  StatusReportRequestGroup,
  exportStatusReportExcel,
  getStatusReport,
} from "./StatusReportApi";

export const StatusReport = () => {
  const [isLoading, setIsLoading] = useState(true);
  const [projects, setProjects] = useState<StatusReportProjectGroup[]>([]);
  const [requests, setRequests] = useState<StatusReportRequestGroup[]>([]);
  const [requestsCompletedCount, setRequestsCompletedCount] = useState(0);
  const [successfulCompletenessChecksCount, setSuccessfulCompletenessChecksCount] = useState(0);
  const [startDate, setStartDate] = useState<Dayjs | null>(dayjs().subtract(30, "day"));
  const [endDate, setEndDate] = useState<Dayjs | null>(dayjs());

  const fetchStatusReport = async () => {
    setIsLoading(true);
    const startDateString = startDate ? startDate.utc().startOf("day").toISOString() : undefined;
    const endDateString = endDate ? endDate.utc().endOf("day").toISOString() : undefined;
    const statusReportResponse = await getStatusReport(startDateString, endDateString);
    if (statusReportResponse) {
      setProjects(statusReportResponse.projects);
      setRequests(statusReportResponse.requests);
      setRequestsCompletedCount(statusReportResponse.allRequests.completedCount);
      setSuccessfulCompletenessChecksCount(statusReportResponse.allRequests.successfulCompletenessChecksCount);
    }
    setIsLoading(false);
  };

  const exportToExcel = async () => {
    try {
      const startDateString = startDate ? startDate.utc().startOf("day").toISOString() : undefined;
      const endDateString = endDate ? endDate.utc().endOf("day").toISOString() : undefined;
      await exportStatusReportExcel(startDateString, endDateString);
      toast.success("Status report exported successfully!");
    } catch (error) {
      toast.error("Failed to export status report. Please try again later.");
      console.error("Error exporting status report:", error);
    }
  };

  useEffect(() => {
    fetchStatusReport();
  }, [startDate, endDate]);

  return (
    <LocalizationProvider dateAdapter={AdapterDayjs}>
      <Box sx={{ display: "flex", justifyContent: "flex-end", marginBottom: 2 }}>
        <Button data-testid="export-button" variant="contained" onClick={exportToExcel} endIcon={<DownloadIcon />}>
          Export
        </Button>
      </Box>
      <Grid container spacing={2}>
        <Grid item sm={12}>
          <InformationBlock icon={<AccountTree sx={{ fontSize: "13px" }} />} title="Filters"></InformationBlock>
        </Grid>
        <Grid item sm={12}>
          <Box>
            <DatePicker
              label="Start Date"
              value={startDate}
              onChange={setStartDate}
              format="DD/MM/YYYY"
              sx={{ minWidth: 200 }}
              slotProps={{
                textField: {
                  size: "small",
                },
              }}
            />
          </Box>

          <Box mt={2}>
            <DatePicker
              label="End Date"
              value={endDate}
              onChange={setEndDate}
              format="DD/MM/YYYY"
              minDate={startDate || undefined}
              sx={{ minWidth: 200 }}
              slotProps={{
                textField: {
                  size: "small",
                },
              }}
            />
          </Box>
        </Grid>
        <Grid item sm={12} md={12}>
          <InformationBlock icon={<AccountTree sx={{ fontSize: "13px" }} />} title="Summary">
            <Box sx={{ display: "flex", flexWrap: "wrap", justifyContent: "center" }}>
              <StatCard
                key={"completedRequests"}
                description={"Total Completed Requests"}
                value={requestsCompletedCount}
              />
              <StatCard
                key={"successChecks"}
                description={"Successful Completeness Checks"}
                value={successfulCompletenessChecksCount}
              />
            </Box>
          </InformationBlock>
        </Grid>
      </Grid>
      <br></br>
      <Grid container spacing={0}>
        <Grid item xs={6}>
          <InformationBlock icon={<AccountTree sx={{ fontSize: "13px" }} />} title="Current Projects">
            <FilterableSortableTable
              columns={[
                {
                  accessorKey: "description",
                  header: "Project Type",
                  enableColumnFilter: false,
                  enableSorting: false,
                },
                {
                  accessorKey: "count",
                  header: "Number of Projects",
                  enableColumnFilter: false,
                  enableSorting: false,
                },
              ]}
              data={projects}
              state={{
                isLoading: isLoading,
              }}
              enableFilters={false}
              enableGlobalFilter={false}
              enablePagination={false}
            />
          </InformationBlock>
        </Grid>
        <Grid item xs={6}>
          <InformationBlock icon={<AccountTree sx={{ fontSize: "13px" }} />} title="Completed Requests">
            <FilterableSortableTable
              columns={[
                {
                  accessorKey: "description",
                  header: "Request Type",
                  enableColumnFilter: false,
                  enableSorting: false,
                },
                {
                  accessorKey: "completedCount",
                  header: "Completed Requests",
                  enableColumnFilter: false,
                  enableSorting: false,
                },
              ]}
              data={requests}
              state={{
                isLoading: isLoading,
              }}
              enableFilters={false}
              enableGlobalFilter={false}
              enablePagination={false}
            />
          </InformationBlock>
        </Grid>
      </Grid>
    </LocalizationProvider>
  );
};
