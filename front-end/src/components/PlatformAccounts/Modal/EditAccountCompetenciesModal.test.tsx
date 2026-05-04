import { fireEvent, render, screen, waitFor } from "@testing-library/react";

import { ReferenceTypeContext } from "../../../common/data-types/DataReferenceTypes";
import { mockPlatformAccounts } from "../../../common/platform-accounts/DebugPlatformAccounts";
import { debugProject } from "../../../common/project/DebugProject";
import { getAllProjects } from "../../Project/Project";
import * as PlatformAccountModule from "../PlatformAccount";

import { EditAccountCompetenciesModal } from "./EditAccountCompetenciesModal";

jest.mock("../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../common/data-types/DataReferenceTypes"),
}));

jest.mock("../PlatformAccount", () => ({
  getAccountCompetencies: jest.fn(),
  updateAccountCompetencies: jest.fn(),
  createAccountCompetencies: jest.fn(),
}));

jest.mock("../../Project/Project", () => ({
  getAllProjects: jest.fn(),
}));

describe("EditAccountCompetenciesModal", () => {
  beforeEach(() => {
    (getAllProjects as jest.Mock).mockResolvedValue([debugProject]);
  });

  it("correctly displays the options for a selected competency", async () => {
    render(
      <ReferenceTypeContext.Provider
        value={{
          activeReferenceTypes: {},
          inactiveReferenceTypes: {},
          allReferenceTypes: {},
          referenceMethodologies: [],
          getCustomReferenceOptions: jest.fn().mockReturnValue([
            { value: 1, label: "Option 1" },
            { value: 2, label: "Option 2" },
          ]),
          refreshReferenceData: () => Promise.resolve(),
        }}
      >
        <EditAccountCompetenciesModal
          isOpen={true}
          onClose={jest.fn()}
          account={mockPlatformAccounts[0]}
          accounts={[]}
        />
      </ReferenceTypeContext.Provider>,
    );

    fireEvent.mouseDown(screen.getByLabelText(/Project Scale/i));

    const options = await screen.findAllByRole("option");
    fireEvent.click(options[0]);

    await waitFor(() => {
      expect(screen.getByLabelText(/Project Scale/i)).toBeInTheDocument();
    });
  });

  it("correctly updates the competencies of an account", async () => {
    (PlatformAccountModule.getAccountCompetencies as jest.Mock).mockResolvedValue({
      estimatedAnnualEmissionReductionAmount: 0,
      projectGsids: ["GS123"],
      projectTypeIds: [],
      projectLocationIds: [],
      projectScaleIds: [],
      methodologyIds: [1],
      requestTypeIds: [],
      certifiedProductIds: [],
      vvbAssignedIds: [],
      projectDeveloperIds: [],
      sustainableDevelopmentGoalIds: [],
    });

    render(
      <ReferenceTypeContext.Provider
        value={{
          activeReferenceTypes: {},
          inactiveReferenceTypes: {},
          allReferenceTypes: {},
          referenceMethodologies: [],
          getCustomReferenceOptions: jest.fn().mockReturnValue([
            { value: 1, label: "Option 1" },
            { value: 2, label: "Option 2" },
          ]),
          refreshReferenceData: () => Promise.resolve(),
        }}
      >
        <EditAccountCompetenciesModal
          isOpen={true}
          onClose={jest.fn()}
          account={mockPlatformAccounts[0]}
          accounts={[]}
        />
      </ReferenceTypeContext.Provider>,
    );

    // Wait for the component to fetch and set the competencies
    await waitFor(() => expect(screen.getByLabelText(/Project Location/i)).toBeInTheDocument());

    // Select competency
    fireEvent.mouseDown(screen.getByLabelText(/Project Location/i));
    let options = await screen.findAllByRole("option");
    fireEvent.click(options[0]);

    fireEvent.mouseDown(screen.getByLabelText(/Project Scale/i));
    options = await screen.findAllByRole("option");
    fireEvent.click(options[0]);

    // Click save
    fireEvent.click(screen.getByText("Save"));

    await waitFor(() =>
      expect(PlatformAccountModule.updateAccountCompetencies).toHaveBeenCalledWith(
        {
          estimatedAnnualEmissionReductionAmount: 0,
          projectGsids: ["GS123"],
          projectTypeIds: [],
          projectLocationIds: [1],
          projectScaleIds: [1],
          methodologyIds: [1],
          requestTypeIds: [],
          certifiedProductIds: [],
          vvbAssignedIds: [],
          projectDeveloperIds: [],
          sustainableDevelopmentGoalIds: [],
        },
        1,
      ),
    );
  });
});
