import { QueryOperator } from "./QueryOperator";

export interface Filter {
  field: string;
  queryOperator: QueryOperator;
  value: unknown;
  values: unknown[];
}
