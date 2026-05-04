import { render, screen, waitFor, within } from "@testing-library/react";

import { debugRequests } from "../../../../common/request/DebugRequests";
import {
  documentPiiResults,
  documentPiiResultsWithLocations,
  documentPiiResultsWithNoPageNumber,
  mockDocuments,
} from "../../../../common/document/DebugDocuments";
import { mockActiveReferenceTypes } from "../../../../common/data-types/DebugReferenceTypes";

import { getPiiResults } from "./ReviewDocumentPii";
import { ReviewDocumentPiiDialog } from "./ReviewDocumentPiiDialog";

jest.mock("./ReviewDocumentPii", () => ({
  getPiiResults: jest.fn(),
}));

jest.mock("../../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../../common/data-types/DataReferenceTypes"),
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
  }),
}));

describe("ReviewDocumentPiiDialog", () => {
  it("should show table of pii results", async () => {
    (getPiiResults as jest.Mock).mockResolvedValue(documentPiiResults);

    render(
      <ReviewDocumentPiiDialog
        isOpen={true}
        request={debugRequests[0]}
        document={mockDocuments[0]}
        onClose={jest.fn()}
        onDocumentModify={jest.fn()}
      />,
    );

    await waitFor(() => {
      expect(screen.getByText("Current Action:")).toBeInTheDocument();
      expect(screen.getByText("No Action Taken")).toBeInTheDocument();
      // Header row
      expect(screen.getByText("Entity")).toBeInTheDocument();
      expect(screen.getByText("Probability")).toBeInTheDocument();
      expect(screen.getByText("Page Number")).toBeInTheDocument();
      expect(screen.getByText("Occurrences")).toBeInTheDocument();
      // First data row
      const firstRow = screen.getAllByRole("row")[1];
      expect(within(firstRow).getByText("ADDRESS")).toBeInTheDocument();
      expect(within(firstRow).getByText("HIGH")).toBeInTheDocument();
      expect(within(firstRow).getByText("1")).toBeInTheDocument();
      expect(within(firstRow).getByText("2")).toBeInTheDocument();
    });
  });

  it("should not include page number in pii results table if not present in response", async () => {
    (getPiiResults as jest.Mock).mockResolvedValue(documentPiiResultsWithNoPageNumber);

    render(
      <ReviewDocumentPiiDialog
        isOpen={true}
        request={debugRequests[0]}
        document={mockDocuments[0]}
        onClose={jest.fn()}
        onDocumentModify={jest.fn()}
      />,
    );

    await waitFor(() => {
      // Header row
      expect(screen.getByText("Entity")).toBeInTheDocument();
      expect(screen.getByText("Probability")).toBeInTheDocument();
      expect(screen.queryByText("Page Number")).not.toBeInTheDocument();
      expect(screen.getByText("Occurrences")).toBeInTheDocument();
      // First data row
      const firstRow = screen.getAllByRole("row")[1];
      expect(within(firstRow).getByText("ADDRESS")).toBeInTheDocument();
      expect(within(firstRow).getByText("HIGH")).toBeInTheDocument();
      expect(within(firstRow).getByText("2")).toBeInTheDocument();
    });
  });

  it("should include location in pii results table if present in response", async () => {
    (getPiiResults as jest.Mock).mockResolvedValue(documentPiiResultsWithLocations);

    render(
      <ReviewDocumentPiiDialog
        isOpen={true}
        request={debugRequests[0]}
        document={mockDocuments[0]}
        onClose={jest.fn()}
        onDocumentModify={jest.fn()}
      />,
    );

    await waitFor(() => {
      // Header row
      expect(screen.getByText("Entity")).toBeInTheDocument();
      expect(screen.getByText("Probability")).toBeInTheDocument();
      expect(screen.queryByText("Location")).toBeInTheDocument();
      expect(screen.getByText("Occurrences")).toBeInTheDocument();
      // First data row
      const firstRow = screen.getAllByRole("row")[1];
      expect(within(firstRow).getByText("ADDRESS")).toBeInTheDocument();
      expect(within(firstRow).getByText("HIGH")).toBeInTheDocument();
      expect(within(firstRow).getByText("Sheet1!A2")).toBeInTheDocument();
      expect(within(firstRow).getByText("2")).toBeInTheDocument();
    });
  });

  it("should paginate pii results with default of 5 per page", async () => {
    (getPiiResults as jest.Mock).mockResolvedValue(documentPiiResults);

    render(
      <ReviewDocumentPiiDialog
        isOpen={true}
        request={debugRequests[0]}
        document={mockDocuments[0]}
        onClose={jest.fn()}
        onDocumentModify={jest.fn()}
      />,
    );

    await waitFor(() => {
      // 5 data rows plus header row = 6 rows total
      expect(screen.getAllByRole("row").length).toBe(6);
      expect(screen.getByTestId("page-1")).toBeInTheDocument();
      expect(screen.getByTestId("page-2")).toBeInTheDocument();
    });
  });

  it("should show current PII action taken", async () => {
    (getPiiResults as jest.Mock).mockResolvedValue(documentPiiResults);

    render(
      <ReviewDocumentPiiDialog
        isOpen={true}
        request={debugRequests[0]}
        document={mockDocuments[4]}
        onClose={jest.fn()}
        onDocumentModify={jest.fn()}
      />,
    );

    await waitFor(() => {
      expect(screen.getByText("Current Action:")).toBeInTheDocument();
      expect(screen.getByText("No Action Taken")).toBeInTheDocument();
    });
  });
});
