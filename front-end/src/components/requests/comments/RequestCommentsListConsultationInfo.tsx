import { Box, Typography } from "@mui/material";
import dayjs from "dayjs";

import { RequestConsultation } from "../../../common/request/GSRequest";

export const RequestCommentsListConsultationInfo = ({ consultations }: { consultations?: RequestConsultation[] }) => {
  const toSpan = (consultation: RequestConsultation) => {
    const label = toLabel(consultation);
    return (
      <Typography data-testid={toTestId(label)} key={consultation.id}>
        <strong>{label}</strong> - {formatDate(consultation.startDate)} to {formatDate(consultation.endDate)}
      </Typography>
    );
  };

  const toTestId = (label: string) => {
    return `comment-${label.toLowerCase().replace(" ", "-")}-dates`;
  };

  const toLabel = (consultation: RequestConsultation) => {
    if (consultation.type.userType === "PUBLIC") {
      return "Public Consultation";
    }
    return "Stakeholder Consultation";
  };

  const formatDate = (date: string) => {
    return dayjs.utc(date).format("DD/MM/YYYY");
  };

  if (consultations?.length === 0) {
    return <></>;
  }

  return (
    <Box sx={{ marginBottom: 3 }}>
      {consultations
        ?.filter((consultation) => consultation.type.userType == "PUBLIC")
        .map((consultation) => toSpan(consultation))}
    </Box>
  );
};
