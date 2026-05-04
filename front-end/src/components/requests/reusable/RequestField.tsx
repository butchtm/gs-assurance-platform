import { Box, Grid, Typography } from "@mui/material";
import React, { ReactNode } from "react";

interface RequestFieldProps {
  name: string;
  value: string | number;
  icon: ReactNode;
}

export const RequestField: React.FC<RequestFieldProps> = ({ name, value, icon }) => {
  return (
    <Grid container sx={{ display: "flex", flexWrap: "nowrap", justifyContent: "space-between", margin: "16px" }}>
      <Box sx={{ display: "flex" }}>
        {icon}
        <Typography sx={{ textAlign: "left", ml: "5px" }}>{name}</Typography>
      </Box>
      <Typography sx={{ textAlign: "right" }}>{value}</Typography>
    </Grid>
  );
};
