import { Typography } from "@mui/material";
import dayjs from "dayjs";
import React, { useContext, useEffect, useState } from "react";
import { MRT_SortingState } from "material-react-table";

import { useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { PlatformAccountCell } from "../../../../common/filterable-sortable-table/cell/PlatformAccountCell";
import { FilterableSortableTable } from "../../../../common/filterable-sortable-table/FilterableSortableTable";
import { AuthContext } from "../../../Auth/Login/AuthProvider";
import { PaginationState } from "../../../Reusable/Pagination";
import { getMethodologyHistory } from "../../Project";
import { createRequestNameLinkCell } from "../../../requests/reusable/TableFunctions";

export interface MethodologyResponse {
  content: MethodologyHistory[];
  pageSize: number;
  pageNumber: number;
  totalPages: number;
  totalElements: number;
}

interface MethodologyHistory {
  id: number;
  projectId: number;
  periodId: number;
  requestId: number;
  gsid: string;
  requestName: string;
  updatedAt: string;
  updatedByAccountId: number | null;
  updatedByAccountName: string;
  updates: MethodologyUpdateRecord[];
  verdict: "REQUEST_APPROVED" | "REQUEST_NOT_APPROVED" | "PROJECT_FORCED";
}

interface MethodologyUpdateRecord {
  id: number;
  originalVersion: number | null;
  updatedVersion: number | null;
  operation: "ADD" | "UPDATE" | "DELETE";
}

interface MethodologyHistoryTableData {
  updatedAt: string;
  account: {
    id: number | null;
    name: string;
  };
  id: number;
  requestId: number;
  gsid: string;
  requestName: string;
  originalVersion: number | null;
  updatedVersion: number | null;
  operation: string;
  verdict: string;
}

enum MethodologiesHistoryVerdict {
  REQUEST_APPROVED = "Approved",
  REQUEST_NOT_APPROVED = "Denied",
  PROJECT_FORCED = "Forced",
}

enum Operation {
  ADD = "New",
  UPDATE = "Version Change",
  DELETE = "Removal",
}

export const MethodologiesHistory: React.FC<{ projectId: number; periodId: string }> = ({ projectId, periodId }) => {
  const [methodologyHistory, setMethodologyHistory] = useState<MethodologyHistoryTableData[]>();
  const [paginationState, setPaginationState] = useState<PaginationState>({
    pageIndex: 0,
    pageSize: 10,
  });
  const [sortingState, setSortingState] = useState<MRT_SortingState>([]);
  const [rowCount, setRowCount] = useState(0);
  const [isLoadingProjects, setIsLoadingProjects] = useState(true);
  const { referenceMethodologies } = useReferenceTypes();
  const authContext = useContext(AuthContext);

  const [editorVisibility, setEditorVisibility] = useState(false);

  const updateEditorVisibility = () => {
    const isEditorVisible = authContext.isAdminUser() || authContext.isArmUser() || authContext.isSupportUser();
    setEditorVisibility(isEditorVisible);
  };

  const getMethodologyName = (methodologyId: number) => {
    return referenceMethodologies.find((x) => x.id === methodologyId)?.description;
  };

  const convertToSingleArray = (rawHistory: MethodologyHistory[]) => {
    const formattedHistory: MethodologyHistoryTableData[] = [];
    for (const history of rawHistory) {
      for (const record of history.updates) {
        formattedHistory.push({
          updatedAt: history.updatedAt,
          requestName: history.requestName,
          account: {
            id: history.updatedByAccountId,
            name: history.updatedByAccountName ? history.updatedByAccountName : "ADMIN",
          },
          id: record.id,
          requestId: history.requestId,
          gsid: history.gsid,
          originalVersion: record.originalVersion,
          updatedVersion: record.updatedVersion,
          operation: Operation[record.operation],
          verdict: MethodologiesHistoryVerdict[history.verdict],
        });
      }
    }
    return formattedHistory;
  };

  useEffect(() => {
    updateEditorVisibility();
    const fetchMethodologyHistory = async () => {
      setIsLoadingProjects(true);
      const methodologyResponse = await getMethodologyHistory(
        projectId,
        periodId,
        sortingState,
        paginationState.pageIndex + 1,
        paginationState.pageSize,
      );
      if (methodologyResponse) {
        setMethodologyHistory(convertToSingleArray(methodologyResponse.content));
        setRowCount(methodologyResponse.totalElements);
      }
      setIsLoadingProjects(false);
    };
    fetchMethodologyHistory();
  }, [paginationState, sortingState]);

  return (
    <>
      <Typography variant="h6" align="left" sx={{ color: "000000", fontWeight: "bold", mt: 2 }}>
        Methodology History
      </Typography>
      {methodologyHistory && methodologyHistory?.length > 0 ? (
        <FilterableSortableTable
          initialState={{ columnVisibility: { account: editorVisibility } }}
          columns={[
            {
              accessorFn: (row) => dayjs.utc(row.updatedAt).format("DD/MM/YYYY"),
              header: "Date",
              accessorKey: "createdTimestamp",
              enableColumnFilter: false,
              enableSorting: true,
            },
            {
              accessorFn: (row) => getMethodologyName(row.id),
              header: "Methodology",
              enableColumnFilter: false,
              enableSorting: false,
            },
            {
              accessorFn: (row) => row.originalVersion ?? "",
              header: "Previous Version",
              enableColumnFilter: false,
              enableSorting: false,
            },
            {
              accessorFn: (row) => row.updatedVersion ?? "",
              header: "New Version",
              enableColumnFilter: false,
              enableSorting: false,
            },
            {
              accessorKey: "account",
              header: "Editor",
              enableColumnFilter: false,
              enableSorting: false,
              Cell: PlatformAccountCell,
            },
            {
              accessorKey: "requestName",
              header: "Request",
              enableColumnFilter: false,
              enableSorting: false,
              Cell: createRequestNameLinkCell(
                (row) => row.gsid,
                (row) => row.requestId,
              ),
            },
            {
              accessorKey: "operation",
              header: "Change Type",
              enableColumnFilter: false,
              enableSorting: false,
            },
            {
              accessorKey: "verdict",
              header: "Verdict",
              enableColumnFilter: false,
              enableSorting: false,
            },
          ]}
          data={methodologyHistory}
          onPaginationChange={setPaginationState}
          rowCount={rowCount}
          state={{
            pagination: paginationState,
            isLoading: isLoadingProjects,
            sorting: sortingState,
          }}
          onSortingChange={setSortingState}
          enableSorting={true}
          enablePagination={true}
          enableHiding={true}
          styleOverride={{ margin: 0 }}
        />
      ) : (
        <Typography sx={{ textAlign: "left" }}>No methodology history present</Typography>
      )}
    </>
  );
};
