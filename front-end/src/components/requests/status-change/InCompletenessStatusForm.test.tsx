import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { act } from "react";

import { mockAssignmentsHasAudit, mockAssignmentsNoAudit } from "../../../common/assignments/DebugAssignments";
import { mockActiveReferenceTypes } from "../../../common/data-types/DebugReferenceTypes";
import { debugInCompletenessRequests } from "../../../common/request/DebugRequests";
import { getRequestAssignments } from "../assignment/Assignment";
import { updateRequest } from "../Requests";

import InCompletenessStatusForm from "./InCompletenessStatusForm";

jest.mock("../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../common/data-types/DataReferenceTypes"),
  getReferenceValueCodeById: jest.fn(),
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
  }),
  getReferenceValueDescriptionById: jest.fn(),
}));

jest.mock("./../Requests", () => ({
  getAllDocumentsForProjectRequest: jest.fn(),
  getRequestTypeStakeHolderConsultationRequired: jest.fn(),
  getRequiredDocumentsForRequest: jest.fn(),
  updateRequest: jest.fn(),
}));

jest.mock("./../assignment/Assignment", () => ({
  getRequestAssignments: jest.fn(),
}));

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => jest.fn(),
}));

describe("InCompletenessForm Component", () => {
  beforeEach(() => {
    jest.resetAllMocks();
  });

  it("Should display has VVB assigned: no if there is no vvb accountId assigned", () => {
    const mockOnSubmit = jest.fn();

    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsNoAudit);

    render(
      <InCompletenessStatusForm
        request={debugInCompletenessRequests[0]} //this request has no audit history
        onSubmit={mockOnSubmit}
      />,
    );

    const vvbAssigned = screen.getByTestId("has-vvb-assigned");
    expect(vvbAssigned).toHaveTextContent("Has VVB Assigned: No");
  });

  it("Should change to in preparation if request not complete and no audit and no vvb assigned", async () => {
    const mockOnSubmit = jest.fn();

    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsNoAudit);

    act(() => {
      render(
        <InCompletenessStatusForm
          request={debugInCompletenessRequests[0]} //this request has no audit history
          onSubmit={mockOnSubmit}
        />,
      );
    });

    const noRadioButton = screen.getByLabelText("No");
    fireEvent.click(noRadioButton);

    await waitFor(() => {
      fireEvent.click(screen.getByText("Submit"));

      expect(updateRequest).toHaveBeenCalledWith(
        {
          id: debugInCompletenessRequests[0].id,
          requestStatusId: mockActiveReferenceTypes.RequestStatus[6].id, //In Preparation
          startStakeholderConsultation: false,
        },
        debugInCompletenessRequests[0].projectId,
      );
    });
  });

  it("Should change to in audit if request not complete and has audit and has vvb assigned", async () => {
    const mockOnSubmit = jest.fn();

    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsHasAudit);

    act(() => {
      render(
        <InCompletenessStatusForm
          request={debugInCompletenessRequests[1]} //this request has audit history
          onSubmit={mockOnSubmit}
        />,
      );
    });

    const noRadioButton = screen.getByLabelText("No");
    fireEvent.click(noRadioButton);

    await waitFor(() => {
      fireEvent.click(screen.getByText("Submit"));

      expect(updateRequest).toHaveBeenCalledWith(
        {
          id: debugInCompletenessRequests[1].id,
          requestStatusId: mockActiveReferenceTypes.RequestStatus[11].id, //In Audit
          startStakeholderConsultation: false,
        },
        debugInCompletenessRequests[1].projectId,
      );
    });
  });

  it("Should change to in assignment if request is complete", async () => {
    const mockOnSubmit = jest.fn();

    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsHasAudit);

    act(() => {
      render(
        <InCompletenessStatusForm
          request={debugInCompletenessRequests[1]} //this request has audit history
          onSubmit={mockOnSubmit}
        />,
      );
    });

    const yesRadioButton = screen.getByLabelText("Yes");
    fireEvent.click(yesRadioButton);

    await waitFor(() => {
      fireEvent.click(screen.getByText("Submit"));

      expect(updateRequest).toHaveBeenCalledWith(
        {
          id: debugInCompletenessRequests[1].id,
          requestStatusId: mockActiveReferenceTypes.RequestStatus[12].id, //In Assignment
          startStakeholderConsultation: false,
          internal: true,
        },
        debugInCompletenessRequests[1].projectId,
      );
    });
  });

  it("can make the request not fast tracked", async () => {
    const mockPreliminaryReview = {
      ...debugInCompletenessRequests[0],
      requestType: {
        id: 1000,
        code: "PRELIMINARY_REVIEW",
        description: "Preliminary Review",
      },
    };

    const mockOnSubmit = jest.fn();

    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsHasAudit);

    render(<InCompletenessStatusForm request={mockPreliminaryReview} onSubmit={mockOnSubmit} />);

    const yesRadioButton = screen.getByLabelText("Yes");
    fireEvent.click(yesRadioButton);

    await waitFor(() => {
      expect(screen.getByText("Fast track this request?")).toBeInTheDocument();
    });

    const fastTrackCheckbox = screen.getByLabelText("Fast track this request?");

    await waitFor(() => fireEvent.click(fastTrackCheckbox));

    await waitFor(() => {
      fireEvent.click(screen.getByText("Submit"));
      expect(updateRequest).toHaveBeenCalledWith(
        {
          id: mockPreliminaryReview.id,
          requestStatusId: 12,
          startStakeholderConsultation: false,
          fastTrack: false,
          internal: true,
        },
        mockPreliminaryReview.project.id,
      );
    });
  });

  it("request is fast tracked by default if necessary", async () => {
    const mockPreliminaryReview = {
      ...debugInCompletenessRequests[0],
      requestType: {
        id: 1000,
        code: "PRELIMINARY_REVIEW",
        description: "Preliminary Review",
      },
    };

    const mockOnSubmit = jest.fn();

    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsHasAudit);

    render(<InCompletenessStatusForm request={mockPreliminaryReview} onSubmit={mockOnSubmit} />);

    const yesRadioButton = screen.getByLabelText("Yes");
    fireEvent.click(yesRadioButton);

    await waitFor(() => {
      fireEvent.click(screen.getByText("Submit"));
      expect(updateRequest).toHaveBeenCalledWith(
        {
          id: mockPreliminaryReview.id,
          requestStatusId: 12,
          startStakeholderConsultation: false,
          fastTrack: true,
          internal: undefined,
        },
        mockPreliminaryReview.project.id,
      );
    });
  });

  it.each([
    ["Internal", true],
    ["External", false],
  ])("can set the request to internal/external for generic request", async (label, internal) => {
    const mockGenericReview = {
      ...debugInCompletenessRequests[0],
      requestType: {
        id: 1000,
        code: "GENERIC",
        description: "Generic",
      },
    };

    const mockOnSubmit = jest.fn();

    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsHasAudit);

    render(<InCompletenessStatusForm request={mockGenericReview} onSubmit={mockOnSubmit} />);

    const yesRadioButton = screen.getByLabelText("Yes");
    fireEvent.click(yesRadioButton);

    await waitFor(() => {
      expect(screen.getByText("Internal")).toBeInTheDocument();
      expect(screen.getByText("External")).toBeInTheDocument();
    });

    const internalCheckBox = screen.getByLabelText(label);
    fireEvent.click(internalCheckBox);

    await waitFor(() => {
      fireEvent.click(screen.getByText("Submit"));
      expect(updateRequest).toHaveBeenCalledWith(
        {
          id: mockGenericReview.id,
          requestStatusId: 12,
          startStakeholderConsultation: false,
          internal: internal,
          fastTrack: undefined,
        },
        mockGenericReview.project.id,
      );
    });
  });

  it("doesn't show PII questions if PII not present", async () => {
    const mockPreliminaryReview = {
      ...debugInCompletenessRequests[0],
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

    render(<InCompletenessStatusForm request={mockPreliminaryReview} onSubmit={mockOnSubmit} />);

    const yesRadioButton = screen.getByLabelText("Yes");
    fireEvent.click(yesRadioButton);

    await waitFor(() => {
      expect(
        screen.queryByText("Please review the project for PII and check any false positives"),
      ).not.toBeInTheDocument();
      expect(screen.queryByText("Is the remaining PII which is present accepted?")).not.toBeInTheDocument();
    });
  });

  it("shows PII not found and acceptance questions if PII present", async () => {
    const mockPreliminaryReview = {
      ...debugInCompletenessRequests[0],
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

    render(<InCompletenessStatusForm request={mockPreliminaryReview} onSubmit={mockOnSubmit} />);

    const yesRadioButton = screen.getByLabelText("Yes");
    fireEvent.click(yesRadioButton);

    await waitFor(() => {
      expect(screen.getByText("Please review the project for PII and check any false positives")).toBeInTheDocument();
      expect(screen.getByText("Project Title")).toBeInTheDocument();
      expect(screen.getByText("Project Description")).toBeInTheDocument();
      expect(screen.getByText("Project Notes")).toBeInTheDocument();
      expect(screen.getByText("Is the remaining PII which is present accepted?")).toBeInTheDocument();
    });
  });
});
