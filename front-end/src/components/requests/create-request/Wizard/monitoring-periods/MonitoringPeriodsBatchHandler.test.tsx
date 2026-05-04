import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { Formik } from "formik";

import { BatchedProjectRequest } from "../../../Requests";
import { CreditingPeriod } from "../../../../../common/project/Project";
import { mockActiveReferenceTypes } from "../../../../../common/data-types/DebugReferenceTypes";

import { MonitoringPeriodsBatchHandler } from "./MonitoringPeriodsBatchHandler";

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

const mockCreditingPeriodFull = {
  periodId: 1,
  startDate: "2025-06-10T00:00:00.000Z",
  endDate: "2030-06-10T00:00:00.000Z",
  methodologies: [],
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
      endDate: "2030-06-10T00:00:00.000Z",
      feeSchedule: 0,
    },
  ],
};

const mockCreditingPeriodNoMonitoringPeriods: CreditingPeriod = {
  periodId: 1,
  startDate: "2025-06-10T00:00:00.000Z",
  endDate: "2030-06-10T00:00:00.000Z",
  methodologies: [],
  monitoringPeriods: [],
};

const mockCreditingPeriodMismatchedMonitoringPeriods: CreditingPeriod = {
  periodId: 1,
  startDate: "2025-06-10T00:00:00.000Z",
  endDate: "2030-06-10T00:00:00.000Z",
  methodologies: [],
  monitoringPeriods: [
    {
      id: 1,
      startDate: "2025-06-10T00:00:00.000Z",
      endDate: "2025-07-10T00:00:00.000Z",
      feeSchedule: 0,
    },
  ],
};

const mockFirstVpa: BatchedProjectRequest = {
  classification: "Real Case VPA",
  gsid: "GS1",
  id: 1,
  name: "Test Real Case",
  requestId: 1,
  creditingPeriods: [mockCreditingPeriod],
  methodologies: [],
};

const mockSecondVpa: BatchedProjectRequest = {
  classification: "Regular Case VPA",
  gsid: "GS2",
  id: 2,
  name: "Test Regular Case 1",
  requestId: 1,
  creditingPeriods: [mockCreditingPeriod],
  methodologies: [],
};

const renderWithOverrides = (options: {
  creditingPeriod: {
    firstId: number | null;
    secondId: number | null;
    period: CreditingPeriod;
  };
  monitoringPeriod: {
    firstId: number | null;
    secondId: number | null;
  };
  batchedProject: BatchedProjectRequest;
  isFixedPeriod: boolean;
}) => {
  render(
    <Formik
      initialValues={{
        batchStepProjectValues: {
          batch: {
            [1]: {
              creditingPeriodId: options.creditingPeriod.firstId,
              monitoringPeriodId: options.monitoringPeriod.firstId,
              newMonitoringPeriodStartDate: null,
              newMonitoringPeriodEndDate: null,
            },
            [2]: {
              creditingPeriodId: options.creditingPeriod.secondId,
              monitoringPeriodId: options.monitoringPeriod.secondId,
              newMonitoringPeriodStartDate: null,
              newMonitoringPeriodEndDate: null,
            },
          },
        },
      }}
      onSubmit={jest.fn()}
    >
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <MonitoringPeriodsBatchHandler
          creditingPeriods={[options.creditingPeriod.period]}
          batchedProject={options.batchedProject}
          batchedProjects={[mockFirstVpa, mockSecondVpa]}
          isFixedPeriod={options.isFixedPeriod}
        />
      </LocalizationProvider>
    </Formik>,
  );
};

describe("MonitoringPeriodsBatchHandler", () => {
  it("should allow selection of a monitoring period for the first VPA", async () => {
    renderWithOverrides({
      creditingPeriod: {
        firstId: 1,
        secondId: null,
        period: mockCreditingPeriod,
      },
      monitoringPeriod: {
        firstId: null,
        secondId: null,
      },
      batchedProject: mockFirstVpa,
      isFixedPeriod: false,
    });
    fireEvent.mouseDown(screen.getByRole("combobox", { name: "Monitoring Period ID" }));
    const options = await screen.findAllByRole("option");
    await waitFor(() => {
      expect(options.length).toEqual(3);
      expect(screen.getByText("Period 1 - 10/06/2025 to 30/06/2025")).toBeInTheDocument();
      expect(screen.getByText("Period 2 - 01/07/2025 to 31/07/2025")).toBeInTheDocument();
      expect(screen.getByText("Period 3 - 01/08/2025 to 31/08/2025")).toBeInTheDocument();
    });
  });

  it("should allow creation of a new monitoring period for the first VPA", async () => {
    renderWithOverrides({
      creditingPeriod: {
        firstId: 1,
        secondId: null,
        period: mockCreditingPeriod,
      },
      monitoringPeriod: {
        firstId: null,
        secondId: null,
      },
      batchedProject: mockFirstVpa,
      isFixedPeriod: false,
    });
    const addNewButton = screen.getByText("Add New Monitoring Period");
    fireEvent.click(addNewButton);
    await waitFor(() => {
      expect(screen.getByLabelText("Monitoring Period Start Date")).toBeInTheDocument();
      expect(screen.getByLabelText("Monitoring Period End Date")).toBeInTheDocument();
      expect(screen.getByText("Use Existing Monitoring Period")).toBeInTheDocument();
    });
  });

  it("should disable monitoring period selection if required", async () => {
    renderWithOverrides({
      creditingPeriod: {
        firstId: 1,
        secondId: 1,
        period: mockCreditingPeriod,
      },
      monitoringPeriod: {
        firstId: 1,
        secondId: 1,
      },
      batchedProject: mockSecondVpa,
      isFixedPeriod: true,
    });
    await waitFor(() => {
      expect(screen.queryByText("Add New Monitoring Period")).not.toBeInTheDocument();
      const periodSelect = screen.getByRole("combobox", { name: "Monitoring Period ID" });
      expect(periodSelect.getAttribute("aria-disabled")).toEqual("true");
      expect(screen.getByText("Period 1 - 10/06/2025 to 30/06/2025")).toBeInTheDocument();
    });
  });

  it("should allow creation of a new monitoring period for subsequent VPAs with a fixed end date if none are available", async () => {
    renderWithOverrides({
      creditingPeriod: {
        firstId: 1,
        secondId: 1,
        period: mockCreditingPeriodNoMonitoringPeriods,
      },
      monitoringPeriod: {
        firstId: 1,
        secondId: null,
      },
      batchedProject: mockSecondVpa,
      isFixedPeriod: false,
    });
    await waitFor(() => {
      const periodSelect = screen.getByRole("combobox", { name: "Monitoring Period ID" });
      expect(periodSelect.getAttribute("aria-disabled")).toEqual("true");
      expect(
        screen.getByText("Monitoring period cannot be selected as none have a matching end date for GS1."),
      ).toBeInTheDocument();
    });

    const addNewButton = screen.getByText("Add New Monitoring Period");
    fireEvent.click(addNewButton);

    await waitFor(() => {
      expect(screen.getByLabelText("Monitoring Period Start Date")).toHaveValue("10/06/2025");
      const endDatePicker = screen.getByLabelText("Monitoring Period End Date");
      expect(endDatePicker).toBeInTheDocument();
      expect(endDatePicker).toBeDisabled();
    });
  });

  it("should prevent creation of a new monitoring period for subsequent VPAs if it cannot contain the fixed end date", async () => {
    renderWithOverrides({
      creditingPeriod: {
        firstId: 1,
        secondId: 1,
        period: mockCreditingPeriodMismatchedMonitoringPeriods,
      },
      monitoringPeriod: {
        firstId: 1,
        secondId: null,
      },
      batchedProject: mockSecondVpa,
      isFixedPeriod: false,
    });
    await waitFor(() => {
      const periodSelect = screen.getByRole("combobox", { name: "Monitoring Period ID" });
      expect(periodSelect.getAttribute("aria-disabled")).toEqual("true");
      expect(
        screen.getByText("Monitoring period cannot be selected as none have a matching end date for GS1."),
      ).toBeInTheDocument();
    });

    await waitFor(() => {
      expect(screen.queryByText("Add New Monitoring Period")).not.toBeInTheDocument();
      expect(
        screen.getByText(
          "Monitoring period cannot be created because no monitoring period can exist which matches the monitoring period end date of GS1.",
        ),
      ).toBeInTheDocument();
    });
  });

  it("should prevent creation of a new monitoring period if a crediting period is full", async () => {
    renderWithOverrides({
      creditingPeriod: {
        firstId: 1,
        secondId: null,
        period: mockCreditingPeriodFull,
      },
      monitoringPeriod: {
        firstId: null,
        secondId: null,
      },
      batchedProject: mockFirstVpa,
      isFixedPeriod: false,
    });
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
