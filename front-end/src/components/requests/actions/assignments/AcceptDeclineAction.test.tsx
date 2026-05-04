import "@testing-library/jest-dom";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";

import { mockAssignments } from "../../../../common/assignments/DebugAssignments";
import { mockActiveReferenceTypes } from "../../../../common/data-types/DebugReferenceTypes";
import { debugInAuditRequests, debugInProgressRequests } from "../../../../common/request/DebugRequests";
import { AuthContext, AuthContextType } from "../../../Auth/Login/AuthProvider";
import { getRequestAssignments } from "../../assignment/Assignment";
import { updateRequest } from "../../Requests";

import AcceptDeclineAction from "./AcceptDeclineAction";

jest.mock("../../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../../common/data-types/DataReferenceTypes"),
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
  }),
}));

jest.mock("../../Requests", () => ({
  updateRequest: jest.fn(),
}));

jest.mock("../../assignment/Assignment", () => ({
  getAllAccountsByAssignmentType: jest.fn(),
  getAssignmentTypeByRequestType: jest.fn(),
  getRequestAssignments: jest.fn(),
  getAssignment: jest.fn(),
  updateRequestAssignmentStatus: jest.fn(),
}));

const mockInAuditRequest = debugInAuditRequests[0];
const mockInProgressRequest = debugInProgressRequests[0];

const mockAuthProvider = {
  isReviewerUser: jest.fn(),
  isRegistryUser: jest.fn(),
  isVvbUser: jest.fn(),
} as Record<keyof AuthContextType, jest.Mock>;

describe("AcceptDeclineAction", () => {
  beforeEach(() => {
    jest.resetAllMocks();
    mockAuthProvider.isReviewerUser.mockReturnValue(false);
    mockAuthProvider.isVvbUser.mockReturnValue(false);
  });

  describe("for VVBs", () => {
    it("should output the response if accepted or declined", async () => {
      (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignments);
      mockAuthProvider.isVvbUser.mockReturnValue(true);

      const refreshCallback = jest.fn();
      render(
        <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
          <AcceptDeclineAction
            request={mockInAuditRequest}
            refreshRequest={jest.fn()}
            refreshAssignments={refreshCallback}
          />
        </AuthContext.Provider>,
      );

      const acceptDeclineButton = await screen.findByText("Accept / Decline Request");
      fireEvent.click(acceptDeclineButton);

      const acceptButton = await screen.findByText("Accept");
      fireEvent.click(acceptButton);

      await waitFor(() => {
        expect(refreshCallback).toHaveBeenCalled();
      });
    });

    it("should update the request status to IN_PREPARATION if the assignment is declined", async () => {
      (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignments);
      mockAuthProvider.isVvbUser.mockReturnValue(true);

      render(
        <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
          <AcceptDeclineAction request={mockInAuditRequest} refreshRequest={jest.fn()} refreshAssignments={jest.fn()} />
        </AuthContext.Provider>,
      );

      const acceptDeclineButton = await screen.findByText("Accept / Decline Request");
      fireEvent.click(acceptDeclineButton);

      const declineButton = await screen.findByText("Decline");
      fireEvent.click(declineButton);

      await waitFor(() => {
        expect(updateRequest).toHaveBeenCalledWith(
          { id: mockInAuditRequest.id, requestStatusId: 6 },
          mockInAuditRequest.project.id,
        );
      });
    });
  });

  describe("for Reviewers", () => {
    it("should update the request status to IN_PROGRESS if the assignment is declined", async () => {
      (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignments);
      mockAuthProvider.isReviewerUser.mockReturnValue(true);

      render(
        <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
          <AcceptDeclineAction
            request={mockInProgressRequest}
            refreshRequest={jest.fn()}
            refreshAssignments={jest.fn()}
          />
        </AuthContext.Provider>,
      );

      const acceptDeclineButton = await screen.findByText("Accept / Decline Request");
      fireEvent.click(acceptDeclineButton);

      const declineButton = await screen.findByText("Decline");
      fireEvent.click(declineButton);

      await waitFor(() => {
        expect(updateRequest).toHaveBeenCalledWith(
          { id: mockInProgressRequest.id, requestStatusId: 12 },
          mockInProgressRequest.project.id,
        );
      });
    });
  });
});
