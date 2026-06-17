import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { fireEvent, screen, waitFor } from "@testing-library/react";
import dayjs from "dayjs";
import { Formik, useFormikContext } from "formik";

import { renderWithProviders } from "@/test/test-utils";
import { ReferenceDataIdsOrId } from "@/common/data-types/DataReferenceTypes";
import { mockActiveReferenceTypes } from "@/common/data-types/DebugReferenceTypes";
import { getInitialEmptyValues } from "@/components/Project/Project";

import { Step2 } from "./Step2";

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

jest.mock("../../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../../common/data-types/DataReferenceTypes"),
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
    getCustomReferenceOptions: () => [
      { value: 1, label: "Option 1" },
      { value: 2, label: "Option 2" },
    ],
  }),
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
            value: 1,
            label: "Large",
          },
        ];
      default:
        return [];
    }
  });

describe("Step2", () => {
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
