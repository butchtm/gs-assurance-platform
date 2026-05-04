import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { render, screen } from "@testing-library/react";
import { Formik } from "formik";

import { StepUpdateFields } from "./StepUpdateFields";

const mockSetFieldValue = jest.fn();
jest.mock("formik", () => ({
  ...jest.requireActual("formik"),
  useFormikContext: jest.fn(() => ({
    values: {
      designCertificationDate: null,
      certifiedProjectDate: null,
      lastDesignCertificationRenewalDate: null,
      lastPerformanceReviewDate: null,
    },
    setFieldValue: mockSetFieldValue,
  })),
}));

describe("StepExtraFields", () => {
  it("renders date fields correctly when project status is provided", () => {
    render(
      <Formik initialValues={{}} onSubmit={jest.fn()}>
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <StepUpdateFields
            projectStatus={{ id: 4, code: "CERTIFIED_PROJECT", description: "Certified Project" }}
            migratedProject={false}
            displayNotRequestedStatusRequests={false}
          />
        </LocalizationProvider>
      </Formik>,
    );
    expect(screen.getByLabelText("Design Certification Date")).toBeInTheDocument();
    expect(screen.getByLabelText("Certified Project Date")).toBeInTheDocument();
    expect(screen.getByLabelText("Last Design Certificate Renewal Date")).toBeInTheDocument();
    expect(screen.getByLabelText("Last Performance Review Date")).toBeInTheDocument();
    expect(screen.getByLabelText("Listing Date")).toBeInTheDocument();
  });
});
