import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { render, screen, waitFor } from "@testing-library/react";
import dayjs from "dayjs";
import { Formik } from "formik";

import { CreditingPeriod } from "../../../../../common/project/Project";

import { Step1NewMonitoringFields } from "./Step1NewMonitoringFields";

const mockCreditingPeriod: CreditingPeriod = {
  periodId: 1,
  startDate: "2025-06-10T00:00:00.000Z",
  endDate: "2030-06-10T00:00:00.000Z",
  methodologies: [],
};

const mockCreditingPeriodWithMonitoringPeriod: CreditingPeriod = {
  ...mockCreditingPeriod,
  monitoringPeriods: [
    {
      id: 1,
      startDate: "2025-06-10T00:00:00.000Z",
      endDate: "2025-06-30T00:00:00.000Z",
      feeSchedule: 0,
    },
  ],
};

const renderWithOverrides = (start: string | null, creditingPeriod: CreditingPeriod) => {
  render(
    <Formik
      initialValues={{
        newMonitoringPeriodStartDate: start ? dayjs.utc(start) : null,
        newMonitoringPeriodEndDate: null,
      }}
      onSubmit={jest.fn()}
    >
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <Step1NewMonitoringFields creditingPeriod={creditingPeriod} />
      </LocalizationProvider>
    </Formik>,
  );
};

describe("Step1NewMonitoringFields", () => {
  it("should render start and end date pickers and select default dates from the crediting period", async () => {
    renderWithOverrides(null, mockCreditingPeriod);
    await waitFor(() => {
      const startDatePicker = screen.getByLabelText("Monitoring Period Start Date*");
      const endDatePicker = screen.getByLabelText("Monitoring Period End Date*");
      expect(startDatePicker).toHaveValue("10/06/2025");
      expect(endDatePicker).toHaveValue("11/06/2025");
    });
  });

  it("should render start and end date pickers and select default dates from the previous monitoring period", async () => {
    renderWithOverrides(null, mockCreditingPeriodWithMonitoringPeriod);
    await waitFor(() => {
      const startDatePicker = screen.getByLabelText("Monitoring Period Start Date*");
      const endDatePicker = screen.getByLabelText("Monitoring Period End Date*");
      expect(startDatePicker).toHaveValue("01/07/2025");
      expect(endDatePicker).toHaveValue("02/07/2025");
    });
  });

  it("should display a gap warning for gaps between the start date and start of the crediting period", async () => {
    renderWithOverrides("2025-06-15T00:00:00.000Z", mockCreditingPeriod);
    await waitFor(() => {
      const startDatePicker = screen.getByLabelText("Monitoring Period Start Date*");
      expect(startDatePicker).toHaveValue("15/06/2025");
      expect(
        screen.getByText(
          "Warning: There is a gap between the previous monitoring period or the start of the crediting period and the new start date. This does not prevent the request from being submitted.",
        ),
      ).toBeInTheDocument();
    });
  });

  it("should display a gap warning for gaps between the start date and end of the last monitoring period", async () => {
    renderWithOverrides("2025-07-06T00:00:00.000Z", mockCreditingPeriodWithMonitoringPeriod);
    await waitFor(() => {
      const startDatePicker = screen.getByLabelText("Monitoring Period Start Date*");
      expect(startDatePicker).toHaveValue("06/07/2025");
      expect(
        screen.getByText(
          "Warning: There is a gap between the previous monitoring period or the start of the crediting period and the new start date. This does not prevent the request from being submitted.",
        ),
      ).toBeInTheDocument();
    });
  });
});
