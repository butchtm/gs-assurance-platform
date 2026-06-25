import { fireEvent, screen, waitFor } from "@testing-library/react";
import { Formik, useField, useFormikContext } from "formik";

import { renderWithProviders } from "@/test/test-utils";
import { getReferenceValueCodeById, getReferenceValueDescriptionById } from "@/common/data-types/DataReferenceTypes";
import { mockActiveReferenceTypes, mockAllReferenceTypes } from "@/common/data-types/DebugReferenceTypes";
import { mockPlatformAccounts } from "@/common/platform-accounts/DebugPlatformAccounts";
import mockProjects, { mockPOAProjects } from "@/common/project/DebugProject";
import { PermissionGroup } from "@/PermissionGroup";
import { Account } from "@/components/PlatformAccounts/PlatformAccountTypes";
import { User } from "@/components/User/User";
import { getCandidatePOAs, getCandidateParentDetails, getCandidateRealCaseVPAs } from "@/components/Project/Project";

import { Step1 } from "./Step1";

jest.mock("../../Project", () => ({
  ...jest.requireActual("../../Project"),
  getCandidateParentDetails: jest.fn(),
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
const mockGetCustomReferenceOptions = jest.fn().mockReturnValue([]);
const mockProjectClassificationReferenceTypes = [
  { id: 7, code: "PROGRAMME_OF_ACTIVITIES", description: "Programme of Activities", active: true },
  { id: 8, code: "REAL_CASE_VPA", description: "Real Case VPA", active: true },
  { id: 9, code: "REGULAR_VPA", description: "Regular VPA", active: true },
  { id: 10, code: "REAL_CASE_VPA_CROSS_LINKAGE", description: "Real Case VPA Cross Linkage", active: false },
  { id: 11, code: "REGULAR_VPA_CROSS_LINKAGE", description: "Regular VPA Cross Linkage", active: false },
];

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
    allReferenceTypes: {
      ...mockAllReferenceTypes,
      ProjectClassification: mockProjectClassificationReferenceTypes,
    },
    getCustomReferenceOptions: mockGetCustomReferenceOptions,
  }),
  useCustomReferenceOptions: jest.fn().mockReturnValue([
    { value: 1, label: "Option 1" },
    { value: 2, label: "Option 2" },
  ]),
  getReferenceValueDescriptionById: jest.fn(),
}));

const mockFormikValues = (formValues: Record<string, unknown>) => {
  const setFieldValue = jest.fn((field: string, value: unknown) => {
    formValues[field] = value;
    mockSetFieldValue(field, value);
  });

  (useFormikContext as jest.Mock).mockImplementation(() => ({
    values: formValues,
    setFieldValue,
    setFieldTouched: mockSetFieldTouched,
    getFieldProps: jest.fn().mockReturnValue({
      onChange: mockOnChange,
      onBlur: mockOnBlur,
      value: "",
    }),
  }));

  (useField as jest.Mock).mockImplementation((name: string) => [
    {
      onChange: jest.fn(),
      onBlur: jest.fn(),
      value: formValues[name] ?? "",
      name,
    },
    {
      touched: false,
      error: "",
    },
    {
      setValue: jest.fn((value: unknown) => {
        formValues[name] = value;
      }),
    },
  ]);

  return setFieldValue;
};

describe("Step1", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (getReferenceValueDescriptionById as jest.Mock).mockReset();
    (getReferenceValueCodeById as jest.Mock).mockReset();
    (getCandidateParentDetails as jest.Mock).mockReset().mockResolvedValue(null);
    mockGetCustomReferenceOptions.mockReset().mockReturnValue([]);
    (getCandidatePOAs as jest.Mock).mockResolvedValue(mockPOAProjects);
    (getCandidateRealCaseVPAs as jest.Mock).mockResolvedValue([]);
    (useFormikContext as jest.Mock).mockImplementation(() => ({
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
    }));
    (useField as jest.Mock).mockImplementation((name: string) => [
      {
        onChange: jest.fn(),
        onBlur: jest.fn(),
        value: "",
        name,
      },
      {
        touched: false,
        error: "",
      },
      {
        setValue: jest.fn(),
      },
    ]);
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

  it.each([
    {
      rawClassificationId: 10,
      rawClassificationCode: "REAL_CASE_VPA_CROSS_LINKAGE",
      visibleClassificationLabel: "Real Case VPA Cross Linkage",
      showsRealCaseInput: false,
    },
    {
      rawClassificationId: 11,
      rawClassificationCode: "REGULAR_VPA_CROSS_LINKAGE",
      visibleClassificationLabel: "Regular VPA Cross Linkage",
      showsRealCaseInput: true,
    },
  ])(
    "keeps raw cross-link classification label for $rawClassificationId in update fields",
    async ({ rawClassificationId, rawClassificationCode, visibleClassificationLabel, showsRealCaseInput }) => {
      (getReferenceValueCodeById as jest.Mock).mockImplementation((classificationId: number | null) => {
        if (classificationId === rawClassificationId) {
          return rawClassificationCode;
        }
      });
      mockGetCustomReferenceOptions.mockReturnValue([
        { value: 8, label: "Real Case VPA" },
        { value: 9, label: "Regular VPA" },
        { value: 10, label: "Real Case VPA Cross Linkage" },
        { value: 11, label: "Regular VPA Cross Linkage" },
      ]);
      (useFormikContext as jest.Mock).mockImplementation(() => ({
        values: {
          accountId: 1,
          accountName: null,
          projectClassificationId: rawClassificationId,
          masterProjectId: 100,
          parentProjectId: 101,
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
      }));
      (useField as jest.Mock).mockImplementation((name: string) => [
        {
          onChange: jest.fn(),
          onBlur: jest.fn(),
          value: name === "projectClassificationId" ? rawClassificationId : "",
          name,
        },
        {
          touched: false,
          error: "",
        },
        {
          setValue: jest.fn(),
        },
      ]);

      renderWithProviders(
        <Formik initialValues={{}} onSubmit={jest.fn()}>
          <Step1 projectId={mockProjects[1].id} isUpdate={true} />
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
        expect(screen.getByDisplayValue(visibleClassificationLabel)).toBeInTheDocument();
        expect(screen.getAllByLabelText("Programme of Activities*")).not.toHaveLength(0);
      });
      expect(getCandidatePOAs).toHaveBeenCalledWith(1, rawClassificationId);

      if (showsRealCaseInput) {
        expect(screen.getByLabelText("Real Case VPA*")).toBeInTheDocument();
      } else {
        expect(screen.queryByLabelText("Real Case VPA*")).not.toBeInTheDocument();
      }
    },
  );

  it.each([
    { nativeClassificationId: 8, classificationLabel: "Real Case VPA" },
    { nativeClassificationId: 9, classificationLabel: "Regular VPA" },
  ])(
    "fetches candidate POAs with native classification ID $nativeClassificationId",
    async ({ nativeClassificationId, classificationLabel }) => {
      (getReferenceValueCodeById as jest.Mock).mockImplementation((classificationId: number | null) => {
        return mockProjectClassificationReferenceTypes.find((classification) => classification.id === classificationId)
          ?.code;
      });
      mockGetCustomReferenceOptions.mockReturnValue([
        { value: 8, label: "Real Case VPA" },
        { value: 9, label: "Regular VPA" },
      ]);
      mockFormikValues({
        accountId: 1,
        accountName: null,
        projectClassificationId: nativeClassificationId,
        masterProjectId: 100,
        parentProjectId: null,
        name: "",
        projectStatusId: 1,
      });

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
        expect(screen.getByDisplayValue(classificationLabel)).toBeInTheDocument();
        expect(screen.getAllByLabelText("Programme of Activities*")).not.toHaveLength(0);
      });
      expect(getCandidatePOAs).toHaveBeenCalledWith(1, nativeClassificationId);
    },
  );

  it("keeps saved cross-link parent projects visible when candidate APIs omit them", async () => {
    (getReferenceValueCodeById as jest.Mock).mockImplementation((classificationId: number | null) => {
      if (classificationId === 11) {
        return "REGULAR_VPA_CROSS_LINKAGE";
      }
    });
    mockGetCustomReferenceOptions.mockReturnValue([{ value: 9, label: "Regular VPA" }]);
    (getCandidatePOAs as jest.Mock).mockResolvedValue([{ id: 1, gsid: "GS1", name: "Candidate POA" }]);
    (getCandidateRealCaseVPAs as jest.Mock).mockResolvedValue([
      { id: 2, gsid: "GS2", name: "Candidate Real Case VPA" },
    ]);
    (getCandidateParentDetails as jest.Mock).mockImplementation(
      ({ candidateProjectId }: { candidateProjectId: number }) => {
        if (candidateProjectId === 900) {
          return Promise.resolve({ id: 900, gsid: "GS-SAVED-POA", name: "Saved POA" });
        }
        if (candidateProjectId === 901) {
          return Promise.resolve({ id: 901, gsid: "GS-SAVED-REAL", name: "Saved Real Case VPA" });
        }
        return Promise.resolve(null);
      },
    );
    (useFormikContext as jest.Mock).mockImplementation(() => ({
      values: {
        accountId: 1,
        accountName: null,
        projectClassificationId: 11,
        masterProjectId: 900,
        parentProjectId: 901,
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
    }));
    (useField as jest.Mock).mockImplementation((name: string) => [
      {
        onChange: jest.fn(),
        onBlur: jest.fn(),
        value:
          {
            projectClassificationId: 11,
            masterProjectId: 900,
            parentProjectId: 901,
          }[name] ?? "",
        name,
      },
      {
        touched: false,
        error: "",
      },
      {
        setValue: jest.fn(),
      },
    ]);

    renderWithProviders(
      <Formik initialValues={{}} onSubmit={jest.fn()}>
        <Step1 projectId={mockProjects[1].id} isUpdate={true} />
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

    expect(await screen.findByDisplayValue("Saved POA (GS-SAVED-POA)")).toBeInTheDocument();
    expect(await screen.findByDisplayValue("Saved Real Case VPA (GS-SAVED-REAL)")).toBeInTheDocument();
    expect(getCandidateRealCaseVPAs).toHaveBeenCalledWith(1, 900, 11);
    expect(getCandidateParentDetails).toHaveBeenCalledWith(
      expect.objectContaining({
        accountId: 1,
        classificationId: 11,
        candidateProjectId: 900,
      }),
    );
    expect(getCandidateParentDetails).toHaveBeenCalledWith(
      expect.objectContaining({
        accountId: 1,
        classificationId: 11,
        candidateProjectId: 901,
        masterProjectId: 900,
      }),
    );
  });

  it("clears stale Real Case VPA parent from Formik state when POA changes for Regular VPA", async () => {
    (getReferenceValueCodeById as jest.Mock).mockImplementation((classificationId: number | null) => {
      return mockProjectClassificationReferenceTypes.find((classification) => classification.id === classificationId)
        ?.code;
    });
    mockGetCustomReferenceOptions.mockReturnValue([{ value: 9, label: "Regular VPA" }]);
    (getCandidatePOAs as jest.Mock).mockResolvedValue(mockPOAProjects);
    (getCandidateRealCaseVPAs as jest.Mock).mockResolvedValue([{ id: 4, gsid: "GS4", name: "Real Case 4" }]);
    const formValues = {
      accountId: 1,
      accountName: null,
      projectClassificationId: 9,
      masterProjectId: 900,
      parentProjectId: 901,
      creditingPeriods: [
        {
          periodId: 1,
          startDate: "2024-01-01T00:00:00.000Z",
          endDate: "2028-12-31T00:00:00.000Z",
          methodologies: [{ id: 99, version: 1 }],
          monitoringPeriods: [
            {
              id: 1,
              startDate: "2024-01-01T00:00:00.000Z",
              endDate: "2024-12-31T00:00:00.000Z",
              feeSchedule: 2,
            },
          ],
        },
      ],
      name: "",
      projectStatusId: 1,
    };
    const setFieldValue = mockFormikValues(formValues);

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
      expect(screen.getAllByLabelText("Programme of Activities*")).not.toHaveLength(0);
    });

    (getCandidateParentDetails as jest.Mock).mockClear();
    (getCandidateRealCaseVPAs as jest.Mock).mockClear();

    fireEvent.change(screen.getAllByLabelText("Programme of Activities*")[0], {
      target: { value: "Poa 1 (GS1)" },
    });
    fireEvent.click(await screen.findByText("Poa 1 (GS1)"));

    await waitFor(() => {
      expect(formValues.parentProjectId).toBeNull();
    });
    expect(formValues.creditingPeriods).toHaveLength(1);
    expect(formValues.creditingPeriods[0].methodologies).toEqual([{ id: -1, version: null }]);
    expect(formValues.creditingPeriods[0].monitoringPeriods).toBeUndefined();
    expect(setFieldValue).toHaveBeenCalledWith("parentProjectId", null);
    expect(getCandidateRealCaseVPAs).toHaveBeenCalledWith(1, 1, 9);
    expect(getCandidateParentDetails).not.toHaveBeenCalledWith(expect.objectContaining({ candidateProjectId: 901 }));
  });

  it("clears stale crediting periods from Formik state when Real Case VPA changes", async () => {
    (getReferenceValueCodeById as jest.Mock).mockImplementation((classificationId: number | null) => {
      return mockProjectClassificationReferenceTypes.find((classification) => classification.id === classificationId)
        ?.code;
    });
    mockGetCustomReferenceOptions.mockReturnValue([{ value: 9, label: "Regular VPA" }]);
    (getCandidateRealCaseVPAs as jest.Mock).mockResolvedValue([{ id: 4, gsid: "GS4", name: "Real Case 4" }]);
    const formValues = {
      accountId: 1,
      accountName: null,
      projectClassificationId: 9,
      masterProjectId: 900,
      parentProjectId: 901,
      creditingPeriods: [
        {
          periodId: 1,
          startDate: "2024-01-01T00:00:00.000Z",
          endDate: "2028-12-31T00:00:00.000Z",
          methodologies: [{ id: 99, version: 1 }],
          monitoringPeriods: [
            {
              id: 1,
              startDate: "2024-01-01T00:00:00.000Z",
              endDate: "2024-12-31T00:00:00.000Z",
              feeSchedule: 2,
            },
          ],
        },
      ],
      name: "",
      projectStatusId: 1,
    };
    mockFormikValues(formValues);

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

    fireEvent.change(await screen.findByLabelText("Real Case VPA*"), {
      target: { value: "Real Case 4 (GS4)" },
    });
    fireEvent.click(await screen.findByText("Real Case 4 (GS4)"));

    await waitFor(() => {
      expect(formValues.parentProjectId).toBe(4);
    });
    expect(formValues.creditingPeriods).toHaveLength(1);
    expect(formValues.creditingPeriods[0].methodologies).toEqual([{ id: -1, version: null }]);
    expect(formValues.creditingPeriods[0].monitoringPeriods).toBeUndefined();
  });

  it("clears stale parent hierarchy and inherited fields from Formik state when classification changes", async () => {
    (getReferenceValueCodeById as jest.Mock).mockImplementation((classificationId: number | null) => {
      return mockProjectClassificationReferenceTypes.find((classification) => classification.id === classificationId)
        ?.code;
    });
    mockGetCustomReferenceOptions.mockReturnValue([
      { value: 8, label: "Real Case VPA" },
      { value: 9, label: "Regular VPA" },
    ]);
    const formValues = {
      accountId: 1,
      accountName: null,
      projectClassificationId: 9,
      masterProjectId: 900,
      parentProjectId: 901,
      locationIds: [1],
      carbonStreamId: 2,
      sustainableDevelopmentGoalIds: [3],
      sizeId: 4,
      typeIds: [5],
      startDate: "2024-01-01T00:00:00.000Z",
      projectAssetIds: [6],
      masterValues: { locationIds: [1] },
      creditingPeriods: [
        {
          periodId: 1,
          startDate: "2024-01-01T00:00:00.000Z",
          endDate: "2028-12-31T00:00:00.000Z",
          methodologies: [{ id: 99, version: 1 }],
          monitoringPeriods: [
            {
              id: 1,
              startDate: "2024-01-01T00:00:00.000Z",
              endDate: "2024-12-31T00:00:00.000Z",
              feeSchedule: 2,
            },
          ],
        },
      ],
      name: "",
      projectStatusId: 1,
    };
    mockFormikValues(formValues);

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

    fireEvent.change(screen.getByLabelText("Project Classification*"), {
      target: { value: "Real Case VPA" },
    });
    fireEvent.click(await screen.findByRole("option", { name: "Real Case VPA" }));

    await waitFor(() => {
      expect(formValues.masterProjectId).toBeNull();
      expect(formValues.parentProjectId).toBeNull();
      expect(formValues.masterValues).toBeUndefined();
    });
    expect(formValues.locationIds).toEqual([]);
    expect(formValues.carbonStreamId).toBeNull();
    expect(formValues.sustainableDevelopmentGoalIds).toEqual([]);
    expect(formValues.sizeId).toBeNull();
    expect(formValues.typeIds).toEqual([]);
    expect(formValues.startDate).toBeNull();
    expect(formValues.projectAssetIds).toEqual([]);
    expect(formValues.creditingPeriods).toHaveLength(1);
    expect(formValues.creditingPeriods[0].methodologies).toEqual([{ id: -1, version: null }]);
    expect(formValues.creditingPeriods[0].monitoringPeriods).toBeUndefined();
  });

  it("clears stale parent hierarchy and inherited fields from Formik state when account changes", async () => {
    (getReferenceValueCodeById as jest.Mock).mockImplementation((classificationId: number | null) => {
      return mockProjectClassificationReferenceTypes.find((classification) => classification.id === classificationId)
        ?.code;
    });
    mockGetCustomReferenceOptions.mockReturnValue([{ value: 9, label: "Regular VPA" }]);
    const formValues = {
      accountId: 1,
      accountName: "Account One",
      projectClassificationId: 9,
      masterProjectId: 900,
      parentProjectId: 901,
      locationIds: [1],
      carbonStreamId: 2,
      sustainableDevelopmentGoalIds: [3],
      sizeId: 4,
      typeIds: [5],
      startDate: "2024-01-01T00:00:00.000Z",
      projectAssetIds: [6],
      masterValues: { locationIds: [1] },
      creditingPeriods: [
        {
          periodId: 1,
          startDate: "2024-01-01T00:00:00.000Z",
          endDate: "2028-12-31T00:00:00.000Z",
          methodologies: [{ id: 99, version: 1 }],
          monitoringPeriods: [
            {
              id: 1,
              startDate: "2024-01-01T00:00:00.000Z",
              endDate: "2024-12-31T00:00:00.000Z",
              feeSchedule: 2,
            },
          ],
        },
      ],
      name: "",
      projectStatusId: 1,
    };
    mockFormikValues(formValues);

    renderWithProviders(
      <Formik initialValues={{}} onSubmit={jest.fn()}>
        <Step1 />
      </Formik>,
      {
        providerOverrides: {
          auth: {
            isProjectDeveloperUser: jest.fn().mockReturnValue(false),
            isAdminUser: jest.fn().mockReturnValue(true),
            isArmUser: jest.fn().mockReturnValue(false),
          },
          reference: {
            activeReferenceTypes: { ProjectState: [{ id: 1, code: "NEW", description: "New" }] },
          },
        },
      },
    );

    fireEvent.change(await screen.findByTestId("account-search-input"), {
      target: { value: "Account Two" },
    });
    fireEvent.click(await screen.findByText("Account Two"));

    await waitFor(() => {
      expect(formValues.masterProjectId).toBeNull();
      expect(formValues.parentProjectId).toBeNull();
      expect(formValues.masterValues).toBeUndefined();
    });
    expect(formValues.accountName).toBe("Account Two");
    expect(formValues.locationIds).toEqual([]);
    expect(formValues.projectAssetIds).toEqual([]);
    expect(formValues.creditingPeriods).toHaveLength(1);
    expect(formValues.creditingPeriods[0].methodologies).toEqual([{ id: -1, version: null }]);
    expect(formValues.creditingPeriods[0].monitoringPeriods).toBeUndefined();
  });

  it("uses capability props to disable protected update fields", async () => {
    (getReferenceValueCodeById as jest.Mock).mockImplementation((classificationId: number | null) => {
      return mockProjectClassificationReferenceTypes.find((classification) => classification.id === classificationId)
        ?.code;
    });
    mockGetCustomReferenceOptions.mockReturnValue([{ value: 9, label: "Regular VPA" }]);
    (getCandidatePOAs as jest.Mock).mockResolvedValue([{ id: 900, gsid: "GS-POA", name: "Protected POA" }]);
    (getCandidateRealCaseVPAs as jest.Mock).mockResolvedValue([
      { id: 901, gsid: "GS-REAL", name: "Protected Real Case VPA" },
    ]);
    mockFormikValues({
      accountId: 1,
      accountName: "Account One",
      projectClassificationId: 9,
      masterProjectId: 900,
      parentProjectId: 901,
      name: "Editable Title",
      projectStatusId: 1,
    });

    renderWithProviders(
      <Formik initialValues={{}} onSubmit={jest.fn()}>
        <Step1
          isUpdate={true}
          canEditDeveloperAccount={false}
          canEditProjectClassification={true}
          canEditProjectTitle={true}
          canEditParentHierarchy={false}
        />
      </Formik>,
      {
        providerOverrides: {
          auth: {
            isProjectDeveloperUser: jest.fn().mockReturnValue(false),
            isAdminUser: jest.fn().mockReturnValue(true),
            isArmUser: jest.fn().mockReturnValue(false),
          },
          reference: {
            activeReferenceTypes: { ProjectState: [{ id: 1, code: "NEW", description: "New" }] },
          },
        },
      },
    );

    expect(await screen.findByTestId("account-search-input")).toBeDisabled();
    expect(screen.getByLabelText("Project Classification*")).not.toBeDisabled();
    expect(screen.getByLabelText("Project Title*")).not.toBeDisabled();
    expect(await screen.findByLabelText("Real Case VPA*")).toBeDisabled();
    expect(screen.getAllByLabelText("Programme of Activities*")[0]).toBeDisabled();
  });

  it("keeps create mode fields editable by default", async () => {
    renderWithProviders(
      <Formik initialValues={{}} onSubmit={jest.fn()}>
        <Step1 />
      </Formik>,
      {
        providerOverrides: {
          auth: {
            isProjectDeveloperUser: jest.fn().mockReturnValue(false),
            isAdminUser: jest.fn().mockReturnValue(true),
            isArmUser: jest.fn().mockReturnValue(false),
          },
          reference: {
            activeReferenceTypes: { ProjectState: [{ id: 1, code: "NEW", description: "New" }] },
          },
        },
      },
    );

    expect(await screen.findByTestId("account-search-input")).not.toBeDisabled();
    expect(screen.getByLabelText("Project Classification*")).not.toBeDisabled();
    expect(screen.getByLabelText("Project Title*")).not.toBeDisabled();
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
        projectClassificationId: 8,
      },
      setFieldValue: mockSetFieldValue,
    }));

    (getCandidateParentDetails as jest.Mock).mockResolvedValue({
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
    expect(getCandidateParentDetails).toHaveBeenCalledWith(
      expect.objectContaining({
        accountId: 0,
        classificationId: 8,
        candidateProjectId: 1,
      }),
    );
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
