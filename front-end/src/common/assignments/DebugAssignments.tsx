import { Assignment, AssignmentStatus } from "../../components/requests/assignment/Assignment";
import { mockPlatformAccounts } from "../platform-accounts/DebugPlatformAccounts";

export const mockAuditAssignmentType = { id: 0, code: "AUDIT", description: "Audit Assignment" };

export const mockRequestTypeAssignments = [
  mockAuditAssignmentType,
  { id: 1, code: "REVIEW", description: "Review" },
  { id: 2, code: "QA/QC", description: "QA/QC" },
];

export const mockOverrideTypeAssignments = [{ id: 4, description: "Override Type Assignment 2", code: "OVR2" }];

export const mockAssignmentStatus: AssignmentStatus[] = [
  { id: 1, code: "Status1", description: "Status1", active: true },
  { id: 2, code: "Status2", description: "Status2", active: true },
  { id: 3, code: "Status3", description: "Status3", active: true },
  { id: 4, code: "ACCEPTED", description: "Accepted", active: true },
  { id: 5, code: "CANCELLED", description: "Cancelled", active: true },
];

export const mockAssignmentTypes = [...mockRequestTypeAssignments, ...mockOverrideTypeAssignments];

export const mockAssignmentsNoAudit: Assignment[] = [
  {
    id: 1,
    assignmentId: 101,
    assignmentType: mockAssignmentTypes[1],
    assignmentStatus: { id: 1, code: "Status1", description: "Status 1 Description", active: true },
    assignmentTypeId: 1,
    assignmentStatusId: 1,
    account: null,
    requestId: 1,
  },
];

export const mockAssignmentsHasAudit: Assignment[] = [
  {
    id: 2,
    assignmentId: 102,
    assignmentType: mockAuditAssignmentType,
    assignmentStatus: { id: 1, code: "Status1", description: "Status 1 Description", active: true },
    assignmentTypeId: 2,
    assignmentStatusId: 1,
    account: mockPlatformAccounts[1],
    requestId: 1,
  },
];

export const mockAssignmentsHasReview: Assignment[] = [
  {
    id: 2,
    assignmentId: 102,
    assignmentType: mockRequestTypeAssignments[1],
    assignmentStatus: { id: 4, code: "ACCEPTED", description: "Accepted", active: true },
    assignmentTypeId: 3,
    assignmentStatusId: 4,
    account: mockPlatformAccounts[2],
    requestId: 1,
  },
];

export const mockAssignments: Assignment[] = [
  {
    id: 1,
    assignmentId: 101,
    assignmentType: { id: 1, code: "Type1", description: "Type 1 Description" },
    assignmentStatus: { id: 1, code: "Status1", description: "Status 1 Description", active: true },
    assignmentTypeId: 1,
    assignmentStatusId: 1,
    account: mockPlatformAccounts[2],
    requestId: 1,
  },
  {
    id: 2,
    assignmentId: 102,
    assignmentType: { id: 2, code: "Type2", description: "Type 2 Description" },
    assignmentStatus: { id: 2, code: "Status2", description: "Status 2 Description", active: true },
    assignmentTypeId: 2,
    assignmentStatusId: 2,
    account: mockPlatformAccounts[2],
    requestId: 1,
  },
  {
    id: 4,
    assignmentId: 104,
    assignmentType: { id: 0, code: "AUDIT", description: "Audit" },
    assignmentStatus: { id: 0, code: "PENDING", description: "Pending", active: true },
    assignmentTypeId: 0,
    assignmentStatusId: 0,
    account: mockPlatformAccounts[1],
    requestId: 1,
  },
  {
    id: 5,
    assignmentId: 105,
    assignmentType: { id: 1, code: "REVIEW", description: "Review" },
    assignmentStatus: { id: 0, code: "PENDING", description: "Pending", active: true },
    assignmentTypeId: 0,
    assignmentStatusId: 0,
    account: mockPlatformAccounts[2],
    requestId: 1,
  },
];
