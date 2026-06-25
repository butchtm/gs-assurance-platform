import { ReferenceType } from "../../common/data-types/DataReferenceTypes";

import {
  canonicalProjectClassificationCode,
  getCanonicalProjectClassificationCodeById,
  getCanonicalProjectClassificationDescriptionById,
  isPoaProjectClassification,
  projectClassificationCodeRequiresPoa,
  projectClassificationCodeRequiresRealCaseVpa,
  projectClassificationRequiresPoa,
  projectClassificationRequiresRealCaseVpa,
} from "./ProjectClassificationSemantics";

const projectClassifications: ReferenceType[] = [
  { id: 7, code: "PROGRAMME_OF_ACTIVITIES", description: "Programme of Activities" },
  { id: 8, code: "REAL_CASE_VPA", description: "Real Case VPA" },
  { id: 9, code: "REGULAR_VPA", description: "Regular VPA" },
  { id: 10, code: "REAL_CASE_VPA_CROSS_LINKAGE", description: "Real Case VPA Cross Linkage" },
  { id: 11, code: "REGULAR_VPA_CROSS_LINKAGE", description: "Regular VPA Cross Linkage" },
  { id: 12, code: "POA", description: "Programme of Activities Legacy" },
  { id: 13, code: "REGULAR_CASE_VPA", description: "Regular Case VPA" },
  { id: 14, code: "REAL_CASE_VPA_LEGACY", description: "Real Case VPA Legacy" },
  { id: 15, code: "VPA_CPA", description: "VPA CPA" },
];

describe("ProjectClassificationSemantics", () => {
  it.each([
    ["REAL_CASE_VPA_CROSS_LINKAGE", "REAL_CASE_VPA"],
    ["REGULAR_VPA_CROSS_LINKAGE", "REGULAR_VPA"],
  ])("maps raw cross-link code %s to canonical code %s", (rawCode, canonicalCode) => {
    expect(canonicalProjectClassificationCode(rawCode)).toBe(canonicalCode);
  });

  it("leaves native, unknown, and empty codes unchanged", () => {
    expect(canonicalProjectClassificationCode("REAL_CASE_VPA")).toBe("REAL_CASE_VPA");
    expect(canonicalProjectClassificationCode("UNKNOWN")).toBe("UNKNOWN");
    expect(canonicalProjectClassificationCode(undefined)).toBeUndefined();
  });

  it.each([
    [10, "REAL_CASE_VPA", "Real Case VPA"],
    [11, "REGULAR_VPA", "Regular VPA"],
  ])(
    "returns canonical code and description for raw classification ID %s",
    (rawClassificationId, canonicalCode, canonicalDescription) => {
      expect(getCanonicalProjectClassificationCodeById(rawClassificationId, projectClassifications)).toBe(
        canonicalCode,
      );
      expect(getCanonicalProjectClassificationDescriptionById(rawClassificationId, projectClassifications)).toBe(
        canonicalDescription,
      );
    },
  );

  it.each([[null], [undefined], [""]])(
    "returns empty canonical values for empty classification ID %s",
    (classificationId) => {
      expect(getCanonicalProjectClassificationCodeById(classificationId, projectClassifications)).toBeUndefined();
      expect(
        getCanonicalProjectClassificationDescriptionById(classificationId, projectClassifications),
      ).toBeUndefined();
      expect(projectClassificationRequiresPoa(classificationId, projectClassifications)).toBe(false);
      expect(projectClassificationRequiresRealCaseVpa(classificationId, projectClassifications)).toBe(false);
    },
  );

  it.each(["PROGRAMME_OF_ACTIVITIES", "POA"])("identifies POA classification code %s", (code) => {
    expect(isPoaProjectClassification(code)).toBe(true);
  });

  it.each(["REGULAR_VPA", "REAL_CASE_VPA", "REGULAR_CASE_VPA", "REAL_CASE_VPA_LEGACY", "VPA_CPA"])(
    "requires a POA for classification code %s",
    (code) => {
      expect(projectClassificationCodeRequiresPoa(code)).toBe(true);
    },
  );

  it.each([
    ["REAL_CASE_VPA_CROSS_LINKAGE", true],
    ["REGULAR_VPA_CROSS_LINKAGE", true],
    ["PROGRAMME_OF_ACTIVITIES", false],
    ["POA", false],
    ["UNKNOWN", false],
    [undefined, false],
  ])("evaluates POA requirement for edge classification code %s", (code, requiresPoa) => {
    expect(projectClassificationCodeRequiresPoa(code)).toBe(requiresPoa);
  });

  it.each([
    [8, true],
    [9, true],
    [10, true],
    [11, true],
    [7, false],
    [999, false],
  ])("evaluates POA requirement for classification ID %s", (classificationId, requiresPoa) => {
    expect(projectClassificationRequiresPoa(classificationId, projectClassifications)).toBe(requiresPoa);
  });

  it.each(["REGULAR_VPA", "REGULAR_CASE_VPA", "REGULAR_VPA_CROSS_LINKAGE"])(
    "requires a Real Case VPA for classification code %s",
    (code) => {
      expect(projectClassificationCodeRequiresRealCaseVpa(code)).toBe(true);
    },
  );

  it.each([
    ["REAL_CASE_VPA", false],
    ["REAL_CASE_VPA_CROSS_LINKAGE", false],
    ["PROGRAMME_OF_ACTIVITIES", false],
    ["UNKNOWN", false],
    [undefined, false],
  ])("evaluates Real Case VPA requirement for edge classification code %s", (code, requiresRealCaseVpa) => {
    expect(projectClassificationCodeRequiresRealCaseVpa(code)).toBe(requiresRealCaseVpa);
  });

  it.each([
    [9, true],
    [11, true],
    [8, false],
    [10, false],
    [999, false],
  ])("evaluates Real Case VPA requirement for classification ID %s", (classificationId, requiresRealCaseVpa) => {
    expect(projectClassificationRequiresRealCaseVpa(classificationId, projectClassifications)).toBe(
      requiresRealCaseVpa,
    );
  });
});
