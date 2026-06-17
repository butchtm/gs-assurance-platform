import { screen } from "@testing-library/react";
import { Formik, useFormikContext } from "formik";

import { renderWithProviders } from "@/test/test-utils";
import { ReferenceDataIdsOrId } from "@/common/data-types/DataReferenceTypes";

import { Step5 } from "./Step5";

const mockSetFieldValue = jest.fn();

jest.mock("formik", () => ({
  ...jest.requireActual("formik"),
  useFormikContext: jest.fn(() => ({
    values: {
      estimatedAnnualUnits: null,
      sustainableDevelopmentGoalIds: [],
      feeScheduleId: null,
    },
    setFieldValue: mockSetFieldValue,
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
  useCustomReferenceOptions: jest.fn().mockReturnValue([
    { value: 1, label: "Option 1" },
    { value: 2, label: "Option 2" },
  ]),
}));

describe("Step5", () => {
  it("renders correctly", () => {
    const mockGetCustomReferenceOptions = jest
      .fn()
      .mockImplementation((reference: string, _currentValue?: ReferenceDataIdsOrId) => {
        switch (reference) {
          case "SustainableDevelopmentGoal":
            return [
              {
                value: 1,
                label: "1 - No Poverty",
              },
            ];
          case "FeeSchedule":
            return [
              {
                value: 0,
                label: "Cash per Credit",
              },
              {
                value: 1,
                label: "Share of Proceeds",
              },
            ];
          default:
            return [];
        }
      });

    renderWithProviders(
      <Formik initialValues={{}} onSubmit={jest.fn()}>
        <Step5 />
      </Formik>,
      {
        providerOverrides: {
          reference: {
            getCustomReferenceOptions: mockGetCustomReferenceOptions,
          },
        },
      },
    );

    expect(screen.getByLabelText("Estimated Annual Emission Reduction/Removal")).toBeInTheDocument();
    expect(screen.getByLabelText("Gold Standard Version")).toBeInTheDocument();
    expect(screen.getByLabelText("Sustainable Development Goals")).toBeInTheDocument();
    expect(screen.getByRole("combobox", { name: "Fee Schedule" })).toBeInTheDocument();
    expect(screen.getByLabelText("Notes")).toBeInTheDocument();
  });

  it("should display warning icon for sustainable development goals when values differ from master values", () => {
    const mockGetCustomReferenceOptions = jest
      .fn()
      .mockImplementation((reference: string, _currentValue?: ReferenceDataIdsOrId) => {
        switch (reference) {
          case "SustainableDevelopmentGoal":
            return [
              {
                value: 1,
                label: "1 - No Poverty",
              },
              {
                value: 2,
                label: "2 - Zero Hunger",
              },
            ];
          case "FeeSchedule":
            return [
              {
                value: 0,
                label: "Cash per Credit",
              },
              {
                value: 1,
                label: "Share of Proceeds",
              },
            ];
          default:
            return [];
        }
      });

    const useFormikContextMock = useFormikContext as jest.Mock;
    useFormikContextMock.mockImplementation(() => ({
      values: {
        estimatedAnnualUnits: null,
        sustainableDevelopmentGoalIds: [1, 2],
        feeScheduleId: null,
        masterValues: {
          sustainableDevelopmentGoalIds: [1, 3],
        },
      },
      setFieldValue: mockSetFieldValue,
    }));

    renderWithProviders(
      <Formik initialValues={{}} onSubmit={jest.fn()}>
        <Step5 />
      </Formik>,
      {
        providerOverrides: {
          reference: {
            getCustomReferenceOptions: mockGetCustomReferenceOptions,
          },
        },
      },
    );

    const warningIcons = screen.getAllByTestId("warning-icon");
    expect(warningIcons.length).toBeGreaterThan(0);
  });

  it("should not display warning icon when sustainable development goals match master values", () => {
    const mockGetCustomReferenceOptions = jest
      .fn()
      .mockImplementation((reference: string, _currentValue?: ReferenceDataIdsOrId) => {
        switch (reference) {
          case "SustainableDevelopmentGoal":
            return [
              {
                value: 1,
                label: "1 - No Poverty",
              },
            ];
          case "FeeSchedule":
            return [
              {
                value: 0,
                label: "Cash per Credit",
              },
            ];
          default:
            return [];
        }
      });

    const useFormikContextMock = useFormikContext as jest.Mock;
    useFormikContextMock.mockImplementation(() => ({
      values: {
        estimatedAnnualUnits: null,
        sustainableDevelopmentGoalIds: [1, 2],
        feeScheduleId: null,
        masterValues: {
          sustainableDevelopmentGoalIds: [1, 2],
        },
      },
      setFieldValue: mockSetFieldValue,
    }));

    renderWithProviders(
      <Formik initialValues={{}} onSubmit={jest.fn()}>
        <Step5 />
      </Formik>,
      {
        providerOverrides: {
          reference: {
            getCustomReferenceOptions: mockGetCustomReferenceOptions,
          },
        },
      },
    );

    const warningIcons = screen.queryAllByTestId("warning-icon");
    expect(warningIcons.length).toBe(0);
  });

  it("should not display warning icon when no master values are present", () => {
    const mockGetCustomReferenceOptions = jest
      .fn()
      .mockImplementation((reference: string, _currentValue?: ReferenceDataIdsOrId) => {
        switch (reference) {
          case "SustainableDevelopmentGoal":
            return [
              {
                value: 1,
                label: "1 - No Poverty",
              },
            ];
          case "FeeSchedule":
            return [
              {
                value: 0,
                label: "Cash per Credit",
              },
            ];
          default:
            return [];
        }
      });

    const useFormikContextMock = useFormikContext as jest.Mock;
    useFormikContextMock.mockImplementation(() => ({
      values: {
        estimatedAnnualUnits: null,
        sustainableDevelopmentGoalIds: [1, 2],
        feeScheduleId: null,
        masterValues: undefined,
      },
      setFieldValue: mockSetFieldValue,
    }));

    renderWithProviders(
      <Formik initialValues={{}} onSubmit={jest.fn()}>
        <Step5 />
      </Formik>,
      {
        providerOverrides: {
          reference: {
            getCustomReferenceOptions: mockGetCustomReferenceOptions,
          },
        },
      },
    );

    const warningIcons = screen.queryAllByTestId("warning-icon");
    expect(warningIcons.length).toBe(0);
  });
});
