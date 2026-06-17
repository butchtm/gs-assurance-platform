import { render, screen, waitFor } from "@testing-library/react";
import { Formik, useFormikContext } from "formik";

import { mockActiveReferenceTypes } from "../../../../common/data-types/DebugReferenceTypes";
import mockProjects from "../../../../common/project/DebugProject";
import { debugRequests, mockBatchRequestMappings } from "../../../../common/request/DebugRequests";
import { AuthContext, AuthContextType } from "../../../Auth/Login/AuthProvider";
import * as RequestModule from "../../Requests";

import { Step1 } from "./Step1";

jest.mock("../../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../../common/data-types/DataReferenceTypes"),
  getReferenceValueCodeById: jest.fn(),
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
    allReferenceTypes: mockActiveReferenceTypes,
    activeRequestTypes: mockActiveReferenceTypes.RequestType.map((type) => ({
      ...type,
      isActive: true,
      supportsPAA: true,
    })),
  }),
}));

jest.mock("./../../Requests", () => ({
  getRequestsByProject: jest.fn().mockResolvedValue(debugRequests),
  getRequiredDocumentsForRequest: jest.fn().mockResolvedValue({
    OTHER: [],
    REQUIRED: [],
    SUPPORTING: [],
  }),
  getBatchedRequestTypeMappings: jest.fn().mockResolvedValue(mockBatchRequestMappings),
  createBatchRequest: jest.fn(),
  getBatchableProjects: jest.fn().mockResolvedValue([]),
}));

const initialFormikFormValues = {
  requestType: "",
  creditingPeriods: [],
  emissionReductionAmount: "",
  createBatchRequest: "",
  batchRequestTypeId: null,
  batchProjectIds: [],
};

jest.mock("formik", () => ({
  ...jest.requireActual("formik"),
  useFormikContext: jest.fn(() => ({
    values: {},
    setFieldValue: jest.fn(),
    setFieldTouched: jest.fn(),
    getFieldProps: jest.fn().mockReturnValue({
      onChange: jest.fn(),
      onBlur: jest.fn(),
      value: "",
    }),
  })),
}));

const mockAuthProvider = {
  isProjectDeveloperUser: jest.fn().mockReturnValue(false),
  isAdminUser: jest.fn().mockReturnValue(false),
  isArmUser: jest.fn().mockReturnValue(false),
} as Record<keyof AuthContextType, jest.Mock>;

describe("Step1", () => {
  beforeEach(() => {
    (RequestModule.getBatchedRequestTypeMappings as jest.Mock).mockResolvedValue([
      { id: 1, name: "Batch Request 1" },
      { id: 2, name: "Batch Request 2" },
    ]);
  });

  it("should enable Create Batch Request field when a request type with batch mappings is selected", async () => {
    const useFormikContextMock = useFormikContext as jest.Mock;
    useFormikContextMock.mockImplementation(() => ({
      values: {
        ...initialFormikFormValues,
        // Preliminary Review
        requestType: "1",
      },
      touched: {
        requestType: false,
      },
      setFieldValue: jest.fn(),
    }));

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <Formik initialValues={{}} onSubmit={jest.fn()}>
          <Step1 project={mockProjects[0]} setIsBatched={() => ({})} setBatchedRequest={() => ({})} />
        </Formik>
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      const createBatchRequestSelect = screen.getByTestId("createBatchRequest-select");
      expect(RequestModule.getBatchedRequestTypeMappings).toHaveBeenCalled();
      expect(createBatchRequestSelect).toBeInTheDocument();
      expect(createBatchRequestSelect).not.toHaveAttribute("disabled");
    });
  });

  it("should disable Create Batch Request field when a request type with batch mappings is selected", async () => {
    const useFormikContextMock = useFormikContext as jest.Mock;
    useFormikContextMock.mockImplementation(() => ({
      values: {
        ...initialFormikFormValues,
        // Annual Report
        requestType: "1014",
      },
      touched: {
        requestType: false,
      },
      setFieldValue: jest.fn(),
    }));

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <Formik initialValues={{}} onSubmit={jest.fn()}>
          <Step1 project={mockProjects[0]} setIsBatched={() => ({})} setBatchedRequest={() => ({})} />
        </Formik>
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      const createBatchRequestSelect = screen.getByTestId("createBatchRequest-select");
      expect(RequestModule.getBatchedRequestTypeMappings).toHaveBeenCalled();
      expect(createBatchRequestSelect).toBeInTheDocument();
      expect(createBatchRequestSelect).toHaveAttribute("disabled");
    });
  });

  it("shows the single request fields when create batch request is set to no", async () => {
    const useFormikContextMock = useFormikContext as jest.Mock;
    useFormikContextMock.mockImplementation(() => ({
      values: {
        ...initialFormikFormValues,
        requestType: "1",
        createBatchRequest: "no",
      },
      touched: {
        requestType: false,
      },
      setFieldValue: jest.fn(),
    }));

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <Formik initialValues={{}} onSubmit={jest.fn()}>
          <Step1 project={mockProjects[0]} setIsBatched={() => ({})} setBatchedRequest={() => ({})} />
        </Formik>
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      expect(screen.getByRole("combobox", { name: "Credit Period ID" })).toBeInTheDocument();
      expect(screen.getByLabelText("Estimated Annual Emission Reduction/Removal")).toBeInTheDocument();
      expect(screen.queryByLabelText("Batch Request Type")).not.toBeInTheDocument();
      expect(screen.queryByLabelText(/Choose Projects to batch this request with/)).not.toBeInTheDocument();
    });
  });

  it("should display batch type selector when Create Batch Request is set to yes", async () => {
    const useFormikContextMock = useFormikContext as jest.Mock;
    useFormikContextMock.mockImplementation(() => ({
      values: {
        ...initialFormikFormValues,
        requestType: "1",
        createBatchRequest: "yes",
      },
      touched: {
        requestType: false,
      },
      setFieldValue: jest.fn(),
      setFieldTouched: jest.fn(),
    }));

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <Formik initialValues={{}} onSubmit={jest.fn()}>
          <Step1 project={mockProjects[0]} setIsBatched={() => ({})} setBatchedRequest={() => ({})} />
        </Formik>
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      expect(screen.getByRole("combobox", { name: "Batch Request Type" })).toBeInTheDocument();
    });
  });

  it("should display batch project selector when a batch type is selected ", async () => {
    const useFormikContextMock = useFormikContext as jest.Mock;
    useFormikContextMock.mockImplementation(() => ({
      values: {
        ...initialFormikFormValues,
        requestType: "1",
        createBatchRequest: "yes",
        batchRequestTypeId: 1,
      },
      touched: {
        requestType: false,
      },
      setFieldValue: jest.fn(),
      setFieldTouched: jest.fn(),
    }));

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <Formik initialValues={{}} onSubmit={jest.fn()}>
          <Step1 project={mockProjects[0]} setIsBatched={() => ({})} setBatchedRequest={() => ({})} />
        </Formik>
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      expect(screen.getByLabelText(/Choose Projects to batch this request with/)).toBeInTheDocument();
    });
  });

  it("should disable certain fields when editing a batched request", async () => {
    const useFormikContextMock = useFormikContext as jest.Mock;
    useFormikContextMock.mockImplementation(() => ({
      values: {
        ...initialFormikFormValues,
        requestType: "1",
        createBatchRequest: "yes",
        batchRequestTypeId: 1,
      },
      touched: {
        requestType: false,
      },
      setFieldValue: jest.fn(),
      setFieldTouched: jest.fn(),
    }));

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <Formik initialValues={{}} onSubmit={jest.fn()}>
          <Step1 project={mockProjects[0]} setIsBatched={() => ({})} setBatchedRequest={() => ({})} requestEditId={1} />
        </Formik>
      </AuthContext.Provider>,
    );
    await waitFor(() => {
      expect(screen.getByLabelText("Request Type*")).toBeDisabled();
      expect(screen.getByTestId("createBatchRequest-select")).toBeDisabled();
      expect(screen.getByTestId("batchRequestTypeId-select")).toBeDisabled();
      expect(screen.getByLabelText(/Choose Projects to batch this request with/)).toBeDisabled();
    });
  });
});
