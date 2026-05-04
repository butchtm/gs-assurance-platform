import { fireEvent, render, screen } from "@testing-library/react";
import { Formik } from "formik";
import dayjs from "dayjs";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";

import { Step1NewCreditingFields } from "./Step1NewCreditingFields";

const setFieldValue = jest.fn();

jest.mock("formik", () => ({
  ...jest.requireActual("formik"),
  useFormikContext: jest.fn(() => ({
    values: {},
    setFieldValue: setFieldValue,
  })),
}));

const formDateFormat = "DD/MM/YYYY";
const muiDatePickerFormat = "YYYY-MM-DD";

describe("Step1NewCreditingFields Component", () => {
  it("should initialise new crediting period datepickers with today as start and 5 years later as end", () => {
    render(
      <Formik initialValues={{}} onSubmit={jest.fn()}>
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <Step1NewCreditingFields creditingPeriods={[]}></Step1NewCreditingFields>
        </LocalizationProvider>
      </Formik>,
    );

    const startDatePicker = screen.getByLabelText("Crediting Period Start Date *");
    const endDatePicker = screen.getByLabelText("Crediting Period End Date *");

    const expectedDefaultStartDate = dayjs.utc().format(formDateFormat);
    const expectedDefaultEndDate = dayjs.utc().add(5, "year").subtract(1, "day").format(formDateFormat);

    expect(startDatePicker).toHaveValue(expectedDefaultStartDate);
    expect(endDatePicker).toHaveValue(expectedDefaultEndDate);
  });

  it("should initialise new crediting period datepickers with newly selected dates if already set in formik values", () => {
    const newCreditingPeriodStartDate = dayjs.utc().format(formDateFormat);
    const newCreditingPeriodEndDate = dayjs.utc().add(5, "year").subtract(1, "day").format(formDateFormat);
    render(
      <Formik
        initialValues={{
          newCreditingPeriodStartDate: newCreditingPeriodStartDate,
          newCreditingPeriodEndDate: newCreditingPeriodEndDate,
        }}
        onSubmit={jest.fn()}
      >
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <Step1NewCreditingFields creditingPeriods={[]}></Step1NewCreditingFields>
        </LocalizationProvider>
      </Formik>,
    );

    const startDatePicker = screen.getByLabelText("Crediting Period Start Date *");
    const endDatePicker = screen.getByLabelText("Crediting Period End Date *");

    expect(startDatePicker).toHaveValue(newCreditingPeriodStartDate);
    expect(endDatePicker).toHaveValue(newCreditingPeriodEndDate);
  });

  it("should update formik crediting period start date value when datepicker changes", () => {
    render(
      <Formik initialValues={{}} onSubmit={jest.fn()}>
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <Step1NewCreditingFields creditingPeriods={[]}></Step1NewCreditingFields>
        </LocalizationProvider>
      </Formik>,
    );

    const startDatePicker = screen.getByLabelText("Crediting Period Start Date *");

    const expectedStartDate = dayjs.utc().add(2, "day");

    fireEvent.change(startDatePicker, { target: { value: expectedStartDate.format(formDateFormat) } });
    fireEvent.blur(startDatePicker);

    expect(setFieldValue).toHaveBeenCalledWith(
      "newCreditingPeriodStartDate",
      expect.stringContaining(expectedStartDate.format(muiDatePickerFormat)),
    );
    expect(startDatePicker).toHaveValue(expectedStartDate.format(formDateFormat));
  });

  it("should update formik crediting period end date value when datepicker changes", () => {
    render(
      <Formik initialValues={{}} onSubmit={jest.fn()}>
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <Step1NewCreditingFields creditingPeriods={[]}></Step1NewCreditingFields>
        </LocalizationProvider>
      </Formik>,
    );

    const endDatePicker = screen.getByLabelText("Crediting Period End Date *");

    const expectedEndDate = dayjs.utc().add(2, "day");

    fireEvent.change(endDatePicker, { target: { value: expectedEndDate.format(formDateFormat) } });
    fireEvent.blur(endDatePicker);

    expect(setFieldValue).toHaveBeenCalledWith(
      "newCreditingPeriodStartDate",
      expect.stringContaining(expectedEndDate.format(muiDatePickerFormat)),
    );
    expect(endDatePicker).toHaveValue(expectedEndDate.format(formDateFormat));
  });
});
