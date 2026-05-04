import { Box, FormControlLabel, FormGroup, Switch } from "@mui/material";
import React, { ChangeEvent } from "react";

interface TACRequestsFilterBlockProps {
  filterByCompetencies: boolean;
  onFilterChange: (filterByCompetencies: boolean) => void;
}

const TACRequestsFilterBlock: React.FC<TACRequestsFilterBlockProps> = ({ filterByCompetencies, onFilterChange }) => {
  const toggleFilter = (event: ChangeEvent<HTMLInputElement>, checked: boolean) => {
    onFilterChange(checked);
  };

  return (
    <Box className="search-container">
      <FormGroup>
        <FormControlLabel
          control={<Switch checked={filterByCompetencies} onChange={toggleFilter} aria-label="competency-switch" />}
          label="Filter by Competencies"
          data-testid="filter-by-competencies"
        />
      </FormGroup>
    </Box>
  );
};

export default TACRequestsFilterBlock;
