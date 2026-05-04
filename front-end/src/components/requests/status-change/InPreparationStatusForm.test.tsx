import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { act } from "react";

import { mockAssignmentsHasAudit, mockAssignmentsNoAudit } from "../../../common/assignments/DebugAssignments";
import { mockActiveReferenceTypes } from "../../../common/data-types/DebugReferenceTypes";
import { mockPlatformAccounts } from "../../../common/platform-accounts/DebugPlatformAccounts";
import { debugInPreparationRequests } from "../../../common/request/DebugRequests";
import { getAllRequestForms } from "../../Forms/Forms";
import { getAllAccountsByAssignmentType, getRequestAssignments } from "../assignment/Assignment";
import { MandatoryStatus } from "../MandatoryStatus";
import { checkIfAssignmentTypeRequiredForRequestType, updateRequest } from "../Requests";

import InPreparationStatusForm from "./InPreparationStatusForm";

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
  getRequiredDocumentsForRequest: jest.fn(),
  checkIfAssignmentTypeRequiredForRequestType: jest.fn(),
  updateRequest: jest.fn(),
  startRoundOfReview: jest.fn(),
  MandatoryStatus: {
    REQUIRED: 0,
    OPTIONAL: 1,
    NOT_REQUIRED: 2,
  },
}));

jest.mock("./../assignment/Assignment", () => ({
  getRequestAssignments: jest.fn(),
  getAllAccountsByAssignmentType: jest.fn(),
  assignChecksToVVBorARMTeam: jest.fn(),
}));

jest.mock("../../Forms/Forms", () => ({
  getAllRequestForms: jest.fn(),
}));

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => jest.fn(),
}));

describe("InPreparationStatusForm Component", () => {
  beforeEach(() => {
    jest.resetAllMocks();
    (getAllRequestForms as jest.Mock).mockResolvedValue([]);
  });

  it("Should change to in progress if completeness check exists", async () => {
    const mockOnSubmit = jest.fn();

    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsNoAudit);
    (getAllAccountsByAssignmentType as jest.Mock).mockResolvedValue(mockPlatformAccounts);
    (checkIfAssignmentTypeRequiredForRequestType as jest.Mock).mockResolvedValue(MandatoryStatus.NOT_REQUIRED);

    act(() => {
      render(<InPreparationStatusForm request={debugInPreparationRequests[1]} onSubmit={mockOnSubmit} />);
    });

    await waitFor(() => {
      fireEvent.click(screen.getByText("Submit"));

      expect(updateRequest).toHaveBeenCalledWith(
        {
          id: debugInPreparationRequests[1].id,
          requestStatusId: mockActiveReferenceTypes.RequestStatus[10].id,
        },
        debugInPreparationRequests[1].projectId,
      );
    });
  });

  it("Should change to in assignment if completeness check exists and review was skipped previously (optional)", async () => {
    const mockOnSubmit = jest.fn();

    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsNoAudit);
    (getAllAccountsByAssignmentType as jest.Mock).mockResolvedValue(mockPlatformAccounts);
    (checkIfAssignmentTypeRequiredForRequestType as jest.Mock).mockResolvedValue(MandatoryStatus.NOT_REQUIRED);

    act(() => {
      render(<InPreparationStatusForm request={debugInPreparationRequests[2]} onSubmit={mockOnSubmit} />);
    });

    await waitFor(() => {
      fireEvent.click(screen.getByText("Submit"));

      expect(updateRequest).toHaveBeenCalledWith(
        {
          id: debugInPreparationRequests[2].id,
          requestStatusId: mockActiveReferenceTypes.RequestStatus[12].id,
        },
        debugInPreparationRequests[2].projectId,
      );
    });
  });

  it("Should change to in round completion if completeness check exists and request is fast tracked", async () => {
    const mockOnSubmit = jest.fn();

    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsNoAudit);
    (getAllAccountsByAssignmentType as jest.Mock).mockResolvedValue(mockPlatformAccounts);
    (checkIfAssignmentTypeRequiredForRequestType as jest.Mock).mockResolvedValue(MandatoryStatus.NOT_REQUIRED);

    act(() => {
      render(<InPreparationStatusForm request={debugInPreparationRequests[3]} onSubmit={mockOnSubmit} />);
    });

    await waitFor(() => {
      fireEvent.click(screen.getByText("Submit"));

      expect(updateRequest).toHaveBeenCalledWith(
        {
          id: debugInPreparationRequests[2].id,
          requestStatusId: mockActiveReferenceTypes.RequestStatus[13].id,
        },
        debugInPreparationRequests[2].projectId,
      );
    });
  });

  it("Should change to in audit if no completeness check, audit optional, audit chosen and has vvb", async () => {
    const mockOnSubmit = jest.fn();

    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsHasAudit);
    (getAllAccountsByAssignmentType as jest.Mock).mockResolvedValue(mockPlatformAccounts);
    (checkIfAssignmentTypeRequiredForRequestType as jest.Mock).mockResolvedValue(MandatoryStatus.OPTIONAL);

    render(<InPreparationStatusForm request={debugInPreparationRequests[4]} onSubmit={mockOnSubmit} />);

    fireEvent.click(await screen.findByTestId("optionalAuditTrue"));
    fireEvent.mouseDown(screen.getByLabelText("Select VVB"));
    fireEvent.click(await screen.findByTestId(`vvb-option-${mockPlatformAccounts[1].id}`));
    fireEvent.click(screen.getByText("Submit"));

    await waitFor(() => {
      expect(updateRequest).toHaveBeenCalledWith(
        {
          id: debugInPreparationRequests[0].id,
          requestStatusId: mockActiveReferenceTypes.RequestStatus[11].id,
        },
        debugInPreparationRequests[0].projectId,
      );
    });
  });

  it("Should change to completeness check, if audit optional, audit not chosen and has vvb", async () => {
    const mockOnSubmit = jest.fn();

    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsHasAudit);
    (getAllAccountsByAssignmentType as jest.Mock).mockResolvedValue(mockPlatformAccounts);
    (checkIfAssignmentTypeRequiredForRequestType as jest.Mock).mockResolvedValue(MandatoryStatus.OPTIONAL);

    act(() => {
      render(<InPreparationStatusForm request={debugInPreparationRequests[4]} onSubmit={mockOnSubmit} />);
    });

    await waitFor(() => {
      fireEvent.click(screen.getByTestId("optionalAuditFalse"));

      fireEvent.click(screen.getByText("Submit"));

      expect(updateRequest).toHaveBeenCalledWith(
        {
          id: debugInPreparationRequests[0].id,
          requestStatusId: mockActiveReferenceTypes.RequestStatus[9].id,
        },
        debugInPreparationRequests[0].projectId,
      );
    });
  });

  it("Should change to in audit if no completeness check, audit required and has vvb", async () => {
    const mockOnSubmit = jest.fn();

    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsHasAudit);
    (getAllAccountsByAssignmentType as jest.Mock).mockResolvedValue(mockPlatformAccounts);
    (checkIfAssignmentTypeRequiredForRequestType as jest.Mock).mockResolvedValue(MandatoryStatus.REQUIRED);

    act(() => {
      render(<InPreparationStatusForm request={debugInPreparationRequests[4]} onSubmit={mockOnSubmit} />);
    });

    await waitFor(() => {
      fireEvent.click(screen.getByText("Submit"));

      expect(updateRequest).toHaveBeenCalledWith(
        {
          id: debugInPreparationRequests[0].id,
          requestStatusId: mockActiveReferenceTypes.RequestStatus[11].id,
        },
        debugInPreparationRequests[0].projectId,
      );
    });
  });

  it("Should change to completeness check without vvb choice if audit not chosen on round 1", async () => {
    const mockOnSubmit = jest.fn();

    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsHasAudit);
    (checkIfAssignmentTypeRequiredForRequestType as jest.Mock).mockResolvedValue(MandatoryStatus.OPTIONAL);

    act(() => {
      render(<InPreparationStatusForm request={debugInPreparationRequests[5]} onSubmit={mockOnSubmit} />);
    });

    await waitFor(() => {
      fireEvent.click(screen.getByText("Submit"));

      expect(updateRequest).toHaveBeenCalledWith(
        {
          id: debugInPreparationRequests[0].id,
          requestStatusId: mockActiveReferenceTypes.RequestStatus[9].id,
        },
        debugInPreparationRequests[0].projectId,
      );
    });
  });

  it("Should change to completeness check - if no completeness check, no audit required and has no vvb", async () => {
    const mockOnSubmit = jest.fn();

    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsNoAudit);
    (getAllAccountsByAssignmentType as jest.Mock).mockResolvedValue(mockPlatformAccounts);
    (checkIfAssignmentTypeRequiredForRequestType as jest.Mock).mockResolvedValue(MandatoryStatus.NOT_REQUIRED);

    act(() => {
      render(<InPreparationStatusForm request={debugInPreparationRequests[0]} onSubmit={mockOnSubmit} />);
    });

    await waitFor(() => {
      fireEvent.click(screen.getByText("Submit"));
      expect(updateRequest).toHaveBeenCalledWith(
        {
          id: debugInPreparationRequests[0].id,
          requestStatusId: mockActiveReferenceTypes.RequestStatus[9].id,
        },
        debugInPreparationRequests[0].projectId,
      );
    });
  });

  it("Should disable submit and show error message when forms are in draft status", async () => {
    const mockOnSubmit = jest.fn();
    const mockFormsWithDraft = [
      {
        id: 1,
        version: 1.0,
        type: mockActiveReferenceTypes.DigitalFormType[0],
        status: mockActiveReferenceTypes.RequestFormStatus[0], // DRAFT status
        createdAt: "2025-04-24T12:43:41.05365Z",
        schema: { steps: [] },
      },
    ];

    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsNoAudit);
    (getAllAccountsByAssignmentType as jest.Mock).mockResolvedValue(mockPlatformAccounts);
    (checkIfAssignmentTypeRequiredForRequestType as jest.Mock).mockResolvedValue(MandatoryStatus.NOT_REQUIRED);
    (getAllRequestForms as jest.Mock).mockResolvedValue(mockFormsWithDraft);

    render(<InPreparationStatusForm request={debugInPreparationRequests[0]} onSubmit={mockOnSubmit} />);

    await waitFor(() => {
      expect(
        screen.getByText(
          "One or more request forms have a draft status. Please complete and submit all forms before proceeding.",
        ),
      ).toBeInTheDocument();
    });

    expect(screen.queryByText("Submit")).not.toBeInTheDocument();
    expect(updateRequest).not.toHaveBeenCalled();
  });

  it("Should enable submit when all forms are submitted", async () => {
    const mockOnSubmit = jest.fn();
    const mockFormsAllSubmitted = [
      {
        id: 1,
        version: 1.0,
        type: mockActiveReferenceTypes.DigitalFormType[0],
        status: mockActiveReferenceTypes.RequestFormStatus[1], // SUBMITTED status
        createdAt: "2025-04-24T12:43:41.05365Z",
        schema: { steps: [] },
      },
    ];

    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsNoAudit);
    (getAllAccountsByAssignmentType as jest.Mock).mockResolvedValue(mockPlatformAccounts);
    (checkIfAssignmentTypeRequiredForRequestType as jest.Mock).mockResolvedValue(MandatoryStatus.NOT_REQUIRED);
    (getAllRequestForms as jest.Mock).mockResolvedValue(mockFormsAllSubmitted);

    render(<InPreparationStatusForm request={debugInPreparationRequests[0]} onSubmit={mockOnSubmit} />);

    await waitFor(() => {
      expect(screen.getByText("Submit")).toBeInTheDocument();
    });

    expect(
      screen.queryByText(
        "One or more request forms have a draft status. Please complete and submit all forms before proceeding.",
      ),
    ).not.toBeInTheDocument();
  });

  it("Should show error message when some forms are submitted but at least one is in draft", async () => {
    const mockOnSubmit = jest.fn();
    const mockFormsMixed = [
      {
        id: 1,
        version: 1.0,
        type: mockActiveReferenceTypes.DigitalFormType[0],
        status: mockActiveReferenceTypes.RequestFormStatus[1], // SUBMITTED status
        createdAt: "2025-04-24T12:43:41.05365Z",
        schema: { steps: [] },
      },
      {
        id: 2,
        version: 1.0,
        type: mockActiveReferenceTypes.DigitalFormType[0],
        status: mockActiveReferenceTypes.RequestFormStatus[0], // DRAFT status
        createdAt: "2025-04-24T12:43:41.05365Z",
        schema: { steps: [] },
      },
    ];

    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsNoAudit);
    (getAllAccountsByAssignmentType as jest.Mock).mockResolvedValue(mockPlatformAccounts);
    (checkIfAssignmentTypeRequiredForRequestType as jest.Mock).mockResolvedValue(MandatoryStatus.NOT_REQUIRED);
    (getAllRequestForms as jest.Mock).mockResolvedValue(mockFormsMixed);

    render(<InPreparationStatusForm request={debugInPreparationRequests[0]} onSubmit={mockOnSubmit} />);

    await waitFor(() => {
      expect(
        screen.getByText(
          "One or more request forms have a draft status. Please complete and submit all forms before proceeding.",
        ),
      ).toBeInTheDocument();
    });

    expect(screen.queryByText("Submit")).not.toBeInTheDocument();
  });

  it("Should change to in progress without vvb choice if audit not chosen on round 1", async () => {
    const mockOnSubmit = jest.fn();

    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsHasAudit);

    act(() => {
      render(<InPreparationStatusForm request={debugInPreparationRequests[6]} onSubmit={mockOnSubmit} />);
    });

    await waitFor(() => {
      fireEvent.click(screen.getByText("Submit"));

      expect(updateRequest).toHaveBeenCalledWith(
        {
          id: debugInPreparationRequests[0].id,
          requestStatusId: mockActiveReferenceTypes.RequestStatus[10].id,
        },
        debugInPreparationRequests[0].projectId,
      );
    });
  });
});
