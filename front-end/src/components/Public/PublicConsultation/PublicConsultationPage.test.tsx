import { fireEvent, render, screen, waitFor } from "@testing-library/react";

import { mockProjectInConsultation } from "../../../common/project/DebugProject";

import { getRequestProjectGsid, getSpecificProjectConsultation } from "./PublicConsultation";
import PublicConsultationInfo from "./PublicConsultationPage";

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => jest.fn(),
  useParams: () => ({ requestId: "1" }),
}));

jest.mock("./PublicConsultation", () => ({
  ...jest.requireActual("./PublicConsultation"),
  getSpecificProjectConsultation: jest.fn(),
  getRequestProjectGsid: jest.fn(),
}));

describe("PublicConsultationPage", () => {
  beforeEach(() => {
    (getRequestProjectGsid as jest.Mock).mockResolvedValue("G1000");
    (getSpecificProjectConsultation as jest.Mock).mockResolvedValue(mockProjectInConsultation);
  });

  it("shows the request information", async () => {
    render(<PublicConsultationInfo></PublicConsultationInfo>);
    await waitFor(() => {
      expect(screen.getByText("Request Name:")).toBeInTheDocument();
      expect(screen.getByText("Request 1")).toBeInTheDocument();
      expect(screen.getByText("Status:")).toBeInTheDocument();
      expect(screen.getByText("In Assignment")).toBeInTheDocument();
    });
  });

  it("displays the request documents", async () => {
    render(<PublicConsultationInfo></PublicConsultationInfo>);
    await waitFor(() => {
      expect(screen.getByText("Documents")).toBeInTheDocument();
      expect(screen.getByText("Document Type (Test Document)")).toBeInTheDocument();
      expect(screen.getByText("File Type (Test File)")).toBeInTheDocument();
    });
  });

  it("displays the request comments", async () => {
    render(<PublicConsultationInfo></PublicConsultationInfo>);

    await waitFor(() => {
      fireEvent.click(screen.getByText("Comments"));

      expect(screen.getByText("Comments")).toBeInTheDocument();
      expect(screen.getByText("Test Comment")).toBeInTheDocument();
      expect(screen.getByText("Test Comment Anonymous")).toBeInTheDocument();
    });
  });

  it("displays the batched projects if a batched request", async () => {
    render(<PublicConsultationInfo></PublicConsultationInfo>);
    await waitFor(() => {
      expect(screen.getByText("Projects in Batch Request")).toBeInTheDocument();
      expect(screen.getByText("GS1000")).toBeInTheDocument();
      expect(screen.getByText("GS1001")).toBeInTheDocument();
    });
  });

  it("displays the project information if not a batched request", async () => {
    const mockProjectNotBatched = mockProjectInConsultation;
    mockProjectNotBatched.requests[0].batchedProjectChildren = [];
    (getSpecificProjectConsultation as jest.Mock).mockResolvedValue(mockProjectNotBatched);

    render(<PublicConsultationInfo></PublicConsultationInfo>);
    await waitFor(() => {
      expect(screen.getByText("Project Impact")).toBeInTheDocument();
      expect(screen.getByText("Project Characteristics")).toBeInTheDocument();
      expect(screen.queryByText("Projects in Batch Request")).not.toBeInTheDocument();
    });
  });
});
