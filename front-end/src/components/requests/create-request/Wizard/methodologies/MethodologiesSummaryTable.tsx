import { Box, Button, Table, TableBody, TableCell, TableHead, TableRow, Typography } from "@mui/material";
import { useFormikContext } from "formik";
import React, { useEffect, useState } from "react";

import { useReferenceTypes } from "../../../../../common/data-types/DataReferenceTypes";
import { BatchedProjectRequest } from "../../../Requests";
import { CreateRequestForm, MethodologyConfiguration } from "../Request";

import { VPA_DESCRIPTIONS_LOWER_CASE } from "./Methodology";

export const MethodologiesSummaryTable: React.FC<{
  batchedProjects: BatchedProjectRequest[];
  changeStep: () => void;
  isPoa: boolean;
}> = ({ batchedProjects, isPoa, changeStep }) => {
  const { values } = useFormikContext<CreateRequestForm>();
  const referenceDataContext = useReferenceTypes();
  const allMethodologyOptions = referenceDataContext.getCustomReferenceOptions("Methodology", []);
  const [methodologies, setMethodologies] = useState<MethodologyConfiguration[]>([]);

  useEffect(() => {
    const relevantProject = batchedProjects?.find((proj) =>
      isPoa
        ? proj.classification === "Programme of Activities"
        : VPA_DESCRIPTIONS_LOWER_CASE.includes(proj.classification.toLowerCase()),
    );
    if (!relevantProject) {
      return;
    }
    const relevantMethodologies = values.batchStepProjectValues?.batch?.[relevantProject.id].methodologies;
    if (relevantMethodologies && relevantMethodologies.length > 0) {
      setMethodologies(relevantMethodologies);
    }
  }, []);

  return (
    <Box sx={{ paddingTop: "20px" }}>
      <Box sx={{ display: "flex" }}>
        <Typography variant="body2" sx={{ margin: "auto 10px auto 0" }}>
          <b>{isPoa ? "Programme of Activities" : "VPA"} Methodologies</b>
        </Typography>
        <Button variant="text" sx={{ padding: "0px" }} onClick={() => changeStep()}>
          Edit
        </Button>
      </Box>
      {methodologies.length > 0 && (
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Methodology</TableCell>
              <TableCell>Version</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {methodologies.map((methodology) => (
              <TableRow key={methodology.methodologyId} sx={{ "&:last-child td, &:last-child th": { border: 0 } }}>
                <TableCell>{allMethodologyOptions.find((x) => x.value == methodology.methodologyId)?.label}</TableCell>
                <TableCell sx={{ width: "70px" }}>{methodology.methodologyVersion}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      )}
    </Box>
  );
};
