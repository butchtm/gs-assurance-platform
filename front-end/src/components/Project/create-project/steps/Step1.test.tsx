import { fireEvent, screen, waitFor } from "@testing-library/react";
import { Formik, useFormikContext } from "formik";

import { renderWithProviders } from "@/test/test-utils";
import { getReferenceValueCodeById, getReferenceValueDescriptionById } from "@/common/data-types/DataReferenceTypes";
import { mockActiveReferenceTypes, mockAllReferenceTypes } from "@/common/data-types/DebugReferenceTypes";
import { mockPlatformAccounts } from "@/common/platform-accounts/DebugPlatformAccounts";
import mockProjects, { mockPOAProjects } from "@/common/project/DebugProject";
import { PermissionGroup } from "@/PermissionGroup";
import { Account } from "@/components/PlatformAccounts/PlatformAccountTypes";
import { User } from "@/components/User/User";
import { getCandidatePOAs, getProject } from "@/components/Project/Project";

import { Step1 } from "./Step1";

jest.mock("../../Project", () => ({
  ...jest.requireActual("../../Project"),
  getProject: jest.fn(),
  getCandidatePOAs: jest.fn(),
  getCandidateRealCaseVPAs: jest.fn(),
}));

jest.mock("../../../PlatformAccounts/PlatformAccount", () => ({
  getAccount: jest.fn(),
  getAccounts: jest.fn(() => ({
    content: mockPlatformAccounts,
    totalPages: 1,
    pageNumber: 1,
    totalElements: mockPlatformAccounts.length,
  })),
}));

const mockSetFieldValue = jest.fn();
const mockSetFieldTouched = jest.fn();
const mockOnChange = jest.fn();
const mockOnBlur = jest.fn();

jest.mock("formik", () => ({
  ...jest.requireActual("formik"),
  useFormikContext: jest.fn(() => ({
    values: {
      accountId: null,
      accountName: null,
      projectClassificationId: null,
      masterProjectId: null,
      name: "",
      projectStatusId: 1,
    },
    setFieldValue: mockSetFieldValue,
    setFieldTouched: mockSetFieldTouched,
    getFieldProps: jest.fn().mockReturnValue({
      onChange: mockOnChange,
      onBlur: mockOnBlur,
      value: "",
    }),
  })),
  useField: jest.fn(() => [
    {
      onChange: jest.fn(),
      onBlur: jest.fn(),
      value: "",
      name: "",
    },
    {
      touched: false,
      error: "",
    },
    {
      setValue: jest.fn(),
    },
  ]),
}));

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => jest.fn(),
}));

jest.mock("../../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../../common/data-types/DataReferenceTypes"),
  getReferenceValueCodeById: jest.fn(),
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
    allReferenceTypes: mockAllReferenceTypes,
    getCustomReferenceOptions: jest.fn().mockReturnValue([]),
  }),
  useCustomReferenceOptions: jest.fn().mockReturnValue([
    { value: 1, label: "Option 1" },
    { value: 2, label: "Option 2" },
  ]),
  getReferenceValueDescriptionById: jest.fn(),
}));

describe("Step1", () => {
  beforeEach(() => {
    (getReferenceValueDescriptionById as jest.Mock).mockReset();
    (getCandidatePOAs as jest.Mock).mockResolvedValue(mockPOAProjects);
  });

  it("renders correctly", async () => {
    renderWithProviders(
      <Formik initialValues={{}} onSubmit={jest.fn()}>
        <Step1 />
      </Formik>,
      {
        providerOverrides: {
          auth: {
            isProjectDeveloperUser: jest.fn().mockReturnValue(false),
            isAdminUser: jest.fn().mockReturnValue(false),
            isArmUser: jest.fn().mockReturnValue(false),
          },
          reference: {
            activeReferenceTypes: { ProjectState: [{ id: 1, code: "NEW", description: "New" }] },
          },
        },
      },
    );

    await waitFor(() => {
      expect(screen.getByLabelText("Project Title*")).toBeInTheDocument();
      expect(screen.getByLabelText("Project Classification*")).toBeInTheDocument();
    });
  });

  it('renders "Programme of Activities*" and "Real Case*" for "REGULAR_VPA"', async () => {
    const mockGetReferenceValueCodeById = getReferenceValueCodeById as jest.Mock;

    mockGetReferenceValueCodeById.mockImplementation(() => {
      return "REGULAR_VPA";
    });

    renderWithProviders(
      <Formik initialValues={{ projectClassificationId: "someId" }} onSubmit={jest.fn()}>
        <Step1 />
      </Formik>,
      {
        providerOverrides: {
          auth: {
            isProjectDeveloperUser: jest.fn().mockReturnValue(false),
            isAdminUser: jest.fn().mockReturnValue(false),
            isArmUser: jest.fn().mockReturnValue(false),
          },
          reference: {
            activeReferenceTypes: { ProjectState: [{ id: 1, code: "NEW", description: "New" }] },
          },
        },
      },
    );

    await waitFor(() => {
      expect(screen.getAllByLabelText("Programme of Activities*")).not.toHaveLength(0);
      expect(screen.getByLabelText("Real Case VPA*")).toBeInTheDocument();
    });
  });

  it('renders "Programme of Activities*" for "REAL_CASE_VPA"', async () => {
    const mockGetReferenceValueCodeById = getReferenceValueCodeById as jest.Mock;

    mockGetReferenceValueCodeById.mockImplementation(() => {
      return "REAL_CASE_VPA";
    });

    renderWithProviders(
      <Formik initialValues={{ projectClassificationId: "someId" }} onSubmit={jest.fn()}>
        <Step1 />
      </Formik>,
      {
        providerOverrides: {
          auth: {
            isProjectDeveloperUser: jest.fn().mockReturnValue(false),
            isAdminUser: jest.fn().mockReturnValue(false),
            isArmUser: jest.fn().mockReturnValue(false),
          },
          reference: {
            activeReferenceTypes: { ProjectState: [{ id: 1, code: "NEW", description: "New" }] },
          },
        },
      },
    );

    await waitFor(() => {
      expect(screen.getAllByLabelText("Programme of Activities*")).not.toHaveLength(0);
      expect(screen.queryByLabelText("Real Case VPA*")).not.toBeInTheDocument();
    });
  });

  it("Uses chosen POA project information by default when type is REAL_CASE_VPA", async () => {
    const mockGetReferenceValueCodeById = getReferenceValueCodeById as jest.Mock;
    mockGetReferenceValueCodeById.mockImplementation(() => {
      return "REAL_CASE_VPA";
    });
    const mockSetFieldValue = jest.fn();
    const useFormikContextMock = useFormikContext as jest.Mock;
    useFormikContextMock.mockImplementation(() => ({
      values: {
        projectClassificationId: "someId",
      },
      setFieldValue: mockSetFieldValue,
    }));

    (getProject as jest.Mock).mockResolvedValue({
      projectLocations: [{ id: 1 }],
      sustainableDevelopmentGoals: [{ id: 2 }],
      projectAssets: [{ id: 3 }],
      carbonStream: { id: 4 },
      projectSize: { id: 5 },
      projectStartDate: "2023-01-01",
    });

    renderWithProviders(
      <Formik initialValues={{}} onSubmit={jest.fn()}>
        <Step1 />
      </Formik>,
      {
        providerOverrides: {
          auth: {
            isProjectDeveloperUser: jest.fn().mockReturnValue(true),
            isAdminUser: jest.fn().mockReturnValue(false),
            isArmUser: jest.fn().mockReturnValue(false),
            user: {
              account: {
                id: 0,
                name: "Waterworks project manager",
              } as Account,
            } as User,
          },
          reference: {
            activeReferenceTypes: { ProjectState: [{ id: 1, code: "NEW", description: "New" }] },
            allReferenceTypes: { ProjectClassification: [{ id: 1, code: "POA", description: "Master Legacy" }] },
            getCustomReferenceOptions: jest.fn().mockReturnValue([{ value: 1, label: "POA" }]),
          },
        },
      },
    );

    const input = screen.getAllByLabelText("Programme of Activities*")[0];
    fireEvent.change(input, { target: { value: "Poa 1 (GS1)" } });

    await waitFor(() => {
      expect(screen.getByText("Poa 1 (GS1)")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByText("Poa 1 (GS1)"));

    await waitFor(() => {
      expect(mockSetFieldValue).toHaveBeenCalledWith("locationIds", [1]);
      expect(mockSetFieldValue).toHaveBeenCalledWith("carbonStreamId", 4);
      expect(mockSetFieldValue).toHaveBeenCalledWith("sustainableDevelopmentGoalIds", [2]);
      expect(mockSetFieldValue).toHaveBeenCalledWith("sizeId", 5);
    });
  });

  it("DOES render update project status button for GS Admin when component rendered as update project form", async () => {
    renderWithProviders(
      <Formik initialValues={{}} onSubmit={jest.fn()}>
        <Step1 projectId={mockProjects[1].id} isUpdate={true} />
      </Formik>,
      {
        providerOverrides: {
          auth: {
            isAdminUser: jest.fn().mockReturnValue(true),
            isAuthenticated: true,
            user: {
              groups: [PermissionGroup.GS_ADMIN],
            } as User,
          },
          reference: {
            activeReferenceTypes: { ProjectState: [{ id: 1, code: "NEW", description: "New" }] },
          },
        },
      },
    );

    await waitFor(() => {
      expect(screen.getByLabelText("Project Title*")).toBeInTheDocument();
      expect(screen.getByLabelText("Project Classification*")).toBeInTheDocument();
    });

    expect(screen.getByTestId("project-status-update-btn")).toBeInTheDocument();
  });

  it("DOES NOT render update project status button for Project Developer when component rendered as update project form", async () => {
    renderWithProviders(
      <Formik initialValues={{}} onSubmit={jest.fn()}>
        <Step1 projectId={mockProjects[1].id} isUpdate={true} />
      </Formik>,
      {
        providerOverrides: {
          auth: {
            isProjectDeveloperUser: jest.fn().mockReturnValue(true),
          },
          reference: {
            activeReferenceTypes: { ProjectState: [{ id: 1, code: "NEW", description: "New" }] },
          },
        },
      },
    );

    await waitFor(() => {
      expect(screen.getByLabelText("Project Title*")).toBeInTheDocument();
      expect(screen.getByLabelText("Project Classification*")).toBeInTheDocument();
    });

    expect(screen.queryByTestId("project-status-update-btn")).not.toBeInTheDocument();
  });
});
