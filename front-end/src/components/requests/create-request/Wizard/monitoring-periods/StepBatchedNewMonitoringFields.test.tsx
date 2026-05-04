import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { render, screen, waitFor } from "@testing-library/react";
import dayjs from "dayjs";
import { Formik } from "formik";

import { CreditingPeriod } from "../../../../../common/project/Project";
import { BatchedProjectRequest } from "../../../Requests";

import { StepBatchedNewMonitoringFields } from "./StepBatchedNewMonitoringFields";

const mockProjectData: BatchedProjectRequest = {
  classification: "Real Case VPA",
  gsid: "GS1",
  id: 1,
  name: "Test Real Case",
  requestId: 1,
  creditingPeriods: [],
  methodologies: [],
};

const mockCreditingPeriod: CreditingPeriod = {
  periodId: 1,
  startDate: "2025-06-10T00:00:00.000Z",
  endDate: "2030-06-10T00:00:00.000Z",
  methodologies: [],
};

const renderWithOverrides = (startDate: string | null, endDate: string | null, isFixed: boolean) => {
  render(
    <Formik
      initialValues={{
        batchStepProjectValues: {
          batch: {
            [1]: {
              newMonitoringPeriodStartDate: startDate ? dayjs.utc(startDate) : null,
              newMonitoringPeriodEndDate: endDate ? dayjs.utc(endDate) : null,
            },
          },
        },
      }}
      onSubmit={jest.fn()}
    >
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <StepBatchedNewMonitoringFields
          creditingPeriod={mockCreditingPeriod}
          project={mockProjectData}
          isFixedEndDate={isFixed}
        />
      </LocalizationProvider>
    </Formik>,
  );
};

describe("StepBatchedNewMonitoringFields", () => {
  it("should render the datepickers and set the default dates", async () => {
    renderWithOverrides(null, null, false);
    await waitFor(() => {
      const startDatePicker = screen.getByLabelText("Monitoring Period Start Date");
      const endDatePicker = screen.getByLabelText("Monitoring Period End Date");
      expect(startDatePicker).toHaveValue("10/06/2025");
      expect(endDatePicker).toHaveValue("11/06/2025");
    });
  });

  it("should display a gap warning for a gap between the start date and previous date", async () => {
    renderWithOverrides("2025-06-15T00:00:00.000Z", null, false);
    await waitFor(() => {
      const startDatePicker = screen.getByLabelText("Monitoring Period Start Date");
      expect(startDatePicker).toHaveValue("15/06/2025");
      expect(
        screen.getByText(
          "Warning: There is a gap between the previous monitoring period or the start of the crediting period and the new start date. This does not prevent the request from being submitted.",
        ),
      ).toBeInTheDocument();
    });
  });

  it("should disable the end date picker and set the value if requested", async () => {
    renderWithOverrides(null, "2025-06-20T00:00:00.000Z", true);
    await waitFor(() => {
      const startDatePicker = screen.getByLabelText("Monitoring Period Start Date");
      const endDatePicker = screen.getByLabelText("Monitoring Period End Date");
      expect(startDatePicker).toHaveValue("10/06/2025");
      expect(endDatePicker).toHaveValue("20/06/2025");
      expect(endDatePicker).toBeDisabled();
    });
  });
});
