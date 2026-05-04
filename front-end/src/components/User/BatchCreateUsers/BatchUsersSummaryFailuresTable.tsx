import { Table, TableBody, TableCell, TableHead, TableRow } from "@mui/material";
import React from "react";

import { BatchUserError } from "../User";

export const BatchUsersSummaryFailuresTable: React.FC<{
  failedUsers: BatchUserError[];
}> = ({ failedUsers }) => {
  return (
    <Table sx={{ width: "500px" }}>
      <TableHead>
        <TableRow>
          <TableCell>Username</TableCell>
          <TableCell>Account</TableCell>
          <TableCell>Error</TableCell>
        </TableRow>
      </TableHead>
      <TableBody>
        {failedUsers.map((user) => (
          <TableRow key={user.userName}>
            <TableCell>{user.userName}</TableCell>
            <TableCell>{user.account}</TableCell>
            <TableCell>{user.message}</TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
};
