import { fireEvent, render, screen, waitFor } from "@testing-library/react";

import { mockBatchProjectsRequests } from "../../../common/request/DebugRequests";
import { getBatchProjects } from "../../Project/Project";

import ViewBatchProjectsAction from "./ViewBatchProjectsAction";

jest.mock("../../Project/Project", () => ({
  getBatchProjects: jest.fn(),
}));

describe("ViewBatchProjectsAction", () => {
  beforeEach(() => {
    (getBatchProjects as jest.Mock).mockResolvedValue(mockBatchProjectsRequests);
  });

  it("action button shows the correct number of projects", async () => {
    render(<ViewBatchProjectsAction request={mockBatchProjectsRequests[0]} />);

    await waitFor(() => {
      expect(screen.getByText("View 3 projects in this request")).toBeInTheDocument();
    });
  });

  it("check modal is displayed on click", async () => {
    render(<ViewBatchProjectsAction request={mockBatchProjectsRequests[0]} />);

    fireEvent.click(screen.getByTestId("open-btn"));
    await waitFor(() => {
      expect(screen.getByText("Projects in this batch request")).toBeInTheDocument();
    });
  });
});
