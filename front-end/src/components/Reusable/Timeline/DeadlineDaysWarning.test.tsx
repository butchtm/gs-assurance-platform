import { render, screen } from "@testing-library/react";

import { DeadlineDaysWarning } from "./DeadlineDaysWarning";

describe("DeadlineDaysWarning Component", () => {
  it("renders on time completed", async () => {
    render(<DeadlineDaysWarning daysRemaining={2} isCompleted />);
    expect(await screen.findByText("On time")).toBeInTheDocument();
  });
  it("renders late but completed", async () => {
    render(<DeadlineDaysWarning daysRemaining={-3} isCompleted />);
    expect(await screen.findByText("3 days late")).toBeInTheDocument();
  });
  it("renders one day late but completed", async () => {
    render(<DeadlineDaysWarning daysRemaining={-1} isCompleted />);
    expect(await screen.findByText("1 day late")).toBeInTheDocument();
  });
  it("renders deadline passed incomplete", async () => {
    render(<DeadlineDaysWarning daysRemaining={-3} isCompleted={false} />);
    expect(await screen.findByText("3 days overdue")).toBeInTheDocument();
  });
  it("renders one day deadline passed incomplete", async () => {
    render(<DeadlineDaysWarning daysRemaining={-1} isCompleted={false} />);
    expect(await screen.findByText("1 day overdue")).toBeInTheDocument();
  });
  it("renders due today incomplete", async () => {
    render(<DeadlineDaysWarning daysRemaining={0} isCompleted={false} />);
    expect(await screen.findByText("Due today")).toBeInTheDocument();
  });
  it("renders deadline-approaching incomplete", async () => {
    render(<DeadlineDaysWarning daysRemaining={1} isCompleted={false} />);
    expect(await screen.findByText("1 day remaining")).toBeInTheDocument();
  });
  it("renders in progress", async () => {
    render(<DeadlineDaysWarning daysRemaining={3} isCompleted={false} />);
    expect(await screen.findByText("3 days remaining")).toBeInTheDocument();
  });
});
