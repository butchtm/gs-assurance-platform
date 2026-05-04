import { ReferenceType } from "../data-types/DataReferenceTypes";

const METHODOLOGY_REQUEST_TYPE_CODES = [
  "DESIGN_CERTIFICATION_RENEWAL_|_PROJECT",
  "DESIGN_CERTIFICATION_RENEWAL_|_POA_WITH_REAL_CASE_VPA",
  "RENEWAL_|_REAL_CASE_VPA",
  "RENEWAL_|_REGULAR_VPA",
  "DESIGN_CHANGE_|_WITH_VVB",
];

export const isMethodologyUpdateRequestType = (requestTypeId: number | undefined, requestTypes: ReferenceType[]) => {
  if (!requestTypeId) {
    return false;
  }
  return toMethodologyRequestTypeIds(requestTypes).includes(Number(requestTypeId));
};

const toMethodologyRequestTypeIds = (requestTypes: ReferenceType[]) => {
  return requestTypes.filter((x) => METHODOLOGY_REQUEST_TYPE_CODES.includes(x.code)).map((x) => x.id);
};
