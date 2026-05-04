import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import dayjs from "dayjs";
import { DropdownOption, MRT_SortingState } from "material-react-table";
import { useContext, useEffect, useState } from "react";
import { toast } from "react-toastify";
import { Helmet } from "react-helmet-async";

import { useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";
import { BoldTextCell } from "../../../common/filterable-sortable-table/cell/BoldTextCell";
import { GsidLinkCell } from "../../../common/filterable-sortable-table/cell/GsidLinkCell";
import { FilterableSortableTable } from "../../../common/filterable-sortable-table/FilterableSortableTable";
import { Filter } from "../../../common/filterable-sortable-table/model/Filter";
import { QueryOperator } from "../../../common/filterable-sortable-table/model/QueryOperator";
import { AuthContext } from "../../../components/Auth/Login/AuthProvider";
import { PaginationState } from "../../Reusable/Pagination";
import { CommonDashboardRequestResponse, exportCommonDashboardReport, getCommonDashboardRequests } from "../Requests";
import {
  buildDropdownOptionsWithTypes,
  buildInvoiceStatusDropdownOptions,
  createRequestNameLinkCell,
  formatDate,
} from "../reusable/TableFunctions";

import { ConfirmExportDialog } from "./ConfirmExportDialog";
import { ExportButton } from "./ExportButton";

export const CommonDashboardTable = () => {
  const getDefaultDateFilter = () => {
    return [
      dayjs.utc().subtract(1, "month").startOf("month"),
      dayjs.utc().subtract(1, "month").endOf("month").endOf("day"),
    ];
  };

  const [requests, setRequests] = useState<CommonDashboardRequestResponse[]>([]);
  const [globalFilter, setGlobalFilter] = useState("");
  const [isLoadingRequests, setIsLoadingRequests] = useState<boolean>(true);
  const [filters, setFilters] = useState<Filter[]>([
    {
      field: "firstRoundStartDate",
      queryOperator: QueryOperator.BETWEEN,
      value: undefined,
      values: getDefaultDateFilter(),
    },
  ]);
  const [paginationState, setPaginationState] = useState<PaginationState>({
    pageIndex: 0,
    pageSize: 10,
  });
  const [sortingState, setSortingState] = useState<MRT_SortingState>([]);
  const [rowCount, setRowCount] = useState(0);
  const [exportDialogOpen, setExportDialogOpen] = useState(false);
  const referenceTypes = useReferenceTypes();
  const authContext = useContext(AuthContext);

  const getMultiSelectFilterSelectedValues = <T,>(fieldName: string) => {
    return filters.find((filter) => filter.field == fieldName)?.values as T[] | [];
  };

  interface DateRangeFilterValues {
    start: string | undefined;
    end: string | undefined;
  }

  const getDateRangeSelectFilterSelectedValues = (fieldName: string): DateRangeFilterValues | undefined => {
    const values = filters.find((filter) => filter.field == fieldName)?.values as string[] | [];
    if (!values || values.length < 2) {
      return undefined;
    }
    return {
      start: values[0] ? dayjs.utc(values[0]).startOf("day").toISOString() : undefined,
      end: values[1] ? dayjs.utc(values[1]).endOf("day").toISOString() : undefined,
    };
  };

  const buildParams = () => {
    const firstRoundStartDateFilterValues = getDateRangeSelectFilterSelectedValues("firstRoundStartDate");
    return {
      requestStatusIds: getMultiSelectFilterSelectedValues<number>("requestStatus.description"),
      requestTypeIds: getMultiSelectFilterSelectedValues<number>("name"),
      roundOfReviewCounts: getMultiSelectFilterSelectedValues<number>("currentRoundIndex"),
      projectClassificationIds: getMultiSelectFilterSelectedValues<number>("project.classification.description"),
      projectTypeIds: getMultiSelectFilterSelectedValues<number>("projectTypesString"),
      invoiceStatusIds: getMultiSelectFilterSelectedValues<number>("invoice.status.description"),
      firstRoundDateStart: firstRoundStartDateFilterValues?.start,
      firstRoundDateEnd: firstRoundStartDateFilterValues?.end,
      projectSearch: globalFilter,
    };
  };

  useEffect(() => {
    const fetchRequests = async () => {
      setIsLoadingRequests(true);

      try {
        const response = await getCommonDashboardRequests(
          buildParams(),
          paginationState.pageIndex + 1,
          paginationState.pageSize,
          sortingState,
        );
        setRequests(response.content);
        setRowCount(response.totalElements);
      } catch (error) {
        toast.error("Failed to fetch requests. Please try again later.");
        console.error("Error fetching requests:", error);
      } finally {
        setIsLoadingRequests(false);
      }
    };
    fetchRequests();
  }, [paginationState.pageIndex, paginationState.pageSize, filters, globalFilter, sortingState]);

  const buildDropdownOptions = (typeName: string): DropdownOption[] => {
    return buildDropdownOptionsWithTypes(referenceTypes, typeName);
  };

  const buildRoundOfReviewCountOptions = (): DropdownOption[] => {
    return [...Array(10).keys()]
      .map((i) => i + 1)
      .map((i) => ({
        value: i,
        label: i.toString(),
      }));
  };

  const exportToExcel = async () => {
    try {
      await exportCommonDashboardReport(buildParams(), sortingState);
      setExportDialogOpen(true);
    } catch (_error) {
      toast.error("Failed to export request report. Please try again later.");
    }
  };

  return (
    <LocalizationProvider dateAdapter={AdapterDayjs}>
      <Helmet>
        <title>Dashboard</title>
      </Helmet>
      <FilterableSortableTable
        columns={[
          {
            accessorKey: "project.gsid",
            header: "GSID",
            id: "projectGsid",
            enableColumnFilter: false,
            enableSorting: false,
            Cell: GsidLinkCell,
          },
          {
            accessorKey: "project.name",
            header: "Project Name",
            enableColumnFilter: false,
            enableSorting: false,
            Cell: BoldTextCell,
          },
          {
            accessorKey: "requestStatus.description",
            header: "Request Status",
            enableColumnFilter: true,
            enableSorting: true,
            filterVariant: "multi-select",
            filterSelectOptions: buildDropdownOptions("RequestStatus"),
          },
          {
            accessorKey: "name",
            header: "Request Type",
            enableColumnFilter: true,
            enableSorting: true,
            Cell: createRequestNameLinkCell(
              (row) => row.project.gsid,
              (row) => row.id,
            ),
            muiTableHeadCellProps: {
              children: "Request",
            },
            filterVariant: "multi-select",
            filterSelectOptions: buildDropdownOptions("RequestType"),
          },
          {
            accessorKey: "project.poaGsid",
            header: "POA GSID",
            enableColumnFilter: false,
            enableSorting: false,
          },
          {
            accessorKey: "project.realCaseVpaGsid",
            header: "Real Case GSID",
            enableColumnFilter: false,
            enableSorting: false,
          },
          {
            accessorKey: "firstRoundStartDate",
            header: "First RoR Start Date",
            enableColumnFilter: true,
            enableSorting: true,
            accessorFn: (row) => formatDate(row.firstRoundStartDate),
            filterVariant: "date-range",
            muiFilterDatePickerProps: {
              format: "DD/MM/YYYY",
              timezone: "UTC",
            },
          },
          {
            accessorKey: "startDate",
            header: "Request Start Date",
            id: "requestStartDate",
            enableColumnFilter: false,
            enableSorting: true,
            accessorFn: (row) => formatDate(row.startDate),
          },
          {
            accessorKey: "currentRoundEndDate",
            header: "Current RoR End Date",
            enableColumnFilter: false,
            enableSorting: true,
            accessorFn: (row) => formatDate(row.currentRoundEndDate),
          },
          {
            accessorKey: "currentRoundIndex",
            header: "RoR Count",
            enableColumnFilter: true,
            enableSorting: true,
            filterVariant: "multi-select",
            filterSelectOptions: buildRoundOfReviewCountOptions(),
          },
          {
            accessorKey: "closedDate",
            header: "Request Closed Date",
            id: "requestEndDate",
            enableColumnFilter: false,
            enableSorting: true,
            accessorFn: (row) => formatDate(row.closedDate),
          },
          {
            accessorKey: "project.classification.description",
            header: "Project Classification",
            enableColumnFilter: true,
            enableSorting: false,
            filterVariant: "multi-select",
            filterSelectOptions: buildDropdownOptions("ProjectClassification"),
          },
          {
            accessorKey: "projectTypesString",
            header: "Project Types",
            enableSorting: true,
            enableColumnFilter: true,
            filterVariant: "multi-select",
            filterSelectOptions: buildDropdownOptions("ProjectType"),
          },
          {
            accessorKey: "invoice.status.description",
            header: "Invoice Status",
            enableColumnFilter: true,
            enableSorting: false,
            filterVariant: "multi-select",
            filterSelectOptions: buildInvoiceStatusDropdownOptions(referenceTypes),
          },
          {
            accessorKey: "id",
            header: "Request ID",
            id: "requestId",
            enableColumnFilter: false,
            enableSorting: true,
          },
          {
            accessorKey: "project.feeSchedule.description",
            header: "Project Fee Schedule",
            enableColumnFilter: false,
            enableSorting: false,
            accessorFn: (row) => row.project.feeSchedule?.description,
          },
          {
            accessorKey: "monitoringPeriod.feeSchedule.description",
            header: "Monitoring Period Fee Schedule",
            enableColumnFilter: false,
            enableSorting: false,
            accessorFn: (row) => row.monitoringPeriod?.feeSchedule?.description,
          },
          {
            accessorKey: "project.accountName",
            header: "Account Name",
            enableColumnFilter: false,
            enableSorting: false,
          },
        ]}
        data={requests}
        onPaginationChange={setPaginationState}
        enableFilters={true}
        onFilterUpdate={setFilters}
        enablePagination={true}
        rowCount={rowCount}
        enableGlobalFilter={true}
        onGlobalFilterChange={setGlobalFilter}
        globalSearchPlaceholder="Name or Exact GSID"
        enableSorting={true}
        manualSorting={false}
        onSortingChange={setSortingState}
        initialColumnFilters={filters.map((filter) => ({ id: filter.field, value: filter.value ?? filter.values }))}
        state={{
          pagination: paginationState,
          isLoading: isLoadingRequests,
          sorting: sortingState,
        }}
        topToolbarActions={<ExportButton onClick={() => exportToExcel()} userEmail={authContext.user?.emailAddress} />}
      />
      <ConfirmExportDialog
        isOpen={exportDialogOpen}
        onClose={() => setExportDialogOpen(false)}
        userEmail={authContext.user?.emailAddress || "Email not configured"}
      />
    </LocalizationProvider>
  );
};
