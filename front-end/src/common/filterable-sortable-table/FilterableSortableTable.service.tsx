import { MRT_ColumnDef, MRT_ColumnFiltersState, MRT_RowData } from "material-react-table";

import { QueryOperator } from "./model/QueryOperator";
import { Filter } from "./model/Filter";

export class FilterableSortableTableService {
  public muiColumnFiltersToFilters<T extends MRT_RowData>(
    columnFilters: MRT_ColumnFiltersState,
    columnDefinitions: MRT_ColumnDef<T>[],
  ): Filter[] {
    return columnFilters.map((columnFilter) => {
      const columnDefinition = columnDefinitions.find((def) => def.accessorKey === columnFilter.id);
      if (!columnDefinition) throw Error("Unknown column being filtered");

      const correctlyTypedValue = this.getColumnFilterValue(columnDefinition, columnFilter.value);
      const columnQueryOperator = this.getColumnFilterQueryOperator(columnDefinition);
      return this.buildFilter(columnFilter.id, correctlyTypedValue, columnQueryOperator);
    });
  }

  private buildFilter(columnId: string, columnValue: unknown, columnQueryOperator: QueryOperator): Filter {
    if (columnQueryOperator === QueryOperator.IN || columnQueryOperator === QueryOperator.BETWEEN) {
      return {
        field: columnId,
        queryOperator: columnQueryOperator,
        value: null,
        values: columnValue as unknown[],
      };
    }
    return {
      field: columnId,
      queryOperator: columnQueryOperator,
      value: columnValue,
      values: [],
    };
  }

  private getColumnFilterQueryOperator<T extends MRT_RowData>(columnDefinition: MRT_ColumnDef<T>): QueryOperator {
    if (columnDefinition.filterVariant === "multi-select") {
      return QueryOperator.IN;
    }
    if (columnDefinition.filterVariant === "checkbox") {
      return QueryOperator.EQUALS;
    }
    if (columnDefinition.filterVariant === "select") {
      return QueryOperator.EQUALS;
    }
    if (columnDefinition.filterVariant === "date-range") {
      return QueryOperator.BETWEEN;
    }
    return QueryOperator.LIKE;
  }

  private getColumnFilterValue<T extends MRT_RowData>(columnDefinition: MRT_ColumnDef<T>, value: unknown): unknown {
    if (columnDefinition.filterVariant == "checkbox") {
      return (value as string).toLowerCase() === "true";
    }
    return value;
  }
}
