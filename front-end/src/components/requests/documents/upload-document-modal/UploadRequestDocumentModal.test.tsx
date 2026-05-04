import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { toast } from "react-toastify";
import { cloneDeep } from "lodash";

import {
  mockActiveReferenceTypes,
  mockAllReferenceTypes,
  mockInactiveReferenceTypes,
} from "../../../../common/data-types/DebugReferenceTypes";
import {
  mockDocuments,
  mockPiiDocuments,
  mockRequiredDocumentsResponse,
} from "../../../../common/document/DebugDocuments";
import { debugClosedRequests, debugRequests } from "../../../../common/request/DebugRequests";
import { PermissionGroup } from "../../../../PermissionGroup";
import { AuthContext, AuthContextType } from "../../../Auth/Login/AuthProvider";
import { User } from "../../../User/User";
import { getAllRequestForms } from "../../../Forms/Forms";

import { uploadDocument } from "./UploadRequestDocument";
import UploadRequestDocumentModal from "./UploadRequestDocumentModal";

jest.mock("./UploadRequestDocument", () => ({
  uploadDocument: jest.fn(),
}));

jest.mock("react-toastify", () => ({
  toast: {
    success: jest.fn(),
    error: jest.fn(),
  },
}));

jest.mock("../../../../common/piiAnalysis/PiiAction", () => ({
  ...jest.requireActual("../../../../common/piiAnalysis/PiiAction"),
}));

jest.mock("../../../Forms/Forms", () => ({
  getAllRequestForms: jest.fn().mockResolvedValue([]),
}));

const mockAuthProvider = {
  isProjectDeveloperUser: jest.fn(),
  isAdminUser: jest.fn(),
  isArmUser: jest.fn(),
  isReviewerUser: jest.fn(),
} as Record<keyof AuthContextType, jest.Mock>;

jest.mock("../../../../common/data-types/DataReferenceTypes", () => ({
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
    inactiveReferenceTypes: mockInactiveReferenceTypes,
    allReferenceTypes: mockAllReferenceTypes,
  }),
}));

describe("UploadRequestDocumentModal", () => {
  beforeEach(() => {
    jest.resetAllMocks();
  });

  it.each([
    ["Completed - Approved", debugClosedRequests[0]],
    ["Completed - Rejected", debugClosedRequests[1]],
    ["Cancelled", debugClosedRequests[2]],
    ["Omitted", debugClosedRequests[3]],
  ])("displays error toast when uploading document to closed request (%s)", async (label, closedRequest) => {
    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <UploadRequestDocumentModal
          isOpen={true}
          projectId={null}
          request={closedRequest}
          edit={false}
          document={mockDocuments[0]}
          onClose={jest.fn()}
        />
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      fireEvent.click(screen.getByText("Upload"));
      expect(uploadDocument).not.toHaveBeenCalled();
      expect(toast.error).toHaveBeenCalled();
    });
  });

  it("allows document to be set to public if document was uploaded before PII scanning (null PiiAction field)", async () => {
    mockAuthProvider.isAdminUser.mockReturnValue(false);
    mockAuthProvider.isArmUser.mockReturnValue(true);
    render(
      <AuthContext.Provider
        value={{
          ...(mockAuthProvider as unknown as AuthContextType),
          isAuthenticated: true,
          user: {
            groups: [PermissionGroup.ARM_TEAM],
          } as User,
        }}
      >
        <UploadRequestDocumentModal
          isOpen={true}
          projectId={null}
          request={debugRequests[0]}
          edit={true}
          document={mockDocuments[4]}
          onClose={jest.fn()}
          requiredDocuments={mockRequiredDocumentsResponse}
        />
      </AuthContext.Provider>,
    );
    const toggle = screen.getByTestId("public-private-toggle");
    await waitFor(() => {
      expect(toggle).toBeInTheDocument();
      expect(toggle).not.toBeDisabled();
    });
  });

  it("admin users can set document to public even if contains unactioned PII", async () => {
    mockAuthProvider.isAdminUser.mockReturnValue(true);
    render(
      <AuthContext.Provider
        value={{
          ...(mockAuthProvider as unknown as AuthContextType),
          isAuthenticated: true,
          user: {
            groups: [PermissionGroup.GS_ADMIN],
          } as User,
        }}
      >
        <UploadRequestDocumentModal
          isOpen={true}
          projectId={null}
          request={debugRequests[0]}
          edit={true}
          document={mockPiiDocuments[0]}
          onClose={jest.fn()}
          requiredDocuments={mockRequiredDocumentsResponse}
        />
      </AuthContext.Provider>,
    );

    const toggle = screen.getByTestId("public-private-toggle");
    expect(toggle).toBeInTheDocument();

    await waitFor(() => {
      expect(screen.queryByTestId("help-icon")).not.toBeInTheDocument();
      expect(screen.getByText("Private Document")).toBeInTheDocument();
      fireEvent.click(toggle);
      expect(screen.getByText("Public Document")).toBeInTheDocument();
      expect(screen.queryByText("Private Document")).not.toBeInTheDocument();
    });
  });

  it("disables the document from being set to public if unactioned PII is present", async () => {
    mockAuthProvider.isAdminUser.mockReturnValue(false);
    render(
      <AuthContext.Provider
        value={{
          ...(mockAuthProvider as unknown as AuthContextType),
          isAuthenticated: true,
          user: {
            groups: [PermissionGroup.ARM_TEAM],
          } as User,
        }}
      >
        <UploadRequestDocumentModal
          isOpen={true}
          projectId={null}
          request={debugRequests[0]}
          edit={true}
          document={mockPiiDocuments[0]}
          onClose={jest.fn()}
          requiredDocuments={mockRequiredDocumentsResponse}
        />
      </AuthContext.Provider>,
    );

    const toggle = screen.getByTestId("public-private-toggle");
    expect(toggle).toBeInTheDocument();

    await waitFor(() => {
      expect(screen.getByTestId("help-icon")).toBeInTheDocument();
      expect(screen.getByText("Private Document")).toBeInTheDocument();
      fireEvent.click(toggle);
      expect(screen.getByText("Private Document")).toBeInTheDocument();
      expect(screen.queryByText("Public Document")).not.toBeInTheDocument();
    });
  });

  it.each([mockPiiDocuments[1], mockPiiDocuments[2]])(
    "allows the document to be set to public if PII has been actioned acceptably (accepted or no pii found)",
    async (document) => {
      mockAuthProvider.isAdminUser.mockReturnValue(false);
      render(
        <AuthContext.Provider
          value={{
            ...(mockAuthProvider as unknown as AuthContextType),
            isAuthenticated: true,
            user: {
              groups: [PermissionGroup.ARM_TEAM],
            } as User,
          }}
        >
          <UploadRequestDocumentModal
            isOpen={true}
            projectId={null}
            request={debugRequests[0]}
            edit={true}
            document={document}
            onClose={jest.fn()}
            requiredDocuments={mockRequiredDocumentsResponse}
          />
        </AuthContext.Provider>,
      );

      const toggle = screen.getByTestId("public-private-toggle");
      expect(toggle).toBeInTheDocument();

      await waitFor(() => {
        expect(screen.queryByTestId("help-icon")).not.toBeInTheDocument();
        expect(screen.getByText("Private Document")).toBeInTheDocument();
        fireEvent.click(toggle);
        expect(screen.getByText("Public Document")).toBeInTheDocument();
        expect(screen.queryByText("Private Document")).not.toBeInTheDocument();
      });
    },
  );

  it("only allows upload of feedback review form for reviewer user", async () => {
    const user = userEvent.setup();
    mockAuthProvider.isReviewerUser.mockReturnValue(true);
    render(
      <AuthContext.Provider
        value={{
          ...(mockAuthProvider as unknown as AuthContextType),
          isAuthenticated: true,
          user: {
            groups: [PermissionGroup.GS_REVIEWER],
          } as User,
        }}
      >
        <UploadRequestDocumentModal
          isOpen={true}
          projectId={null}
          request={debugRequests[0]}
          edit={true}
          document={null}
          onClose={jest.fn()}
          requiredDocuments={mockRequiredDocumentsResponse}
        />
      </AuthContext.Provider>,
    );

    const input = screen.getByTestId("document-type-select").querySelector("input");
    await waitFor(() => {
      expect(input).not.toBeNull();
      expect(input).toHaveValue("Review Feedback Form");
    });

    if (!input) return;
    await user.click(input);
    const options = await screen.findAllByRole("option");
    await waitFor(() => {
      expect(options).toHaveLength(1);
      expect(options[0]).toHaveTextContent("Review Feedback Form");
    });
  });

  it("disables the document from being set to public if uploaded by a reviewer", async () => {
    mockAuthProvider.isAdminUser.mockReturnValue(true);
    const document = cloneDeep(mockDocuments[0]);
    document.access = false;
    document.uploadedByAccountType = { id: 0, code: "REVIEWER", description: "Reviewer" };

    render(
      <AuthContext.Provider
        value={{
          ...(mockAuthProvider as unknown as AuthContextType),
          isAuthenticated: true,
          user: {
            groups: [PermissionGroup.GS_ADMIN],
          } as User,
        }}
      >
        <UploadRequestDocumentModal
          isOpen={true}
          projectId={null}
          request={debugRequests[0]}
          edit={true}
          document={document}
          onClose={jest.fn()}
          requiredDocuments={mockRequiredDocumentsResponse}
        />
      </AuthContext.Provider>,
    );

    const toggle = screen.getByTestId("public-private-toggle");
    expect(toggle).toBeInTheDocument();

    await waitFor(() => {
      expect(screen.getByTestId("help-icon")).toBeInTheDocument();
      expect(screen.getByText("Private Document")).toBeInTheDocument();
      fireEvent.click(toggle);
      expect(screen.getByText("Private Document")).toBeInTheDocument();
      expect(screen.queryByText("Public Document")).not.toBeInTheDocument();
    });
  });

  it("prevents upload of a document which has an associated form", async () => {
    const user = userEvent.setup();
    const formType = mockActiveReferenceTypes.DigitalFormType[0];
    (getAllRequestForms as jest.Mock).mockResolvedValue([
      {
        id: 1,
        version: 1.0,
        type: formType,
        status: mockActiveReferenceTypes.RequestFormStatus[0],
        createdAt: "",
      },
    ]);
    const expectedRequiredDoc = mockRequiredDocumentsResponse.REQUIRED?.find((x) => x.formType?.id === formType.id);
    const expectedDocType = mockActiveReferenceTypes.DocumentType.find(
      (x) => x.id == expectedRequiredDoc?.documentTypeId,
    );
    render(
      <AuthContext.Provider
        value={{
          ...(mockAuthProvider as unknown as AuthContextType),
          isAuthenticated: true,
          user: {
            groups: [PermissionGroup.GS_ADMIN],
          } as User,
        }}
      >
        <UploadRequestDocumentModal
          isOpen={true}
          projectId={null}
          request={debugRequests[0]}
          edit={false}
          document={null}
          onClose={jest.fn()}
          requiredDocuments={mockRequiredDocumentsResponse}
        />
      </AuthContext.Provider>,
    );
    const input = screen.getByTestId("document-type-select").querySelector("input");
    if (!input) throw Error("Test input not available");
    user.click(input);
    const options = await screen.findAllByRole("option");
    await waitFor(() => {
      expect(options.filter((x) => x.textContent === expectedDocType?.description)).toHaveLength(0);
    });
  });
});
