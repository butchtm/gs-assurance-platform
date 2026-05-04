import { render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";

import { getAssignedRequests } from "../../Requests";
import { mockActiveReferenceTypes } from "../../../../common/data-types/DebugReferenceTypes";

import FinanceRequestsTable from "./FinanceRequestsTable";

jest.mock("../../Requests", () => ({
  getAssignedRequests: jest.fn(),
}));

jest.mock("../../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../../common/data-types/DataReferenceTypes"),
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
  }),
}));

const mockRequestsResponse = {
  content: [
    {
      requestId: 1,
      assignmentId: 101,
      gsid: "GSID1",
      projectName: "Project 1",
      projectAccountName: "Developer 1",
      requestName: "Request 1",
      assignmentCreatedDate: "2024-07-18",
      estimatedAnnualEmissionReductionAmount: 1000,
      projectSize: { description: "Large" },
      projectCarbonStream: { description: "Stream 1" },
      invoiceStatus: "Pending",
      submittedByGivenName: "Joe",
      submittedByFamilyName: "Blogs",
    },
    {
      requestId: 2,
      assignmentId: 102,
      gsid: "GSID2",
      projectName: "Project 2",
      projectAccountName: "Developer 2",
      requestName: "Request 2",
      assignmentCreatedDate: "2024-07-19",
      estimatedAnnualEmissionReductionAmount: 2000,
      projectSize: { description: "Small" },
      projectCarbonStream: { description: "Stream 2" },
      invoiceStatus: "Completed",
      submittedByGivenName: "Jane",
      submittedByFamilyName: "Doe",
    },
  ],
  totalPages: 1,
};

describe("FinanceRequestsTable", () => {
  beforeEach(() => {
    (getAssignedRequests as jest.Mock).mockResolvedValue(mockRequestsResponse);
  });

  test("loads and displays requests", async () => {
    render(
      <MemoryRouter>
        <FinanceRequestsTable />
      </MemoryRouter>,
    );

    await waitFor(() => {
      expect(screen.getByText("GSID1")).toBeInTheDocument();
      expect(screen.getByText("GSID2")).toBeInTheDocument();
    });

    expect(screen.getByText("Project 1")).toBeInTheDocument();
    expect(screen.getByText("Project 2")).toBeInTheDocument();
    expect(screen.getByText("Developer 1")).toBeInTheDocument();
    expect(screen.getByText("Developer 2")).toBeInTheDocument();
    expect(screen.getByText("Request 1")).toBeInTheDocument();
    expect(screen.getByText("Request 2")).toBeInTheDocument();
    expect(screen.getByText("1000")).toBeInTheDocument();
    expect(screen.getByText("2000")).toBeInTheDocument();
    expect(screen.getByText("Large")).toBeInTheDocument();
    expect(screen.getByText("Small")).toBeInTheDocument();
    expect(screen.getByText("Stream 1")).toBeInTheDocument();
    expect(screen.getByText("Stream 2")).toBeInTheDocument();
    expect(screen.getByText("Pending")).toBeInTheDocument();
    expect(screen.getByText("Completed")).toBeInTheDocument();
  });

  test('displays "No records to display" when there are no requests', async () => {
    (getAssignedRequests as jest.Mock).mockResolvedValue({ content: [], totalPages: 0 });

    render(
      <MemoryRouter>
        <FinanceRequestsTable />
      </MemoryRouter>,
    );

    await waitFor(() => {
      expect(screen.getByText("No records to display")).toBeInTheDocument();
    });
  });
});
