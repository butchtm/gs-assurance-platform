import { Search as SearchIcon } from "@mui/icons-material";
import { Box, Button, IconButton, InputAdornment, TextField } from "@mui/material";
import {
  MaterialReactTable,
  MaterialReactTableProps,
  MRT_ColumnFiltersState,
  MRT_Row,
  MRT_RowData,
  useMaterialReactTable,
  type MRT_ColumnDef,
} from "material-react-table";
import { CSSProperties, ReactNode, useEffect, useMemo, useState } from "react";

import Pagination from "../../components/Reusable/Pagination";

import { MultiSelectAutocompleteFilter } from "./filter/MultiSelectAutocompleteFilter";
import "./FilterableSortableTable.css";
import { FilterableSortableTableService } from "./FilterableSortableTable.service";
import { FilterButtonMenu } from "./FilterButtonMenu";
import { Filter } from "./model/Filter";
import { useDebounce } from "./util/Debounce";
import { useDeepCompareEffect } from "./util/DeepCompareUseEffect";
import { ObjectMerger } from "./util/ObjectMerger";

interface RowAction<T extends MRT_RowData> {
  label: string;
  action: (row: T) => void;
  visible?: (row: T) => boolean;
  icon?: ReactNode;
}

interface AdditionalProps<T extends MRT_RowData> {
  topToolbarActions?: ReactNode;
  topToolbarCustomActions?: ReactNode;
  onRowClick?: (event: React.MouseEvent<HTMLTableRowElement, MouseEvent>, row: MRT_Row<T>) => void;
  rowActions?: RowAction<T>[];
  onFilterUpdate?: (filters: Filter[]) => void;
  globalSearchPlaceholder?: string;
  styleOverride?: CSSProperties;
  initialColumnFilters?: MRT_ColumnFiltersState;
}
type BlockedDefaultProps<T extends MRT_RowData> = Extract<
  keyof MaterialReactTableProps<T>,
  | "enableRowActions"
  | "enableTopToolbar"
  | "muiTableBodyRowProps"
  | "muiTableContainerProps"
  | "muiTableHeadProps"
  | "muiSearchTextFieldProps"
>;

interface TableColumnFilterFn<T extends MRT_RowData> {
  autoRemove: (value: unknown, column: MRT_ColumnDef<T>) => boolean;
}

export const FilterableSortableTable = <T extends MRT_RowData>({
  topToolbarActions,
  topToolbarCustomActions,
  onRowClick,
  rowActions,
  onFilterUpdate,
  globalSearchPlaceholder,
  styleOverride,
  initialColumnFilters,
  ...rest
}: Omit<MaterialReactTableProps<T>, BlockedDefaultProps<T>> & AdditionalProps<T>) => {
  const [columnFilters, setColumnFilters] = useState<MRT_ColumnFiltersState>(initialColumnFilters ?? []);
  const filterableSortableTableService = new FilterableSortableTableService();
  const [globalFilter, setGlobalFilter] = useState("");

  const debouncedSearchInput = useDebounce(globalFilter, 400);

  useDeepCompareEffect(() => {
    if (onFilterUpdate && rest.columns) {
      const filters = filterableSortableTableService.muiColumnFiltersToFilters(columnFilters, rest.columns);

      // Hacky fix for converting accountTypeids from string, needed as 0 wont work on first try of filtering
      const apiFilters = filters.map((filter) => {
        if (filter.field === "accountType.id" && typeof filter.value === "string") {
          return {
            ...filter,
            value: parseInt(filter.value, 10),
          };
        }
        return filter;
      });

      onFilterUpdate(apiFilters);
    }
  }, [columnFilters]);

  const drawCustomActionsAndFilters = () => {
    return (
      <Box display="flex" flexDirection="row">
        <Box>{topToolbarCustomActions}</Box>
        <Box>
          {tableConfig
            .getLeafHeaders()
            .filter(
              (header) =>
                header.column.columnDef.enableColumnFilter == undefined || header.column.columnDef.enableColumnFilter,
            )
            .map((header) => (
              <FilterButtonMenu key={header.id} header={header} tableConfig={tableConfig} />
            ))}
        </Box>
        <Box sx={{ marginLeft: "8px" }}>{topToolbarActions}</Box>
        {rest.enableGlobalFilter && (
          <TextField
            type="search"
            value={globalFilter}
            onChange={(e) => {
              setGlobalFilter(e.target.value);
            }}
            variant="outlined"
            sx={{
              margin: 0,
              marginLeft: "24px",
              width: 250,
              '& input[type="search"]::-webkit-search-cancel-button': {
                cursor: "pointer",
                filter: "brightness(0) invert(65%)", // MS-Edge colour consistency fix
              },
            }}
            size="small"
            placeholder={globalSearchPlaceholder ?? "Search"}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon />
                </InputAdornment>
              ),
            }}
            inputProps={{ "data-testid": "global-search-input" }}
          />
        )}
      </Box>
    );
  };

  const drawCustomRowActions = (props: { row: MRT_Row<T> }) => (
    <Box sx={{ display: "flex", gap: "8px" }}>
      {rowActions
        ?.filter((rowAction) => rowAction.visible == undefined || rowAction.visible(props.row.original))
        .map((rowAction) => {
          if (rowAction.icon) {
            return (
              <IconButton
                data-testid={rowAction.label}
                key={rowAction.label}
                aria-label={rowAction.label}
                onClick={(e) => {
                  e.stopPropagation();
                  rowAction.action(props.row.original);
                }}
              >
                {rowAction.icon}
              </IconButton>
            );
          }
          return (
            <Button
              key={rowAction.label}
              onClick={(e) => {
                e.stopPropagation();
                rowAction.action(props.row.original);
              }}
            >
              {rowAction.label}
            </Button>
          );
        })}
    </Box>
  );

  const areAnyRowActionsVisible = () =>
    rowActions !== undefined &&
    rest.data?.some((row) => rowActions.some((rowAction) => rowAction.visible == undefined || rowAction.visible(row)));

  const defaultTableConfig: MaterialReactTableProps<T> = {
    data: rest.data as T[],
    columns: rest.columns as MRT_ColumnDef<T, unknown>[],
    enableFullScreenToggle: false,
    enableDensityToggle: false,
    enableColumnActions: false,
    enableHiding: false,
    enablePagination: false,
    enableFilters: false,
    enableGlobalFilter: false,
    enableSorting: false,
    enableTopToolbar: !!(topToolbarActions || topToolbarCustomActions || rest.enableFilters),
    renderToolbarInternalActions: () => <></>, // Hide filter toggle button
    manualPagination: true,
    manualFiltering: true,
    sortDescFirst: false,
    onColumnFiltersChange: setColumnFilters,
    onGlobalFilterChange: setGlobalFilter,
    state: {
      columnFilters: columnFilters,
      globalFilter: debouncedSearchInput,
    },
    initialState: {
      showGlobalFilter: false,
      showColumnFilters: true,
      columnPinning: {
        left: ["mrt-row-expand", "mrt-row-select"],
        right: ["mrt-row-actions"],
      },
    },
    columnFilterDisplayMode: "custom",
    muiTablePaperProps: {
      elevation: 0,
      sx: {
        overflow: "visible", // Prevent filter button status count being cropped
      },
    },
    muiTopToolbarProps: {
      sx: {
        justifyContent: "flex-end",
        overflow: "visible", // Prevent filter button status count being cropped
      },
    },
    muiTableBodyRowProps: ({ row }) => ({
      onClick: (event: React.MouseEvent<HTMLTableRowElement, MouseEvent>) => {
        if (onRowClick !== undefined) {
          onRowClick(event, row);
        }
      },
      hover: onRowClick !== undefined,
      sx: {
        cursor: onRowClick == undefined ? "initial" : "pointer",
      },
    }),
    enableRowActions: areAnyRowActionsVisible(),
    renderRowActions: drawCustomRowActions,
    muiTableContainerProps: () => ({
      sx: {
        overflowX: rest.state?.isLoading ? "hidden" : undefined,
      },
    }),
    muiTableHeadProps: () => ({
      sx: {
        visibility: rest.state?.isLoading ? "hidden" : undefined,
      },
    }),
    muiTableBodyCellProps: {
      sx: {
        fontSize: "13px",
        width: "unset",
        minWidth: "unset",
      },
    },
    muiTableHeadCellProps: {
      sx: {
        color: "gray",
        width: "unset",
        minWidth: "unset",
      },
    },
    paginationDisplayMode: "custom",
    renderTopToolbarCustomActions: drawCustomActionsAndFilters,
  };

  /**
   * By default, TanStack table's single-select drop down treats the filter value 0 as unset and auto-removes it.
   * Some of our data depends on IDs of zero, this custom auto-remove re-implementation allows for it.
   *
   * @param value The filter value
   * @returns True if the cleared filter should be auto-removed
   */
  const safeAutoRemover = (value: unknown): boolean => {
    if (Array.isArray(value) && value.length == 0) return true;
    return typeof value === "undefined" || (typeof value === "string" && !value);
  };

  const applyColumnFilterFixes = (columns: MRT_ColumnDef<T, unknown>[]): MRT_ColumnDef<T, unknown>[] =>
    columns.map((column) => {
      if (column.filterFn) {
        (column.filterFn as unknown as TableColumnFilterFn<T>).autoRemove = safeAutoRemover;
      }

      if (column.filterVariant == "multi-select" && column.Filter == undefined) {
        column.Filter = MultiSelectAutocompleteFilter;
      }

      return column;
    });

  const tableConfig = useMaterialReactTable(
    useMemo(() => {
      const completeTableConfig = ObjectMerger.mergeDeep(defaultTableConfig, rest);
      if (completeTableConfig.columns) {
        completeTableConfig.columns = applyColumnFilterFixes(completeTableConfig.columns);
      }

      return completeTableConfig;
    }, [rest.data, rest.columns, rest]),
  );

  useEffect(() => {
    tableConfig.setGlobalFilter(debouncedSearchInput);
  }, [debouncedSearchInput, tableConfig]);

  return (
    <div style={{ margin: "20px", ...styleOverride }}>
      <MaterialReactTable table={tableConfig} />

      {rest.enablePagination && !tableConfig.getState().isLoading && (
        <Pagination
          total={tableConfig.getPageCount()}
          current={tableConfig.getState().pagination.pageIndex + 1}
          onChange={(newPage) => {
            tableConfig.setPageIndex(newPage - 1);
          }}
        />
      )}
    </div>
  );
};
