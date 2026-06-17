import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import dayjs from "dayjs";
import { Formik, useFormikContext } from "formik";
import React from "react";

import { mockActiveReferenceTypes } from "../../../../common/data-types/DebugReferenceTypes";
import { CreateProjectForm } from "../../../../common/project/Project";
import { getCandidateParentDetails } from "../../Project";

import { Step3 } from "./Step3";

jest.mock("../../../../common/data-types/DataReferenceTypes", () => ({
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
    getCustomReferenceOptions: () => [
      { value: 1, label: "Option 1" },
      { value: 2, label: "Option 2" },
      { value: 3, label: "Option 3" },
    ],
    referenceMethodologies: [
      mockActiveReferenceTypes.Methodology[0],
      {
        ...mockActiveReferenceTypes.Methodology[1],
        active: false,
      },
      mockActiveReferenceTypes.Methodology[2],
    ],
  }),
}));

const mockProject = {
  creditingPeriods: [
    {
      periodId: 1,
      startDate: dayjs.utc().toISOString(),
      endDate: dayjs.utc().add(5, "year").subtract(1, "day").toISOString(),
      methodologies: [
        { id: 1, version: 1.1 },
        { id: 2, version: 2.2 },
      ],
    },
  ],
  methodologies: [],
};

jest.mock("../../Project", () => ({
  getCandidateParentDetails: jest.fn(),
}));

const FormStateDisplay: React.FC = () => {
  const { values } = useFormikContext<CreateProjectForm>();
  return <pre data-testid="form-state">{JSON.stringify(values)}</pre>;
};

interface TestFormProps {
  initialValues: Partial<CreateProjectForm>;
  children: React.ReactNode;
}

const RenderFormikWithLocalization: React.FC<TestFormProps> = ({ initialValues, children }) => (
  <Formik initialValues={initialValues} onSubmit={jest.fn()}>
    <LocalizationProvider dateAdapter={AdapterDayjs}>
      {children}
      <FormStateDisplay />
    </LocalizationProvider>
  </Formik>
);

describe("Step3 Component", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (getCandidateParentDetails as jest.Mock).mockResolvedValue(mockProject);
  });

  const initialFormValues = {
    parentProjectId: undefined,
    creditingPeriods: [
      {
        periodId: 1,
        startDate: dayjs.utc().toISOString(),
        endDate: dayjs.utc().add(5, "year").subtract(1, "day").toISOString(),
        methodologies: [{ id: null, version: null }],
      },
    ],
    methodologies: [],
  };

  it("renders Step3 and initializes default crediting period", async () => {
    render(
      <RenderFormikWithLocalization initialValues={initialFormValues}>
        <Step3 />
      </RenderFormikWithLocalization>,
    );
    await waitFor(() => {
      expect(screen.getByText("1")).toBeInTheDocument();
    });
    expect(screen.getByText(/Crediting Periods/i)).toBeInTheDocument();
  });

  it("adds a new crediting period when clicking the button", async () => {
    render(
      <RenderFormikWithLocalization initialValues={initialFormValues}>
        <Step3 />
      </RenderFormikWithLocalization>,
    );
    await waitFor(() => {
      expect(screen.getByText("1")).toBeInTheDocument();
    });
    const addPeriodButton = screen.getByRole("button", { name: /Add Crediting Period/i });
    userEvent.click(addPeriodButton);
    await waitFor(() => {
      expect(screen.getByText("2")).toBeInTheDocument();
    });
  });

  it("should allow for editing methodologies in the first crediting period", async () => {
    render(
      <RenderFormikWithLocalization
        initialValues={{
          creditingPeriods: [
            {
              periodId: 1,
              startDate: "2024-01-01",
              endDate: "2025-01-01",
              methodologies: [{ id: -1, version: null }],
            },
          ],
        }}
      >
        <Step3 />
      </RenderFormikWithLocalization>,
    );
    await waitFor(() => {
      expect(screen.getByText("1")).toBeInTheDocument();
      expect(screen.getByTestId("methodology-autocomplete")).toBeInTheDocument();
    });
  });

  it("should only allow selection of active and any existing methodologies for non-child projects", async () => {
    render(
      <RenderFormikWithLocalization
        initialValues={{
          creditingPeriods: [
            {
              periodId: 1,
              startDate: "2024-01-01",
              endDate: "2025-01-01",
              methodologies: [{ id: 2, version: 2.2 }],
            },
          ],
        }}
      >
        <Step3 isUpdate={true} />
      </RenderFormikWithLocalization>,
    );
    const methodologySelect = screen.getByLabelText("Methodology*");
    await waitFor(() => {
      expect(methodologySelect).toHaveValue("Option 2");
    });
    fireEvent.mouseDown(methodologySelect);
    const options = screen.getAllByRole("option");
    await waitFor(() => {
      expect(options.length).toBe(3);
      expect(screen.getByRole("option", { name: "Option 1" })).toBeInTheDocument();
      expect(screen.getByRole("option", { name: "Option 2" })).toBeInTheDocument();
      expect(screen.getByRole("option", { name: "Option 3" })).toBeInTheDocument();
    });
  });

  it("should only allow selection of parent and any existing methodologies for child projects", async () => {
    render(
      <RenderFormikWithLocalization
        initialValues={{
          accountId: 1,
          projectClassificationId: 11,
          masterProjectId: 900,
          parentProjectId: 1,
          creditingPeriods: [
            {
              periodId: 1,
              startDate: "2024-01-01",
              endDate: "2025-01-01",
              methodologies: [{ id: 2, version: 2.2 }],
            },
          ],
        }}
      >
        <Step3 isUpdate={true} />
      </RenderFormikWithLocalization>,
    );
    await waitFor(() => {
      const methodologySelect = screen.getByTestId("methodology-input-0");
      expect(getCandidateParentDetails).toHaveBeenCalledWith({
        accountId: 1,
        classificationId: 11,
        candidateProjectId: 1,
        masterProjectId: 900,
      });
      expect(methodologySelect).toHaveValue("Option 2");
      fireEvent.mouseDown(methodologySelect);
      const options = screen.getAllByRole("option");
      expect(options.length).toBe(2);
      expect(screen.getByRole("option", { name: "Option 1" })).toBeInTheDocument();
      expect(screen.getByRole("option", { name: "Option 2" })).toBeInTheDocument();
    });
  });
});
