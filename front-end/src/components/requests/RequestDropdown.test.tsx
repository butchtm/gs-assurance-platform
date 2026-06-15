import { fireEvent, render, screen, waitFor } from "@testing-library/react";

import {
  mockActiveReferenceTypes,
  mockAllReferenceTypes,
  mockInactiveReferenceTypes,
} from "../../common/data-types/DebugReferenceTypes";
import { mockDocuments, mockRequiredDocumentsResponse } from "../../common/document/DebugDocuments";
import { debugProject } from "../../common/project/DebugProject";
import { mockBatchedProjectsRealCase } from "../../common/project/Project";
import { debugRequests, mockCurrentPathwayGroupTimelineActivities } from "../../common/request/DebugRequests";
import { AuthContext, AuthContextType } from "../Auth/Login/AuthProvider";
import { getProject } from "../Project/Project";

import { RequestDropdown } from "./RequestDropdown";
import * as Requests from "./Requests";

jest.mock("../Project/Project", () => ({
  getAllProjectsForAccount: jest.fn(),
  createProject: jest.fn(),
  getProject: jest.fn(),
}));

jest.mock("./comments/RequestComments", () => ({
  getRequestComments: jest.fn(),
}));

jest.mock("../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../common/data-types/DataReferenceTypes"),
  getReferenceValueCodeById: jest.fn(),
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
    inactiveReferenceTypes: mockInactiveReferenceTypes,
    allReferenceTypes: mockAllReferenceTypes,
  }),
  getReferenceValueDescriptionById: jest.fn(),
}));

jest.mock("./Requests", () => ({
  getRequestsByProject: jest.fn().mockResolvedValue(debugRequests),
  getAllDocumentsForProjectRequest: jest.fn(),
  getRequiredDocumentsForRequest: jest.fn(),
  getAvailableRequestStatusTransitions: jest.fn(),
  getPublicBatchedProjectRequests: jest.fn(),
  getConsultationsForRequest: jest.fn(),
  getPrivateBatchProjectRequests: jest.fn(),
  getAllDocumentsForRequestByCategory: jest.fn().mockResolvedValue([]),
  getCurrentPathwayGroupTimeline: jest.fn(),
}));

jest.mock("../../components/requests/assignment/Assignment", () => ({
  getAssignmentTypeByRequestType: jest.fn().mockResolvedValue([]),
  getRequestAssignments: jest.fn().mockResolvedValue([]),
  isAssignmentTypeMandatory: jest.fn().mockResolvedValue(false),
}));

jest.mock("../Forms/Forms", () => ({
  getAllRequestForms: jest.fn().mockResolvedValue([]),
}));

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => jest.fn(),
  useSearchParams: () => [
    {
      get: jest.fn(),
    },
    jest.fn(),
  ],
}));

jest.mock("../../common/env/env", () => ({
  isEndConsultationEnabled: jest.fn(),
}));

const mockAuthProvider = {
  isProjectDeveloperUser: jest.fn(),
  isAdminUser: jest.fn(),
  isArmUser: jest.fn(),
  isFinanceUser: jest.fn(),
  isReviewerUser: jest.fn(),
  isRegistryUser: jest.fn(),
  isTacUser: jest.fn(),
  isVvbUser: jest.fn(),
  isSupportUser: jest.fn(),
} as Record<keyof AuthContextType, jest.Mock>;

describe("RequestDropdown", () => {
  beforeEach(() => {
    (Requests.getAllDocumentsForProjectRequest as jest.Mock).mockClear();
    (Requests.getAllDocumentsForProjectRequest as jest.Mock).mockResolvedValue(mockDocuments);
    (Requests.getRequiredDocumentsForRequest as jest.Mock).mockResolvedValue(mockRequiredDocumentsResponse);
    (Requests.getPublicBatchedProjectRequests as jest.Mock).mockResolvedValue(mockBatchedProjectsRealCase);
    (Requests.getCurrentPathwayGroupTimeline as jest.Mock).mockResolvedValue(mockCurrentPathwayGroupTimelineActivities);
    (getProject as jest.Mock).mockResolvedValue(debugProject);
  });

  it("renders correctly", async () => {
    render(<RequestDropdown request={debugRequests[0]} opened={false} projectId={1} dashboardFormat={false} />);
    await waitFor(() => {
      expect(screen.getByText("Request 1")).toBeInTheDocument();
    });
  });

  it("Dropdown opened prop makes content visible when true", async () => {
    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <RequestDropdown request={debugRequests[0]} opened={true} projectId={1} dashboardFormat={false} />{" "}
      </AuthContext.Provider>,
    );

    expect(await screen.findByText("Status")).toBeInTheDocument();
  });

  it("toggles dropdown content visibility", async () => {
    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <RequestDropdown request={debugRequests[0]} opened={false} projectId={1} dashboardFormat={false} />]
      </AuthContext.Provider>,
    );
    await waitFor(() => {
      // Initial state should not display dropdown content
      expect(screen.queryByText("Status")).not.toBeInTheDocument();

      // Toggle dropdown
      fireEvent.click(screen.getByText("Request 1"));
      expect(screen.queryByText("Status")).toBeInTheDocument();

      fireEvent.click(screen.getByText("Request 1"));
      expect(screen.queryByText("Status")).not.toBeInTheDocument();
    });
  });

  it.each([
    { request: { inStakeholderConsultation: true }, expectedTag: "In Stakeholder Consultation" },
    { request: { fastTracked: true }, expectedTag: "Fast Track" },
    { request: { internal: true }, expectedTag: "Internal" },
    { request: { internal: false }, expectedTag: "External" },
    { request: { paa: true }, expectedTag: "PAA" },
  ])("shows the $expectedTag tag for the corresponding request", async ({ request, expectedTag }) => {
    render(
      <RequestDropdown
        request={{ ...debugRequests[0], ...request }}
        opened={false}
        projectId={1}
        dashboardFormat={false}
      />,
    );
    await waitFor(() => {
      expect(screen.getByText(expectedTag)).toBeInTheDocument();
    });
  });
});
