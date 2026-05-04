import { fireEvent, render, screen, waitFor } from "@testing-library/react";

import { mockAssignmentsHasAudit, mockAssignmentsNoAudit } from "../../../../common/assignments/DebugAssignments";
import { mockActiveReferenceTypes } from "../../../../common/data-types/DebugReferenceTypes";
import { updateProjectPiiToNotFound } from "../../../../common/piiAnalysis/PiiAction";
import {
  debugInRoundCompletionRequests,
  debugRequestTransitionResponse,
} from "../../../../common/request/DebugRequests";
import { getRequestAssignments } from "../../../../components/requests/assignment/Assignment";
import { updateRequest } from "../../../requests/Requests";

import InRoundCompletionStatusForm from "./InRoundCompletionStatusForm";

jest.mock("../../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../../common/data-types/DataReferenceTypes"),
  getReferenceValueCodeById: jest.fn(),
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
  }),
  getReferenceValueDescriptionById: jest.fn(),
}));

jest.mock("./../../Requests", () => ({
  getAllDocumentsForProjectRequest: jest.fn(),
  getRequiredDocumentsForRequest: jest.fn(),
  updateRequest: jest.fn(),
}));

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => jest.fn(),
}));

jest.mock("./../../assignment/Assignment", () => ({
  getRequestAssignments: jest.fn(),
  getAllAccountsByAssignmentType: jest.fn(),
  assignChecksToVVBorARMTeam: jest.fn(),
}));

jest.mock("../../../../common/piiAnalysis/PiiAction", () => ({
  ...jest.requireActual("../../../../common/piiAnalysis/PiiAction"),
  updateProjectPiiToNotFound: jest.fn(),
}));

describe("InRoundCompletionStatusForm Component", () => {
  it("Progress should not change if review is not complete", async () => {
    const mockOnSubmit = jest.fn();

    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsNoAudit);

    render(
      <InRoundCompletionStatusForm
        request={debugInRoundCompletionRequests[0]}
        availableTransitionStatuses={debugRequestTransitionResponse}
        onSubmit={mockOnSubmit}
      />,
    );

    await waitFor(() => {
      expect(updateRequest).not.toHaveBeenCalled();
    });
  });

  it("Change to in preparation if request has issue, no vvb and not rejected", async () => {
    const mockOnSubmit = jest.fn();

    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsNoAudit); // no vvb

    render(
      <InRoundCompletionStatusForm
        request={debugInRoundCompletionRequests[0]}
        availableTransitionStatuses={debugRequestTransitionResponse}
        onSubmit={mockOnSubmit}
      />,
    );

    const reviewCompleteNoButton = screen.getByTestId("review-complete-yes");
    fireEvent.click(reviewCompleteNoButton);

    const requestHasIssueButton = screen.getByTestId("request-issue-yes");
    fireEvent.click(requestHasIssueButton);

    const requestedRejectButton = screen.getByTestId("reject-request-no");
    fireEvent.click(requestedRejectButton);

    fireEvent.click(screen.getByText("Submit"));

    await waitFor(() => {
      expect(updateRequest).toHaveBeenCalledWith(
        {
          id: debugInRoundCompletionRequests[0].id,
          requestStatusId: mockActiveReferenceTypes.RequestStatus[6].id, //In preperation
        },
        debugInRoundCompletionRequests[0].projectId,
      );
    });
  });

  it("Change to in audit if request has issue, has vvb and not rejected", async () => {
    const mockOnSubmit = jest.fn();

    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsHasAudit); // has vvb

    render(
      <InRoundCompletionStatusForm
        request={debugInRoundCompletionRequests[0]}
        availableTransitionStatuses={debugRequestTransitionResponse}
        onSubmit={mockOnSubmit}
      />,
    );

    const reviewCompleteNoButton = screen.getByTestId("review-complete-yes");
    fireEvent.click(reviewCompleteNoButton);

    const requestHasIssueButton = screen.getByTestId("request-issue-yes");
    fireEvent.click(requestHasIssueButton);

    const requestedRejectButton = screen.getByTestId("reject-request-no");
    fireEvent.click(requestedRejectButton);

    await waitFor(() => {
      fireEvent.click(screen.getByText("Submit"));

      expect(updateRequest).toHaveBeenCalledWith(
        {
          id: debugInRoundCompletionRequests[0].id,
          requestStatusId: mockActiveReferenceTypes.RequestStatus[11].id, //In audit
        },
        debugInRoundCompletionRequests[0].projectId,
      );
    });
  });

  it("Change to complete rejected if request has issue and request is rejected", async () => {
    const mockOnSubmit = jest.fn();

    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsHasAudit); // has vvb

    render(
      <InRoundCompletionStatusForm
        request={debugInRoundCompletionRequests[0]}
        availableTransitionStatuses={debugRequestTransitionResponse}
        onSubmit={mockOnSubmit}
      />,
    );

    const reviewCompleteYesButton = screen.getByTestId("review-complete-yes");
    fireEvent.click(reviewCompleteYesButton);

    const requestHasIssueButton = screen.getByTestId("request-issue-yes");
    fireEvent.click(requestHasIssueButton);

    const requestedRejectButton = screen.getByTestId("reject-request-yes");
    fireEvent.click(requestedRejectButton);

    await waitFor(() => {
      fireEvent.click(screen.getByText("Submit"));

      const confirmButton = screen.getByTestId("confirm-complete-request-button");
      fireEvent.click(confirmButton);

      expect(updateRequest).toHaveBeenCalledWith(
        {
          id: debugInRoundCompletionRequests[0].id,
          requestStatusId: mockActiveReferenceTypes.RequestStatus[5].id, //Completed Rejected
        },
        debugInRoundCompletionRequests[0].projectId,
      );
    });
  });

  it("Change to complete approved if review complete and no issue", async () => {
    const mockOnSubmit = jest.fn();

    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsHasAudit); // has vvb

    render(
      <InRoundCompletionStatusForm
        request={debugInRoundCompletionRequests[0]}
        availableTransitionStatuses={debugRequestTransitionResponse}
        onSubmit={mockOnSubmit}
      />,
    );

    const reviewCompleteNoButton = screen.getByTestId("review-complete-yes");
    fireEvent.click(reviewCompleteNoButton);

    const requestHasIssueButton = screen.getByTestId("request-issue-no");
    fireEvent.click(requestHasIssueButton);

    await waitFor(() => {
      fireEvent.click(screen.getByText("Submit"));

      const confirmButton = screen.getByTestId("confirm-complete-request-button");
      fireEvent.click(confirmButton);

      expect(updateRequest).toHaveBeenCalledWith(
        {
          id: debugInRoundCompletionRequests[0].id,
          requestStatusId: mockActiveReferenceTypes.RequestStatus[4].id, //Completed Approved
        },
        debugInRoundCompletionRequests[0].projectId,
      );
    });
  });

  it("Dropdown should update project status for GENERIC request type", async () => {
    const mockOnSubmit = jest.fn();
    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsHasAudit); // has vvb

    render(
      <InRoundCompletionStatusForm
        request={debugInRoundCompletionRequests[0]}
        availableTransitionStatuses={debugRequestTransitionResponse}
        onSubmit={mockOnSubmit}
      />,
    );

    const reviewCompleteNoButton = screen.getByTestId("review-complete-yes");
    fireEvent.click(reviewCompleteNoButton);

    await waitFor(() => {
      const updateProjectStatusSection = screen.queryByTestId("update-project-status-section");
      expect(updateProjectStatusSection).toBeInTheDocument();
    });

    expect(await screen.findByText("Update project status?")).toBeInTheDocument();
  });

  it("doesn't show PII questions if PII not present", async () => {
    const mockPreliminaryReview = {
      ...debugInRoundCompletionRequests[0],
      requestType: {
        id: 1000,
        code: "PRELIMINARY_REVIEW",
        description: "Preliminary Review",
      },
      project: {
        gsid: "GS1",
        id: 1,
        name: "Test project",
        programmeOfActivitiesId: 0,
        piiMetadata: {
          piiPresent: false,
          action: null,
          piiDetail: [],
        },
      },
    };

    const mockOnSubmit = jest.fn();
    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsHasAudit);

    render(
      <InRoundCompletionStatusForm
        request={mockPreliminaryReview}
        availableTransitionStatuses={debugRequestTransitionResponse}
        onSubmit={mockOnSubmit}
      />,
    );

    const reviewCompleteNoButton = screen.getByTestId("review-complete-yes");
    fireEvent.click(reviewCompleteNoButton);

    await waitFor(() => {
      expect(
        screen.queryByText("Please review the project for PII and check any false positives"),
      ).not.toBeInTheDocument();
      expect(screen.queryByText("Is the remaining PII which is present accepted?")).not.toBeInTheDocument();
    });
  });

  it("shows PII not found and acceptance questions if PII present", async () => {
    const mockPreliminaryReview = {
      ...debugInRoundCompletionRequests[0],
      requestType: {
        id: 1000,
        code: "PRELIMINARY_REVIEW",
        description: "Preliminary Review",
      },
      project: {
        gsid: "GS1",
        id: 1,
        name: "Test project",
        programmeOfActivitiesId: 0,
        piiMetadata: {
          piiPresent: true,
          action: null,
          piiDetail: ["title", "description", "notes"],
        },
      },
    };

    const mockOnSubmit = jest.fn();
    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsHasAudit);

    render(
      <InRoundCompletionStatusForm
        request={mockPreliminaryReview}
        availableTransitionStatuses={debugRequestTransitionResponse}
        onSubmit={mockOnSubmit}
      />,
    );

    const reviewCompleteNoButton = screen.getByTestId("review-complete-yes");
    fireEvent.click(reviewCompleteNoButton);

    await waitFor(() => {
      expect(screen.getByText("Please review the project for PII and check any false positives")).toBeInTheDocument();
      expect(screen.getByText("Project Title")).toBeInTheDocument();
      expect(screen.getByText("Project Description")).toBeInTheDocument();
      expect(screen.getByText("Project Notes")).toBeInTheDocument();
      expect(screen.getByText("Is the remaining PII which is present accepted?")).toBeInTheDocument();
    });
  });

  it("submits any fields where the user has selected PII not found", async () => {
    const mockPreliminaryReview = {
      ...debugInRoundCompletionRequests[0],
      requestType: {
        id: 1000,
        code: "PRELIMINARY_REVIEW",
        description: "Preliminary Review",
      },
      project: {
        gsid: "GS1",
        id: 1,
        name: "Test project",
        programmeOfActivitiesId: 0,
        piiMetadata: {
          piiPresent: true,
          action: null,
          piiDetail: ["title", "description", "notes"],
        },
      },
    };

    const mockOnSubmit = jest.fn();
    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsHasAudit);
    (updateProjectPiiToNotFound as jest.Mock).mockResolvedValue(null);

    render(
      <InRoundCompletionStatusForm
        request={mockPreliminaryReview}
        availableTransitionStatuses={debugRequestTransitionResponse}
        onSubmit={mockOnSubmit}
      />,
    );

    const reviewCompleteNoButton = screen.getByTestId("review-complete-yes");
    fireEvent.click(reviewCompleteNoButton);

    const piiAcceptedButton = await screen.findByTestId("request-pii-issue-yes");
    fireEvent.click(piiAcceptedButton);

    const noIssuesButton = await screen.findByTestId("request-issue-no");
    fireEvent.click(noIssuesButton);

    await waitFor(() => {
      expect(screen.getByText("Please review the project for PII and check any false positives")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByText("Project Title"));
    fireEvent.click(screen.getByText("Project Description"));
    fireEvent.click(screen.getByText("Project Notes"));

    fireEvent.click(screen.getByText("Submit"));

    const confirmButton = await screen.findByTestId("confirm-complete-request-button");
    fireEvent.click(confirmButton);

    await waitFor(() => {
      expect(updateProjectPiiToNotFound).toHaveBeenCalledWith(1, ["title", "description", "notes"]);
    });
  });

  it("request is blocked from progressing to any status while in stakeholder consultation", async () => {
    const mockOnSubmit = jest.fn();

    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsHasAudit); // has vvb

    render(
      <InRoundCompletionStatusForm
        request={debugInRoundCompletionRequests[1]}
        availableTransitionStatuses={debugRequestTransitionResponse}
        onSubmit={mockOnSubmit}
      />,
    );

    await waitFor(() => {
      expect(
        screen.getByText("Requests of this type in Stakeholder Consultation cannot be submitted."),
      ).toBeInTheDocument();
      expect(screen.queryByTestId("submit-button")).not.toBeInTheDocument();
    });
  });

  it("presents review complete decision with no default option", () => {
    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsNoAudit);

    render(
      <InRoundCompletionStatusForm
        request={debugInRoundCompletionRequests[0]}
        availableTransitionStatuses={debugRequestTransitionResponse}
        onSubmit={jest.fn()}
      />,
    );
    expect(screen.getByTestId("review-complete-yes")).not.toBeChecked();
    expect(screen.getByTestId("review-complete-no")).not.toBeChecked();
  });
  it("presents request findings decision with no default and prevents submission", () => {
    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsNoAudit);

    render(
      <InRoundCompletionStatusForm
        request={debugInRoundCompletionRequests[0]}
        availableTransitionStatuses={debugRequestTransitionResponse}
        onSubmit={jest.fn()}
      />,
    );
    const reviewCompleteNoButton = screen.getByTestId("review-complete-yes");
    fireEvent.click(reviewCompleteNoButton);

    expect(screen.getByTestId("request-issue-yes")).not.toBeChecked();
    expect(screen.getByTestId("request-issue-no")).not.toBeChecked();
    expect(screen.getByText("Submit")).toBeDisabled();
  });
  it("presents reject request decision with no default and prevents submission", () => {
    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsNoAudit);

    render(
      <InRoundCompletionStatusForm
        request={debugInRoundCompletionRequests[0]}
        availableTransitionStatuses={debugRequestTransitionResponse}
        onSubmit={jest.fn()}
      />,
    );
    const reviewCompleteNoButton = screen.getByTestId("review-complete-yes");
    fireEvent.click(reviewCompleteNoButton);

    const hasFindingsButton = screen.getByTestId("request-issue-yes");
    fireEvent.click(hasFindingsButton);

    expect(screen.getByTestId("reject-request-yes")).not.toBeChecked();
    expect(screen.getByTestId("reject-request-no")).not.toBeChecked();

    expect(screen.getByText("Submit")).toBeDisabled();
  });
  it("presents a confirmation modal on approval", () => {
    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsNoAudit);

    render(
      <InRoundCompletionStatusForm
        request={debugInRoundCompletionRequests[0]}
        availableTransitionStatuses={debugRequestTransitionResponse}
        onSubmit={jest.fn()}
      />,
    );
    const reviewCompleteNoButton = screen.getByTestId("review-complete-yes");
    fireEvent.click(reviewCompleteNoButton);

    const hasFindingsButton = screen.getByTestId("request-issue-no");
    fireEvent.click(hasFindingsButton);

    fireEvent.click(screen.getByText("Submit"));

    expect(screen.getByText("Confirm Request Approved")).toBeInTheDocument();
  });
  it("presents a confirmation modal on rejection", () => {
    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsNoAudit);

    render(
      <InRoundCompletionStatusForm
        request={debugInRoundCompletionRequests[0]}
        availableTransitionStatuses={debugRequestTransitionResponse}
        onSubmit={jest.fn()}
      />,
    );
    const reviewCompleteNoButton = screen.getByTestId("review-complete-yes");
    fireEvent.click(reviewCompleteNoButton);

    const hasFindingsButton = screen.getByTestId("request-issue-yes");
    fireEvent.click(hasFindingsButton);

    const rejectButton = screen.getByTestId("reject-request-yes");
    fireEvent.click(rejectButton);

    fireEvent.click(screen.getByText("Submit"));

    expect(screen.getByText("Confirm Request Rejected")).toBeInTheDocument();
  });
});
