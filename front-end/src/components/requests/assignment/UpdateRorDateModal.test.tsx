import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import dayjs from "dayjs";
import { toast } from "react-toastify";

import { mockActiveReferenceTypes } from "../../../common/data-types/DebugReferenceTypes";
import { RequestRoundUpdate } from "../Requests";

import { UpdateRorDateModal } from "./UpdateRorDateModal";

jest.mock("../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../common/data-types/DataReferenceTypes"),
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
  }),
}));

const mockUpdateRoundOfReview = jest.fn();
jest.mock("../Requests", () => ({
  updateRoundOfReview: (requestId: number, payload: RequestRoundUpdate, currentIndex: number) =>
    mockUpdateRoundOfReview(requestId, payload, currentIndex),
}));

jest.mock("react-toastify", () => ({
  toast: {
    success: jest.fn(),
    error: jest.fn(),
  },
}));
const onClose = jest.fn();
const updateRequest = jest.fn();
const requestId = 55;
const currentRoundIndex = 2;

describe("UpdateRorDateModal", () => {
  it("toasts success and calls updateRequest and onClose after a successful updateRoundOfReview", async () => {
    mockUpdateRoundOfReview.mockResolvedValue(undefined);

    const start = "2024-01-01T00:00:00Z";
    const end = "2024-01-31T00:00:00Z";
    const expectedStartISO = dayjs.utc(start).toISOString();
    const expectedEndISO = dayjs.utc(end).toISOString();

    render(
      <UpdateRorDateModal
        isOpen={true}
        onClose={onClose}
        requestId={requestId}
        requestProjectName="My Project"
        requestCurrentRoundIndex={currentRoundIndex}
        defaultRequestCurrentRoundStartDate={start}
        defaultRequestCurrentRoundEndDate={end}
        currentRorStatus={mockActiveReferenceTypes.RequestRoundStatus[0].id}
        updateRequest={updateRequest}
      />,
    );

    fireEvent.click(screen.getByRole("button", { name: /save/i }));

    await waitFor(() => {
      expect(mockUpdateRoundOfReview).toHaveBeenCalledWith(
        requestId,
        expect.objectContaining({
          startDate: expectedStartISO,
          endDate: expectedEndISO,
          statusId: mockActiveReferenceTypes.RequestRoundStatus[0].id,
        }),
        currentRoundIndex,
      );
      expect(updateRequest).toHaveBeenCalled();
      expect(onClose).toHaveBeenCalled();
      expect(toast.success).toHaveBeenCalledWith("Round of review dates updated");
    });
  });

  it("toasts success and calls updateRequest and onClose after a successful updateRoundOfReview", async () => {
    const error = new Error("API down");
    mockUpdateRoundOfReview.mockRejectedValue(error);
    const consoleErrorSpy = jest.spyOn(console, "error").mockImplementation(() => jest.fn());
    const start = "2024-01-01T00:00:00Z";
    const end = "2024-01-31T00:00:00Z";
    const expectedStartISO = dayjs.utc(start).toISOString();
    const expectedEndISO = dayjs.utc(end).toISOString();

    render(
      <UpdateRorDateModal
        isOpen={true}
        onClose={onClose}
        requestId={requestId}
        requestProjectName="My Project"
        requestCurrentRoundIndex={currentRoundIndex}
        defaultRequestCurrentRoundStartDate={start}
        defaultRequestCurrentRoundEndDate={end}
        currentRorStatus={mockActiveReferenceTypes.RequestRoundStatus[0].id}
        updateRequest={updateRequest}
      />,
    );

    fireEvent.click(screen.getByRole("button", { name: /save/i }));

    await waitFor(() => {
      expect(mockUpdateRoundOfReview).toHaveBeenCalledWith(
        requestId,
        expect.objectContaining({
          startDate: expectedStartISO,
          endDate: expectedEndISO,
          statusId: mockActiveReferenceTypes.RequestRoundStatus[0].id,
        }),
        currentRoundIndex,
      );
      expect(consoleErrorSpy).toHaveBeenCalledWith("Error updating round of review:", error);
      expect(toast.error).toHaveBeenCalledWith("Error updating round of review dates");
    });
  });
});
