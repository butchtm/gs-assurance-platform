import { render, screen, waitFor } from "@testing-library/react";

import { mockProjectInConsultation } from "../../../common/project/DebugProject";

import PublicConsultationProjectOnlyPage from "./PublicConsultationProjectOnlyPage";

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => jest.fn(),
  useLocation: jest.fn().mockImplementation(() => {
    return {
      state: {
        project: mockProjectInConsultation,
        request: mockProjectInConsultation.requests[0],
      },
    };
  }),
}));

describe("PublicConsultationProjectOnlyPage", () => {
  it("should correctly display the request information", async () => {
    render(<PublicConsultationProjectOnlyPage />);
    await waitFor(() => {
      expect(screen.getByText("Request Name:")).toBeInTheDocument();
      expect(screen.getByText("Request 1")).toBeInTheDocument();
      expect(screen.getByText("Status:")).toBeInTheDocument();
      expect(screen.getByText("In Assignment")).toBeInTheDocument();
    });
  });

  it("should correctly display the project information", async () => {
    render(<PublicConsultationProjectOnlyPage />);
    await waitFor(() => {
      expect(screen.getByText("Project Impact")).toBeInTheDocument();
      expect(screen.getByText("Project Characteristics")).toBeInTheDocument();
    });
  });
});
