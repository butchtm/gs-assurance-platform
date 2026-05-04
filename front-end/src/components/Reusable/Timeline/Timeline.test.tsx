import { render, screen } from "@testing-library/react";

import { Timeline } from "./Timeline";

describe("Timeline Component", () => {
  it("renders message when nodes is empty", () => {
    render(<Timeline timelineNodes={[]} noNodesMessage="No current activities" />);
    expect(screen.getByText("No current activities")).toBeInTheDocument();
  });

  it("renders completed activity", () => {
    render(
      <Timeline
        timelineNodes={[
          {
            index: 1,
            name: "Assigning for Review",
            startDate: "2024-02-10T15:00:00.000000Z",
            dueDate: "2024-02-13T15:00:00.000000Z",
            endDate: "2024-02-12T12:00:00.000000Z",
            completed: true,
            current: true,
            skipped: false,
            customComponent: <></>,
          },
        ]}
        noNodesMessage=""
      />,
    );
    expect(screen.getByText("Completed:")).toBeInTheDocument();
    expect(screen.getByText("12/02/2024")).toBeInTheDocument();
  });

  it("renders custom component per node", () => {
    render(
      <Timeline
        timelineNodes={[
          {
            index: 1,
            name: "Assigning for Review",
            startDate: "2024-02-10T15:00:00.000000Z",
            dueDate: "2024-02-13T15:00:00.000000Z",
            endDate: "2024-02-12T12:00:00.000000Z",
            completed: false,
            current: true,
            skipped: false,
            customComponent: <span>(External)</span>,
          },
        ]}
        noNodesMessage=""
      />,
    );
    expect(screen.getByText("(External)")).toBeInTheDocument();
  });

  it("renders all deadline states", () => {
    render(
      <Timeline
        timelineNodes={[
          {
            index: 1,
            name: "Assigning for Review",
            startDate: "2024-02-10T15:00:00.000000Z",
            dueDate: "3024-02-13T15:00:00.000000Z",
            endDate: "3024-02-12T12:00:00.000000Z",
            completed: true,
            current: false,
            skipped: false,
            customComponent: <></>,
          },
          {
            index: 2,
            name: "Round 1 Review",
            startDate: "2024-02-12T15:00:00.000000Z",
            dueDate: "3024-02-26T15:00:00.000000Z",
            completed: false,
            current: true,
            skipped: false,
            customComponent: <></>,
          },
          {
            index: 3,
            name: "Round 1 Review",
            startDate: "2024-02-12T15:00:00.000000Z",
            dueDate: "1024-02-26T15:00:00.000000Z",
            completed: false,
            current: false,
            skipped: false,
            customComponent: <></>,
          },
        ]}
        noNodesMessage=""
      />,
    );
    expect(screen.getByTestId("CheckIcon")).toBeInTheDocument();
    expect(screen.getByTestId("CircleIcon")).toBeInTheDocument();
    expect(screen.getByTestId("PriorityHighIcon")).toBeInTheDocument();
  });

  it("renders due date for incomplete", () => {
    render(
      <Timeline
        timelineNodes={[
          {
            index: 1,
            name: "Assigning for Review",
            startDate: "2024-02-10T15:00:00.000000Z",
            dueDate: "3024-02-13T15:00:00.000000Z",
            endDate: "3024-02-12T12:00:00.000000Z",
            completed: false,
            current: true,
            skipped: false,
            customComponent: <></>,
          },
        ]}
        noNodesMessage=""
      />,
    );
    expect(screen.getByText("Deadline:")).toBeInTheDocument();
    expect(screen.getByText("13/02/3024")).toBeInTheDocument();
  });
});
