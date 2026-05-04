import HelpOutlineIcon from "@mui/icons-material/HelpOutline";
import { Box, CircularProgress, Divider, Grid, IconButton, Tooltip, Typography } from "@mui/material";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import dayjs, { Dayjs } from "dayjs";
import { DropdownOption, MRT_ColumnDef } from "material-react-table";
import { useEffect, useState } from "react";

import { FilterableSortableTable } from "../../../common/filterable-sortable-table/FilterableSortableTable";
import { PaginationState } from "../../Reusable/Pagination";
import { useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";
import { getRequestTypeSelectOptions } from "../Requests";
import { MultiSelectAutocomplete } from "../../../common/multi-select/MultiSelectAutoComplete";

import { AssignmentSummaryTotals } from "./AssignmentSummaryTotals";
import { AssignmentSummary, AssignmentSummaryFilters, getArmAssignmentSummaryResponse } from "./Assignment";

const TOOLTIP_UPCOMING_REQUESTS =
  "This is the number of requests that are coming to the end of their review activity. This is defaulted to the next 10 working days but can be updated using the 'Due Before' date picker";
const TOOLTIP_DUE_REQUESTS =
  "This is the number of requests that are due to complete their harmonisation of feedback activity soon. This is defaulted to the next 10 working days but can be updated using the 'Due Before' date picker";
const TOOLTIP_OVERDUE_REQUESTS =
  "This is the number of requests that should have completed their harmonisation of feedback activity already. This is not impacted by the 'Due Before' date picker";

const UpcomingRequestsHeader = () => (
  <Box sx={{ display: "flex", alignItems: "center", gap: 0.5 }}>
    <span>Upcoming Requests</span>
    <Tooltip title={TOOLTIP_UPCOMING_REQUESTS}>
      <IconButton size="small" sx={{ padding: 0 }}>
        <HelpOutlineIcon fontSize="small" />
      </IconButton>
    </Tooltip>
  </Box>
);

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

export const AssignmentSummaryArmTab = ({
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
  const referenceTypes = useReferenceTypes();
  const [selectedProjectTypes, setSelectedProjectTypes] = useState<DropdownOption[]>([]);
  const [selectedRequestTypes, setSelectedRequestTypes] = useState<DropdownOption[]>([]);

  const getRequestTypeOptions = (): DropdownOption[] => {
    return getRequestTypeSelectOptions(referenceTypes);
  };

  const getProjectTypeOptions = (): DropdownOption[] => {
    return referenceTypes.activeReferenceTypes.ProjectType.filter((projectType) => projectType.active).map(
      (projectType) => ({
        value: projectType.id,
        label: projectType.description,
      }),
    );
  };

  const getIdsFromFilterOptions = (selected: DropdownOption[]): Set<number> => {
    return new Set(
      selected
        .filter((option): option is { value: number } => typeof option !== "string")
        .map((option) => option.value),
    );
  };

  const handleProjectFiltersChange = (selected: DropdownOption[]) => {
    setSelectedProjectTypes(selected);
    filters.projectTypeIds = [...getIdsFromFilterOptions(selected)];
    setRefresh(true);
  };

  const handleRequestFiltersChange = (selected: DropdownOption[]) => {
    setSelectedRequestTypes(selected);
    filters.requestTypeIds = [...getIdsFromFilterOptions(selected)];
    setRefresh(true);
  };

  const getSelectedProjectOptions = (): DropdownOption[] => {
    return getProjectTypeOptions().filter(
      (option) => typeof option !== "string" && filters.projectTypeIds?.includes(option.value),
    );
  };

  const getSelectedRequestOptions = (): DropdownOption[] => {
    return getRequestTypeOptions().filter(
      (option) => typeof option !== "string" && filters.requestTypeIds?.includes(option.value),
    );
  };

  useEffect(() => {
    setRefresh(false);
    setSelectedProjectTypes(getSelectedProjectOptions());
    setSelectedRequestTypes(getSelectedRequestOptions());
    const fetchAssignmentSummary = async () => {
      setIsLoading(true);
      try {
        const response = await getArmAssignmentSummaryResponse(
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
  }, [paginationState.pageIndex, paginationState.pageSize, refresh]);

  const getColumns = (): MRT_ColumnDef<AssignmentSummary>[] => [
    {
      accessorKey: "accountName",
      header: "Name",
      enableColumnFilter: false,
      enableSorting: false,
    },
    {
      accessorKey: "upcomingRequests",
      header: "Upcoming Requests",
      enableColumnFilter: false,
      enableSorting: false,
      Header: UpcomingRequestsHeader,
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
  ];

  const handleEndDateChange = (date: Dayjs | null) => {
    if (date) {
      filters.requestDueBefore = date ? date.utc().startOf("day") : undefined;
      onFilterChange(filters);
      setRefresh(true);
    }
  };

  const totalItems = [
    { description: "Upcoming", value: assignmentSummaryTotals?.upcomingRequests || 0 },
    { description: "Due", value: assignmentSummaryTotals?.dueRequests || 0 },
    { description: "Overdue", value: assignmentSummaryTotals?.overdueRequests || 0 },
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
                  onChange={handleEndDateChange}
                  minDate={dayjs().utc()}
                  format="DD/MM/YYYY"
                  timezone="UTC"
                  slotProps={{ textField: { size: "small", fullWidth: true } }}
                />
              </Box>
            </LocalizationProvider>
            <Grid container spacing={1}>
              <Grid item>
                <MultiSelectAutocomplete
                  options={getProjectTypeOptions()}
                  label="Project Type"
                  selectedOptions={selectedProjectTypes}
                  onSelectionChange={handleProjectFiltersChange}
                />
              </Grid>
              <Grid item>
                <MultiSelectAutocomplete
                  options={getRequestTypeOptions()}
                  label="Request Type"
                  selectedOptions={selectedRequestTypes}
                  onSelectionChange={handleRequestFiltersChange}
                />
              </Grid>
            </Grid>
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
