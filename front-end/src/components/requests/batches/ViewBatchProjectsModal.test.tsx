import { fireEvent, render, screen, waitFor } from "@testing-library/react";

import mockProjects from "../../../common/project/DebugProject";
import { mockBatchProjectsRequests } from "../../../common/request/DebugRequests";
import { getBatchProjects } from "../../Project/Project";

import ViewBatchProjectsModal from "./ViewBatchProjectsModal";

jest.mock("../../Project/Project", () => ({
  getBatchProjects: jest.fn(),
}));

describe("ViewBatchProjectsModal", () => {
  beforeEach(() => {
    (getBatchProjects as jest.Mock).mockResolvedValue({
      content: mockProjects.slice(0, 3),
      totalPages: 1,
    });
  });

  it("renders the modal when isOpen is true", async () => {
    render(<ViewBatchProjectsModal isOpen={true} onClose={jest.fn()} request={mockBatchProjectsRequests[0]} />);

    await waitFor(() => {
      expect(screen.getByText("GSID123")).toBeInTheDocument();
      expect(screen.getByText("Project 1")).toBeInTheDocument();

      expect(screen.getByText("GSID234")).toBeInTheDocument();
      expect(screen.getByText("Project 2")).toBeInTheDocument();

      expect(screen.getByText("GSID567")).toBeInTheDocument();
      expect(screen.getByText("Project 3")).toBeInTheDocument();
    });
  });

  it("calls onClose when the modal is closed", async () => {
    const handleClose = jest.fn();
    render(<ViewBatchProjectsModal isOpen={true} onClose={handleClose} request={mockBatchProjectsRequests[0]} />);

    fireEvent.click(screen.getByTestId("close-btn"));
    await waitFor(() => {
      expect(handleClose).toHaveBeenCalled();
    });
  });

  it("calls the endpoint when the modal is opened", async () => {
    render(<ViewBatchProjectsModal isOpen={true} onClose={jest.fn()} request={mockBatchProjectsRequests[0]} />);

    await waitFor(() => {
      expect(getBatchProjects).toHaveBeenCalledWith([1, 2, 3], 1);
    });
  });
});
