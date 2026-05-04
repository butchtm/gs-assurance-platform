import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { useNavigate } from "react-router-dom";

import { mockActiveReferenceTypes, mockAllReferenceTypes } from "../../../common/data-types/DebugReferenceTypes";
import { mockDocuments } from "../../../common/document/DebugDocuments";
import { DocumentCategoryType } from "../../../common/document/GSDocument";
import { GSDocumentScrap } from "../../../common/document/GSDocumentScrap";
import { debugRequests } from "../../../common/request/DebugRequests";
import { AuthContext, AuthContextType } from "../../Auth/Login/AuthProvider";
import { isFormEnabled } from "../../../common/env/env";
import { getAllRequestForms } from "../../Forms/Forms";

import { DocumentList } from "./DocumentList";

export const mockOnDocumentModify = jest.fn();

jest.mock("../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../common/data-types/DataReferenceTypes"),
  getReferenceValueCodeById: jest.fn(),
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
    allReferenceTypes: mockAllReferenceTypes,
  }),
  getReferenceValueDescriptionById: jest.fn(),
}));

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: jest.fn(),
}));

jest.mock("./../Requests", () => ({
  getAllDocumentsForRequestByCategory: jest.fn().mockResolvedValue(mockDocuments),
}));

jest.mock("../../../common/env/env", () => ({
  isFormEnabled: jest.fn(() => true),
}));

jest.mock("../../Forms/Forms", () => ({
  getAllRequestForms: jest.fn().mockResolvedValue([]),
}));

const mockRequiredDocuments: GSDocumentScrap[] = [
  {
    requestTypeId: 2,
    documentTypeId: 1,
    documentDisplayCategoryId: 1,
    documentTypeDescription: "Required Document Type 1",
    documentDisplayCategoryCode: "RDC1",
  },
  {
    requestTypeId: 2,
    documentTypeId: 2,
    documentDisplayCategoryId: 1,
    documentTypeDescription: "Required Document Type 2",
    documentDisplayCategoryCode: "RDC2",
  },
  {
    requestTypeId: 2,
    documentTypeId: 3,
    documentDisplayCategoryId: 1,
    documentTypeDescription: "Required Document Type 3",
    documentDisplayCategoryCode: "RDC3",
  },
  {
    requestTypeId: 2,
    documentTypeId: 88,
    documentDisplayCategoryId: 1,
    documentTypeDescription: "Design Consultation Report",
    documentDisplayCategoryCode: "RDC3",
  },
  {
    requestTypeId: 2,
    documentTypeId: 89,
    documentDisplayCategoryId: 1,
    documentTypeDescription: "VPA Design Document",
    documentDisplayCategoryCode: "RDC3",
    formType: mockActiveReferenceTypes.DigitalFormType[0],
  },
];

describe("DocumentList", () => {
  const renderList = (isPd?: boolean) => {
    const mockAuthProvider = {
      isAdminUser: jest.fn().mockReturnValue(isPd ? false : true),
      isArmUser: jest.fn().mockReturnValue(false),
      isSupportUser: jest.fn().mockReturnValue(false),
      isProjectDeveloperUser: jest.fn().mockReturnValue(isPd ?? false),
    } as Record<keyof AuthContextType, jest.Mock>;

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <DocumentList
          projectId={debugRequests[0].projectId}
          request={{ ...debugRequests[0], batchId: null }}
          onDocumentModify={mockOnDocumentModify}
          documentCategoryType={DocumentCategoryType.REQUIRED}
          requiredDocuments={mockRequiredDocuments}
          refreshDocuments={false}
        />
      </AuthContext.Provider>,
    );
  };

  it("renders correctly", async () => {
    renderList();
    expect(await screen.findAllByText(/Document1.pdf|Document2.docx/)).toHaveLength(2);
    expect(await screen.findByText("Document1.pdf")).toBeInTheDocument();
    expect(await screen.findByText("Document2.docx")).toBeInTheDocument();
  });

  it("renders missing documents correctly", async () => {
    renderList();
    expect(await screen.findByText("VPA Design Document")).toBeInTheDocument();
    expect(await screen.findByText("Design Consultation Report")).toBeInTheDocument();
  });

  it("renders upload and modified date", async () => {
    renderList();
    expect(await screen.findByText("Document1.pdf")).toBeInTheDocument();
    expect(await screen.findByText("01/01/2023 10:00")).toBeInTheDocument();
    expect(await screen.findByText("01/01/2023 12:00")).toBeInTheDocument();
  });

  it("renders the document access", async () => {
    renderList();
    expect(await screen.findAllByText("PUBLIC")).toHaveLength(2);
    expect(await screen.findAllByText("PRIVATE")).toHaveLength(1);
  });

  it("renders the form completion button if appropriate", async () => {
    (isFormEnabled as jest.Mock).mockReturnValue(true);
    renderList();
    expect(
      await screen.findByTestId(`start-form-${mockActiveReferenceTypes.DigitalFormType[0].id}`),
    ).toBeInTheDocument();
  });

  it("does not render the form completion button if not in production", async () => {
    (isFormEnabled as jest.Mock).mockReturnValue(false);
    renderList();
    await waitFor(() => {
      expect(
        screen.queryByTestId(`start-form-${mockActiveReferenceTypes.DigitalFormType[0].id}`),
      ).not.toBeInTheDocument();
    });
  });

  it("renders the form details if available", async () => {
    (getAllRequestForms as jest.Mock).mockResolvedValue([
      {
        id: 1,
        version: 1.0,
        type: mockActiveReferenceTypes.DigitalFormType[0],
        status: mockActiveReferenceTypes.RequestFormStatus[0],
        createdAt: "2025-04-24T12:43:41.05365Z",
        updatedAt: "2025-04-27T09:51:41.05365Z",
      },
    ]);
    renderList();
    await waitFor(() => {
      expect(screen.getByText("Annual Report Form")).toBeInTheDocument();
      expect(screen.getByText("24/04/2025 12:43")).toBeInTheDocument();
      expect(screen.getByText("27/04/2025 09:51")).toBeInTheDocument();
    });
  });

  it("allows the resuming of a form", async () => {
    const navigateMock = jest.fn();
    (useNavigate as jest.Mock).mockReturnValue(navigateMock);
    const mockForm = {
      id: 1,
      version: 1.0,
      type: mockActiveReferenceTypes.DigitalFormType[0],
      status: mockActiveReferenceTypes.RequestFormStatus[0],
      createdAt: "2025-04-24T12:43:41.05365Z",
      updatedAt: "2025-04-27T09:51:41.05365Z",
    };
    (getAllRequestForms as jest.Mock).mockResolvedValue([mockForm]);
    renderList();
    await waitFor(() => {
      const resumeButton = screen.getByTestId(`resume-form-${mockActiveReferenceTypes.DigitalFormType[0].id}`);
      fireEvent.click(resumeButton);
      expect(navigateMock).toHaveBeenCalledWith(
        `/projects/${debugRequests[0].project.gsid}/requests/${debugRequests[0].id}/form/${mockForm.id}`,
        {
          state: {
            formType: mockActiveReferenceTypes.DigitalFormType[0],
            requestId: debugRequests[0].id,
            form: mockForm,
          },
        },
      );
    });
  });
  it("renders the view button if the form is submitted", async () => {
    const navigateMock = jest.fn();
    (useNavigate as jest.Mock).mockReturnValue(navigateMock);
    const mockForm = {
      id: 1,
      version: 1.0,
      type: mockActiveReferenceTypes.DigitalFormType[0],
      status: mockActiveReferenceTypes.RequestFormStatus[1],
      createdAt: "2025-04-24T12:43:41.05365Z",
      updatedAt: "2025-04-27T09:51:41.05365Z",
    };
    (getAllRequestForms as jest.Mock).mockResolvedValue([mockForm]);
    renderList(true);
    await waitFor(() => {
      const viewButton = screen.getByTestId(`view-form-${mockActiveReferenceTypes.DigitalFormType[0].id}`);
      const editButton = screen.queryByTestId(`edit-form-${mockActiveReferenceTypes.DigitalFormType[0].id}`);
      expect(viewButton).toBeInTheDocument();
      expect(editButton).not.toBeInTheDocument();
    });
  });
  it("renders the edit button if correct permissions and submitted form", async () => {
    const mockForm = {
      id: 1,
      version: 1.0,
      type: mockActiveReferenceTypes.DigitalFormType[0],
      status: mockActiveReferenceTypes.RequestFormStatus[1],
      createdAt: "2025-04-24T12:43:41.05365Z",
      updatedAt: "2025-04-27T09:51:41.05365Z",
    };
    (getAllRequestForms as jest.Mock).mockResolvedValue([mockForm]);
    renderList();
    await waitFor(() => {
      const editButton = screen.getByTestId(`edit-form-${mockActiveReferenceTypes.DigitalFormType[0].id}`);
      expect(editButton).toBeInTheDocument();
    });
  });
});
