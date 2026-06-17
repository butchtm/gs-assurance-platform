import { Box, Card, Container, Typography } from "@mui/material";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { Form, Formik } from "formik";
import React from "react";

import { renderWithProviders } from "@/test/test-utils";
import { ReferenceDataIdsOrId, ReferenceType } from "@/common/data-types/DataReferenceTypes";
import { mockActiveReferenceTypes } from "@/common/data-types/DebugReferenceTypes";
import { ProbablePii } from "@/common/piiAnalysis/PiiAnalysis";
import { mockCreateProjectFormValues } from "@/common/project/DebugProject";

import { Step1 } from "../create-project/steps/Step1";
import { Step2 } from "../create-project/steps/Step2";
import { Step3 } from "../create-project/steps/Step3";
import { Step4 } from "../create-project/steps/Step4";
import { Step5 } from "../create-project/steps/Step5";

import { StepUpdateFields } from "./steps/StepUpdateFields";
import { usePiiAnalysis } from "./usePiiAnalysis";

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => jest.fn(),
  useLocation: () => ({
    state: { projectId: 1 },
  }),
}));

jest.mock("../../../common/piiAnalysis/PiiAnalysis", () => ({
  analyseText: jest.fn(),
}));

jest.mock("../Project", () => ({
  ...jest.requireActual("../Project"),
  getProject: jest.fn().mockResolvedValue({
    ...mockCreateProjectFormValues,
    projectStatus: { id: 1, code: "NEW", description: "New" },
  }),
  updateProject: jest.fn().mockResolvedValue(true),
  getInitialEmptyValues: jest.fn().mockReturnValue(mockCreateProjectFormValues),
  getCandidatePOAs: jest.fn().mockResolvedValue([]),
  getCandidateRealCaseVPAs: jest.fn().mockResolvedValue([]),
}));

jest.mock("./UpdateProjectHelper", () => ({
  mapProjectDataToFormValues: jest.fn().mockResolvedValue({
    ...mockCreateProjectFormValues,
    name: "Test Project with John Smith",
    description: "This description contains john@example.com",
    notes: "Contact John Doe at 555-123-4567",
  }),
}));

jest.mock("../../PlatformAccounts/PlatformAccount", () => ({
  getAccounts: jest.fn().mockResolvedValue({
    content: [
      { id: 1, name: "Test Account 1", accountTypeId: 1 },
      { id: 2, name: "Test Account 2", accountTypeId: 1 },
    ],
    totalElements: 2,
  }),
}));

jest.mock("./usePiiAnalysis");

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
    referenceMethodologies: [
      { id: 1, description: "Methodology 1", active: true, inUse: true, code: "METH1" },
      { id: 2, description: "Methodology 2", active: true, inUse: true, code: "METH2" },
      { id: 3, description: "Methodology 3", active: false, inUse: false, code: "METH3" },
    ],
  }),
  getReferenceValueDescriptionById: jest.fn(),
}));

const mockPiiData: ProbablePii[] = [
  {
    field: "title",
    identifiedString: "John Smith",
    entity: "PERSON",
    probability: "0.95",
  },
  {
    field: "description",
    identifiedString: "john@example.com",
    entity: "EMAIL_ADDRESS",
    probability: "0.98",
  },
  {
    field: "notes",
    identifiedString: "555-123-4567",
    entity: "PHONE_NUMBER",
    probability: "0.92",
  },
  {
    field: "notes",
    identifiedString: "John Doe",
    entity: "PERSON",
    probability: "0.89",
  },
];

const FormContent: React.FC<{ probablePii: ProbablePii[] }> = ({ probablePii }) => {
  const mockUsePiiAnalysis = usePiiAnalysis as jest.MockedFunction<typeof usePiiAnalysis>;
  mockUsePiiAnalysis.mockReturnValue({
    probablePii: probablePii,
    isAnalyzing: false,
    error: null,
    hasPii: probablePii.length > 0,
    getPiiForField: (fieldName: string) => probablePii.filter((pii) => pii.field === fieldName),
  });

  return (
    <Form autoComplete="off">
      <Step1 projectId={1} isUpdate={true} probablePii={probablePii} />
      <Step2 probablePii={probablePii} />
      <Step3 isUpdate={true} />
      <Step4 />
      <Step5 probablePii={probablePii} />
      <StepUpdateFields projectStatus={{ id: 1, code: "NEW", description: "New" }} />
    </Form>
  );
};

describe("UpdateProject PiiAnalysis Popover Tests", () => {
  beforeEach(() => {
    jest.clearAllMocks();

    const mockUsePiiAnalysis = usePiiAnalysis as jest.MockedFunction<typeof usePiiAnalysis>;
    mockUsePiiAnalysis.mockReturnValue({
      probablePii: mockPiiData,
      isAnalyzing: false,
      error: null,
      hasPii: true,
      getPiiForField: (fieldName: string) => mockPiiData.filter((pii) => pii.field === fieldName),
    });
  });

  const renderUpdateProjectForm = (customPiiData = mockPiiData) => {
    const formValues = {
      ...mockCreateProjectFormValues,
      name: "Test Project John Smith",
      description: "This description contains john@example.com",
      notes: "Contact John Doe at 555-123-4567",
    };

    renderWithProviders(
      <Container maxWidth="md">
        <Card className="wizard-card">
          <Box>
            <Typography variant="h4">Update Project</Typography>
            <br />
          </Box>
          <Formik initialValues={formValues} onSubmit={jest.fn()}>
            <FormContent probablePii={customPiiData} />
          </Formik>
        </Card>
      </Container>,
      {
        providerOverrides: {
          reference: {
            activeReferenceTypes: mockActiveReferenceTypes,
            referenceMethodologies: [
              { id: 1, description: "Methodology 1", active: true, inUse: true, code: "METH1" },
              { id: 2, description: "Methodology 2", active: true, inUse: true, code: "METH2" },
              { id: 3, description: "Methodology 3", active: false, inUse: false, code: "METH3" },
            ],
            getCustomReferenceOptions: mockGetCustomReferenceOptions,
          },
        },
      },
    );
  };

  it("should display PII warning icon for name field when PII is detected", async () => {
    renderUpdateProjectForm();

    await waitFor(() => {
      const nameFields = screen.getAllByLabelText("Project Title*");
      expect(nameFields.length).toBeGreaterThan(0);
      const nameField = nameFields[0];
      expect(nameField).toBeInTheDocument();

      const warningIcons = screen.queryAllByTestId("WarningIcon") || screen.queryAllByRole("button");
      expect(warningIcons.length).toBeGreaterThan(0);
    });
  });

  it("should show popover with PII details when warning icon is clicked for name field", async () => {
    const user = userEvent.setup();
    renderUpdateProjectForm();

    await waitFor(() => {
      const warningIcons = screen.queryAllByTestId("WarningIcon");
      expect(warningIcons.length).toBeGreaterThan(0);
    });

    const warningIcons = screen.getAllByTestId("WarningIcon");
    const piiWarningIcon = warningIcons[0].closest("button");
    expect(piiWarningIcon).toBeInTheDocument();

    if (piiWarningIcon) {
      await user.click(piiWarningIcon);
    }

    await waitFor(() => {
      expect(screen.getByText("PII Detected")).toBeInTheDocument();
      expect(
        screen.getByText("The following potentially personally identifiable information was detected in this field:"),
      ).toBeInTheDocument();
      expect(screen.getByText("John Smith")).toBeInTheDocument();
      expect(screen.getByText("PERSON (0.95 probability)")).toBeInTheDocument();
    });
  });

  it("should display PII warning icon for description field when PII is detected", async () => {
    renderUpdateProjectForm();

    await waitFor(() => {
      const descriptionField = screen.getByLabelText("Project Description");
      expect(descriptionField).toBeInTheDocument();

      const warningIcons = screen.queryAllByTestId("WarningIcon");
      expect(warningIcons.length).toBeGreaterThanOrEqual(2);
    });
  });

  it("should show popover with email PII details when warning icon is clicked for description field", async () => {
    const user = userEvent.setup();
    renderUpdateProjectForm();

    await waitFor(() => {
      const warningIcons = screen.queryAllByTestId("WarningIcon");
      expect(warningIcons.length).toBeGreaterThanOrEqual(2);
    });

    const warningIcons = screen.getAllByTestId("WarningIcon");
    const piiWarningIcon = warningIcons[1].closest("button");
    expect(piiWarningIcon).toBeInTheDocument();

    if (piiWarningIcon) {
      await user.click(piiWarningIcon);
    }

    await waitFor(() => {
      expect(screen.getByText("PII Detected")).toBeInTheDocument();
      expect(screen.getByText("john@example.com")).toBeInTheDocument();
      expect(screen.getByText("EMAIL_ADDRESS (0.98 probability)")).toBeInTheDocument();
    });
  });

  it("should display PII warning icon for notes field when PII is detected", async () => {
    renderUpdateProjectForm();

    await waitFor(() => {
      const notesField = screen.getByLabelText("Notes");
      expect(notesField).toBeInTheDocument();

      const warningIcons = screen.queryAllByTestId("WarningIcon");
      expect(warningIcons.length).toBeGreaterThanOrEqual(3);
    });
  });

  it("should show popover with multiple PII details when warning icon is clicked for notes field", async () => {
    const user = userEvent.setup();
    renderUpdateProjectForm();

    await waitFor(() => {
      const warningIcons = screen.queryAllByTestId("WarningIcon");
      expect(warningIcons.length).toBeGreaterThanOrEqual(3);
    });

    const warningIcons = screen.getAllByTestId("WarningIcon");
    const piiWarningIcon = warningIcons[2].closest("button");
    expect(piiWarningIcon).toBeInTheDocument();

    if (piiWarningIcon) {
      await user.click(piiWarningIcon);
    }

    await waitFor(() => {
      expect(screen.getByText("PII Detected")).toBeInTheDocument();
      const hasPhoneNumber = screen.queryByText("555-123-4567");
      const hasPersonName = screen.queryByText("John Doe");
      expect(hasPhoneNumber || hasPersonName).toBeTruthy();
    });
  });
});
