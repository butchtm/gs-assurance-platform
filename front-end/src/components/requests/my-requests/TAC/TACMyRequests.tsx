import { ThemeProvider } from "@mui/material";
import React, { useState } from "react";

import { theme } from "../../../../theme";

import TACRequestsFilterBlock from "./TACRequestsFilterBlock";
import TACRequestsTable from "./TACRequestsTable";

const TACMyRequests: React.FC = () => {
  const [filterByCompetencies, setFilterByCompetencies] = useState<boolean>(true);

  return (
    <ThemeProvider theme={theme}>
      <TACRequestsFilterBlock filterByCompetencies={filterByCompetencies} onFilterChange={setFilterByCompetencies} />
      <TACRequestsTable filterByCompetencies={filterByCompetencies} />
    </ThemeProvider>
  );
};

export default TACMyRequests;
