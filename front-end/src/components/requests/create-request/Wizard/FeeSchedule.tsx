import { useFormikContext } from "formik";

import { useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { helperTexts } from "../../../Project/create-project/HelperText";
import { CustomFormSelect } from "../../../Reusable/FormFields/CustomFormSelect";
import { canRequestTypeSetFeeSchedule, GSRequest, isNullOrUndefined } from "../../../../common/request/GSRequest";
import { BatchedProjectRequest } from "../../Requests";

import { CreateRequestForm, ProjectIdWithRequestValues } from "./Request";

export const FeeSchedule = ({ batch }: { batch: boolean }) => {
  const { getCustomReferenceOptions } = useReferenceTypes();
  const { values } = useFormikContext<CreateRequestForm>();
  const feeSchedules = getCustomReferenceOptions("FeeSchedule", values.feeSchedule);
  return (
    <CustomFormSelect
      name="feeSchedule"
      label="Fee Schedule"
      options={feeSchedules}
      helperText={helperTexts.feeSchedule(batch)}
      required={true}
    />
  );
};

export function getStandaloneFeeScheduleValue(request: GSRequest): number | null {
  if (!canRequestTypeSetFeeSchedule(request.requestType.code)) {
    return null;
  }
  if (!isNullOrUndefined(request.monitoringPeriod.feeSchedule)) {
    return request.monitoringPeriod.feeSchedule;
  }
  const projectMonitoringPeriod = request.creditingPeriod?.monitoringPeriods?.find(
    (period) => period.id == request.monitoringPeriod.id,
  );
  return projectMonitoringPeriod?.feeSchedule ?? null;
}

export function getBatchedFeeScheduleValue(
  request: GSRequest,
  firstChildProjectData: BatchedProjectRequest,
  firstChildRequest: ProjectIdWithRequestValues,
): number | null {
  if (!canRequestTypeSetFeeSchedule(request.requestType.code)) {
    return null;
  }
  const firstProjectFeeSchedule = getStandaloneFeeScheduleValue(request);
  if (!isNullOrUndefined(firstProjectFeeSchedule)) {
    return firstProjectFeeSchedule;
  }

  if (!isNullOrUndefined(firstChildRequest.feeSchedule)) {
    return firstChildRequest.feeSchedule;
  }
  const creditingPeriod = firstChildProjectData.creditingPeriods?.find(
    (period) => period.periodId == firstChildRequest.creditingPeriodId,
  );
  const monitoringPeriod = creditingPeriod?.monitoringPeriods?.find(
    (period) => period.id == firstChildRequest.monitoringPeriodId,
  );
  return monitoringPeriod?.feeSchedule ?? null;
}
