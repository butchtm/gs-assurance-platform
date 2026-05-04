import { Button } from "@mui/material";
import { useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { Helmet } from "react-helmet-async";

import { useReferenceTypes } from "../../common/data-types/DataReferenceTypes";
import { IsActiveCell } from "../../common/filterable-sortable-table/cell/IsActiveCell";
import { PlatformAccountTypeCell } from "../../common/filterable-sortable-table/cell/PlatformAccountTypeCell";
import { FilterableSortableTable } from "../../common/filterable-sortable-table/FilterableSortableTable";
import { Filter } from "../../common/filterable-sortable-table/model/Filter";
import { AuthContext } from "../Auth/Login/AuthProvider";
import { PaginationState } from "../Reusable/Pagination";
import "../Reusable/Pagination.css";

import { CreateAccountModal } from "./Modal/CreateAccountModal";
import { AccountFilters, getAccounts } from "./PlatformAccount";
import { PlatformAccount } from "./PlatformAccountTypes";

export const PlatformAccountsList: React.FC = () => {
  const [accounts, setAccounts] = useState<PlatformAccount[]>([]);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isLoadingAccounts, setIsLoadingAccounts] = useState(true);
  const [filters, setFilters] = useState<Filter[]>([]);
  const [paginationState, setPaginationState] = useState<PaginationState>({
    pageIndex: 0,
    pageSize: 10,
  });
  const [rowCount, setRowCount] = useState(0);
  const navigate = useNavigate();
  const referenceTypes = useReferenceTypes();
  const authContext = useContext(AuthContext);

  const handleCloseEditModal = () => {
    setIsEditModalOpen(false);
    refreshAccounts();
  };

  const handleOpenAccountView = (accountId: number) => {
    navigate(`/account/${accountId}`);
  };

  const getFilterValue = (fieldName: string): unknown => {
    const filterValue = filters.find((f) => f.field === fieldName)?.value;
    if (filterValue === undefined || filterValue === null) {
      return undefined;
    }
    return filterValue;
  };

  const buildAccountFilters = () => {
    const filters: AccountFilters = {};
    const accountTypeIds = getFilterValue("accountType.id");
    if (accountTypeIds !== undefined) {
      filters.accountTypeIds = accountTypeIds as number[];
    }
    const name = getFilterValue("name");
    if (name) {
      filters.name = name as string;
    }
    const active = getFilterValue("isActive");
    if (active !== undefined) {
      filters.active = active as boolean;
    }
    return filters;
  };

  const refreshAccounts = async () => {
    setIsLoadingAccounts(true);
    try {
      const fetchedAccounts = await getAccounts(
        buildAccountFilters(),
        paginationState.pageIndex + 1,
        paginationState.pageSize,
      );
      setAccounts(fetchedAccounts.content);
      setRowCount(fetchedAccounts.totalElements);
    } catch (error) {
      console.error("Failed to fetch accounts:", error);
      toast.error("Failed to fetch accounts");
      throw error;
    }
    setIsLoadingAccounts(false);
  };

  useEffect(() => {
    refreshAccounts();
  }, [paginationState.pageIndex, paginationState.pageSize, filters]);

  const buildDropdownOptions = () =>
    referenceTypes.activeReferenceTypes.AccountType.map((accountType) => ({
      label: accountType.description,
      value: String(accountType.id),
    }));

  return (
    <>
      <Helmet>
        <title>Platform Accounts</title>
      </Helmet>
      <FilterableSortableTable
        columns={[
          {
            accessorKey: "id",
            header: "ID",
            enableColumnFilter: false,
          },
          {
            accessorKey: "accountType.id",
            header: "Type",
            filterVariant: "select",
            filterSelectOptions: buildDropdownOptions(),
            enableColumnFilter: true,
            Cell: PlatformAccountTypeCell,
          },
          {
            accessorKey: "name",
            header: "Name",
          },
          {
            accessorFn: (row) => row.users?.length || 0,
            header: "Users (count)",
            enableColumnFilter: false,
          },
          {
            accessorFn: (row) => Boolean(row.active),
            accessorKey: "isActive",
            header: "Active",
            filterVariant: "checkbox",
            Cell: IsActiveCell,
          },
        ]}
        data={accounts}
        topToolbarActions={
          authContext.isAdminUser() && (
            <Button key="Create" onClick={() => setIsEditModalOpen(true)}>
              Create
            </Button>
          )
        }
        onPaginationChange={setPaginationState}
        onFilterUpdate={setFilters}
        enablePagination={true}
        enableFilters={true}
        onRowClick={(event, row) => handleOpenAccountView(row.getValue<number>("id"))}
        rowCount={rowCount}
        state={{
          pagination: paginationState,
          isLoading: isLoadingAccounts,
        }}
      />
      <CreateAccountModal isOpen={isEditModalOpen} onClose={handleCloseEditModal} />
    </>
  );
};
