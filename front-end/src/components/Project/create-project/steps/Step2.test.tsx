import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { act, fireEvent, screen, waitFor } from "@testing-library/react";
import dayjs from "dayjs";
import { Formik, useFormikContext } from "formik";

import { renderWithProviders } from "@/test/test-utils";
import { ReferenceDataIdsOrId } from "@/common/data-types/DataReferenceTypes";
import { mockActiveReferenceTypes } from "@/common/data-types/DebugReferenceTypes";
import { getCandidateParentDetails, getInitialEmptyValues, getProject } from "@/components/Project/Project";

import { Step2 } from "./Step2";

jest.mock("../../../Reusable/FormFields/CustomTooltipText", () => {
  const actual = jest.requireActual("../../../Reusable/FormFields/CustomTooltipText");
  return {
    __esModule: true,
    ...actual,
    default: ({ text, type }: { text: string | JSX.Element; type: number }) => (
      <div data-testid={type === actual.CustomTooltip.HELP ? "help-icon" : "warning-icon"}>{text}</div>
    ),
  };
});

jest.mock("../../Project", () => ({
  ...jest.requireActual("../../Project"),
  getProject: jest.fn(),
  getCandidateParentDetails: jest.fn(),
}));

jest.mock("formik", () => ({
  ...jest.requireActual("formik"),
  useFormikContext: jest.fn(() => ({
    values: {
      sizeId: null,
      typeIds: [],
    },
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

const mockGetCustomReferenceOptions = jest
  .fn()
  .mockImplementation((reference: string, _currentValue?: ReferenceDataIdsOrId) => {
    switch (reference) {
      case "ProjectSize":
        return [
          {
            value: 1,
            label: "Micro",
          },
          {
            value: 2,
            label: "Small",
          },
          {
            value: 3,
            label: "Large",
          },
        ];
      default:
        return [
          { value: 1, label: "Option 1" },
          { value: 2, label: "Option 2" },
        ];
    }
  });

jest.mock("../../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../../common/data-types/DataReferenceTypes"),
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
    allReferenceTypes: {
      ...mockActiveReferenceTypes,
      ProjectClassification: [
        ...mockActiveReferenceTypes.ProjectClassification,
        { id: 10, code: "REAL_CASE_VPA_CROSS_LINKAGE", description: "Real Case VPA Cross Linkage", active: false },
        {
          id: 11,
          code: "REGULAR_VPA_CROSS_LINKAGE",
          description: "Regular VPA Cross Linkage",
          active: false,
        },
      ],
    },
    getCustomReferenceOptions: mockGetCustomReferenceOptions,
  }),
}));

const createDeferred = <T,>() => {
  let resolve: (value: T) => void = () => {};
  const promise = new Promise<T>((promiseResolve) => {
    resolve = promiseResolve;
  });
  return { promise, resolve };
};

describe("Step2", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (getProject as jest.Mock).mockResolvedValue(null);
    (getCandidateParentDetails as jest.Mock).mockResolvedValue(null);
  });

  it("renders correctly", async () => {
    const useFormikContextMock = useFormikContext as jest.Mock;

    useFormikContextMock.mockImplementation(() => ({
      values: getInitialEmptyValues(),
      setFieldValue: jest.fn(),
    }));

    renderWithProviders(
      <Formik initialValues={getInitialEmptyValues()} onSubmit={jest.fn()}>
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <Step2 />
        </LocalizationProvider>
      </Formik>,
      {
        providerOverrides: {
          reference: {
            activeReferenceTypes: mockActiveReferenceTypes,
            getCustomReferenceOptions: mockGetCustomReferenceOptions,
          },
        },
      },
    );

    await waitFor(() => {
      expect(screen.getByLabelText("Project Description")).toBeInTheDocument();
      expect(screen.getByLabelText("Project Location")).toBeInTheDocument();

      expect(screen.getByRole("combobox", { name: "Project Scale" })).toBeInTheDocument();

      expect(screen.getByLabelText("Project Type(s)*")).toBeInTheDocument();
      expect(screen.getByLabelText("Project Start Date")).toBeInTheDocument();
      expect(screen.getByRole("combobox", { name: "Registration Type" })).toBeInTheDocument();
      expect(screen.getByLabelText("Certified Products")).toBeInTheDocument();
      expect(screen.getByRole("combobox", { name: "Carbon Stream" })).toBeInTheDocument();
    });
  });

  it.each([
    [10, "Only one scale can be selected for the Real Case VPA."],
    [11, "Only one scale can be selected for the Regular Case VPA."],
  ])(
    "uses canonical scale tooltip text for cross-link project classification %s",
    async (classificationId, tooltip) => {
      const useFormikContextMock = useFormikContext as jest.Mock;

      useFormikContextMock.mockImplementation(() => ({
        values: {
          ...getInitialEmptyValues(),
          projectClassificationId: classificationId,
        },
        setFieldValue: jest.fn(),
      }));

      renderWithProviders(
        <Formik initialValues={getInitialEmptyValues()} onSubmit={jest.fn()}>
          <LocalizationProvider dateAdapter={AdapterDayjs}>
            <Step2 />
          </LocalizationProvider>
        </Formik>,
      );

      await screen.findByRole("combobox", { name: "Project Scale" });

      expect(await screen.findByText(tooltip)).toBeInTheDocument();
    },
  );

  it("keeps scale options from the latest project selection when requests resolve out of order", async () => {
    const useFormikContextMock = useFormikContext as jest.Mock;
    const firstProject = createDeferred<{ projectSize: { id: number } }>();
    const secondProject = createDeferred<{ projectSize: { id: number } }>();
    const setFieldValue = jest.fn();
    let values = {
      ...getInitialEmptyValues(),
      accountId: 1,
      projectClassificationId: 11,
      masterProjectId: 1,
      parentProjectId: null,
    };

    (getCandidateParentDetails as jest.Mock).mockImplementation(
      ({ candidateProjectId }: { candidateProjectId: number }) => {
        if (candidateProjectId === 1) {
          return firstProject.promise;
        }
        if (candidateProjectId === 2) {
          return secondProject.promise;
        }
        return Promise.resolve(null);
      },
    );
    useFormikContextMock.mockImplementation(() => ({
      values,
      setFieldValue,
    }));

    const renderStep = () => (
      <Formik initialValues={getInitialEmptyValues()} onSubmit={jest.fn()}>
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <Step2 />
        </LocalizationProvider>
      </Formik>
    );
    const { rerender } = renderWithProviders(renderStep());

    await waitFor(() =>
      expect(getCandidateParentDetails).toHaveBeenCalledWith(
        expect.objectContaining({
          accountId: 1,
          classificationId: 11,
          candidateProjectId: 1,
          masterProjectId: 1,
        }),
      ),
    );

    values = {
      ...values,
      masterProjectId: 2,
    };
    useFormikContextMock.mockImplementation(() => ({
      values,
      setFieldValue,
    }));
    rerender(renderStep());

    await waitFor(() =>
      expect(getCandidateParentDetails).toHaveBeenCalledWith(
        expect.objectContaining({
          accountId: 1,
          classificationId: 11,
          candidateProjectId: 2,
          masterProjectId: 2,
        }),
      ),
    );

    await act(async () => {
      secondProject.resolve({ projectSize: { id: 1 } });
      await secondProject.promise;
    });

    fireEvent.mouseDown(screen.getByRole("combobox", { name: "Project Scale" }));

    await waitFor(() => {
      expect(screen.getByTestId("sizeId-option-1")).toBeInTheDocument();
      expect(screen.queryByTestId("sizeId-option-2")).not.toBeInTheDocument();
      expect(screen.queryByTestId("sizeId-option-3")).not.toBeInTheDocument();
    });

    await act(async () => {
      firstProject.resolve({ projectSize: { id: 3 } });
      await firstProject.promise;
    });

    await waitFor(() => {
      expect(screen.getByTestId("sizeId-option-1")).toBeInTheDocument();
      expect(screen.queryByTestId("sizeId-option-2")).not.toBeInTheDocument();
      expect(screen.queryByTestId("sizeId-option-3")).not.toBeInTheDocument();
    });
    expect(getProject).not.toHaveBeenCalled();
  });

  it("any date can be selected as project start date", () => {
    const useFormikContextMock = useFormikContext as jest.Mock;
    useFormikContextMock.mockImplementation(() => ({
      values: getInitialEmptyValues(),
      setFieldValue: jest.fn(),
    }));

    renderWithProviders(
      <Formik initialValues={getInitialEmptyValues()} onSubmit={jest.fn()}>
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <Step2 />
        </LocalizationProvider>
      </Formik>,
      {
        providerOverrides: {
          reference: {
            activeReferenceTypes: mockActiveReferenceTypes,
            getCustomReferenceOptions: mockGetCustomReferenceOptions,
          },
        },
      },
    );

    const projectStartDate = screen.getByLabelText(/Project Start Date/i);

    const yesterday = dayjs.utc().subtract(1, "day").format("DD/MM/YYYY");
    const today = dayjs.utc().format("DD/MM/YYYY");
    const tomorrow = dayjs.utc().add(1, "day").format("DD/MM/YYYY");

    fireEvent.change(projectStartDate, { target: { value: yesterday } });
    expect(projectStartDate).toHaveValue(yesterday);
    expect(projectStartDate).toBeValid();

    fireEvent.change(projectStartDate, { target: { value: today } });
    expect(projectStartDate).toHaveValue(today);
    expect(projectStartDate).toBeValid();

    fireEvent.change(projectStartDate, { target: { value: tomorrow } });
    expect(projectStartDate).toHaveValue(tomorrow);
    expect(projectStartDate).toBeValid();
  });

  it("should display warning icon for location when value differs from master values", async () => {
    const useFormikContextMock = useFormikContext as jest.Mock;
    useFormikContextMock.mockImplementation(() => ({
      values: {
        ...getInitialEmptyValues(),
        locationIds: [1, 2],
        masterValues: {
          locationIds: [1, 3],
        },
      },
      setFieldValue: jest.fn(),
    }));

    renderWithProviders(
      <Formik initialValues={getInitialEmptyValues()} onSubmit={jest.fn()}>
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <Step2 />
        </LocalizationProvider>
      </Formik>,
      {
        providerOverrides: {
          reference: {
            activeReferenceTypes: mockActiveReferenceTypes,
            getCustomReferenceOptions: mockGetCustomReferenceOptions,
          },
        },
      },
    );

    await waitFor(() => {
      const warningIcons = screen.getAllByTestId("warning-icon");
      expect(warningIcons.length).toBeGreaterThan(0);
    });
  });

  it("should display warning icon for sizeId when value differs from master values", async () => {
    const useFormikContextMock = useFormikContext as jest.Mock;
    useFormikContextMock.mockImplementation(() => ({
      values: {
        ...getInitialEmptyValues(),
        sizeId: 1,
        masterValues: {
          sizeId: 2,
        },
      },
      setFieldValue: jest.fn(),
    }));

    renderWithProviders(
      <Formik initialValues={getInitialEmptyValues()} onSubmit={jest.fn()}>
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <Step2 />
        </LocalizationProvider>
      </Formik>,
      {
        providerOverrides: {
          reference: {
            activeReferenceTypes: mockActiveReferenceTypes,
            getCustomReferenceOptions: mockGetCustomReferenceOptions,
          },
        },
      },
    );

    await waitFor(() => {
      const warningIcons = screen.getAllByTestId("warning-icon");
      expect(warningIcons.length).toBeGreaterThan(0);
    });
  });

  it("should display warning icon for project start date when value differs from master values", async () => {
    const useFormikContextMock = useFormikContext as jest.Mock;
    const currentDate = dayjs.utc("2024-01-01").toISOString();
    const masterDate = dayjs.utc("2024-06-01").toISOString();

    useFormikContextMock.mockImplementation(() => ({
      values: {
        ...getInitialEmptyValues(),
        startDate: currentDate,
        masterValues: {
          startDate: masterDate,
        },
      },
      setFieldValue: jest.fn(),
    }));

    renderWithProviders(
      <Formik initialValues={getInitialEmptyValues()} onSubmit={jest.fn()}>
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <Step2 />
        </LocalizationProvider>
      </Formik>,
      {
        providerOverrides: {
          reference: {
            activeReferenceTypes: mockActiveReferenceTypes,
            getCustomReferenceOptions: mockGetCustomReferenceOptions,
          },
        },
      },
    );

    await waitFor(() => {
      const warningIcons = screen.getAllByTestId("warning-icon");
      expect(warningIcons.length).toBeGreaterThan(0);
    });
  });
});
