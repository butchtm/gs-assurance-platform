import { AccountTree, CalendarToday, Link } from "@mui/icons-material";
import { Box, Card, CardContent, Grid } from "@mui/material";
import dayjs from "dayjs";
import React from "react";

import { TransitionDetail } from "../../../common/project/Project";

interface TransitioningProjectInformationProps {
  transitionDetail: TransitionDetail | null;
}

const TransitioningProjectInformation: React.FC<TransitioningProjectInformationProps> = ({ transitionDetail }) => {
  const cardStyle = {
    marginBottom: 2,
    backgroundColor: "var(--color-surface-card)",
    ":last-child": {
      marginBottom: 0,
    },
    border: "1px solid var(--border-color-card)",
    borderRadius: "5px",
  };

  const labelStyle = {
    fontSize: "13px",
    display: "block",
    color: "var(--color-accent-teal-dark)",
    textTransform: "uppercase",
    fontWeight: "bold",
    marginBottom: "0.5rem",
  };

  const contentStyle = {
    fontSize: "small",
    color: "var(--color-text-muted)",
    marginBottom: "10px",
  };

  return (
    <Card sx={cardStyle}>
      <CardContent>
        <h3 className="detail-item-heading">Transition Scheme</h3>
        <Grid container spacing={2}>
          <Grid item xs={4}>
            <Box sx={{ ...labelStyle }}>
              <AccountTree sx={{ fontSize: "13px" }} /> TRANSITION FROM OTHER SCHEME
            </Box>
            <Box sx={{ ...contentStyle }}>{transitionDetail?.transitionFromOtherScheme?.description}</Box>
          </Grid>
          <Grid item xs={4}>
            <Box sx={{ ...labelStyle }}>
              <AccountTree sx={{ fontSize: "13px" }} /> TRANSITION PATHWAY
            </Box>
            <Box sx={{ ...contentStyle }}>{transitionDetail?.transitionPathway?.description}</Box>
          </Grid>
          <Grid item xs={4}>
            <Box sx={{ ...labelStyle }}>
              <CalendarToday sx={{ fontSize: "13px" }} /> CREDITING PERIOD OTHER SCHEME
            </Box>
            <Box sx={{ ...contentStyle }}>
              {transitionDetail?.creditingPeriodStartDateOtherSchemes &&
                transitionDetail?.creditingPeriodEndDateOtherSchemes &&
                dayjs.utc(transitionDetail?.creditingPeriodStartDateOtherSchemes).format("DD/MM/YYYY") +
                  " - " +
                  dayjs.utc(transitionDetail?.creditingPeriodEndDateOtherSchemes).format("DD/MM/YYYY")}
            </Box>
          </Grid>
        </Grid>
        <Grid container justifyContent="space-evenly" spacing={2} marginTop={2}>
          <Grid item sm={12} md="auto" xl={6}>
            <Box sx={{ ...labelStyle }}>
              <AccountTree sx={{ fontSize: "13px" }} /> TRANSITION APPROVAL PROCEDURE
            </Box>
            <Box sx={{ ...contentStyle }}>{transitionDetail?.transitionApprovalProcedure?.description}</Box>
          </Grid>
          {transitionDetail?.projectLinkInOtherScheme && (
            <Grid item sm={12} md="auto" xl={6}>
              <Box sx={{ ...labelStyle }}>
                <Link sx={{ fontSize: "13px" }} /> PROJECT LINK IN OTHER SCHEME
              </Box>
              <Box sx={{ ...contentStyle }}>
                <a target="_blank" href={transitionDetail.projectLinkInOtherScheme} rel="noreferrer">
                  {transitionDetail.projectLinkInOtherScheme}
                </a>
              </Box>
            </Grid>
          )}
        </Grid>
      </CardContent>
    </Card>
  );
};

export default TransitioningProjectInformation;
