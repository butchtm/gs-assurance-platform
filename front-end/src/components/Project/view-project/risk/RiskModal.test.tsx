import { fireEvent, render, screen, waitFor } from "@testing-library/react";

import { updateProjectRisk } from "../../Project";

import { RiskModal } from "./RiskModal";

const mockProject = {
  id: 1,
  name: "Test Project",
  gsid: "GS1",
};

const mockRisk = {
  projectId: 1,
  risk: true,
  summary: "Project has been deemed high risk",
  dateApplied: "2024-07-01T08:58:19.044809Z",
};

jest.mock("../../Project", () => ({
  updateProjectRisk: jest.fn(),
}));

describe("RiskModal", () => {
  it("load and display any existing high risk flag and summary", async () => {
    render(<RiskModal isOpen={true} onClose={() => null} project={mockProject} currentRisk={mockRisk} />);

    await waitFor(() => {
      expect(screen.getByText("GS1 - Test Project")).toBeInTheDocument();
      expect(screen.getByLabelText("Summary *")).toHaveValue("Project has been deemed high risk");
      expect(screen.getByRole("checkbox")).toHaveProperty("checked", true);
    });
  });
  it("should prevent submission if no summary has been provided", async () => {
    render(<RiskModal isOpen={true} onClose={() => null} project={mockProject} />);

    await waitFor(() => {
      expect(screen.getByLabelText("Summary *")).toHaveValue("");
      expect(screen.getByRole("button", { name: "Update" })).toBeDisabled();
    });
  });
  it("should allow for submission of new values", async () => {
    render(<RiskModal isOpen={true} onClose={() => null} project={mockProject} currentRisk={mockRisk} />);

    await waitFor(() => {
      expect(screen.getByLabelText("Summary *")).toHaveValue("Project has been deemed high risk");
    });

    fireEvent.click(screen.getByRole("button", { name: "Update" }));

    await waitFor(() => {
      expect(updateProjectRisk as jest.Mock).toHaveBeenCalledWith(1, {
        risk: true,
        summary: "Project has been deemed high risk",
        dateApplied: expect.any(String),
      });
    });
  });

  it("should enforce a maximum of 2000 characters for the summary", async () => {
    render(<RiskModal isOpen={true} onClose={() => null} project={mockProject} />);

    const summaryField = screen.getByLabelText("Summary *") as HTMLTextAreaElement;
    const text2000chars = "a".repeat(2000);

    fireEvent.change(summaryField, { target: { value: text2000chars } });
    await waitFor(() => {
      expect(summaryField).toHaveValue(text2000chars);
      expect(screen.getByText("2000/2000 characters")).toBeInTheDocument();
    });

    expect(summaryField).toHaveAttribute("maxlength", "2000");
  });

  it("should display character count as user types", async () => {
    render(<RiskModal isOpen={true} onClose={() => null} project={mockProject} />);

    const summaryField = screen.getByLabelText("Summary *");

    // Initially shows 0/2000
    expect(screen.getByText("0/2000 characters")).toBeInTheDocument();

    // Type some text
    fireEvent.change(summaryField, { target: { value: "Test summary text" } });

    await waitFor(() => {
      expect(screen.getByText("17/2000 characters")).toBeInTheDocument();
    });

    // Type more text
    fireEvent.change(summaryField, { target: { value: "This is a longer test summary" } });

    await waitFor(() => {
      expect(screen.getByText("29/2000 characters")).toBeInTheDocument();
    });
  });
});
