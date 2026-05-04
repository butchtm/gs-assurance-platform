import { Button } from "@mui/material";
import dayjs from "dayjs";
import { DropdownOption, MRT_ColumnDef, MRT_SortingState } from "material-react-table";
import { useContext, useEffect, useState } from "react";
import { toast } from "react-toastify";
import { Helmet } from "react-helmet-async";

import { AccountTypeIds } from "../../../AccountType";
import { useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";
import { BoldTextCell } from "../../../common/filterable-sortable-table/cell/BoldTextCell";
import { DaysDifferenceCell } from "../../../common/filterable-sortable-table/cell/DaysDifferenceCell";
import { GsidWithRiskCell } from "../../../common/filterable-sortable-table/cell/GsidWithRiskCell";
import { FilterableSortableTable } from "../../../common/filterable-sortable-table/FilterableSortableTable";
import { Filter } from "../../../common/filterable-sortable-table/model/Filter";
import { AuthContext } from "../../Auth/Login/AuthProvider";
import { getAccounts } from "../../PlatformAccounts/PlatformAccount";
import { PlatformAccount } from "../../PlatformAccounts/PlatformAccountTypes";
import { PaginationState } from "../../Reusable/Pagination";
import { formatName } from "../../User/User";
import { AssignmentModal } from "../assignment/AssignmentModal";
import { AssignmentSummaryModal } from "../assignment/AssignmentSummaryModal";
import { AllRequestsResponse, getAllRequests, getRequestTypeSelectOptions } from "../Requests";
import { createOpenRequestAssignmentsCell, createRequestNameLinkCell } from "../reusable/TableFunctions";
import { QaQcAccountUnread } from "../../../common/filterable-sortable-table/cell/QaQcAccountUnread";

export const AllRequestsTable = () => {
  const [requests, setRequests] = useState<AllRequestsResponse[]>([]);
  const [allAccounts, setAllAccounts] = useState<PlatformAccount[]>([]);
  const [globalFilter, setGlobalFilter] = useState("");
  const [isLoadingRequests, setIsLoadingRequests] = useState<boolean>(true);
  const [filters, setFilters] = useState<Filter[]>([]);
  const [paginationState, setPaginationState] = useState<PaginationState>({
    pageIndex: 0,
    pageSize: 25,
  });
  const [sortingState, setSortingState] = useState<MRT_SortingState>([]);
  const [rowCount, setRowCount] = useState(0);
  const referenceTypes = useReferenceTypes();
  const [isAssignmentModalOpen, setIsAssignmentModalOpen] = useState(false);
  const [isSummaryModalOpen, setIsSummaryModalOpen] = useState(false);
  const [assignmentModalRequest, setAssignmentModalRequest] = useState<AllRequestsResponse>();
  const authContext = useContext(AuthContext);

  const handleOpenAssignmentModal = (request: AllRequestsResponse) => {
    setAssignmentModalRequest(request);
    setIsAssignmentModalOpen(true);
  };

  const handleSummaryModal = () => {
    setIsSummaryModalOpen((prev) => !prev);
  };

  const handleOpenSummaryModal = () => {
    setIsSummaryModalOpen(true);
  };

  const handleCloseAssignmentModal = () => {
    setIsAssignmentModalOpen(false);
  };

  const getMultiSelectFilterSelectedValues = <T,>(fieldName: string) =>
    filters.find((filter) => filter.field == fieldName)?.values as T[] | undefined;

  const formatLastSubmittedName = (request: AllRequestsResponse): string =>
    formatName(request.submittedByGivenName, request.submittedByFamilyName);

  useEffect(() => {
    const fetchRequests = async () => {
      setIsLoadingRequests(true);
      try {
        const params = {
          requestStatusIds: getMultiSelectFilterSelectedValues<number>("requestStatus"),
          requestTypeIds: getMultiSelectFilterSelectedValues<number>("requestName"),
          projectAccountIds: getMultiSelectFilterSelectedValues<number>("projectAccountName"),
          reviewerAccountIds: getMultiSelectFilterSelectedValues<number>("reviewerAccountName"),
          vvbAccountIds: getMultiSelectFilterSelectedValues<number>("vvbAccountName"),
          qaqcAccountIds: getMultiSelectFilterSelectedValues<number>("qaqcAccountName"),
          projectSearch: globalFilter,
        };

        const response = await getAllRequests(
          params,
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

  useEffect(() => {
    const fetchAllAccounts = async () => {
      const filters = {
        accountTypeIds: [
          AccountTypeIds.VVB,
          AccountTypeIds.PROJECT_DEVELOPER,
          AccountTypeIds.GS_REVIEWER,
          AccountTypeIds.ARM_TEAM,
        ],
      };
      const accounts = await getAccounts(filters);
      setAllAccounts(accounts.content);
    };

    fetchAllAccounts();
  }, []);

  const buildStatusOptions = (): DropdownOption[] =>
    referenceTypes.activeReferenceTypes.RequestStatus.map((status) => ({
      value: status.id,
      label: status.description,
    }));

  const getAccountOptions = (accountTypeIds: number[]): DropdownOption[] =>
    allAccounts
      .filter(
        (account) => typeof account.accountType?.id === "number" && accountTypeIds.includes(account.accountType.id),
      )
      .map((account) => ({ value: account.id, label: account.name }));

  const getRequestTypeOptions = (): DropdownOption[] => {
    return getRequestTypeSelectOptions(referenceTypes);
  };

  const formatDate = (date: string | undefined) => {
    if (date) {
      return dayjs.utc(date).format("DD/MM/YYYY");
    }
    return "";
  };

  const getColumns = () => {
    const columns: MRT_ColumnDef<AllRequestsResponse, unknown>[] = [
      {
        accessorKey: "gsid",
        header: "GSID",
        enableColumnFilter: false,
        enableSorting: false,
        Cell: GsidWithRiskCell,
      },
      {
        accessorKey: "projectName",
        header: "Project Name",
        enableColumnFilter: false,
        enableSorting: false,
        Cell: BoldTextCell,
      },
      {
        accessorKey: "projectAccountName",
        header: "Developer",
        enableColumnFilter: true,
        enableSorting: false,
        filterVariant: "multi-select",
        filterSelectOptions: getAccountOptions([AccountTypeIds.PROJECT_DEVELOPER]),
      },
      {
        accessorKey: "requestName",
        header: "Request Type",
        enableColumnFilter: true,
        enableSorting: false,
        filterVariant: "multi-select",
        filterSelectOptions: getRequestTypeOptions(),
        Cell: createRequestNameLinkCell(
          (row) => row.gsid,
          (row) => row.requestId,
        ),
        muiTableHeadCellProps: {
          children: "Request",
        },
      },
      {
        accessorKey: "batchSize",
        header: "Batch Size",
        enableColumnFilter: false,
        enableSorting: false,
      },
      {
        accessorKey: "requestStatus",
        header: "Request Status",
        enableColumnFilter: true,
        enableSorting: false,
        filterVariant: "multi-select",
        filterSelectOptions: buildStatusOptions(),
      },
      {
        accessorKey: "currentRoundIndex",
        header: "Round",
        enableColumnFilter: false,
        enableSorting: false,
      },
      {
        accessorKey: "requestRoundEndDate",
        accessorFn: (row) => row.currentRoundEndDate,
        header: "Round of Review End Date",
        enableColumnFilter: false,
        enableSorting: true,
        Cell: DaysDifferenceCell,
      },
      {
        accessorKey: "reviewerAccountName",
        header: "Reviewer",
        enableColumnFilter: true,
        enableSorting: false,
        filterVariant: "multi-select",
        filterSelectOptions: getAccountOptions([AccountTypeIds.GS_REVIEWER]),
      },
      {
        accessorKey: "vvbAccountName",
        header: "VVB",
        enableColumnFilter: true,
        enableSorting: false,
        filterVariant: "multi-select",
        filterSelectOptions: getAccountOptions([AccountTypeIds.VVB]),
      },
      {
        accessorKey: "qaqcAccountName",
        header: "QAQC",
        enableColumnFilter: true,
        enableSorting: false,
        Cell: QaQcAccountUnread,
        filterVariant: "multi-select",
        filterSelectOptions: getAccountOptions([AccountTypeIds.GS_REVIEWER, AccountTypeIds.ARM_TEAM]),
      },
      {
        accessorKey: "activityName",
        header: "Activity",
        enableColumnFilter: false,
        enableSorting: false,
      },
      {
        accessorKey: "latestActivityPlannedEndDate",
        header: "Weekdays Remaining",
        enableColumnFilter: false,
        enableSorting: true,
        accessorFn: (row) => row.plannedEndDate,
        Cell: DaysDifferenceCell,
      },
      {
        accessorKey: "responsibleAccountName",
        header: "Responsibility",
        enableColumnFilter: false,
        enableSorting: false,
      },
      {
        accessorKey: "submittedByName",
        header: "Last Submitted",
        enableColumnFilter: false,
        enableSorting: false,
        accessorFn: formatLastSubmittedName,
      },
      {
        accessorKey: "lastRequestStatusChangeDate",
        header: "Last Submitted Date",
        enableColumnFilter: false,
        enableSorting: true,
        accessorFn: (row) => formatDate(row.lastRequestStatusChangeDate),
      },
      {
        accessorKey: "invoiceStatus",
        header: "Invoice Status",
        enableColumnFilter: false,
        enableSorting: false,
      },
    ];
    if (authContext.isArmUser()) {
      columns.push({
        accessorKey: "assignments",
        header: "Assignments",
        enableColumnFilter: false,
        enableSorting: false,
        Cell: createOpenRequestAssignmentsCell((row) => handleOpenAssignmentModal(row)),
      });
    }
    return columns;
  };

  return (
    <>
      <Helmet>
        <title>All Requests</title>
      </Helmet>
      <FilterableSortableTable
        columns={getColumns()}
        data={requests}
        onPaginationChange={setPaginationState}
        enableFilters={true}
        onFilterUpdate={setFilters}
        enablePagination={true}
        rowCount={rowCount}
        enableGlobalFilter={true}
        onGlobalFilterChange={setGlobalFilter}
        enableSorting={true}
        manualSorting={true}
        onSortingChange={setSortingState}
        globalSearchPlaceholder="Name or Exact GSID"
        state={{
          pagination: paginationState,
          isLoading: isLoadingRequests,
          sorting: sortingState,
        }}
        topToolbarCustomActions={
          <Button onClick={handleSummaryModal} variant="contained" sx={{ mr: "12px" }}>
            Assignments Summary
          </Button>
        }
      />
      {authContext.isArmUser() && isAssignmentModalOpen && assignmentModalRequest && (
        <AssignmentModal
          readonlyOnOpen
          isOpen={isAssignmentModalOpen}
          onClose={handleCloseAssignmentModal}
          requestId={assignmentModalRequest.requestId}
          requestProjectName={assignmentModalRequest.projectName}
          requestTypeId={assignmentModalRequest.requestTypeId}
          requestTypeDescription={assignmentModalRequest.requestTypeDescription}
          requestProjectDeveloperName={assignmentModalRequest.projectAccountName}
          requestStatusId={assignmentModalRequest.requestStatusId}
          handleOpenSummaryModal={handleOpenSummaryModal}
        />
      )}
      {(authContext.isAdminUser() || authContext.isArmUser()) && isSummaryModalOpen && (
        <AssignmentSummaryModal isOpen={isSummaryModalOpen} onClose={handleSummaryModal} />
      )}
    </>
  );
};
