import { Box, Card, Container, Typography } from "@mui/material";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { Formik, useFormikContext } from "formik";

import {
  ReferenceDataIdsOrId,
  ReferenceType,
  ReferenceTypeContext,
} from "../../../common/data-types/DataReferenceTypes";
import { mockActiveReferenceTypes } from "../../../common/data-types/DebugReferenceTypes";
import { mockCreateProjectFormValues } from "../../../common/project/DebugProject";
import { AuthContext, AuthContextType } from "../../Auth/Login/AuthProvider";
import { FormikStep } from "../../Reusable/Formik/FormikStep";
import { FormikStepper } from "../../Reusable/Formik/FormikStepper";

import { getWarningForProjectTypes, warningText } from "./CreateProject";
import { ReviewStep } from "./steps/ReviewStep";
import { Step1 } from "./steps/Step1";
import { Step2 } from "./steps/Step2";
import { Step3 } from "./steps/Step3";
import { Step4 } from "./steps/Step4";
import { Step5 } from "./steps/Step5";

const mockAuthProvider = {
  isProjectDeveloperUser: jest.fn(),
  isAdminUser: jest.fn(),
  isArmUser: jest.fn(),
} as Record<keyof AuthContextType, jest.Mock>;

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => jest.fn(),
}));

jest.mock("../../../common/piiAnalysis/PiiAnalysis", () => ({
  analyseText: jest.fn(),
}));

jest.mock("../Project", () => ({
  ...jest.requireActual("../Project"),
  getProject: jest.fn().mockResolvedValue(mockCreateProjectFormValues),
  getCandidatePOAs: jest.fn(),
  getCandidateRealCaseVPAs: jest.fn(),
}));

jest.mock("formik", () => ({
  ...jest.requireActual("formik"),
  useFormikContext: jest.fn(() => ({
    values: mockCreateProjectFormValues,
  })),
}));

const toOptions = (array: ReferenceType[]) => {
  return array.map((item) => {
    return {
      label: item.description,
      value: item.id,
    };
  });
};

const mockGetCustomReferenceOptions = jest
  .fn()
  .mockImplementation((reference: string, _currentValue?: ReferenceDataIdsOrId) => {
    switch (reference) {
      case "TransitionPathway":
        return toOptions(mockActiveReferenceTypes.TransitionPathway);
      case "TransitionApprovalProcedure":
        return toOptions(mockActiveReferenceTypes.TransitionApprovalProcedure);
      case "TransitionFromOtherScheme":
        return toOptions(mockActiveReferenceTypes.TransitionFromOtherScheme);
      case "SustainableDevelopmentGoal":
        return toOptions(mockActiveReferenceTypes.SustainableDevelopmentGoal);
      case "ProjectSize":
        return toOptions(mockActiveReferenceTypes.ProjectSize);
      case "CarbonStream":
        return toOptions(mockActiveReferenceTypes.CarbonStream);
      case "ProjectType":
        return toOptions(mockActiveReferenceTypes.ProjectType);
      case "RegistrationType":
        return toOptions(mockActiveReferenceTypes.RegistrationType);
      case "FeeSchedule":
        return toOptions(mockActiveReferenceTypes.FeeSchedule);
      case "ProjectAsset":
        return toOptions(mockActiveReferenceTypes.ProjectAsset);
      default:
        return [];
    }
  });

jest.mock("../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../common/data-types/DataReferenceTypes"),
  getReferenceValueCodeById: jest.fn(),
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
    getCustomReferenceOptions: mockGetCustomReferenceOptions,
  }),
  getReferenceValueDescriptionById: jest.fn(),
}));

describe("CreateProject", () => {
  it.each([
    { id: "Project Title", expectedText: "Project Title*" },
    { id: "Project Scale", expectedText: "Project Scale", comboBox: true },
    { id: "Certified Products", expectedText: "Certified Products" },
    { id: "Transitioning Project", expectedText: "Transitioning Project", comboBox: true },
    { id: "Project Start Date", expectedText: "Project Start Date" },
    { id: "Project Classification", expectedText: "Project Classification*" },
    { id: "Project Country", expectedText: "Project Location" },
    { id: "Registration Type", expectedText: "Registration Type", comboBox: true },
    { id: "Carbon Stream", expectedText: "Carbon Stream", comboBox: true },
    { id: "Project Types", expectedText: "Project Type(s)*" },
    { id: "Estimated Annual Emission Reduction", expectedText: "Estimated Annual Emission Reduction/Removal" },
    { id: "Fee Schedule", expectedText: "Fee Schedule", comboBox: true },
    { id: "Notes", expectedText: "Notes" },
    { id: "Project SDG", expectedText: "Sustainable Development Goals" },
  ])("when edit field is clicked correct step is displayed %o", async (params) => {
    const useFormikContextMock = useFormikContext as jest.Mock;

    useFormikContextMock.mockImplementation(() => ({
      values: mockCreateProjectFormValues,
      setFieldValue: jest.fn(),
    }));

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <ReferenceTypeContext.Provider
          value={{
            activeReferenceTypes: mockActiveReferenceTypes,
            inactiveReferenceTypes: {},
            allReferenceTypes: {},
            referenceMethodologies: [],
            getCustomReferenceOptions: mockGetCustomReferenceOptions,
            refreshReferenceData: () => Promise.resolve(),
          }}
        >
          <Formik initialValues={mockCreateProjectFormValues} onSubmit={jest.fn()}>
            <LocalizationProvider dateAdapter={AdapterDayjs}>
              <Container maxWidth="md">
                <Card className="wizard-card">
                  <Box>
                    <Typography variant="h4">New Project</Typography>
                    <br></br>
                  </Box>
                  <FormikStepper
                    initialValues={mockCreateProjectFormValues}
                    onSubmit={async (values) => {
                      return await Promise.resolve(values);
                    }}
                    setToLastStep={true}
                  >
                    <FormikStep label="Start">
                      <Step1 />
                    </FormikStep>
                    <FormikStep label="Details">
                      <Step2 />
                    </FormikStep>
                    <FormikStep label="Methodologies">
                      <Step3 />
                    </FormikStep>
                    <FormikStep label="Transitioning">
                      <Step4 />
                    </FormikStep>
                    <FormikStep label="Goals">
                      <Step5 />
                    </FormikStep>
                    <FormikStep label="Review">
                      <ReviewStep />
                    </FormikStep>
                  </FormikStepper>
                </Card>
              </Container>
            </LocalizationProvider>
          </Formik>
        </ReferenceTypeContext.Provider>
      </AuthContext.Provider>,
    );

    expect(screen.getByText("Review Your Information")).toBeInTheDocument();
    const linkElement = screen.getByTestId(params.id);
    fireEvent.click(linkElement);

    await waitFor(() => {
      const getInput = (isComboBox: boolean, expectedText: string): HTMLElement => {
        if (isComboBox) {
          return screen.getByRole("combobox", { name: expectedText });
        }
        return screen.getByLabelText(expectedText);
      };
      expect(getInput(params.comboBox ?? false, params.expectedText)).toBeInTheDocument();
    });
  });
});

describe("getWarningForProjectTypes", () => {
  it("should return undefined when no current value exists", () => {
    expect(getWarningForProjectTypes(undefined, [1, 2])).toBeUndefined();
  });

  it("should return undefined when no master value exists", () => {
    expect(getWarningForProjectTypes([1, 2], undefined)).toBeUndefined();
  });

  it("should return undefined when current types are a subset of master types", () => {
    // Master has [1, 2], current has [1]
    expect(getWarningForProjectTypes([1], [1, 2])).toBeUndefined();
  });

  it("should return undefined when current types match master types exactly", () => {
    // Master has [1, 2], current has [1, 2]
    expect(getWarningForProjectTypes([1, 2], [1, 2])).toBeUndefined();
  });

  it("should return undefined when current types are same as master but different order", () => {
    // Master has [1, 2], current has [2, 1]
    expect(getWarningForProjectTypes([2, 1], [1, 2])).toBeUndefined();
  });

  it("should return warning when current types include types not in master", () => {
    // Master has [1, 2], current has [1, 3] - includes type 3 which is not in master
    expect(getWarningForProjectTypes([1, 3], [1, 2])).toBe(warningText);
  });

  it("should return warning when current types are completely different from master", () => {
    // Master has [1, 2], current has [3]
    expect(getWarningForProjectTypes([3], [1, 2])).toBe(warningText);
  });

  it("should return warning when current types include master types plus additional ones", () => {
    // Master has [1, 2], current has [1, 2, 3] - includes all master types plus type 3
    expect(getWarningForProjectTypes([1, 2, 3], [1, 2])).toBe(warningText);
  });
});
