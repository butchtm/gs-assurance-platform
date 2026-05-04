import { fireEvent, render, screen, waitFor } from "@testing-library/react";

import { mockProjectInConsultation } from "../../../../common/project/DebugProject";

import PublicConsultationBatchedProjects from "./PublicConsultationBatchedProjects";

const mockedNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockedNavigate,
}));

describe("PublicConsultationBatchedProjects", () => {
  const mockRequest = mockProjectInConsultation.requests[0];

  it("should display the details of all projects related to a batch", async () => {
    render(
      <PublicConsultationBatchedProjects
        parentProject={mockProjectInConsultation}
        requestInConsultation={mockRequest}
      ></PublicConsultationBatchedProjects>,
    );

    await waitFor(() => {
      expect(screen.getByText("GS1000")).toBeInTheDocument();
      expect(screen.getByText("GS1001")).toBeInTheDocument();

      expect(screen.getByText("Programme of Activities")).toBeInTheDocument();
      expect(screen.getByText("Real Case VPA")).toBeInTheDocument();

      expect(screen.getByText("Period 1 - 01/07/2024 to 01/07/2029")).toBeInTheDocument();
      expect(screen.getByText("Period 2 - 02/07/2029 to 02/07/2034")).toBeInTheDocument();

      expect(screen.getByText("1234")).toBeInTheDocument();
      expect(screen.getByText("5678")).toBeInTheDocument();
    });
  });

  it("should route to the project information when selected", async () => {
    render(
      <PublicConsultationBatchedProjects
        parentProject={mockProjectInConsultation}
        requestInConsultation={mockRequest}
      ></PublicConsultationBatchedProjects>,
    );
    await waitFor(() => {
      fireEvent.click(screen.getByText("GS1000"));
      expect(mockedNavigate).toHaveBeenCalledTimes(1);
    });
  });
});
