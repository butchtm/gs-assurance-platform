import { render, screen } from "@testing-library/react";
import dayjs, { Dayjs } from "dayjs";

import { CreditingPeriod } from "../../../../common/project/Project";

import ProjectCharacteristics from "./ProjectCharacteristics";

describe("ProjectCharacteristics Component", () => {
  const originalUtc = dayjs.utc;
  let fixedToday: Dayjs;

  beforeAll(() => {
    fixedToday = dayjs.utc("2025-05-15T00:00:00.000Z");
    jest.spyOn(dayjs, "utc").mockImplementation((arg) => {
      if (arg === undefined) {
        return fixedToday;
      }
      return originalUtc(arg);
    });
  });

  it("displays the current crediting and monitoring periods when one is active", () => {
    const creditingPeriods: CreditingPeriod[] = [
      {
        periodId: 1,
        startDate: "2025-05-10T00:00:00.000Z",
        endDate: "2025-05-20T00:00:00.000Z",
        methodologies: [
          { id: 1, description: "MethA", version: 1.5 },
          { id: 2, description: "MethB", version: 2.2 },
        ],
        monitoringPeriods: [
          {
            id: 11,
            startDate: "2025-05-01T00:00:00.000Z",
            endDate: "2025-05-31T00:00:00.000Z",
            feeSchedule: null,
          },
          {
            id: 12,
            startDate: "2025-04-01T00:00:00.000Z",
            endDate: "2025-04-30T00:00:00.000Z",
            feeSchedule: null,
          },
        ],
      },
      {
        periodId: 2,
        startDate: "2025-04-01T00:00:00.000Z",
        endDate: "2025-04-30T00:00:00.000Z",
        methodologies: [{ id: 3, description: "MethC", version: 3 }],
        monitoringPeriods: [
          {
            id: 21,
            startDate: "2025-03-01T00:00:00.000Z",
            endDate: "2025-03-31T00:00:00.000Z",
            feeSchedule: null,
          },
        ],
      },
    ];

    render(<ProjectCharacteristics creditingPeriods={creditingPeriods} />);

    expect(screen.getByText("Current Crediting Period", { exact: false })).toBeInTheDocument();
    expect(screen.getByText("10/05/2025 - 20/05/2025")).toBeInTheDocument();

    expect(screen.getByText("Current Monitoring Period", { exact: false })).toBeInTheDocument();
    expect(screen.getByText("01/05/2025 - 31/05/2025")).toBeInTheDocument();

    expect(screen.getByText("MethA")).toBeInTheDocument();
    expect(screen.getByText("1.5")).toBeInTheDocument();
    expect(screen.getByText("MethB")).toBeInTheDocument();
    expect(screen.getByText("2.2")).toBeInTheDocument();
  });

  it("falls back to the latest (past) crediting and monitoring periods if none are current", () => {
    const creditingPeriods: CreditingPeriod[] = [
      {
        periodId: 1,
        startDate: "2025-06-01T00:00:00.000Z",
        endDate: "2025-06-30T00:00:00.000Z",
        methodologies: [{ id: 1, description: "FutureMeth", version: 0.1 }],
        monitoringPeriods: [
          {
            id: 11,
            startDate: "2025-06-01T00:00:00.000Z",
            endDate: "2025-06-30T00:00:00.000Z",
            feeSchedule: null,
          },
        ],
      },
      {
        periodId: 2,
        startDate: "2025-04-01T00:00:00.000Z",
        endDate: "2025-04-30T00:00:00.000Z",
        methodologies: [
          { id: 2, description: "PastMeth1", version: 1.1 },
          { id: 3, description: "PastMeth2", version: 2.2 },
        ],
        monitoringPeriods: [
          {
            id: 21,
            startDate: "2025-03-01T00:00:00.000Z",
            endDate: "2025-03-31T00:00:00.000Z",
            feeSchedule: null,
          },
          {
            id: 22,
            startDate: "2025-02-01T00:00:00.000Z",
            endDate: "2025-02-28T00:00:00.000Z",
            feeSchedule: null,
          },
        ],
      },
    ];

    render(<ProjectCharacteristics creditingPeriods={creditingPeriods} />);

    expect(screen.getByText("Latest Crediting Period", { exact: false })).toBeInTheDocument();
    expect(screen.getByText("01/04/2025 - 30/04/2025")).toBeInTheDocument();

    expect(screen.getByText("Latest Monitoring Period", { exact: false })).toBeInTheDocument();
    expect(screen.getByText("01/03/2025 - 31/03/2025")).toBeInTheDocument();

    expect(screen.getByText("PastMeth1")).toBeInTheDocument();
    expect(screen.getByText("1.1")).toBeInTheDocument();
    expect(screen.getByText("PastMeth2")).toBeInTheDocument();
    expect(screen.getByText("2.2")).toBeInTheDocument();
  });
});
