import HelpOutlineIcon from "@mui/icons-material/HelpOutline";
import {
  Box,
  CircularProgress,
  Divider,
  FormControlLabel,
  IconButton,
  Switch,
  Tooltip,
  Typography,
} from "@mui/material";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import dayjs, { Dayjs } from "dayjs";
import { MRT_ColumnDef } from "material-react-table";
import { useEffect, useState } from "react";

import { FilterableSortableTable } from "../../../common/filterable-sortable-table/FilterableSortableTable";
import { PermissionGroup } from "../../../PermissionGroup";
import ProtectedComponent from "../../../ProtectedComponent";
import { PaginationState } from "../../Reusable/Pagination";

import { AssignmentSummary, AssignmentSummaryFilters, getRoeAssignmentSummaryResponse } from "./Assignment";
import { AssignmentSummaryTotals } from "./AssignmentSummaryTotals";

const TOOLTIP_DUE_REQUESTS =
  "This is the number of requests that are coming to the end of their review activity. This is defaulted to the next 10 working days but can be updated using the 'Due Before' date picker";
const TOOLTIP_OVERDUE_REQUESTS =
  "This is the number of requests that should have completed their review activity already. This is not impacted by the 'Due Before' date picker";

const RequestsDueHeader = () => (
  <Box sx={{ display: "flex", alignItems: "center", gap: 0.5 }}>
    <span>Requests Due</span>
    <Tooltip title={TOOLTIP_DUE_REQUESTS}>
      <IconButton size="small" sx={{ padding: 0 }}>
        <HelpOutlineIcon fontSize="small" />
      </IconButton>
    </Tooltip>
  </Box>
);

const OverdueRequestsHeader = () => (
  <Box sx={{ display: "flex", alignItems: "center", gap: 0.5 }}>
    <span>Overdue Requests</span>
    <Tooltip title={TOOLTIP_OVERDUE_REQUESTS}>
      <IconButton size="small" sx={{ padding: 0 }}>
        <HelpOutlineIcon fontSize="small" />
      </IconButton>
    </Tooltip>
  </Box>
);

export const AssignmentSummaryRoETab = ({
  filters,
  onFilterChange,
}: {
  filters: AssignmentSummaryFilters;
  onFilterChange: (filters: AssignmentSummaryFilters) => void;
}) => {
  const [isLoading, setIsLoading] = useState<boolean>(true);

  const [refresh, setRefresh] = useState(false);
  const [assignmentSummary, setAssignmentSummary] = useState<AssignmentSummary[]>([]);
  const [assignmentSummaryTotals, setAssignmentSummaryTotals] = useState<AssignmentSummary>();
  const [paginationState, setPaginationState] = useState<PaginationState>({
    pageIndex: 0,
    pageSize: 5,
  });
  const [rowCount, setRowCount] = useState(0);

  const setLastSubmittedAfter = (date: Dayjs | null) => {
    filters.lastSubmittedAfter = date ? date.utc().startOf("day") : undefined;
    onFilterChange(filters);
    setRefresh(!refresh);
  };

  const setLastSubmittedBefore = (date: Dayjs | null) => {
    filters.lastSubmittedBefore = date ? date.utc().endOf("day") : undefined;
    onFilterChange(filters);
    setRefresh(!refresh);
  };

  const setQaqcAssignedToCallingAccount = (assigned: boolean) => {
    filters.qaqcAssignedToCallingAccount = assigned;
    onFilterChange(filters);
    setRefresh(!refresh);
  };

  const setRequestDueBefore = (date: Dayjs | null) => {
    filters.requestDueBefore = date ? date.utc().startOf("day") : undefined;
    onFilterChange(filters);
    setRefresh(!refresh);
  };

  useEffect(() => {
    const fetchAssignmentSummary = async () => {
      setIsLoading(true);
      try {
        const response = await getRoeAssignmentSummaryResponse(
          filters,
          paginationState.pageIndex + 1,
          paginationState.pageSize,
        );

        setAssignmentSummary(response.assignmentSummary.content);
        setRowCount(response.assignmentSummary.totalElements);
        setAssignmentSummaryTotals(response.assignmentTotals);
      } catch (error) {
        console.error("Failed to fetch assignment summary from API", error);
        throw error;
      } finally {
        setIsLoading(false);
      }
    };
    fetchAssignmentSummary();
  }, [refresh, paginationState.pageIndex, paginationState.pageSize]);

  const getColumns = (): MRT_ColumnDef<AssignmentSummary, unknown>[] => {
    return [
      {
        accessorKey: "accountName",
        header: "Name",
        enableColumnFilter: false,
        enableSorting: false,
      },
      {
        accessorKey: "dueRequests",
        header: "Requests Due",
        enableColumnFilter: false,
        enableSorting: false,
        Header: RequestsDueHeader,
      },
      {
        accessorKey: "overdueRequests",
        header: "Overdue Requests",
        enableColumnFilter: false,
        enableSorting: false,
        Header: OverdueRequestsHeader,
      },
      {
        accessorKey: "roundOneRequests",
        header: "Round 1",
        enableColumnFilter: false,
        enableSorting: false,
      },
      {
        accessorKey: "inProgressRequests",
        header: "Active",
        enableColumnFilter: false,
        enableSorting: false,
      },
      {
        accessorKey: "openRequests",
        header: "Open",
        enableColumnFilter: false,
        enableSorting: false,
      },
      {
        accessorKey: "closedRequests",
        header: "Closed",
        enableColumnFilter: false,
        enableSorting: false,
      },
    ];
  };

  const totalItems = [
    { description: "Due", value: assignmentSummaryTotals?.dueRequests || 0 },
    { description: "Overdue", value: assignmentSummaryTotals?.overdueRequests || 0 },
    { description: "Round 1", value: assignmentSummaryTotals?.roundOneRequests || 0 },
    { description: "Active", value: assignmentSummaryTotals?.inProgressRequests || 0 },
    { description: "Open", value: assignmentSummaryTotals?.openRequests || 0 },
    { description: "Closed", value: assignmentSummaryTotals?.closedRequests || 0 },
  ];

  return (
    <Box sx={{ textAlign: "center", minHeight: "350px" }}>
      {assignmentSummaryTotals && (
        <Box
          sx={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "flex-start",
            mb: 3,
            mt: 3,
            flexWrap: "wrap",
            gap: 2,
          }}
        >
          <Box sx={{ flex: 1 }}>
            {isLoading ? <CircularProgress color="primary" /> : <AssignmentSummaryTotals items={totalItems} />}
          </Box>

          <Divider orientation="vertical" flexItem sx={{ mx: 2 }} />

          <Box sx={{ display: "flex", flexDirection: "column", gap: 1, flex: 1, minWidth: 200 }}>
            <Typography sx={{ textAlign: "start" }}>Filters</Typography>
            <LocalizationProvider dateAdapter={AdapterDayjs}>
              <Box sx={{ display: "flex", flexDirection: "column", gap: 1 }}>
                <DatePicker
                  label="Due Before"
                  value={filters.requestDueBefore}
                  onChange={setRequestDueBefore}
                  minDate={dayjs().utc()}
                  format="DD/MM/YYYY"
                  timezone="UTC"
                  slotProps={{
                    textField: {
                      size: "small",
                      placeholder: "Select date",
                      fullWidth: true,
                    },
                    field: { clearable: true },
                  }}
                />
              </Box>
              <Box sx={{ display: "flex", flexDirection: "column", gap: 1 }}>
                <DatePicker
                  label="Last Submitted After"
                  value={filters.lastSubmittedAfter}
                  onChange={setLastSubmittedAfter}
                  format="DD/MM/YYYY"
                  slotProps={{
                    textField: {
                      size: "small",
                      placeholder: "Select date",
                      fullWidth: true,
                    },
                    field: { clearable: true },
                  }}
                  data-testid="last-submitted-after-picker"
                />
                <DatePicker
                  label="Last Submitted Before"
                  value={filters.lastSubmittedBefore}
                  onChange={setLastSubmittedBefore}
                  format="DD/MM/YYYY"
                  slotProps={{
                    textField: {
                      size: "small",
                      placeholder: "Select date",
                      fullWidth: true,
                    },
                    field: { clearable: true },
                  }}
                  data-testid="last-submitted-before-picker"
                />

                <ProtectedComponent roles={[PermissionGroup.ARM_TEAM]}>
                  <FormControlLabel
                    control={
                      <Switch
                        checked={filters.qaqcAssignedToCallingAccount}
                        onChange={(e) => setQaqcAssignedToCallingAccount(e.target.checked)}
                        size="small"
                        data-testid="qaqc-assigned-to-calling-user-switch"
                      />
                    }
                    label="QA/QC Assigned to Me"
                    data-testid="qaqc-assigned-to-calling-user-label"
                  />
                </ProtectedComponent>
              </Box>
            </LocalizationProvider>
          </Box>
        </Box>
      )}
      <Box data-testid="summary-table">
        <FilterableSortableTable
          columns={getColumns()}
          data={assignmentSummary}
          rowCount={rowCount}
          enablePagination={true}
          onPaginationChange={setPaginationState}
          state={{
            pagination: paginationState,
            isLoading: isLoading,
          }}
          styleOverride={{ margin: "8px 0 0 0" }}
          muiTableBodyCellProps={{
            sx: {
              justifyContent: "space-between",
            },
          }}
        />
      </Box>
    </Box>
  );
};
