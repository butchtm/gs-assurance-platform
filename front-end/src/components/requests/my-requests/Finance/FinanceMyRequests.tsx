import { Grid, ThemeProvider } from "@mui/material";
import React from "react";

import { theme } from "../../../../theme";

import FinanceRequestsTable from "./FinanceRequestsTable";

const FinanceMyRequests: React.FC = () => {
  return (
    <ThemeProvider theme={theme}>
      <Grid item xs={12}>
        <FinanceRequestsTable />
      </Grid>
    </ThemeProvider>
  );
};

export default FinanceMyRequests;
