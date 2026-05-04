import { useEffect, useState } from "react";
import { Box, Grid, Typography } from "@mui/material";
import dayjs from "dayjs";

import { useReferenceTypes, getReferenceValueDescriptionById } from "../../../common/data-types/DataReferenceTypes";
import { FilterableSortableTable } from "../../../common/filterable-sortable-table/FilterableSortableTable";
import { Project } from "../../../common/project/Project";
import {
  GSRequest,
  RequestCreditingPeriod,
  RequestMonitoringPeriod,
  canRequestTypeViewFeeSchedule,
  isNullOrUndefined,
} from "../../../common/request/GSRequest";
import { PermissionGroup } from "../../../PermissionGroup";
import ProtectedComponent from "../../../ProtectedComponent";

export const CreditingPeriodSection = ({ request, project }: { request: GSRequest; project?: Project | null }) => {
  const { referenceMethodologies, allReferenceTypes } = useReferenceTypes();

  const creditingPeriod: RequestCreditingPeriod = request.creditingPeriod;
  const [monitoringPeriod, setMonitoringPeriod] = useState<RequestMonitoringPeriod>(request.monitoringPeriod);

  const hasCreditingPeriod = !!creditingPeriod?.periodId || !!creditingPeriod?.startDate || !!creditingPeriod?.endDate;

  const hasMonitoringPeriod = () => {
    return !!monitoringPeriod?.id || !!monitoringPeriod?.startDate || !!monitoringPeriod?.endDate;
  };

  const shouldDisplayFeeSchedule = () => {
    return canRequestTypeViewFeeSchedule(request.requestType.code);
  };

  const getRequestMonitoringPeriodFeeSchedule = () => {
    if (!hasMonitoringPeriod() || isNullOrUndefined(request.monitoringPeriod?.feeSchedule)) {
      return null;
    }
    return getReferenceValueDescriptionById(request.monitoringPeriod.feeSchedule, allReferenceTypes.FeeSchedule);
  };

  const getProjectMonitoringPeriodFeeSchedule = () => {
    if (!hasMonitoringPeriod() || !request.monitoringPeriod?.id || !project?.creditingPeriods) {
      return null;
    }

    for (const creditingPeriod of project.creditingPeriods) {
      if (creditingPeriod.monitoringPeriods) {
        const matchingMP = creditingPeriod.monitoringPeriods.find((mp) => mp.id === request.monitoringPeriod.id);
        if (matchingMP && !isNullOrUndefined(matchingMP.feeSchedule)) {
          return getReferenceValueDescriptionById(matchingMP.feeSchedule, allReferenceTypes.FeeSchedule);
        }
      }
    }
    return null;
  };

  const getFeeSchedule = () => {
    const requestFeeSchedule = getRequestMonitoringPeriodFeeSchedule();
    if (requestFeeSchedule) {
      return requestFeeSchedule;
    }

    const projectMonitoringFeeSchedule = getProjectMonitoringPeriodFeeSchedule();
    if (projectMonitoringFeeSchedule) {
      return projectMonitoringFeeSchedule;
    }

    if (project?.feeSchedule) {
      return project.feeSchedule.description;
    }

    return "Not Set";
  };

  useEffect(() => {
    if (request.monitoringPeriod.id !== null && request.creditingPeriod?.monitoringPeriods) {
      const monitoringPeriod = request.creditingPeriod?.monitoringPeriods.find(
        (period) => period.id === request.monitoringPeriod.id,
      );
      if (monitoringPeriod) {
        setMonitoringPeriod(monitoringPeriod);
      }
    }
  }, [request]);

  if (!hasCreditingPeriod) {
    return (
      <Grid container spacing={2}>
        <Grid item xs={12}>
          <Typography variant="h6">Crediting Periods</Typography>
        </Grid>
        <Grid item xs={12}>
          <Typography>No matching crediting period found.</Typography>
        </Grid>
      </Grid>
    );
  }

  const methodologyData = request.methodologies.filter(
    (m) => m.creditingPeriodId?.toString() == request.creditingPeriod.periodId?.toString(),
  );

  const formatDate = (date: string | null) => {
    if (date) {
      return dayjs.utc(date).format("DD/MM/YYYY");
    }
  };

  const creditingPeriodTitle = () => {
    if (creditingPeriod.periodId) {
      return `Crediting Period ${creditingPeriod.periodId}`;
    }
    return "New Crediting Period";
  };

  const monitoringPeriodTitle = () => {
    if (monitoringPeriod.id) {
      return `Monitoring Period ${monitoringPeriod.id}`;
    }
    return "New Monitoring Period";
  };

  return (
    <>
      <Grid container spacing={2}>
        <Grid item xs={12}>
          <Box sx={{ mb: 1 }}>
            <Typography data-testid="crediting-period">
              <strong>{creditingPeriodTitle()}</strong> - {formatDate(creditingPeriod.startDate)} to{" "}
              {formatDate(creditingPeriod.endDate)}
            </Typography>
            {hasMonitoringPeriod() && (
              <Typography data-testid="monitoring-period" sx={{ mt: 1 }}>
                <strong>{monitoringPeriodTitle()}</strong> - {formatDate(monitoringPeriod.startDate)} to{" "}
                {formatDate(monitoringPeriod.endDate)}
              </Typography>
            )}
            <ProtectedComponent
              roles={[
                PermissionGroup.GS_ADMIN,
                PermissionGroup.FINANCE,
                PermissionGroup.ARM_TEAM,
                PermissionGroup.PROJECT_DEVELOPER,
                PermissionGroup.SUPPORT,
              ]}
            >
              {shouldDisplayFeeSchedule() && (
                <Typography data-testid="fee-schedule" sx={{ mt: 1 }}>
                  <strong>Fee Schedule -</strong> {getFeeSchedule()}
                </Typography>
              )}
            </ProtectedComponent>
          </Box>
        </Grid>
      </Grid>
      {methodologyData.length > 0 && (
        <FilterableSortableTable
          columns={[
            {
              accessorFn: (row) => referenceMethodologies?.find((ref) => ref.id === row.methodologyId)?.description,
              id: "methodologyDescription",
              header: "Methodology Description",
              enableColumnFilter: false,
            },
            {
              accessorKey: "methodologyVersion",
              header: "Methodology Version",
              enableColumnFilter: false,
            },
          ]}
          data={methodologyData}
          rowCount={methodologyData.length}
        />
      )}
    </>
  );
};
