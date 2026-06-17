import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { screen } from "@testing-library/react";
import { Formik, useFormikContext } from "formik";

import { renderWithProviders } from "@/test/test-utils";
import { ReferenceDataIdsOrId } from "@/common/data-types/DataReferenceTypes";

import { Step4 } from "./Step4";

const mockSetFieldValue = jest.fn();

jest.mock("formik", () => ({
  ...jest.requireActual("formik"),
  useFormikContext: jest.fn(() => ({
    values: {},
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

describe("Step4", () => {
  it("Step4 renders initial select correctly", () => {
    renderWithProviders(
      <Formik initialValues={{}} onSubmit={jest.fn()}>
        <Step4 />
      </Formik>,
    );

    expect(screen.getByRole("combobox", { name: "Transitioning Project" })).toBeInTheDocument();
  });

  it("Step4 renders correctly when transition is yes", () => {
    const useFormikContextMock = useFormikContext as jest.Mock;

    const mockGetCustomReferenceOptions = jest
      .fn()
      .mockImplementation((reference: string, _currentValue?: ReferenceDataIdsOrId) => {
        switch (reference) {
          case "TransitionPathway":
            return [
              {
                value: 0,
                label: "CDM to GS CDM",
              },
            ];
          case "TransitionApprovalProcedure":
            return [
              {
                value: 1,
                label: "Preliminary review by SC, validation by VVB and design review by SC",
              },
              {
                value: 2,
                label: "Combined preliminary Review, validation and design review by SC",
              },
            ];
          case "TransitionFromOtherScheme":
            return [
              {
                value: 0,
                label: "None",
              },
              {
                value: 1,
                label: "CDM",
              },
            ];
          default:
            return [];
        }
      });

    useFormikContextMock.mockImplementation(() => ({
      values: {
        transitioningProject: "yes",
      },
      setFieldValue: jest.fn(),
    }));

    renderWithProviders(
      <Formik initialValues={{ transitioningProject: "yes" }} onSubmit={jest.fn()}>
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <Step4 />
        </LocalizationProvider>
      </Formik>,
      {
        providerOverrides: {
          reference: {
            getCustomReferenceOptions: mockGetCustomReferenceOptions,
          },
        },
      },
    );

    expect(screen.getByRole("combobox", { name: "Transitioning Project" })).toBeInTheDocument();
    expect(screen.getByLabelText("Other Standard*")).toBeInTheDocument();
    expect(screen.getByLabelText("Other Schemes Crediting Period Start Date*")).toBeInTheDocument();
    expect(screen.getByLabelText("Other Schemes Crediting Period End Date*")).toBeInTheDocument();
    expect(screen.getByLabelText("Transition Pathway*")).toBeInTheDocument();
    expect(screen.getByRole("combobox", { name: "Transitioning Approval Procedure" })).toBeInTheDocument();
    expect(screen.getByLabelText("Project Link in Other Scheme*")).toBeInTheDocument();
  });
});
