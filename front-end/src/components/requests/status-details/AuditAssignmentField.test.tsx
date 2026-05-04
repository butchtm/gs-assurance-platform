import { render, screen, waitFor } from "@testing-library/react";

import { getReferenceTypeByCode } from "../../../common/data-types/DataReferenceTypes";
import { mockActiveReferenceTypes } from "../../../common/data-types/DebugReferenceTypes";
import { mockPlatformAccounts } from "../../../common/platform-accounts/DebugPlatformAccounts";
import {
  mockAuditAssignmentType,
  mockAssignmentTypes,
  mockAssignmentStatus,
} from "../../../common/assignments/DebugAssignments";
import { Assignment, AssignmentType } from "../assignment/Assignment";

import { AuditAssignmentField } from "./AuditAssignmentField";

jest.mock("../../../common/data-types/DataReferenceTypes", () => ({
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
  }),
  getReferenceTypeByCode: jest.fn(),
}));

const assignedAccount = mockPlatformAccounts[1];
const givenAssignment = (assignmentType: AssignmentType): Assignment => {
  const status = mockAssignmentStatus[0];
  return {
    id: 1,
    assignmentType: assignmentType,
    assignmentTypeId: assignmentType.id,
    assignmentStatus: status,
    assignmentStatusId: status.id,
    account: assignedAccount,
    requestId: 1,
  };
};

describe("Audit Assignment Field", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (getReferenceTypeByCode as jest.Mock).mockReturnValue(mockAuditAssignmentType);
  });

  it("should lookup audit assignment type", async () => {
    render(<AuditAssignmentField assignments={[]} />);
    await waitFor(() => {
      expect(getReferenceTypeByCode).toHaveBeenCalledWith("AUDIT", mockActiveReferenceTypes.AssignmentType);
    });
  });

  it("should not render label if audit assignment is not present", async () => {
    (getReferenceTypeByCode as jest.Mock).mockReturnValueOnce(null);

    render(<AuditAssignmentField assignments={[]} />);

    await waitFor(() => {
      expect(screen.queryByText(mockAuditAssignmentType.description)).not.toBeInTheDocument();
    });
  });

  it("should not render assignment account name if audit assignment not present", async () => {
    const anyOtherAssignmentType = mockAssignmentTypes[1];
    const assignment = givenAssignment(anyOtherAssignmentType);

    render(<AuditAssignmentField assignments={[assignment]} />);

    await waitFor(() => {
      expect(screen.queryByText(assignedAccount.name)).not.toBeInTheDocument();
    });
  });

  it("should render audit assignment account name if present", async () => {
    const assignment = givenAssignment(mockAuditAssignmentType);

    render(<AuditAssignmentField assignments={[assignment]} />);

    await waitFor(() => {
      expect(screen.queryByText(assignedAccount.name)).toBeInTheDocument();
    });
  });
});
