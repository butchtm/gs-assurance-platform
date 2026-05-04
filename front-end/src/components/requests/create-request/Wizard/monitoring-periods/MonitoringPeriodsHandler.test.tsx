import { render, waitFor, screen, fireEvent } from "@testing-library/react";
import { Formik } from "formik";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";

import { CreditingPeriod } from "../../../../../common/project/Project";
import { mockActiveReferenceTypes } from "../../../../../common/data-types/DebugReferenceTypes";

import { MonitoringPeriodsHandler } from "./MonitoringPeriodsHandler";

jest.mock("../../../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../../../common/data-types/DataReferenceTypes"),
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
  }),
}));

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
    {
      id: 2,
      startDate: "2025-07-01T00:00:00.000Z",
      endDate: "2025-07-31T00:00:00.000Z",
      feeSchedule: 0,
    },
    {
      id: 3,
      startDate: "2025-08-01T00:00:00.000Z",
      endDate: "2025-08-31T00:00:00.000Z",
      feeSchedule: 0,
    },
  ],
};

const mockCreditingPeriodFull: CreditingPeriod = {
  ...mockCreditingPeriod,
  monitoringPeriods: [
    {
      id: 1,
      startDate: "2025-06-10T00:00:00.000Z",
      endDate: "2026-06-10T00:00:00.000Z",
      feeSchedule: 0,
    },
    {
      id: 2,
      startDate: "2026-06-11T00:00:00.000Z",
      endDate: "2027-06-10T00:00:00.000Z",
      feeSchedule: 0,
    },
    {
      id: 3,
      startDate: "2027-06-11T00:00:00.000Z",
      endDate: "2030-06-10T00:00:00.000Z",
      feeSchedule: 0,
    },
  ],
};

const renderWithOverrides = (creditingPeriod: CreditingPeriod) => {
  render(
    <Formik
      initialValues={{
        creditingPeriodId: 1,
        newMonitoringPeriodStartDate: null,
        newMonitoringPeriodEndDate: null,
      }}
      onSubmit={jest.fn()}
    >
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <MonitoringPeriodsHandler creditingPeriods={[creditingPeriod]} />
      </LocalizationProvider>
    </Formik>,
  );
};

describe("MonitoringPeriodsHandler", () => {
  it("should allow selection of a monitoring period", async () => {
    renderWithOverrides(mockCreditingPeriodWithMonitoringPeriod);
    fireEvent.mouseDown(screen.getByRole("combobox", { name: "Monitoring Period ID" }));
    const options = await screen.findAllByRole("option");
    await waitFor(() => {
      expect(options.length).toEqual(3);
      expect(screen.getByText("Period 1 - 10/06/2025 to 30/06/2025")).toBeInTheDocument();
      expect(screen.getByText("Period 2 - 01/07/2025 to 31/07/2025")).toBeInTheDocument();
      expect(screen.getByText("Period 3 - 01/08/2025 to 31/08/2025")).toBeInTheDocument();
    });
  });

  it("should allow for creation of a new monitoring period", async () => {
    renderWithOverrides(mockCreditingPeriod);
    const addNewButton = screen.getByText("Add New Monitoring Period");
    fireEvent.click(addNewButton);
    await waitFor(() => {
      expect(screen.getByLabelText("Monitoring Period Start Date*")).toBeInTheDocument();
      expect(screen.getByLabelText("Monitoring Period End Date*")).toBeInTheDocument();
      expect(screen.getByText("Use Existing Monitoring Period")).toBeInTheDocument();
    });
  });

  it("should prevent creation of monitoring period if crediting period is full", async () => {
    renderWithOverrides(mockCreditingPeriodFull);
    await waitFor(() => {
      expect(screen.queryByText("Add New Monitoring Period")).not.toBeInTheDocument();
      expect(screen.getByRole("combobox", { name: "Monitoring Period ID" })).toBeInTheDocument();
      expect(
        screen.getByText(
          "Monitoring period cannot be created because the crediting period has full coverage (every date in the crediting period is covered by a monitoring period).",
        ),
      ).toBeInTheDocument();
    });
  });
});
