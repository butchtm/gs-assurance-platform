import { fireEvent, render, screen, waitFor } from "@testing-library/react";

import { PiiActionCode } from "../../common/piiAnalysis/PiiAction";

import { PiiActionWarningPill } from "./PiiActionWarningPill";

describe("PiiActionWarningPill Component", () => {
  it("should show identified personal information (pii) for action NEW", async () => {
    render(<PiiActionWarningPill action={PiiActionCode.NEW} />);

    await waitFor(() => {
      expect(screen.getByTestId("pii-warning-pill")).toBeInTheDocument();
      expect(screen.getByText("IDENTIFIED PERSONAL INFORMATION (PII)")).toBeInTheDocument();
    });
  });

  it("should show under pii under investigation for action under investigation", async () => {
    render(<PiiActionWarningPill action={PiiActionCode.UNDER_INVESTIGATION} />);

    await waitFor(() => {
      expect(screen.getByTestId("pii-warning-pill")).toBeInTheDocument();
      expect(screen.getByText("PII UNDER INVESTIGATION")).toBeInTheDocument();
    });
  });

  it("should show under pii not suitable for the public for action pii not suitable", async () => {
    render(<PiiActionWarningPill action={PiiActionCode.PII_NOT_SUITABLE} />);

    await waitFor(() => {
      expect(screen.getByTestId("pii-warning-pill")).toBeInTheDocument();
      expect(screen.getByText("PII NOT SUITABLE FOR PUBLIC")).toBeInTheDocument();
    });
  });

  it("should show document being removed due to pii for action document removed", async () => {
    render(<PiiActionWarningPill action={PiiActionCode.DOCUMENT_REMOVED} />);

    await waitFor(() => {
      expect(screen.getByTestId("pii-warning-pill")).toBeInTheDocument();
      expect(screen.getByText("DOCUMENT BEING REMOVED DUE TO PII")).toBeInTheDocument();
    });
  });

  it("should show no warning pill for action accepted", async () => {
    render(<PiiActionWarningPill action={PiiActionCode.ACCEPTED} />);

    await waitFor(() => {
      expect(screen.queryByTestId("pii-warning-pill")).not.toBeInTheDocument();
    });
  });

  it("should show no warning pill for action no pii found", async () => {
    render(<PiiActionWarningPill action={PiiActionCode.NO_PII_FOUND} />);

    await waitFor(() => {
      expect(screen.queryByTestId("pii-warning-pill")).not.toBeInTheDocument();
    });
  });

  it("should render tooltip with helper text if provided", async () => {
    render(<PiiActionWarningPill action={PiiActionCode.NEW} helperText={<>TEST_HELPER_TEXT</>} />);

    fireEvent.click(screen.getByTestId("help-icon"));

    await waitFor(() => {
      expect(screen.getByTestId("help-icon")).toBeInTheDocument();
      expect(screen.getByText("TEST_HELPER_TEXT")).toBeInTheDocument();
    });
  });

  it("should not render tooltip if no helper text provided", async () => {
    render(<PiiActionWarningPill action={PiiActionCode.NEW} />);

    await waitFor(() => {
      expect(screen.queryByTestId("help-icon")).not.toBeInTheDocument();
    });
  });
});
