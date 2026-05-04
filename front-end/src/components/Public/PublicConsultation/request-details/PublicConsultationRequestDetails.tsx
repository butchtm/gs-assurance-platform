import { Grid, ThemeProvider, Typography } from "@mui/material";
import dayjs from "dayjs";
import React from "react";

import { theme } from "../../../../theme";
import { getCreditingPeriod, RequestInConsultation } from "../PublicConsultation";

interface PublicConsultationRequestDetailsProps {
  request: RequestInConsultation;
}

const PublicConsultationRequestDetails: React.FC<PublicConsultationRequestDetailsProps> = ({ request }) => {
  return (
    <ThemeProvider theme={theme}>
      <div style={{ width: "100%" }}>
        <Typography variant="h5" sx={{ textAlign: "left", width: "fit-content" }}>
          Request
        </Typography>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12}>
            <Grid container spacing={2}>
              <Grid item xs={2}>
                <Typography variant="h6" sx={{ fontWeight: "bold", fontSize: "16px", textAlign: "left" }}>
                  Request Name:
                </Typography>
              </Grid>
              <Grid item xs={2}>
                <Typography sx={{ textAlign: "left" }}>{request.requestName}</Typography>
              </Grid>
              <Grid item xs={2}>
                <Typography variant="h6" sx={{ fontWeight: "bold", fontSize: "16px", textAlign: "left" }}>
                  Consultation Start Date:
                </Typography>
              </Grid>
              <Grid item xs={2}>
                <Typography sx={{ textAlign: "left" }}>
                  {dayjs.utc(request.consultationStartDate).format("DD/MM/YYYY")}
                </Typography>
              </Grid>
              <Grid item xs={2}>
                <Typography variant="h6" sx={{ fontWeight: "bold", fontSize: "16px", textAlign: "left" }}>
                  Crediting Period:
                </Typography>
              </Grid>
              <Grid item xs={2}>
                <Typography sx={{ textAlign: "left" }}>{getCreditingPeriod(request)}</Typography>
              </Grid>
              <Grid item xs={2}>
                <Typography variant="h6" sx={{ fontWeight: "bold", fontSize: "16px", textAlign: "left" }}>
                  Status:
                </Typography>
              </Grid>
              <Grid item xs={2}>
                <Typography sx={{ textAlign: "left" }}>{request.requestStatus}</Typography>
              </Grid>
              <Grid item xs={2}>
                <Typography variant="h6" sx={{ fontWeight: "bold", fontSize: "16px", textAlign: "left" }}>
                  Consultation End Date:
                </Typography>
              </Grid>
              <Grid item xs={2}>
                <Typography sx={{ textAlign: "left" }}>
                  {dayjs.utc(request.consultationEndDate).format("DD/MM/YYYY")}
                </Typography>
              </Grid>
              <Grid item xs={2}>
                <Typography variant="h6" sx={{ fontWeight: "bold", fontSize: "16px", textAlign: "left" }}>
                  Estimated Annual Emission Reduction:
                </Typography>
              </Grid>
              <Grid item xs={2}>
                <Typography sx={{ textAlign: "left" }}>
                  {request.emissionReductionAmount?.toLocaleString() || "Not Set"}
                </Typography>
              </Grid>
            </Grid>
          </Grid>
        </Grid>
      </div>
    </ThemeProvider>
  );
};

export default PublicConsultationRequestDetails;
