import { fireEvent, render, screen, waitFor } from "@testing-library/react";

import { debugRequests } from "../../../../common/request/DebugRequests";
import { mockDocuments } from "../../../../common/document/DebugDocuments";
import { mockActiveReferenceTypes } from "../../../../common/data-types/DebugReferenceTypes";

import { DocumentReviewPiiAction } from "./DocumentReviewPiiAction";

jest.mock("../../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../../common/data-types/DataReferenceTypes"),
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
  }),
}));

describe("DocumentReviewPiiAction Component", () => {
  it("should show review pii button", async () => {
    render(
      <DocumentReviewPiiAction request={debugRequests[0]} document={mockDocuments[0]} onDocumentModify={jest.fn()} />,
    );

    await waitFor(() => {
      expect(screen.getByRole("button", { name: "Review PII" })).toBeInTheDocument();
    });
  });

  it("should open pii results dialog on clicking review pii button", async () => {
    render(
      <DocumentReviewPiiAction request={debugRequests[0]} document={mockDocuments[0]} onDocumentModify={jest.fn()} />,
    );

    fireEvent.click(screen.getByRole("button", { name: "Review PII" }));

    await waitFor(() => {
      expect(screen.getByText("Document PII Results")).toBeInTheDocument();
    });
  });
});
