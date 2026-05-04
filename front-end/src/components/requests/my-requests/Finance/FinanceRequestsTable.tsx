import { useEffect, useState } from "react";
import { MRT_SortingState } from "material-react-table";
import { toast } from "react-toastify";

import { PaginationState } from "../../../Reusable/Pagination";
import { AssignedRequestsResponse, getAssignedRequests } from "../../Requests";
import { FilterableSortableTable } from "../../../../common/filterable-sortable-table/FilterableSortableTable";
import { GsidLinkCell } from "../../../../common/filterable-sortable-table/cell/GsidLinkCell";
import {
  buildInvoiceStatusDropdownOptions,
  convertMrtSortToSearchParam,
  createRequestNameLinkCell,
  formatDate,
} from "../../reusable/TableFunctions";
import { useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { Filter } from "../../../../common/filterable-sortable-table/model/Filter";

const FinanceRequestsTable = () => {
  const [globalFilter, setGlobalFilter] = useState("");
  const [requestAssignments, setRequestAssignments] = useState<AssignedRequestsResponse[]>([]);
  const [isLoadingRequests, setIsLoadingRequests] = useState<boolean>(true);
  const [filters, setFilters] = useState<Filter[]>([]);
  const [paginationState, setPaginationState] = useState<PaginationState>({
    pageIndex: 0,
    pageSize: 10,
  });
  const [sortingState, setSortingState] = useState<MRT_SortingState>([
    { id: "lastRequestStatusChangeDate", desc: true },
  ]);
  const [rowCount, setRowCount] = useState(0);
  const referenceTypes = useReferenceTypes();

  const getMultiSelectFilterSelectedValues = <T,>(fieldName: string) => {
    return filters?.find((filter) => filter.field == fieldName)?.values as T[] | [];
  };

  useEffect(() => {
    const fetchRequests = async () => {
      setIsLoadingRequests(true);

      try {
        const params = {
          invoiceStatusIds: getMultiSelectFilterSelectedValues<number>("invoiceStatus"),
          projectSearch: globalFilter,
        };
        const requestsData = await getAssignedRequests(
          params,
          paginationState.pageIndex + 1,
          convertMrtSortToSearchParam(sortingState),
          paginationState.pageSize,
        );
        setRequestAssignments(requestsData.content);
        setRowCount(requestsData.totalElements);
      } catch (error) {
        toast.error("Failed to fetch requests. Please try again later.");
        console.error("Error fetching requests:", error);
      } finally {
        setIsLoadingRequests(false);
      }
    };
    fetchRequests();
  }, [paginationState.pageIndex, paginationState.pageSize, filters, globalFilter, sortingState]);

  return (
    <FilterableSortableTable
      columns={[
        {
          accessorKey: "gsid",
          header: "GSID",
          id: "projectGsid",
          enableColumnFilter: false,
          enableSorting: false,
          Cell: GsidLinkCell,
        },
        {
          accessorKey: "projectName",
          header: "Project",
          enableColumnFilter: false,
          enableSorting: false,
        },
        {
          accessorKey: "requestName",
          header: "Request Name",
          enableColumnFilter: false,
          enableSorting: false,
          Cell: createRequestNameLinkCell(
            (row) => row.gsid,
            (row) => row.requestId,
          ),
        },
        {
          accessorKey: "projectAccountName",
          header: "Project Developer",
          enableColumnFilter: false,
          enableSorting: false,
        },
        {
          accessorKey: "assignmentCreatedDate",
          header: "Assigned Date",
          enableColumnFilter: false,
          enableSorting: true,
          accessorFn: (row) => formatDate(row.assignmentCreatedDate),
        },
        {
          accessorKey: "lastRequestStatusChangeDate",
          header: "Last Submitted Date",
          enableColumnFilter: false,
          enableSorting: true,
          accessorFn: (row) => formatDate(row.lastRequestStatusChangeDate),
        },
        {
          accessorKey: "estimatedAnnualEmissionReductionAmount",
          header: "Emissions Reduction Amount",
          enableColumnFilter: false,
          enableSorting: false,
        },
        {
          accessorKey: "projectSize.description",
          header: "Project Scale",
          enableColumnFilter: false,
          enableSorting: false,
        },
        {
          accessorKey: "projectCarbonStream.description",
          header: "Carbon Stream",
          enableColumnFilter: false,
          enableSorting: false,
        },
        {
          accessorKey: "invoiceStatus",
          header: "Invoice Status",
          enableColumnFilter: true,
          enableSorting: false,
          filterVariant: "multi-select",
          filterSelectOptions: buildInvoiceStatusDropdownOptions(referenceTypes),
        },
      ]}
      data={requestAssignments}
      onPaginationChange={setPaginationState}
      enableFilters={true}
      onFilterUpdate={setFilters}
      enablePagination={true}
      rowCount={rowCount}
      enableGlobalFilter={true}
      onGlobalFilterChange={setGlobalFilter}
      globalSearchPlaceholder="Name or Exact GSID"
      enableSorting={true}
      manualSorting={true}
      onSortingChange={setSortingState}
      initialColumnFilters={filters.map((filter) => ({ id: filter.field, value: filter.value ?? filter.values }))}
      state={{
        pagination: paginationState,
        isLoading: isLoadingRequests,
        sorting: sortingState,
      }}
    />
  );
};

export default FinanceRequestsTable;
