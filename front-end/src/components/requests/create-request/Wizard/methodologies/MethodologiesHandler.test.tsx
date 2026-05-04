import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { Formik, useFormikContext } from "formik";

import { mockActiveReferenceTypes } from "../../../../../common/data-types/DebugReferenceTypes";
import { CreditingPeriod } from "../../../../../common/project/Project";
import { mockCreateRequestFormInitialValues } from "../../../../../common/request/DebugRequests";
import { CreateRequestForm } from "../Request";

import { MethodologiesHandler } from "./MethodologiesHandler";

jest.mock("../../../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../../../common/data-types/DataReferenceTypes"),
  useReferenceTypes: () => ({
    referenceMethodologies: [
      mockActiveReferenceTypes.Methodology[0],
      {
        ...mockActiveReferenceTypes.Methodology[1],
        active: false,
      },
      mockActiveReferenceTypes.Methodology[2],
    ],
    getCustomReferenceOptions: () => [
      { value: 1, label: "Methodology 1" },
      { value: 2, label: "Methodology 2" },
      { value: 3, label: "Methodology 3" },
    ],
  }),
}));

jest.mock("formik", () => ({
  ...jest.requireActual("formik"),
  useFormikContext: jest.fn(() => ({
    values: mockCreateRequestForm,
  })),
}));

const mockMethodology = {
  creditingPeriodId: 1,
  methodologyId: 1,
  methodologyVersion: 1.1,
};

const mockCreditingPeriod: CreditingPeriod = {
  periodId: 1,
  startDate: "2025-06-10T00:00:00.000Z",
  endDate: "2030-06-10T00:00:00.000Z",
  methodologies: [],
  monitoringPeriods: [
    {
      id: 1,
      startDate: "2025-06-10T00:00:00.000Z",
      endDate: "2025-06-30T00:00:00.000Z",
      feeSchedule: 0,
    },
  ],
};

const mockCreateRequestForm: CreateRequestForm = {
  ...mockCreateRequestFormInitialValues,
  creditingPeriodId: mockCreditingPeriod.periodId,
};

describe("MethodologiesHandler", () => {
  it("should only allow active and any existing methodologies to be selected", async () => {
    const useFormikContextMock = useFormikContext as jest.Mock;
    useFormikContextMock.mockImplementation(() => ({
      values: {
        ...mockCreateRequestForm,
        methodologyConfigurations: [mockMethodology],
      },
      setFieldValue: jest.fn(),
    }));

    render(
      <Formik
        initialValues={{ creditingPeriodId: 1, methodologyConfigurations: [mockMethodology] }}
        onSubmit={jest.fn()}
      >
        <MethodologiesHandler creditingPeriods={[mockCreditingPeriod]} />
      </Formik>,
    );

    fireEvent.mouseDown(screen.getByText("Add Methodology"));
    fireEvent.mouseDown(screen.getByTestId("methodology-input-0"));
    const options = screen.getAllByRole("option");
    await waitFor(() => {
      expect(options.length).toBe(2);
      expect(screen.getByRole("option", { name: "Methodology 1" })).toBeInTheDocument();
      expect(screen.queryByRole("option", { name: "Methodology 2" })).not.toBeInTheDocument();
      expect(screen.getByRole("option", { name: "Methodology 3" })).toBeInTheDocument();
    });
  });

  it("allows inactive methodology that were originally selected to be reselected", async () => {
    const inactiveMethodology = { ...mockMethodology, methodologyId: 2 };

    const useFormikContextMock = useFormikContext as jest.Mock;
    useFormikContextMock.mockImplementation(() => ({
      values: {
        ...mockCreateRequestForm,
        methodologyConfigurations: [inactiveMethodology],
      },
      setFieldValue: jest.fn(),
    }));

    render(
      <Formik
        initialValues={{ creditingPeriodId: 1, methodologyConfigurations: [inactiveMethodology] }}
        onSubmit={jest.fn()}
      >
        <MethodologiesHandler creditingPeriods={[mockCreditingPeriod]} />
      </Formik>,
    );

    fireEvent.mouseDown(screen.getByText("Add Methodology"));
    fireEvent.mouseDown(screen.getByTestId("methodology-input-0"));
    const options = screen.getAllByRole("option");
    await waitFor(() => {
      expect(options.length).toBe(3);
      expect(screen.getByRole("option", { name: "Methodology 2" })).toBeInTheDocument();
      fireEvent.mouseDown(screen.getByRole("option", { name: "Methodology 1" }));
    });
    await waitFor(() => {
      expect(options.length).toBe(3);
      expect(screen.getByRole("option", { name: "Methodology 2" })).toBeInTheDocument();
    });
  });
});
