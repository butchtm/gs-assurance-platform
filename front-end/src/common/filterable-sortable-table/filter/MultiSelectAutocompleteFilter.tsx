import { TextField } from "@mui/material";
import Autocomplete from "@mui/material/Autocomplete";
import { DropdownOption, MRT_Column, MRT_Header, MRT_RowData, MRT_TableInstance } from "material-react-table";
import { useEffect, useState } from "react";

import { FilterOptions } from "../../../components/requests/CommonRequestFilters";

export const MultiSelectAutocompleteFilter = <TData extends MRT_RowData>({
  column,
  header,
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  rangeFilterIndex,
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  table,
}: {
  column: MRT_Column<TData, unknown>;
  header: MRT_Header<TData>;
  rangeFilterIndex?: number;
  table: MRT_TableInstance<TData>;
}) => {
  const [filterOptions, setFilterOptions] = useState<FilterOptions[]>([]);
  const [menuOpen, setMenuOpen] = useState(false);

  useEffect(() => {
    setFilterOptions(getFilterOptions());
    setTimeout(() => setMenuOpen(true), 200);
  }, []);

  const getFilterOptions = (): FilterOptions[] => {
    const filterSelectOptions = header.column.columnDef.filterSelectOptions;
    if (!filterSelectOptions) return [];

    return dropdownOptionsToFilterOptions(filterSelectOptions);
  };

  const dropdownOptionsToFilterOptions = (dropdownOptions: DropdownOption[]): FilterOptions[] => {
    const filterOptions = dropdownOptions.filter((dropdownOption) => typeof dropdownOption !== "string") as {
      label?: string;
      value: unknown;
    }[];

    return filterOptions.map((option) => {
      return { value: option.value as string | number, label: option.label ?? "" };
    });
  };

  const getCurrentFilterOptions = (): FilterOptions[] => {
    const currentValue = header.column.getFilterValue() as unknown[] | undefined;
    if (!currentValue) {
      return [];
    }
    return filterOptions.filter((filterOption) => currentValue.includes(filterOption.value));
  };

  const handleChange = (event: React.SyntheticEvent<Element, Event>, newValues: FilterOptions[]): void => {
    column.setFilterValue(newValues.map((newValue) => newValue.value));
  };

  return (
    <Autocomplete
      multiple
      open={menuOpen}
      id={header.column.columnDef.id}
      options={filterOptions}
      getOptionLabel={(option) => (option ? option.label : "")}
      value={getCurrentFilterOptions()}
      onChange={handleChange}
      renderInput={(params) => (
        <TextField
          {...params}
          label={header.column.columnDef.header}
          sx={{ minWidth: 250, minHeight: 56 }}
          data-testid={header.column.columnDef.header + "-mutliselect"}
        />
      )}
    />
  );
};
