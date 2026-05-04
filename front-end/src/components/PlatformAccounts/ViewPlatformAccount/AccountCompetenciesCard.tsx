import EditIcon from "@mui/icons-material/Edit";
import { Box, Typography } from "@mui/material";
import React, { useContext } from "react";

import { AuthContext } from "../../Auth/Login/AuthProvider";
import InformationCard from "../../Reusable/Card/InformationCard";
import { CompetenciesDisplay } from "../PlatformAccountTypes";

import { AccountChip } from "./AccountChip";

interface AccountCompetenciesCardProps {
  handleOpenEditModal: () => void;
  competencies?: CompetenciesDisplay;
}

export const AccountCompetenciesCard: React.FC<AccountCompetenciesCardProps> = ({
  handleOpenEditModal,
  competencies,
}) => {
  const authContext = useContext(AuthContext);
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

  const formatCompetencyText = (competencyType: string): string => {
    let formatted = competencyType.replace(/([A-Z])/g, " $1").replace(/^./, (str) => str.toUpperCase());
    if (formatted.endsWith(" ids") || formatted.endsWith(" Ids")) {
      formatted = formatted.slice(0, -4);
    }
    return formatted;
  };

  return (
    <InformationCard
      title="Competencies"
      action={
        authContext.isAdminUser()
          ? {
              ariaLabel: "Edit competencies",
              icon: <EditIcon />,
              onClick: handleOpenEditModal,
            }
          : undefined
      }
    >
      {competencies ? (
        <Box gap={2}>
          {(Object.keys(competencies) as Array<keyof CompetenciesDisplay>)
            .filter((key) => {
              const value = competencies[key];
              return (
                value !== null &&
                value !== undefined &&
                ((Array.isArray(value) && value.length > 0) || (!Array.isArray(value) && value !== null))
              );
            })
            .map((key) => (
              <Box key={key} mb={2}>
                <Box sx={{ ...labelStyle }}>{formatCompetencyText(key)}</Box>
                {Array.isArray(competencies[key]) ? (
                  (competencies[key] as string[]).map((desc) => <AccountChip key={key} label={desc} />)
                ) : (
                  <Box sx={{ ...contentStyle }}>{competencies[key]}</Box>
                )}
              </Box>
            ))}
        </Box>
      ) : (
        <Typography variant="body1">No Competencies Found</Typography>
      )}
    </InformationCard>
  );
};
