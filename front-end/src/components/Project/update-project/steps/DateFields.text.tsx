import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { render, screen } from "@testing-library/react";
import { Formik } from "formik";

import { DateFields } from "./DateFields";

const mockSetFieldValue = jest.fn();
jest.mock("formik", () => ({
  ...jest.requireActual("formik"),
  useFormikContext: jest.fn(() => ({
    values: {
      designCertificationDate: null,
      certifiedProjectDate: null,
      lastDesignCertificationRenewalDate: null,
      lastPerformanceReviewDate: null,
    },
    setFieldValue: mockSetFieldValue,
  })),
}));

describe("ExtraDateFields", () => {
  it("renders correctly", () => {
    render(
      <Formik initialValues={{}} onSubmit={jest.fn()}>
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <DateFields projectStatus={{ id: 4, code: "CERTIFIED_PROJECT", description: "Certified Project" }} />
        </LocalizationProvider>
      </Formik>,
    );
    expect(screen.getByLabelText("Certified Design Date")).toBeInTheDocument();
    expect(screen.getByLabelText("Certified Project Date")).toBeInTheDocument();
    expect(screen.getByLabelText("Last Design Certificate Renewal Date")).toBeInTheDocument();
    expect(screen.getByLabelText("Last Performance Review Date")).toBeInTheDocument();
    expect(screen.getByLabelText("Listing Date")).toBeInTheDocument();
  });
});
