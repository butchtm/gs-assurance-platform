import { render, screen } from "@testing-library/react";

import { mockActiveReferenceTypes } from "../../../../common/data-types/DebugReferenceTypes";

import Fees from "./Fees";

jest.mock("../../../../common/data-types/DataReferenceTypes", () => ({
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
  }),
}));

describe("Fees", () => {
  it("displays not set for no fee schedule", () => {
    render(<Fees />);
    expect(screen.getByText("Fee Schedule")).toBeInTheDocument();
    expect(screen.getByText("Not Set")).toBeInTheDocument();
  });

  it("displays only the project fee schedule if no monitoring period fee schedule available", () => {
    render(<Fees feeSchedule={mockActiveReferenceTypes.FeeSchedule[0]} creditingPeriods={[]} />);
    expect(screen.getByText("Fee Schedule")).toBeInTheDocument();
    expect(screen.getByText("Cash per credit")).toBeInTheDocument();
    expect(screen.queryByText("Latest Fee Schedule")).not.toBeInTheDocument();
  });

  it("displays only the project fee schedule if monitoring period fee schedule available but matches project", () => {
    render(
      <Fees
        feeSchedule={mockActiveReferenceTypes.FeeSchedule[0]}
        creditingPeriods={[
          {
            periodId: 1,
            startDate: "2022-08-06T00:00:00.000Z",
            endDate: "2025-08-06T00:00:00.000Z",
            methodologies: [],
            monitoringPeriods: [
              {
                id: 1,
                startDate: "2022-08-06T00:00:00.000Z",
                endDate: "2023-08-06T00:00:00.000Z",
                feeSchedule: 2,
              },
              {
                id: 2,
                startDate: "2023-08-07T00:00:00.000Z",
                endDate: "2024-08-07T00:00:00.000Z",
                feeSchedule: 1,
              },
            ],
          },
        ]}
      />,
    );
    expect(screen.getByText("Fee Schedule")).toBeInTheDocument();
    expect(screen.getByText("Cash per credit")).toBeInTheDocument();
    expect(screen.queryByText("Latest Fee Schedule")).not.toBeInTheDocument();
  });
  it("displays the project fee schedule and latest monitoring period fee schedule if non matching", () => {
    render(
      <Fees
        feeSchedule={mockActiveReferenceTypes.FeeSchedule[0]}
        creditingPeriods={[
          {
            periodId: 1,
            startDate: "2022-08-06T00:00:00.000Z",
            endDate: "2025-08-06T00:00:00.000Z",
            methodologies: [],
            monitoringPeriods: [
              {
                id: 1,
                startDate: "2022-08-06T00:00:00.000Z",
                endDate: "2023-08-06T00:00:00.000Z",
                feeSchedule: 1,
              },
              {
                id: 2,
                startDate: "2023-08-07T00:00:00.000Z",
                endDate: "2024-08-07T00:00:00.000Z",
                feeSchedule: 2,
              },
            ],
          },
        ]}
      />,
    );
    expect(screen.getByText("Project Fee Schedule")).toBeInTheDocument();
    expect(screen.getByText("Cash per credit")).toBeInTheDocument();
    expect(screen.getByText("Latest Fee Schedule")).toBeInTheDocument();
    expect(screen.getByText("Share of proceeds")).toBeInTheDocument();
  });
});
