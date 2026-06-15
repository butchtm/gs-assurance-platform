import { render, screen, within } from "@testing-library/react";

import { debugRequests } from "@/common/request/DebugRequests";

import { StatusDropdownRequestPanel } from "./StatusDropdownRequestPanel";

describe("StatusDropdownRequestPanel", () => {
  it.each([
    { request: debugRequests[0], label: "Request ID", expectedText: debugRequests[0].id },
    { request: debugRequests[0], label: "Request Status", expectedText: debugRequests[0].requestStatus.description },
    { request: debugRequests[0], label: "Documents Status", expectedText: "testDocumentStatus" },
    { request: debugRequests[0], label: "Request Type", expectedText: debugRequests[0].requestType.description },
    { request: debugRequests[0], label: "PAA request", expectedText: "Yes" },
    { request: debugRequests[1], label: "PAA request", expectedText: "No" },
  ])("renders request details correctly", ({ request, label, expectedText }) => {
    render(
      <StatusDropdownRequestPanel request={request} dashboardFormat={false} documentStatus={"testDocumentStatus"} />,
    );

    const labelElement = screen.getByText(label);
    const row = labelElement.closest(".MuiGrid-container"); // row container created by MUI Grid/Box
    expect(row).toBeTruthy();

    expect(within(row as HTMLElement).getByText(expectedText)).toBeInTheDocument();
  });
});
