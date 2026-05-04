import { render, screen, waitFor } from "@testing-library/react";
import { Formik, useFormikContext } from "formik";

import { mockActiveReferenceTypes } from "../../../../common/data-types/DebugReferenceTypes";
import {
  mockBatchedProjectsFormikValues,
  mockBatchedProjectsFormikValuesWithNewCreditingPeriod,
  mockBatchedProjectsList,
  mockBatchedProjectsListWithNewCreditingPeriod,
} from "../../../../common/request/DebugBatchRequest";
import { AuthContext, AuthContextType } from "../../../Auth/Login/AuthProvider";

import { Summary } from "./Summary";

jest.mock("../../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../../common/data-types/DataReferenceTypes"),
  getReferenceValueCodeById: jest.fn(),
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
    getCustomReferenceOptions: () =>
      mockActiveReferenceTypes.Methodology.map((m) => {
        return { label: m.description, value: m.id };
      }),
  }),
}));

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

describe("CreateRequestWizard Component", () => {
  it("should show a summary page of batch requests", async () => {
    const useFormikContextMock = useFormikContext as jest.Mock;
    useFormikContextMock.mockImplementation(() => mockBatchedProjectsFormikValues);

    render(
      <AuthContext.Provider
        value={
          {
            isAdminUser: () => true,
            isArmUser: () => true,
          } as AuthContextType
        }
      >
        <Formik initialValues={{}} onSubmit={jest.fn()}>
          <Summary
            batchedProjects={mockBatchedProjectsList}
            setChangeStep={() => ({})}
            isMethodologyUpdateRequestType={true}
          />
        </Formik>
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      expect(screen.getByText("GS1")).toBeTruthy();
      expect(screen.getByText("GS2")).toBeTruthy();

      expect(screen.getByText("999")).toBeTruthy();
      expect(screen.getByText("998")).toBeTruthy();

      expect(screen.getByText("Period 1 - 24/03/2025 to 23/03/2030")).toBeTruthy();
      expect(screen.getByText("Period 1 - 24/02/2025 to 23/02/2030")).toBeTruthy();

      expect(screen.getByText("Programme of Activities Methodologies")).toBeTruthy();
      expect(screen.getByText("Methodology1")).toBeTruthy();
      expect(screen.getByText("2.3")).toBeTruthy();

      expect(screen.getByText("VPA Methodologies")).toBeTruthy();
      expect(screen.getByText("Methodology2")).toBeTruthy();
      expect(screen.getByText("1.5")).toBeTruthy();
    });
  });

  it("should show non-existing crediting periods as New Period 1", async () => {
    const useFormikContextMock = useFormikContext as jest.Mock;
    useFormikContextMock.mockImplementation(() => mockBatchedProjectsFormikValuesWithNewCreditingPeriod);

    render(
      <AuthContext.Provider
        value={
          {
            isAdminUser: () => true,
            isArmUser: () => true,
          } as AuthContextType
        }
      >
        <Formik initialValues={{}} onSubmit={jest.fn()}>
          <Summary
            batchedProjects={mockBatchedProjectsListWithNewCreditingPeriod}
            setChangeStep={() => ({})}
            isMethodologyUpdateRequestType={true}
          />
        </Formik>
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      expect(screen.getByText("Period 1 - 24/03/2025 to 23/03/2030")).toBeTruthy();
      expect(screen.getByText("New Period - 24/02/2025 to 23/02/2030")).toBeTruthy();
    });
  });
});
