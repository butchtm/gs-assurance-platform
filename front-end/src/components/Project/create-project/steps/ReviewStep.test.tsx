import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { render, screen, waitFor } from "@testing-library/react";
import dayjs from "dayjs";
import { Formik, useFormikContext } from "formik";

import * as DataReferenceTypes from "../../../../common/data-types/DataReferenceTypes";
import { mockActiveReferenceTypes } from "../../../../common/data-types/DebugReferenceTypes";
import { analyseText, highProbability, mediumProbability } from "../../../../common/piiAnalysis/PiiAnalysis";
import mockProjects, { mockCreateProjectFormValues } from "../../../../common/project/DebugProject";
import { AuthContext, AuthContextType } from "../../../Auth/Login/AuthProvider";
import { getAllProjectsForAccount } from "../../Project";

import { ReviewStep } from "./ReviewStep";

jest.mock("../../Project", () => ({
  getProject: jest.fn(),
  getAllProjectsForAccount: jest.fn(),
}));

jest.mock("../../../../common/piiAnalysis/PiiAnalysis", () => ({
  ...jest.requireActual("../../../../common/piiAnalysis/PiiAnalysis"),
  analyseText: jest.fn(),
}));

const mockGetAllProjectsForAccount = getAllProjectsForAccount as jest.MockedFunction<typeof getAllProjectsForAccount>;
const mockAnalyseText = analyseText as jest.MockedFunction<typeof analyseText>;

jest.mock("formik", () => ({
  ...jest.requireActual("formik"),
  useFormikContext: jest.fn(() => ({
    values: {
      locationIds: [],
      name: "",
      sizeId: null,
      projectAssetIds: [],
      transitioningProject: "",
      startDate: null,
      projectClassificationId: null,
      registrationTypeId: null,
      carbonStreamId: null,
      description: "",
      typeIds: [],
      methodologyIds: [],
      estimatedAnnualUnits: null,
      goldStandardVersion: "",
      feeScheduleId: null,
      notes: "",
      sustainableDevelopmentGoalIds: [],
      creditingPeriods: [],
      textAnalysisResults: undefined,
    },
  })),
}));

jest.mock("../../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../../common/data-types/DataReferenceTypes"),
  getReferenceValueCodeById: jest.fn(),
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
  }),
  useCustomReferenceOptions: jest.fn().mockReturnValue([
    { value: 1, label: "Option 1" },
    { value: 2, label: "Option 2" },
  ]),
  getReferenceValueDescriptionById: jest.fn(),
}));

const mockAuthProvider = {
  isAdminUser: jest.fn(),
  isArmUser: jest.fn(),
} as Record<keyof AuthContextType, jest.Mock>;

describe("ReviewStep", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockGetAllProjectsForAccount.mockResolvedValue(mockProjects);
    (DataReferenceTypes.getReferenceValueDescriptionById as jest.Mock).mockReset();
    mockAuthProvider.isAdminUser.mockReturnValue(true);
    mockAuthProvider.isArmUser.mockReturnValue(false);
  });

  it("ReviewStep renders values correctly", async () => {
    const useFormikContextMock = useFormikContext as jest.Mock;

    useFormikContextMock.mockImplementation(() => ({
      values: mockCreateProjectFormValues,
      setFieldValue: jest.fn(),
    }));

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <Formik initialValues={mockCreateProjectFormValues} onSubmit={jest.fn()}>
          <LocalizationProvider dateAdapter={AdapterDayjs}>
            <ReviewStep />
          </LocalizationProvider>
        </Formik>
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      expect(screen.getByText("Test Project")).toBeInTheDocument();

      expect(screen.getByText("Test Project Description")).toBeInTheDocument();
      expect(screen.getByText("Note-taking")).toBeInTheDocument();

      const startDate = dayjs.utc().add(+10, "day");
      expect(screen.getByText(startDate.format("DD/MM/YYYY"))).toBeInTheDocument();

      const creditingPeriodStartDateOtherSchemes = dayjs.utc().add(+1, "day");
      expect(screen.getByText(creditingPeriodStartDateOtherSchemes.format("DD/MM/YYYY"))).toBeInTheDocument();

      const creditingPeriodEndDateOtherSchemes = dayjs.utc().add(+2, "day");
      expect(screen.getByText(creditingPeriodEndDateOtherSchemes.format("DD/MM/YYYY"))).toBeInTheDocument();

      const transitioningProject = "yes";
      expect(screen.getByText(transitioningProject)).toBeInTheDocument();
      expect(screen.getByText("ProjectLocation1, ProjectLocation2")).toBeInTheDocument();
    });

    await waitFor(() => {
      expect(DataReferenceTypes.getReferenceValueDescriptionById).toHaveBeenCalled();
      expect(DataReferenceTypes.getReferenceValueDescriptionById).toHaveBeenCalledWith(
        expect.any(Number),
        expect.arrayContaining([expect.objectContaining({ id: expect.any(Number) })]),
      );
    });
  });

  it("highlights probable PII with correct styles and tooltip", async () => {
    const probablePii = [
      { entity: "NAME", probability: "HIGH", identifiedString: "Joe Bloggs", field: "title" },
      { entity: "NAME", probability: "MEDIUM", identifiedString: "Jane Doe", field: "description" },
    ];

    const initialValues = {
      name: "Joe Bloggs Project",
      description: "Jane Doe is helping",
      notes: "Not PII",
      locationIds: [],
      creditingPeriods: [],
      textAnalysisResults: { probablePii },
    };

    const useFormikContextMock = useFormikContext as jest.Mock;

    useFormikContextMock.mockImplementation(() => ({
      values: initialValues,
      setFieldValue: jest.fn(),
    }));

    mockAnalyseText.mockResolvedValue({
      probablePii: probablePii,
    });

    useFormikContextMock.mockReturnValue({
      values: initialValues,
      setFieldValue: jest.fn(),
    });

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <Formik initialValues={initialValues} onSubmit={jest.fn()}>
          <LocalizationProvider dateAdapter={AdapterDayjs}>
            <ReviewStep />
          </LocalizationProvider>
        </Formik>
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      expect(mockAnalyseText).toHaveBeenCalledWith({
        fieldsToAnalyse: [
          { field: "title", text: "Joe Bloggs Project" },
          { field: "description", text: "Jane Doe is helping" },
          { field: "notes", text: "Not PII" },
        ],
      });
    });

    await waitFor(() => {
      const joeSpan = screen.getByText("Joe");
      expect(joeSpan).toHaveStyle("font-weight: bold");
      expect(joeSpan).toHaveStyle("color: " + highProbability.toString());

      const bloggsSpan = screen.getByText("Bloggs");
      expect(bloggsSpan).toHaveStyle("font-weight: bold");
      expect(bloggsSpan).toHaveStyle("color: " + highProbability.toString());

      const janeSpan = screen.getByText("Jane");
      expect(janeSpan).toHaveStyle("font-weight: bold");
      expect(janeSpan).toHaveStyle("color: " + mediumProbability.toString());

      const doeSpan = screen.getByText("Doe");
      expect(doeSpan).toHaveStyle("font-weight: bold");
      expect(doeSpan).toHaveStyle("color: " + mediumProbability.toString());
    });

    const warningIcons = screen.getAllByTestId("warning-icon");
    expect(warningIcons.length).toBeGreaterThan(0);
  });
});
