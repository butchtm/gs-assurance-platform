import { render, screen, waitFor } from "@testing-library/react";

import { mockProjectInConsultation } from "../../../../common/project/DebugProject";

import PublicConsultationRequestDetails from "./PublicConsultationRequestDetails";

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => jest.fn(),
}));

describe("PublicConsultationRequestDetails", () => {
  it("correctly displays the request details", async () => {
    render(<PublicConsultationRequestDetails request={mockProjectInConsultation.requests[0]} />);
    await waitFor(() => {
      expect(screen.getByText("Request 1")).toBeInTheDocument();
      expect(screen.getByText("In Assignment")).toBeInTheDocument();
      expect(screen.getByText("08/07/2024")).toBeInTheDocument();
      expect(screen.getByText("09/07/2024")).toBeInTheDocument();
      expect(screen.getByText("Period 1 - 01/07/2024 to 01/07/2029")).toBeInTheDocument();
      expect(screen.getByText("1,234")).toBeInTheDocument();
    });
  });
});
