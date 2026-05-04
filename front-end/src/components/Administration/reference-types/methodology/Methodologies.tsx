import EditIcon from "@mui/icons-material/Edit";
import { Box, Button } from "@mui/material";
import { useEffect, useState } from "react";

import { useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { FilterableSortableTable } from "../../../../common/filterable-sortable-table/FilterableSortableTable";
import { IsActiveCell } from "../../../../common/filterable-sortable-table/cell/IsActiveCell";
import { PaginationState } from "../../../Reusable/Pagination";
import { getMethodologies, MethodologyReferenceType } from "../ReferenceTypes";

import { CreateEditMethodologyModal } from "./CreateEditMethodologyModal";

export const Methodologies = () => {
  const [paginationState, setPaginationState] = useState<PaginationState>({
    pageIndex: 0,
    pageSize: 25,
  });
  const [rowCount, setRowCount] = useState(0);
  const [globalSearch, setGlobalSearch] = useState("");
  const [isLoading, setIsLoading] = useState(true);
  const [methodologies, setMethodologies] = useState<MethodologyReferenceType[]>([]);
  const [methodologyToEdit, setMethodologyToEdit] = useState<MethodologyReferenceType>();
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const { refreshReferenceData } = useReferenceTypes();

  const fetchMethodologies = async () => {
    setIsLoading(true);
    const methodologyResponse = await getMethodologies(
      globalSearch,
      paginationState.pageIndex + 1,
      paginationState.pageSize,
    );
    if (methodologyResponse) {
      setMethodologies(methodologyResponse.content);
      setRowCount(methodologyResponse.totalElements);
    }
    setIsLoading(false);
  };

  useEffect(() => {
    fetchMethodologies();
  }, [paginationState.pageIndex, paginationState.pageSize, globalSearch]);

  const handleCloseModal = (submitted: boolean, created?: boolean) => {
    setIsModalOpen(false);
    setMethodologyToEdit(undefined);
    if (submitted) {
      fetchMethodologies();
      refreshReferenceData();
    }
    if (submitted && created) {
      setLastPage();
    }
  };

  const setLastPage = () => {
    setPaginationState({
      pageIndex: Math.floor(rowCount / paginationState.pageSize),
      pageSize: paginationState.pageSize,
    });
  };

  return (
    <>
      <Box>
        <FilterableSortableTable
          columns={[
            {
              accessorKey: "description",
              header: "Description",
              enableColumnFilter: false,
              enableSorting: false,
            },
            {
              accessorKey: "active",
              header: "Active",
              Cell: IsActiveCell,
              enableColumnFilter: false,
              enableSorting: false,
            },
          ]}
          data={methodologies}
          onPaginationChange={setPaginationState}
          rowCount={rowCount}
          state={{
            pagination: paginationState,
            isLoading: isLoading,
          }}
          enableFilters={true}
          enableGlobalFilter={true}
          onGlobalFilterChange={setGlobalSearch}
          globalSearchPlaceholder="Description"
          enablePagination={true}
          rowActions={[
            {
              label: "Edit",
              action: (row) => {
                setMethodologyToEdit(row);
                setIsModalOpen(true);
              },
              icon: <EditIcon />,
            },
          ]}
          topToolbarActions={
            <Button key="Create" onClick={() => setIsModalOpen(true)}>
              Create
            </Button>
          }
          styleOverride={{
            margin: 0,
          }}
        />
      </Box>
      {isModalOpen && (
        <CreateEditMethodologyModal
          open={isModalOpen}
          onClose={(submitted: boolean, created?: boolean) => handleCloseModal(submitted, created)}
          methodology={methodologyToEdit}
        />
      )}
    </>
  );
};
