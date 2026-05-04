import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow } from "@mui/material";
import { useFormikContext } from "formik";

import { CreateRequestForm } from "./Request";

export const RequiredDocumentsTable = () => {
  const { values } = useFormikContext<CreateRequestForm>();

  return (
    <TableContainer>
      <Table sx={{ minWidth: 350 }} aria-label="simple table">
        <TableHead>
          <TableRow>
            <TableCell>{"Required Documents"}</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {values.requiredDocuments ? (
            values.requiredDocuments?.map((document) => (
              <TableRow key={document.documentTypeId} sx={{ "&:last-child td, &:last-child th": { border: 0 } }}>
                <TableCell component="th" scope="row" sx={{ textAlign: "center" }}>
                  {document.documentTypeDescription}
                </TableCell>
              </TableRow>
            ))
          ) : (
            <TableRow sx={{ "&:last-child td, &:last-child th": { border: 0 } }}>
              <TableCell component="th" scope="row" sx={{ textAlign: "center" }}>
                No Required Documents
              </TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>
    </TableContainer>
  );
};
