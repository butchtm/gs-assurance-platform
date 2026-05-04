import { Button, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import dayjs from "dayjs";
import { MRT_SortingState } from "material-react-table";

import { getProjectRiskHistory } from "../../Project";
import { PaginationState } from "../../../Reusable/Pagination";
import { FilterableSortableTable } from "../../../../common/filterable-sortable-table/FilterableSortableTable";
import { DetailedRiskSummary } from "../../../../common/project/Project";
import { HighRiskCell } from "../../../../common/filterable-sortable-table/cell/HighRiskCell";

export const RiskManagementTab = ({
  projectId,
  openRiskModal,
  refreshProject,
}: {
  projectId: number;
  openRiskModal: () => void;
  refreshProject?: boolean;
}) => {
  const [riskHistory, setRiskHistory] = useState<DetailedRiskSummary[]>([]);
  const [paginationState, setPaginationState] = useState<PaginationState>({
    pageIndex: 0,
    pageSize: 10,
  });
  const [sortingState, setSortingState] = useState<MRT_SortingState>([]);
  const [rowCount, setRowCount] = useState(0);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const getRiskHistory = async () => {
      setIsLoading(true);
      const response = await getProjectRiskHistory(
        projectId,
        sortingState,
        paginationState.pageIndex + 1,
        paginationState.pageSize,
      );
      if (response) {
        setRiskHistory(response.content);
        setRowCount(response.totalElements);
      }
      setIsLoading(false);
    };
    getRiskHistory();
  }, [paginationState, sortingState, refreshProject]);

  return (
    <>
      <Button onClick={() => openRiskModal()}>Update Risk</Button>
      <Typography variant="h6" align="left" sx={{ fontWeight: "bold", mt: 2 }}>
        History
      </Typography>
      <FilterableSortableTable
        columns={[
          {
            accessorFn: (row) => dayjs.utc(row.dateApplied).format("DD/MM/YYYY"),
            accessorKey: "dateApplied",
            header: "Date Applied",
            enableSorting: true,
          },
          {
            accessorKey: "risk",
            header: "Risk Status",
            Cell: HighRiskCell,
            enableSorting: false,
          },
          {
            accessorKey: "user",
            header: "User",
            enableSorting: false,
          },
          {
            accessorKey: "summary",
            header: "Description",
            enableSorting: false,
          },
        ]}
        data={riskHistory}
        onPaginationChange={setPaginationState}
        rowCount={rowCount}
        state={{
          pagination: paginationState,
          isLoading: isLoading,
          sorting: sortingState,
        }}
        onSortingChange={setSortingState}
        enableSorting={true}
        enablePagination={true}
        styleOverride={{ margin: 0 }}
      />
    </>
  );
};
