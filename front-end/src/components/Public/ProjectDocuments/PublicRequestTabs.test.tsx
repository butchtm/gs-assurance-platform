import { fireEvent, render, screen, waitFor } from "@testing-library/react";

import { downloadPublicDocument } from "../../../common/document/ProjectDocuments";
import { mockProjectDocumentsResponse } from "../../../common/project/DebugProject";

import { PublicRequestTabs } from "./PublicRequestTabs";

jest.mock("../../../common/document/ProjectDocuments", () => ({
  downloadPublicDocument: jest.fn(),
}));

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
}));

const comments = mockProjectDocumentsResponse.requests[0].comments;
const documents = mockProjectDocumentsResponse.requests[0].documents;

describe("PublicRequestTabs", () => {
  it("should display the public project documents", async () => {
    render(
      <PublicRequestTabs comments={comments} documents={documents} isPublicConsultationPage={false} requestId={0} />,
    );

    await waitFor(() => {
      expect(screen.getByText("Project Design Document (TestDocument.docx)")).toBeInTheDocument();
      expect(screen.getByText("26/10/2021")).toBeInTheDocument();
    });
  });

  it("should allow for document downloading", async () => {
    (downloadPublicDocument as jest.Mock).mockResolvedValue(undefined);
    global.URL.revokeObjectURL = jest.fn();
    HTMLAnchorElement.prototype.click = jest.fn();

    render(
      <PublicRequestTabs comments={comments} documents={documents} isPublicConsultationPage={false} requestId={0} />,
    );

    await waitFor(() => {
      const downloadButton = screen.getByTestId("download-button");
      fireEvent.click(downloadButton);
      expect(downloadPublicDocument).toHaveBeenCalledWith("69f6ab50-8966-43f0-4e79-08db2aed1332", "TestDocument.docx");
    });
  });

  it("should display comment details when included in the response", async () => {
    render(
      <PublicRequestTabs comments={comments} documents={documents} isPublicConsultationPage={false} requestId={0} />,
    );

    fireEvent.click(screen.getByText("Comments"));

    await waitFor(() => {
      expect(screen.getByText("Test Comment")).toBeInTheDocument();
      expect(screen.getByText("Test Comment Anonymous")).toBeInTheDocument();

      expect(screen.getByText("Test User")).toBeInTheDocument();

      expect(screen.getByText("01/07/2024")).toBeInTheDocument();
      expect(screen.getByText("02/07/2024")).toBeInTheDocument();
    });
  });

  it("should display message when no documents included but comments are included", async () => {
    render(<PublicRequestTabs comments={comments} documents={[]} isPublicConsultationPage={false} requestId={0} />);

    await waitFor(() => {
      expect(screen.getByText("No Public Documents Found")).toBeInTheDocument();
    });
  });

  it("should display message when no comments included but documents are included", async () => {
    render(<PublicRequestTabs comments={[]} documents={documents} isPublicConsultationPage={false} requestId={0} />);

    fireEvent.click(screen.getByText("Comments"));

    await waitFor(() => {
      expect(screen.getByText("No Public Comments Found")).toBeInTheDocument();
    });
  });
});
