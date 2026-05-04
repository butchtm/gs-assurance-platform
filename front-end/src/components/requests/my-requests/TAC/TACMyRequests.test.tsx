import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import dayjs from "dayjs";
import { MemoryRouter } from "react-router-dom";

import { mockRequestConsultations, mockTacDashboardRequests } from "../../../../common/request/DebugRequests";
import * as requestModule from "../../Requests";

import TACMyRequests from "./TACMyRequests";

jest.mock("../../Requests", () => ({
  getRequestsInConsultation: jest.fn(),
  getRequestsInConsultationWithInfo: jest.fn(),
  getRequestsInConsultationByCompetencies: jest.fn(),
  getLiveConsultationsForRequest: jest.fn(),
}));

const mockResponse = {
  data: mockTacDashboardRequests,
  totalPages: 1,
};

describe("TACMyRequests", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (requestModule.getRequestsInConsultationWithInfo as jest.Mock).mockResolvedValue(mockResponse);
    (requestModule.getRequestsInConsultationByCompetencies as jest.Mock).mockResolvedValue(mockResponse);
    (requestModule.getLiveConsultationsForRequest as jest.Mock).mockResolvedValue(mockRequestConsultations);
  });

  it("fetches and displays requests filtered by competency", async () => {
    render(
      <MemoryRouter>
        <TACMyRequests />
      </MemoryRouter>,
    );

    await waitFor(() => {
      expect(requestModule.getRequestsInConsultationByCompetencies).toHaveBeenCalledTimes(1);
      expect(requestModule.getRequestsInConsultationWithInfo).toHaveBeenCalledTimes(0);
      mockTacDashboardRequests.forEach((request) => {
        expect(screen.getByText(request.projectName)).toBeInTheDocument();
        expect(screen.getByText(request.requestName)).toBeInTheDocument();
        expect(screen.getByText(request.projectGsid)).toBeInTheDocument();
        expect(screen.getByText(request.vvbAccountName)).toBeInTheDocument();
        expect(screen.getAllByText(request.requestStatus.description)).not.toHaveLength(0);
      });
    });
  });

  it("can turn off the filter and load non filtered requests", async () => {
    render(
      <MemoryRouter>
        <TACMyRequests />
      </MemoryRouter>,
    );

    await waitFor(() => {
      expect(requestModule.getRequestsInConsultationWithInfo).toHaveBeenCalledTimes(0);
      expect(screen.getByText("project 1")).toBeTruthy();
    });

    fireEvent.click(screen.getByLabelText("competency-switch"));

    await waitFor(() => {
      expect(requestModule.getRequestsInConsultationWithInfo).toHaveBeenCalledTimes(1);
    });
  });

  it("displays no requests message when there are no requests", async () => {
    (requestModule.getRequestsInConsultationByCompetencies as jest.Mock).mockResolvedValueOnce({
      data: [],
      totalPages: 0,
    });
    render(
      <MemoryRouter>
        <TACMyRequests />
      </MemoryRouter>,
    );

    await waitFor(() => {
      expect(screen.getByText("No Requests found")).toBeInTheDocument();
    });
  });

  it("displays consultation start and end dates correctly", async () => {
    render(
      <MemoryRouter>
        <TACMyRequests />
      </MemoryRouter>,
    );

    await waitFor(async () => {
      mockTacDashboardRequests.forEach((request) => {
        expect(screen.getByText(request.projectName)).toBeInTheDocument();
      });

      const startDateFormatted = dayjs.utc(mockRequestConsultations[0].startDate).format("DD/MM/YYYY");
      const endDateFormatted = dayjs.utc(mockRequestConsultations[0].endDate).format("DD/MM/YYYY");

      await waitFor(() => {
        const startDateElements = screen.getAllByText(startDateFormatted);
        expect(startDateElements.length).toBeGreaterThan(1);
      });

      await waitFor(() => {
        const endDateElements = screen.getAllByText(endDateFormatted);
        expect(endDateElements.length).toBeGreaterThan(1);
      });
    });
  });
});
