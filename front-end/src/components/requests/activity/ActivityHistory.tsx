import dayjs from "dayjs";
import React, { useEffect, useState } from "react";

import { FilterableSortableTable } from "../../../common/filterable-sortable-table/FilterableSortableTable";
import { GSRequest, RequestActivity } from "../../../common/request/GSRequest";
import { apiClient } from "../../APIClient/Client";
import { PaginationState } from "../../Reusable/Pagination";

const formatDate = (date: string | undefined) => {
  if (date) {
    return dayjs.utc(date).format("DD/MM/YYYY");
  }
  return "";
};

const formatName = (activity: RequestActivity) => {
  if (activity.skipped) {
    return `${activity.name} (skipped)`;
  }
  return activity.name;
};

export const ActivityHistory: React.FC<{ request: GSRequest }> = ({ request }) => {
  const [history, setHistory] = useState<RequestActivity[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [paginationState, setPaginationState] = useState<PaginationState>({
    pageIndex: 0,
    pageSize: 10,
  });
  const [rowCount, setRowCount] = useState(0);

  useEffect(() => {
    setIsLoading(true);
    const fetchHistory = async () => {
      const response = await apiClient.makeRequest(`/requests/${request.id}/activities/pages`, "GET", undefined, {
        page: paginationState.pageIndex + 1,
        pageSize: paginationState.pageSize,
      });
      setHistory(response.content);
      setRowCount(response.totalElements);
      setIsLoading(false);
    };
    fetchHistory();
  }, [request, paginationState]);

  return (
    <FilterableSortableTable
      columns={[
        {
          accessorFn: (row) => formatName(row),
          header: "Activity",
          enableColumnFilter: false,
        },
        {
          accessorFn: (row) => formatDate(row.startDate),
          header: "Started",
          enableColumnFilter: false,
        },
        {
          accessorFn: (row) => formatDate(row.dueDate),
          header: "Deadline",
          enableColumnFilter: false,
        },
        {
          accessorFn: (row) => formatDate(row.endDate),
          header: "Completed",
          enableColumnFilter: false,
        },
        {
          accessorKey: "account.name",
          header: "Responsibility",
          enableColumnFilter: false,
        },
      ]}
      data={history}
      onPaginationChange={setPaginationState}
      enablePagination={true}
      rowCount={rowCount}
      state={{
        pagination: paginationState,
        isLoading: isLoading,
      }}
    />
  );
};
