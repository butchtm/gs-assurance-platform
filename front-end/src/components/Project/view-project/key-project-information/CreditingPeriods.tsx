import { Table, TableBody, TableCell, TableHead, TableRow } from "@mui/material";
import dayjs from "dayjs";
import React from "react";
import { useNavigate } from "react-router-dom";

import { CreditingPeriod, Project } from "../../../../common/project/Project";
import { ProjectInConsultation } from "../../../Public/PublicConsultation/PublicConsultation";
import InformationBox from "../../../Reusable/Card/InformationBox";
import InformationCard from "../../../Reusable/Card/InformationCard";

interface CreditingPeriodsProps {
  creditingPeriods?: CreditingPeriod[];
  project?: Partial<Project> | ProjectInConsultation;
}

const CreditingPeriods: React.FC<CreditingPeriodsProps> = ({ creditingPeriods, project }): JSX.Element => {
  const navigate = useNavigate();

  const handleNavigateMethodologies = (period: CreditingPeriod) => {
    navigate(`/projects/creditingPeriods/${period.periodId}`, {
      state: { creditingPeriod: period, project: project },
    });
  };

  return (
    <InformationCard title="Crediting Periods">
      <InformationBox>
        {creditingPeriods && creditingPeriods.length > 0 ? (
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Period</TableCell>
                <TableCell>Start Date</TableCell>
                <TableCell>End Date</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {creditingPeriods
                .sort((a, b) => a.periodId - b.periodId)
                .map((period) => (
                  <TableRow
                    key={period.periodId}
                    onClick={() => handleNavigateMethodologies(period)}
                    style={{ cursor: "pointer" }}
                    hover
                  >
                    <TableCell>Period {period.periodId}</TableCell>
                    <TableCell>{dayjs.utc(period.startDate).format("DD/MM/YYYY")}</TableCell>
                    <TableCell>{dayjs.utc(period.endDate).format("DD/MM/YYYY")}</TableCell>
                  </TableRow>
                ))}
            </TableBody>
          </Table>
        ) : (
          "No Crediting Periods"
        )}
      </InformationBox>
    </InformationCard>
  );
};

export default CreditingPeriods;
