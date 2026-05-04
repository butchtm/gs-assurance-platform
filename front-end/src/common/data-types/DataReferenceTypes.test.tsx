import * as DataReferenceTypes from "./DataReferenceTypes";
import { mockActiveReferenceTypes } from "./DebugReferenceTypes";

describe("DataReferenceTypes", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("getReferenceValueDescriptionById should return the correct description", () => {
    const referenceType = mockActiveReferenceTypes.ProjectLocation;
    const expectedDescription = referenceType[0].description;
    const result = DataReferenceTypes.getReferenceValueDescriptionById(referenceType[0].id, referenceType);

    expect(result).toEqual(expectedDescription);
  });

  it("getReferenceValueCodeById should return the correct code", () => {
    const referenceType = mockActiveReferenceTypes.ProjectLocation;
    const expectedCode = referenceType[0].code;
    const result = DataReferenceTypes.getReferenceValueCodeById(referenceType[0].id, referenceType);

    expect(result).toEqual(expectedCode);
  });
});
