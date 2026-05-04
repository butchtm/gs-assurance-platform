import { fireEvent, render, screen, waitFor } from "@testing-library/react";

import { getProjectRiskHistory } from "../../Project";

import { RiskManagementTab } from "./RiskManagementTab";

jest.mock("../../Project", () => ({
  getProjectRiskHistory: jest.fn(),
}));

describe("RiskManagementTab", () => {
  it("should allow for opening of the update risk modal", async () => {
    const mockOpenModal = jest.fn();
    render(<RiskManagementTab openRiskModal={mockOpenModal} projectId={1} />);

    fireEvent.click(screen.getByRole("button", { name: "Update Risk" }));
    await waitFor(() => {
      expect(mockOpenModal).toHaveBeenCalled();
    });
  });

  it("should fetch and display the project risk history", async () => {
    (getProjectRiskHistory as jest.Mock).mockResolvedValue({
      content: [
        {
          projectId: 1,
          risk: true,
          summary: "The project is deemed high risk",
          dateApplied: "2025-08-21T10:00:00.000Z",
          user: "Jane Smith",
        },
      ],
      pageSize: 10,
      pageNumber: 1,
      totalElements: 1,
    });
    render(<RiskManagementTab openRiskModal={jest.fn()} projectId={1} />);
    await waitFor(() => {
      expect(getProjectRiskHistory as jest.Mock).toHaveBeenCalledWith(1, [], 1, 10);
      expect(screen.getByText("Date Applied")).toBeInTheDocument();
      expect(screen.getByText("21/08/2025")).toBeInTheDocument();
      expect(screen.getByText("Risk Status")).toBeInTheDocument();
      expect(screen.getByText("High Risk")).toBeInTheDocument();
      expect(screen.getByText("User")).toBeInTheDocument();
      expect(screen.getByText("Jane Smith")).toBeInTheDocument();
      expect(screen.getByText("Description")).toBeInTheDocument();
      expect(screen.getByText("The project is deemed high risk")).toBeInTheDocument();
    });
  });
});
