import { render, screen, waitFor } from "@testing-library/react";

import { mockActiveReferenceTypes, mockAllReferenceTypes } from "../../../common/data-types/DebugReferenceTypes";
import { mockDocuments, mockRequiredDocumentsResponse } from "../../../common/document/DebugDocuments";
import { debugRequests } from "../../../common/request/DebugRequests";
import { AuthContext, AuthContextType } from "../../Auth/Login/AuthProvider";
import { GSDocument } from "../../../common/document/GSDocument";

import { DocumentLists } from "./DocumentLists";

jest.mock("../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../common/data-types/DataReferenceTypes"),
  getReferenceValueCodeById: jest.fn(),
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
    allReferenceTypes: mockAllReferenceTypes,
  }),
  getReferenceValueDescriptionById: jest.fn(),
}));

jest.mock("./../Requests", () => ({
  getAllDocumentsForRequestByCategory: jest.fn().mockResolvedValue(mockDocuments),
  getRequestsByProject: jest.fn().mockResolvedValue(debugRequests),
  getAllDocumentsForProjectRequest: jest.fn(),
  getRequiredDocumentsForRequest: jest.fn(),
}));

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => jest.fn(),
}));

jest.mock("../../../common/env/env", () => ({
  isFormEnabled: jest.fn(() => true),
}));

jest.mock("../../Forms/Forms", () => ({
  getAllRequestForms: jest.fn().mockResolvedValue([]),
}));

const mockAuthProvider = {
  isAdminUser: jest.fn(),
  isArmUser: jest.fn(),
  isSupportUser: jest.fn(),
} as Record<keyof AuthContextType, jest.Mock>;

describe("DocumentLists", () => {
  it("renders this component correctly", async () => {
    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <DocumentLists
          request={debugRequests[0]}
          updateDocuments={() => jest.fn()}
          projectId={debugRequests[0].projectId}
          requestTypeRequiredDocuments={mockRequiredDocumentsResponse}
          requestDocuments={[]}
          refreshDocuments={false}
        />
      </AuthContext.Provider>,
    );

    const requiredDocsButton = screen.getByTestId("required-documents");
    requiredDocsButton.click();

    expect(await screen.findByText("Document1.pdf")).toBeInTheDocument();
  });

  it("renders the number of documents", async () => {
    const mockDocument: GSDocument = {
      id: "1",
      containerName: "GS1",
      fileName: "Test.txt",
      fileSize: 100,
      hashValue: "",
      access: true,
      category: true,
      documentTypeId: 1,
      contentType: "application/json",
      documentCategoryType: 1,
      latest: true,
      modifiedTimestamp: "2025-04-10T10:56:57.718888Z",
      uploadedTimestamp: "2025-04-10T10:56:57.718888Z",
    };
    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <DocumentLists
          request={debugRequests[0]}
          updateDocuments={() => jest.fn()}
          projectId={debugRequests[0].projectId}
          requestTypeRequiredDocuments={mockRequiredDocumentsResponse}
          requestDocuments={[
            {
              ...mockDocument,
              documentCategoryType: 1,
            },
            {
              ...mockDocument,
              documentCategoryType: 2,
            },
            {
              ...mockDocument,
              documentCategoryType: 2,
            },
            {
              ...mockDocument,
              documentCategoryType: 3,
            },
            {
              ...mockDocument,
              documentCategoryType: 3,
            },
            {
              ...mockDocument,
              documentCategoryType: 3,
            },
          ]}
          refreshDocuments={false}
        />
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      expect(screen.getByText(1)).toBeInTheDocument();
      expect(screen.getByText(2)).toBeInTheDocument();
      expect(screen.getByText(3)).toBeInTheDocument();
    });
  });
});
