import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import dayjs from "dayjs";
import { toast } from "react-toastify";

import { StatusReport } from "./StatusReport";
import { exportStatusReportExcel, getStatusReport } from "./StatusReportApi";

jest.mock("./StatusReportApi", () => ({
  getStatusReport: jest.fn(),
  exportStatusReportExcel: jest.fn(),
}));

jest.mock("react-toastify", () => ({
  toast: {
    success: jest.fn(),
    error: jest.fn(),
  },
}));

const mockStatusReportResponse = {
  projects: [
    {
      type: "GEOTHERMAL",
      description: "Geothermal",
      count: 3,
    },
  ],
  requests: [
    {
      type: "GENERIC",
      description: "Generic",
      completedCount: 5,
    },
  ],
  allRequests: {
    completedCount: 61,
    successfulCompletenessChecksCount: 56,
  },
};

describe("StatusReport", () => {
  const user = userEvent.setup();

  beforeEach(() => {
    (getStatusReport as jest.Mock).mockResolvedValue(mockStatusReportResponse);
    (exportStatusReportExcel as jest.Mock).mockResolvedValue(undefined);
    jest.clearAllMocks();
  });

  const renderWithProvider = (component: React.ReactElement) => {
    return render(<LocalizationProvider dateAdapter={AdapterDayjs}>{component}</LocalizationProvider>);
  };

  it("should call getStatusReport with default date range (30 days ago to today)", async () => {
    renderWithProvider(<StatusReport />);

    await waitFor(() => {
      expect(getStatusReport).toHaveBeenCalledWith(
        expect.stringMatching(/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}.\d{3}Z$/),
        expect.stringMatching(/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}.\d{3}Z$/),
      );
    });

    const calls = (getStatusReport as jest.Mock).mock.calls;
    const startDateArg = calls[0][0];
    const endDateArg = calls[0][1];

    const startDate = dayjs.utc(startDateArg);
    const endDate = dayjs.utc(endDateArg);
    const expectedStartDate = dayjs.utc().subtract(30, "day");
    const expectedEndDate = dayjs.utc();

    expect(Math.abs(startDate.diff(expectedStartDate, "day"))).toBeLessThanOrEqual(1);
    expect(Math.abs(endDate.diff(expectedEndDate, "day"))).toBeLessThanOrEqual(1);
  });

  it("should display total number of completed requests", async () => {
    renderWithProvider(<StatusReport />);
    await waitFor(() => {
      expect(screen.getByText("Total Completed Requests")).toBeInTheDocument();
      expect(screen.getByText(mockStatusReportResponse.allRequests.completedCount)).toBeInTheDocument();
    });
  });

  it("should display total number of successful completeness checks", async () => {
    renderWithProvider(<StatusReport />);
    await waitFor(() => {
      expect(screen.getByText("Successful Completeness Checks")).toBeInTheDocument();
      expect(
        screen.getByText(mockStatusReportResponse.allRequests.successfulCompletenessChecksCount),
      ).toBeInTheDocument();
    });
  });

  it("should display project description with count", async () => {
    renderWithProvider(<StatusReport />);
    await waitFor(() => {
      expect(screen.getByText(mockStatusReportResponse.projects[0].description)).toBeInTheDocument();
      expect(screen.getByText(mockStatusReportResponse.projects[0].count)).toBeInTheDocument();
    });
  });

  it("should display request description with completed count", async () => {
    renderWithProvider(<StatusReport />);
    await waitFor(() => {
      expect(screen.getByText(mockStatusReportResponse.requests[0].description)).toBeInTheDocument();
      expect(screen.getByText(mockStatusReportResponse.requests[0].completedCount)).toBeInTheDocument();
    });
  });

  it("should call exportStatusReportExcel when export button is clicked", async () => {
    renderWithProvider(<StatusReport />);

    await waitFor(() => {
      expect(screen.getByTestId("export-button")).toBeInTheDocument();
    });

    const exportButton = screen.getByTestId("export-button");
    await user.click(exportButton);

    await waitFor(() => {
      expect(exportStatusReportExcel).toHaveBeenCalledWith(
        expect.stringMatching(/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}.\d{3}Z$/),
        expect.stringMatching(/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}.\d{3}Z$/),
      );
    });
  });

  it("should show success toast when export succeeds", async () => {
    renderWithProvider(<StatusReport />);

    await waitFor(() => {
      expect(screen.getByTestId("export-button")).toBeInTheDocument();
    });

    const exportButton = screen.getByTestId("export-button");
    await user.click(exportButton);

    await waitFor(() => {
      expect(toast.success).toHaveBeenCalledWith("Status report exported successfully!");
    });
  });

  it("should show error toast when export fails", async () => {
    const consoleErrorSpy = jest.spyOn(console, "error").mockImplementation();
    (exportStatusReportExcel as jest.Mock).mockRejectedValueOnce(new Error("Export failed"));

    renderWithProvider(<StatusReport />);

    await waitFor(() => {
      expect(screen.getByTestId("export-button")).toBeInTheDocument();
    });

    const exportButton = screen.getByTestId("export-button");
    await user.click(exportButton);

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith("Failed to export status report. Please try again later.");
    });

    consoleErrorSpy.mockRestore();
  });

  it("should call exportStatusReportExcel with correct date format", async () => {
    renderWithProvider(<StatusReport />);

    await waitFor(() => {
      expect(screen.getByTestId("export-button")).toBeInTheDocument();
    });

    const exportButton = screen.getByTestId("export-button");
    await user.click(exportButton);

    await waitFor(() => {
      expect(exportStatusReportExcel).toHaveBeenCalled();
    });

    const calls = (exportStatusReportExcel as jest.Mock).mock.calls;
    const startDateArg = calls[0][0];
    const endDateArg = calls[0][1];

    const startDate = dayjs.utc(startDateArg);
    const endDate = dayjs.utc(endDateArg);
    const expectedStartDate = dayjs.utc().subtract(30, "day");
    const expectedEndDate = dayjs.utc();

    expect(Math.abs(startDate.diff(expectedStartDate, "day"))).toBeLessThanOrEqual(1);
    expect(Math.abs(endDate.diff(expectedEndDate, "day"))).toBeLessThanOrEqual(1);
  });
});
