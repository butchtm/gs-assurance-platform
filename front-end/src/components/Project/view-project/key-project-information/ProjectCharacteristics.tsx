import { AccountTree, Folder, Layers } from "@mui/icons-material";
import { Grid, List, ListItem } from "@mui/material";
import dayjs from "dayjs";
import React, { useEffect, useState } from "react";

import { CreditingPeriod, MonitoringPeriod } from "../../../../common/project/Project";
import InformationBlock from "../../../Reusable/Card/InformationBlock";
import InformationCard from "../../../Reusable/Card/InformationCard";

interface ProjectCharacteristicsProps {
  types?: string[];
  size?: string;
  currentStandardVersion?: string;
  creditingPeriods?: CreditingPeriod[];
  isCurrentCreditingPeriod?: boolean;
  bundleName?: string;
  openBundleModal?: () => void;
}

const ProjectCharacteristics: React.FC<ProjectCharacteristicsProps> = ({
  types,
  size,
  currentStandardVersion,
  creditingPeriods,
  bundleName,
  openBundleModal,
}): JSX.Element => {
  const [isCurrentCreditingPeriod, setIsCurrentCreditingPeriod] = useState<boolean>(false);
  const [isCurrentMonitoringPeriod, setIsCurrentMonitoringPeriod] = useState<boolean>(false);
  const [monitoringPeriod, setMonitoringPeriod] = useState<MonitoringPeriod | undefined>();
  const [creditingPeriod, setCreditingPeriod] = useState<CreditingPeriod | undefined>();

  const findCurrentMonitoringPeriod = (monitoringPeriods: MonitoringPeriod[]) => {
    const currentPeriod = monitoringPeriods.find((monitoringPeriod) =>
      validateCurrentMonitoringPeriod(monitoringPeriod),
    );
    if (currentPeriod) {
      setIsCurrentMonitoringPeriod(true);
      return currentPeriod;
    }
    return findLatestMonitoringPeriod(monitoringPeriods);
  };

  const findCurrentOrLatestPeriod = (creditingPeriods: CreditingPeriod[]) => {
    const currentPeriod = creditingPeriods.find((creditingPeriod) => validateCurrentPeriod(creditingPeriod));
    if (currentPeriod) {
      setIsCurrentCreditingPeriod(true);
      return currentPeriod;
    }
    return findLatestCreditingPeriod(creditingPeriods);
  };

  const validateCurrentPeriod = (creditingPeriod: CreditingPeriod) => {
    const today = dayjs.utc();
    return dayjs.utc(creditingPeriod.startDate) < today && dayjs.utc(creditingPeriod.endDate) > today;
  };

  const validateCurrentMonitoringPeriod = (monitoringPeriod: MonitoringPeriod) => {
    const today = dayjs.utc();
    return dayjs.utc(monitoringPeriod.startDate) < today && dayjs.utc(monitoringPeriod.endDate) > today;
  };

  const findLatestCreditingPeriod = (creditingPeriods: CreditingPeriod[]) => {
    const today = dayjs.utc();
    creditingPeriods.sort((a, b) => dayjs.utc(a.endDate).diff(dayjs.utc(b.endDate)));
    let latestPeriod = creditingPeriods[0];
    const isPeriodMoreRecent = (creditingPeriod: CreditingPeriod) => {
      return (
        Math.abs(dayjs.utc(latestPeriod.endDate).diff(today)) > Math.abs(dayjs.utc(creditingPeriod.endDate).diff(today))
      );
    };
    creditingPeriods.forEach((creditingPeriod) => {
      if (dayjs.utc(creditingPeriod.endDate) < today && isPeriodMoreRecent(creditingPeriod)) {
        latestPeriod = creditingPeriod;
      }
    });
    setIsCurrentCreditingPeriod(false);
    return latestPeriod;
  };

  const findLatestMonitoringPeriod = (monitoringPeriods: MonitoringPeriod[]) => {
    const today = dayjs.utc();
    monitoringPeriods.sort((a, b) => dayjs.utc(a.endDate).diff(dayjs.utc(b.endDate)));
    let latestPeriod = monitoringPeriods[0];
    const isPeriodMoreRecent = (monitoringPeriod: MonitoringPeriod) => {
      return (
        Math.abs(dayjs.utc(latestPeriod.endDate).diff(today)) >
        Math.abs(dayjs.utc(monitoringPeriod.endDate).diff(today))
      );
    };
    monitoringPeriods.forEach((monitoringPeriod) => {
      if (dayjs.utc(monitoringPeriod.endDate) < today && isPeriodMoreRecent(monitoringPeriod)) {
        latestPeriod = monitoringPeriod;
      }
    });
    setIsCurrentCreditingPeriod(false);
    return latestPeriod;
  };

  useEffect(() => {
    if (!creditingPeriods) return;

    const selected = findCurrentOrLatestPeriod(creditingPeriods);
    setCreditingPeriod(selected);

    if (selected?.monitoringPeriods && selected.monitoringPeriods.length > 0) {
      const selectedMonitoringPeriod = findCurrentMonitoringPeriod(selected.monitoringPeriods);
      setMonitoringPeriod(selectedMonitoringPeriod);
    }
  }, [creditingPeriods]);

  return (
    <InformationCard title="Project Characteristics">
      <Grid container spacing={2}>
        <Grid item xs={6}>
          <InformationBlock icon={<AccountTree sx={{ fontSize: "13px" }} />} title="Project Type">
            {types && types.length > 0 ? (
              <ul>
                {types.map((projectType) => (
                  <li key={projectType}>{projectType}</li>
                ))}
              </ul>
            ) : (
              "Not Set"
            )}
          </InformationBlock>
        </Grid>
        <Grid item xs={6}>
          <InformationBlock icon={<Layers sx={{ fontSize: "13px" }} />} title="Project Scale">
            {size ?? "Not Set"}
          </InformationBlock>
        </Grid>
        <Grid item xs={6}>
          <InformationBlock icon={<Folder sx={{ fontSize: "13px" }} />} title="Gold Standard Version">
            {currentStandardVersion}
          </InformationBlock>
        </Grid>
        <Grid item xs={6}>
          <InformationBlock
            icon={<AccountTree sx={{ fontSize: "13px" }} />}
            title={isCurrentCreditingPeriod ? "Current Crediting Period" : "Latest Crediting Period"}
          >
            {dayjs.utc(creditingPeriod?.startDate).format("DD/MM/YYYY") +
              " - " +
              dayjs.utc(creditingPeriod?.endDate).format("DD/MM/YYYY") || "Not Set"}
          </InformationBlock>
        </Grid>
        {monitoringPeriod && (
          <Grid item xs={6}>
            <InformationBlock
              icon={<AccountTree sx={{ fontSize: "13px" }} />}
              title={isCurrentMonitoringPeriod ? "Current Monitoring Period" : "Latest Monitoring Period"}
            >
              {dayjs.utc(monitoringPeriod?.startDate).format("DD/MM/YYYY") +
                " - " +
                dayjs.utc(monitoringPeriod?.endDate).format("DD/MM/YYYY") || "Not Set"}
            </InformationBlock>
          </Grid>
        )}
        <Grid item xs={12}>
          <InformationBlock icon={<AccountTree sx={{ fontSize: "13px" }} />} title="Methodologies">
            {creditingPeriod?.methodologies && creditingPeriod.methodologies.length > 0 ? (
              <List sx={{ width: "fit-content", margin: "auto", padding: "0" }}>
                <ListItem
                  sx={{
                    justifyContent: "space-between",
                    padding: "0",
                    fontWeight: "bold",
                  }}
                >
                  <span>Methodology</span>
                  <span>Version</span>
                </ListItem>
                {creditingPeriod.methodologies.map((methodology) => (
                  <ListItem
                    key={methodology.id}
                    sx={{
                      justifyContent: "space-between",
                      gap: "16px",
                      padding: "0",
                    }}
                  >
                    <span>{methodology.description}</span>
                    <span>{methodology.version ?? "Not set"}</span>
                  </ListItem>
                ))}
              </List>
            ) : (
              "Not Set"
            )}
          </InformationBlock>
        </Grid>

        {bundleName && (
          <Grid item xs={6}>
            <InformationBlock icon={<Folder sx={{ fontSize: "13px" }} />} title="VPA Bundle" onClick={openBundleModal}>
              {bundleName}
            </InformationBlock>
          </Grid>
        )}
      </Grid>
    </InformationCard>
  );
};

export default ProjectCharacteristics;
