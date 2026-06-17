import { Project } from "../../../common/project/Project";
import { mockProjectForHierarchy } from "../../../common/project/DebugProject";

import { mapProjectDataToFormValues } from "./UpdateProjectHelper";

const createProject = (projectClassificationId: number, displayProjectClassificationId: number): Project => ({
  ...mockProjectForHierarchy,
  projectClassificationId,
  projectClassification: {
    ...mockProjectForHierarchy.projectClassification,
    id: displayProjectClassificationId,
  },
});

describe("mapProjectDataToFormValues", () => {
  it.each([
    { rawClassificationId: 10, displayClassificationId: 8 },
    { rawClassificationId: 11, displayClassificationId: 9 },
  ])(
    "uses raw project classification id $rawClassificationId instead of normalized display id $displayClassificationId",
    ({ rawClassificationId, displayClassificationId }) => {
      const formValues = mapProjectDataToFormValues(createProject(rawClassificationId, displayClassificationId));

      expect(formValues.projectClassificationId).toBe(rawClassificationId);
    },
  );
});
