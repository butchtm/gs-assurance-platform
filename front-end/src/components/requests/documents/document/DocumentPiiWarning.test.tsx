import { render, screen, waitFor } from "@testing-library/react";

import { mockDocuments } from "../../../../common/document/DebugDocuments";
import { debugRequests } from "../../../../common/request/DebugRequests";
import { PiiActionCode } from "../../../../common/piiAnalysis/PiiAction";

import { DocumentPiiWarning } from "./DocumentPiiWarning";

describe("DocumentPiiWarning Component", () => {
  it("should show warning pill and review pii button for document with scan status COMPLETED_PII_FOUND and action NEW", async () => {
    render(<DocumentPiiWarning request={debugRequests[0]} document={mockDocuments[0]} onDocumentModify={jest.fn()} />);

    await waitFor(() => {
      expect(screen.getByText("IDENTIFIED PERSONAL INFORMATION (PII)")).toBeInTheDocument();
      expect(screen.getByRole("button", { name: "Review PII" })).toBeInTheDocument();
    });
  });

  it("should show scan in progress pill for document with scan status IN_PROGRESS", async () => {
    render(<DocumentPiiWarning request={debugRequests[0]} document={mockDocuments[1]} onDocumentModify={jest.fn()} />);

    await waitFor(() => {
      expect(screen.getByText("PII SCAN IN PROGRESS")).toBeInTheDocument();
      expect(screen.queryByRole("button", { name: "Review PII" })).not.toBeInTheDocument();
    });
  });

  it("should show no pii related pill for document with scan status COMPLETED_NO_PII", async () => {
    render(<DocumentPiiWarning request={debugRequests[0]} document={mockDocuments[2]} onDocumentModify={jest.fn()} />);

    await waitFor(() => {
      expect(screen.queryByTestId("document-pii-warning-pill")).not.toBeInTheDocument();
      expect(screen.queryByRole("button", { name: "Review PII" })).not.toBeInTheDocument();
    });
  });

  it("should show identification not possible pill for document with scan status FAILED", async () => {
    render(<DocumentPiiWarning request={debugRequests[0]} document={mockDocuments[3]} onDocumentModify={jest.fn()} />);

    await waitFor(() => {
      expect(screen.getByText("PII IDENTIFICATION NOT POSSIBLE")).toBeInTheDocument();
      expect(screen.queryByRole("button", { name: "Review PII" })).not.toBeInTheDocument();
    });
  });

  it("should show no pii related pill for document with no scan status", async () => {
    render(<DocumentPiiWarning request={debugRequests[0]} document={mockDocuments[4]} onDocumentModify={jest.fn()} />);

    await waitFor(() => {
      expect(screen.queryByTestId("document-pii-warning-pill")).not.toBeInTheDocument();
      expect(screen.queryByRole("button", { name: "Review PII" })).not.toBeInTheDocument();
    });
  });

  it("should show warning pill and review pii button for document with action UNDER_INVESTIGATION", async () => {
    const docWithAction = mockDocuments[0];
    docWithAction.piiAction = PiiActionCode.UNDER_INVESTIGATION;
    render(<DocumentPiiWarning request={debugRequests[0]} document={docWithAction} onDocumentModify={jest.fn()} />);

    await waitFor(() => {
      expect(screen.getByText("PII UNDER INVESTIGATION")).toBeInTheDocument();
      expect(screen.getByRole("button", { name: "Review PII" })).toBeInTheDocument();
    });
  });

  it("should show warning pill and review pii button for document with action PII_NOT_SUITABLE", async () => {
    const docWithAction = mockDocuments[0];
    docWithAction.piiAction = PiiActionCode.PII_NOT_SUITABLE;
    render(<DocumentPiiWarning request={debugRequests[0]} document={docWithAction} onDocumentModify={jest.fn()} />);

    await waitFor(() => {
      expect(screen.getByText("PII NOT SUITABLE FOR PUBLIC")).toBeInTheDocument();
      expect(screen.getByRole("button", { name: "Review PII" })).toBeInTheDocument();
    });
  });

  it("should show warning pill and review pii button for document with action DOCUMENT_REMOVED", async () => {
    const docWithAction = mockDocuments[0];
    docWithAction.piiAction = PiiActionCode.DOCUMENT_REMOVED;
    render(<DocumentPiiWarning request={debugRequests[0]} document={docWithAction} onDocumentModify={jest.fn()} />);

    await waitFor(() => {
      expect(screen.getByText("DOCUMENT BEING REMOVED DUE TO PII")).toBeInTheDocument();
      expect(screen.getByRole("button", { name: "Review PII" })).toBeInTheDocument();
    });
  });

  it("should not show warning pill and review pii button for document with action ACCEPTED", async () => {
    const docWithAction = mockDocuments[0];
    docWithAction.piiAction = PiiActionCode.ACCEPTED;
    render(<DocumentPiiWarning request={debugRequests[0]} document={docWithAction} onDocumentModify={jest.fn()} />);

    await waitFor(() => {
      expect(screen.queryByTestId("document-pii-warning-pill")).not.toBeInTheDocument();
      expect(screen.queryByRole("button", { name: "Review PII" })).not.toBeInTheDocument();
    });
  });

  it("should not show warning pill and review pii button for document with action NO_PII_FOUND", async () => {
    const docWithAction = mockDocuments[0];
    docWithAction.piiAction = PiiActionCode.NO_PII_FOUND;
    render(<DocumentPiiWarning request={debugRequests[0]} document={docWithAction} onDocumentModify={jest.fn()} />);

    await waitFor(() => {
      expect(screen.queryByTestId("document-pii-warning-pill")).not.toBeInTheDocument();
      expect(screen.queryByRole("button", { name: "Review PII" })).not.toBeInTheDocument();
    });
  });
});
