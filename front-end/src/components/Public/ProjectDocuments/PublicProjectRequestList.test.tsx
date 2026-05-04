import { render, screen, waitFor } from "@testing-library/react";
import { useParams } from "react-router-dom";

import { getPublicProjectDocuments } from "../../../common/document/ProjectDocuments";
import { mockProjectDocumentsResponse } from "../../../common/project/DebugProject";

import PublicProjectRequestList from "./PublicProjectRequestList";

jest.mock("../../../common/document/ProjectDocuments", () => ({
  getPublicProjectDocuments: jest.fn(),
}));

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useParams: jest.fn(),
}));

describe("PublicProjectRequestList", () => {
  beforeEach(() => {
    (getPublicProjectDocuments as jest.Mock).mockResolvedValue(mockProjectDocumentsResponse);
    (useParams as jest.Mock).mockReturnValue({ gsId: "GS123" });
  });

  it("should display the public project documents", async () => {
    render(<PublicProjectRequestList />);

    await waitFor(() => {
      expect(screen.getByText("GS123 - Test Project")).toBeInTheDocument();
      expect(screen.getByText("Preliminary Review")).toBeInTheDocument();
      expect(screen.getByText("Project Design Document (TestDocument.docx)")).toBeInTheDocument();
      expect(screen.getByText("26/10/2021")).toBeInTheDocument();
    });
  });

  it("should not be case sensitive", async () => {
    (useParams as jest.Mock).mockReturnValue({ gsId: "gs123" });
    render(<PublicProjectRequestList />);

    await waitFor(() => {
      expect(getPublicProjectDocuments).toHaveBeenCalledWith("GS123");
      expect(screen.getByText("GS123 - Test Project")).toBeInTheDocument();
    });
  });

  it("should display stakeholder consultation dates when included in the response", async () => {
    render(<PublicProjectRequestList />);

    await waitFor(() => {
      expect(screen.getByText("Stakeholder Consultation 23/09/2025 - 26/09/2025")).toBeInTheDocument();
    });
  });

  it("should not display stakeholder consultation dates when not included in the response", async () => {
    (getPublicProjectDocuments as jest.Mock).mockResolvedValue({
      gsid: "GS123",
      name: "Test Project",
      requests: [{ id: 1 }],
    });
    render(<PublicProjectRequestList />);

    await waitFor(() => {
      expect(screen.queryByText("Stakeholder Consultation")).not.toBeInTheDocument();
    });
  });

  it("should display comment and document tabs when comments and documents", async () => {
    render(<PublicProjectRequestList />);

    await waitFor(() => {
      expect(screen.getByText("Documents")).toBeInTheDocument();
      expect(screen.getByText("Comments")).toBeInTheDocument();
    });
  });
});
