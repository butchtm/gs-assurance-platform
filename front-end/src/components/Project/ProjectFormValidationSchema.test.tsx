import { ReferenceTypes } from "../../common/data-types/DataReferenceTypes";
import { AuthContextType } from "../Auth/Login/AuthProvider";

import { getValidationSchemaStep1 } from "./ProjectFormValidationSchema";

const projectClassifications: ReferenceTypes["ProjectClassification"] = [
  { id: 0, code: "NONE", description: "None" },
  { id: 8, code: "REAL_CASE_VPA", description: "Real Case VPA" },
  { id: 9, code: "REGULAR_VPA", description: "Regular VPA" },
  { id: 10, code: "REAL_CASE_VPA_CROSS_LINKAGE", description: "Real Case VPA Cross Linkage" },
  { id: 11, code: "REGULAR_VPA_CROSS_LINKAGE", description: "Regular VPA Cross Linkage" },
];

const allReferenceTypes = {
  ProjectClassification: projectClassifications,
} as ReferenceTypes;

const authContext = {
  isAdminUser: jest.fn().mockReturnValue(false),
  isArmUser: jest.fn().mockReturnValue(false),
} as unknown as AuthContextType;

const validBaseValues = {
  accountId: null,
  projectClassificationId: 8,
  name: "Cross-linked project",
  masterProjectId: 1,
  parentProjectId: null,
};

describe("getValidationSchemaStep1", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (authContext.isAdminUser as jest.Mock).mockReturnValue(false);
    (authContext.isArmUser as jest.Mock).mockReturnValue(false);
  });

  it("requires project developer account for ARM users", async () => {
    (authContext.isArmUser as jest.Mock).mockReturnValue(true);

    await expect(
      getValidationSchemaStep1(allReferenceTypes, authContext).validate(
        {
          ...validBaseValues,
          accountId: null,
        },
        { abortEarly: false },
      ),
    ).rejects.toMatchObject({
      errors: expect.arrayContaining(["Project developer account is required"]),
    });
  });

  it("requires Programme of Activities for raw Real Case VPA cross-link classification", async () => {
    await expect(
      getValidationSchemaStep1(allReferenceTypes, authContext).validate(
        {
          ...validBaseValues,
          projectClassificationId: 10,
          masterProjectId: null,
        },
        { abortEarly: false },
      ),
    ).rejects.toMatchObject({
      errors: expect.arrayContaining(["Programme of Activities is required"]),
    });
  });

  it("requires Programme of Activities and Real Case VPA for raw Regular VPA cross-link classification", async () => {
    await expect(
      getValidationSchemaStep1(allReferenceTypes, authContext).validate(
        {
          ...validBaseValues,
          projectClassificationId: 11,
          masterProjectId: null,
          parentProjectId: null,
        },
        { abortEarly: false },
      ),
    ).rejects.toMatchObject({
      errors: expect.arrayContaining(["Programme of Activities is required", "Real Case VPA is required"]),
    });
  });
});
