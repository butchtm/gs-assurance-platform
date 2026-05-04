import { render, screen, waitFor } from "@testing-library/react";

import { mockDocuments, mockRequiredDocumentsResponse } from "../../../common/document/DebugDocuments";
import { debugRequests } from "../../../common/request/DebugRequests";

import { StatusDropdown } from "./StatusDropdown";

jest.mock("../../Project/Project", () => ({
  getAllProjectsForAccount: jest.fn(),
  createProject: jest.fn(),
}));

jest.mock("../Requests", () => ({
  getRequestsByProject: jest.fn().mockResolvedValue(debugRequests),
  getRequiredDocumentsForRequest: jest.fn(),
  getAllDocumentsForProjectRequest: jest.fn(),
}));

jest.mock("../../Invoice/Invoice", () => ({
  getInvoice: jest.fn(),
}));

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => jest.fn(),
}));

describe("Status Dropdown Component", () => {
  afterEach(() => {
    jest.restoreAllMocks();
  });

  it("renders fields correctly", async () => {
    render(
      <StatusDropdown
        request={debugRequests[1]}
        dashboardFormat={false}
        refreshRequest={false}
        requestDocuments={mockDocuments}
        requestTypeRequiredDocs={mockRequiredDocumentsResponse}
        assignments={[]}
      />,
    );

    await waitFor(() => {
      expect(screen.getByText("Type2")).toBeInTheDocument();
      expect(screen.getByText("Project developer for preparation")).toBeInTheDocument();
      expect(screen.getByText("3 / 2")).toBeInTheDocument();
    });
  });
});
