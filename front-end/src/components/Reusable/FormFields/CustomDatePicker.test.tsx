import { render, screen } from "@testing-library/react";
import { Formik } from "formik";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";

import { CustomDatePicker } from "./CustomDatePicker";

describe("CustomDatePicker Component", () => {
  it("renders correctly with label", () => {
    render(
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <Formik initialValues={{ dateField: null }} onSubmit={jest.fn()}>
          <CustomDatePicker name="dateField" label="Select Date" />
        </Formik>
      </LocalizationProvider>,
    );

    expect(screen.getByLabelText("Select Date")).toBeInTheDocument();
  });

  it("displays error message when touched and has error", () => {
    render(
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <Formik
          initialValues={{ dateField: null }}
          initialErrors={{ dateField: "Date is required" }}
          initialTouched={{ dateField: true }}
          onSubmit={jest.fn()}
        >
          <CustomDatePicker name="dateField" label="Select Date" />
        </Formik>
      </LocalizationProvider>,
    );

    expect(screen.getByText("Date is required")).toBeInTheDocument();
  });

  it("renders with required field indicator", () => {
    render(
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <Formik initialValues={{ dateField: null }} onSubmit={jest.fn()}>
          <CustomDatePicker name="dateField" label="Select Date" required={true} />
        </Formik>
      </LocalizationProvider>,
    );

    const input = screen.getByPlaceholderText("DD/MM/YYYY");
    expect(input).toBeInTheDocument();
  });
});
