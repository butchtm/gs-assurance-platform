import { render, screen, waitFor } from "@testing-library/react";

import { DocumentPiiScanStatus } from "../../../../common/document/GSDocument";

import { DocumentPiiScanPill } from "./DocumentPiiScanPill";

describe("DocumentPiiScanPill Component", () => {
  it("should show pii scan in progress for status in_progress", async () => {
    render(<DocumentPiiScanPill scanStatus={DocumentPiiScanStatus.IN_PROGRESS} />);

    await waitFor(() => {
      expect(screen.getByTestId("document-pii-warning-pill")).toBeInTheDocument();
      expect(screen.getByText("PII SCAN IN PROGRESS")).toBeInTheDocument();
    });
  });

  it("should show pii identification not possible for status failed", async () => {
    render(<DocumentPiiScanPill scanStatus={DocumentPiiScanStatus.FAILED} />);

    await waitFor(() => {
      expect(screen.getByTestId("document-pii-warning-pill")).toBeInTheDocument();
      expect(screen.getByText("PII IDENTIFICATION NOT POSSIBLE")).toBeInTheDocument();
    });
  });

  it("should show no status warning pill for status completed no pii", async () => {
    render(<DocumentPiiScanPill scanStatus={DocumentPiiScanStatus.COMPLETED_NO_PII} />);

    await waitFor(() => {
      expect(screen.queryByTestId("document-pii-warning-pill")).not.toBeInTheDocument();
    });
  });

  it("should show no status warning pill for status completed pii found", async () => {
    render(<DocumentPiiScanPill scanStatus={DocumentPiiScanStatus.COMPLETED_NO_PII} />);

    await waitFor(() => {
      expect(screen.queryByTestId("document-pii-warning-pill")).not.toBeInTheDocument();
    });
  });
});
