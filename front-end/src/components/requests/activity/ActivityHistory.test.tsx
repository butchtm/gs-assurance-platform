import { render, screen, waitFor } from "@testing-library/react";

import { apiClient } from "../../APIClient/Client";
import { RequestActivity } from "../../../common/request/GSRequest";
import { debugRequests } from "../../../common/request/DebugRequests";

import { ActivityHistory } from "./ActivityHistory";

jest.mock("../../APIClient/Client", () => ({
  apiClient: {
    makeRequest: jest.fn(),
  },
}));

describe("ActivityHistory", () => {
  beforeEach(() => {
    (apiClient.makeRequest as jest.Mock).mockResolvedValue({
      content: [mockRequestActivity],
      totalElements: 1,
    });
  });
  it("should display the activity history", async () => {
    render(<ActivityHistory request={debugRequests[0]} />);
    await waitFor(() => {
      expect(apiClient.makeRequest).toHaveBeenCalledTimes(1);
      expect(screen.getByText("Activity")).toBeInTheDocument();
      expect(screen.getByText("Round 2 Review")).toBeInTheDocument();
      expect(screen.getByText("Started")).toBeInTheDocument();
      expect(screen.getByText("20/11/2024")).toBeInTheDocument();
      expect(screen.getByText("Completed")).toBeInTheDocument();
      expect(screen.getByText("28/11/2024")).toBeInTheDocument();
      expect(screen.getByText("Deadline")).toBeInTheDocument();
      expect(screen.getByText("01/12/2024")).toBeInTheDocument();
      expect(screen.getByText("Responsibility")).toBeInTheDocument();
      expect(screen.getByText("Reviewer")).toBeInTheDocument();
    });
  });
});

const mockRequestActivity: RequestActivity = {
  id: 2,
  index: 2,
  name: "Round 2 Review",
  startDate: "2024-11-20T10:21:37.54Z",
  endDate: "2024-11-28T10:21:37.54Z",
  dueDate: "2024-12-01T10:21:37.54Z",
  skipped: false,
  activity: {
    id: 2,
    index: 2,
    name: "Conclusion of Review",
    displayName: "Round 2+ Review",
    totalDays: 8,
    internal: false,
  },
  account: {
    id: 7,
    name: "Reviewer",
    accountType: {
      id: 0,
      code: "REVIEWER",
      description: "Reviewer",
      isActive: true,
    },
  },
};
