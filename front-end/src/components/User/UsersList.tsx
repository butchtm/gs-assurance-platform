import { Button } from "@mui/material";
import { MRT_SortingState } from "material-react-table";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { Helmet } from "react-helmet-async";

import { IsActiveCell } from "../../common/filterable-sortable-table/cell/IsActiveCell";
import { MfaSettingsCell } from "../../common/filterable-sortable-table/cell/MfaSettingsCell";
import { PlatformAccountCell } from "../../common/filterable-sortable-table/cell/PlatformAccountCell";
import { FilterableSortableTable } from "../../common/filterable-sortable-table/FilterableSortableTable";
import { Filter } from "../../common/filterable-sortable-table/model/Filter";
import { PaginationState } from "../Reusable/Pagination";
import "../Reusable/Pagination.css";

import { BatchCreateUsersModalsManager } from "./BatchCreateUsers/BatchCreateUsersModalsManager";
import { formatName, getUsers, User } from "./User";

export const UsersList: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [isLoadingUsers, setIsLoadingUsers] = useState(true);
  const [globalFilter, setGlobalFilter] = useState("");
  const [filters, setFilters] = useState<Filter[]>([]);
  const [sortingState, setSortingState] = useState<MRT_SortingState>([]);
  const [rowCount, setRowCount] = useState(0);
  const [paginationState, setPaginationState] = useState<PaginationState>({
    pageIndex: 0,
    pageSize: 10,
  });
  const [isBatchCreatingUsers, setIsBatchCreatingUsers] = useState<boolean>(false);

  const fetchUsers = async () => {
    setIsLoadingUsers(true);
    const filterByActive = filters.find((filter) => filter.field == "isActive")?.value as boolean | undefined;
    try {
      const fetchedUsers = await getUsers(
        paginationState.pageIndex + 1,
        paginationState.pageSize,
        sortingState,
        globalFilter,
        filterByActive,
      );
      setUsers(fetchedUsers.content);
      setRowCount(fetchedUsers.totalElements);
    } catch (error) {
      console.error("Failed to fetch users:", error);
      toast.error("Failed to fetch users");
      throw error;
    }
    setIsLoadingUsers(false);
  };

  useEffect(() => {
    fetchUsers();
  }, [paginationState.pageIndex, paginationState.pageSize, filters, globalFilter, sortingState]);

  const userRowNameBuilder = (user: User) => {
    return formatName(user.givenName, user.familyName);
  };

  return (
    <>
      <Helmet>
        <title>Users</title>
      </Helmet>
      <FilterableSortableTable
        columns={[
          {
            accessorKey: "id",
            header: "ID",
            enableColumnFilter: false,
            enableSorting: true,
          },
          {
            accessorKey: "userName",
            header: "Username",
            enableColumnFilter: false,
          },
          {
            accessorFn: userRowNameBuilder,
            header: "Name",
            enableColumnFilter: false,
            enableSorting: false,
          },
          {
            accessorKey: "emailAddress",
            header: "Email",
            enableColumnFilter: false,
            enableSorting: false,
          },
          {
            accessorFn: (row) => row.groups.join(", "),
            accessorKey: "groups",
            header: "Role",
            enableColumnFilter: false,
            enableSorting: false,
          },
          {
            accessorKey: "isActive",
            header: "Active",
            filterVariant: "checkbox",
            Cell: IsActiveCell,
            enableSorting: false,
          },
          {
            accessorKey: "account",
            header: "Account",
            enableColumnFilter: false,
            enableSorting: false,
            Cell: PlatformAccountCell,
          },
          {
            accessorKey: "settings",
            header: "Settings",
            enableColumnFilter: false,
            enableSorting: false,
            Cell: MfaSettingsCell,
          },
        ]}
        data={users}
        topToolbarActions={
          <Button key="Batch Create Users" onClick={() => setIsBatchCreatingUsers(true)}>
            Batch Create Users
          </Button>
        }
        onPaginationChange={setPaginationState}
        onFilterUpdate={setFilters}
        enablePagination={true}
        enableFilters={true}
        enableSorting={true}
        manualSorting={true}
        onSortingChange={setSortingState}
        enableGlobalFilter={true}
        onGlobalFilterChange={setGlobalFilter}
        globalSearchPlaceholder="Username or Email"
        rowCount={rowCount}
        state={{
          pagination: paginationState,
          isLoading: isLoadingUsers,
          sorting: sortingState,
        }}
      />
      <BatchCreateUsersModalsManager
        isOpen={isBatchCreatingUsers}
        onClose={() => {
          setIsBatchCreatingUsers(false);
        }}
        refreshUsers={fetchUsers}
      ></BatchCreateUsersModalsManager>
    </>
  );
};
