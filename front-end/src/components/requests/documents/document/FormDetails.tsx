import { Box } from "@mui/material";
import dayjs from "dayjs";

import { RequestForm } from "../../../Forms/Forms";

export const FormDetails = ({ form }: { form: RequestForm }) => {
  return (
    <Box>
      <Box sx={{ display: "flex", justifyContent: "flex-start" }}>
        <span style={{ fontWeight: "bold" }}>{form.type.description} Form</span>
      </Box>
      <Box sx={{ display: "flex", flexDirection: "column", textAlign: "left" }}>
        <span>
          <b>Created:</b> {dayjs.utc(form.createdAt).format("DD/MM/YYYY HH:mm")}
        </span>
        {form.updatedAt && (
          <span>
            <b>Updated:</b> {dayjs.utc(form.updatedAt).format("DD/MM/YYYY HH:mm")}
          </span>
        )}
        {form.submittedAt && (
          <span>
            <b>Submitted:</b> {dayjs.utc(form.submittedAt).format("DD/MM/YYYY HH:mm")}
          </span>
        )}
      </Box>
    </Box>
  );
};
