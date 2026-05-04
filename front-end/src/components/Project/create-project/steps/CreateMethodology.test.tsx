import { render, screen, waitFor } from "@testing-library/react";

import { mockActiveReferenceTypes } from "../../../../common/data-types/DebugReferenceTypes";
import { Methodology } from "../../../../common/project/Project";
import { FormikStepper } from "../../../Reusable/Formik/FormikStepper";

import CreateMethodologies from "./CreateMethodology";

const mockMethodologies = [
  {
    ...mockActiveReferenceTypes.Methodology[0],
    id: 1,
    version: 1.1,
  },
  {
    ...mockActiveReferenceTypes.Methodology[1],
    id: 2,
    version: 2.2,
  },
];

const mockMethodologyOptions = mockActiveReferenceTypes.Methodology.map((x) => ({ value: x.id, label: x.description }));

const renderComponent = (methodologies?: Methodology[]) => {
  render(
    <FormikStepper
      initialValues={{
        creditingPeriods: [
          {
            methodologies: methodologies ?? [],
          },
        ],
      }}
      onSubmit={jest.fn()}
    >
      <CreateMethodologies
        currentMethodologies={methodologies ?? []}
        creditingPeriodIndex={0}
        methodologyOptions={mockMethodologyOptions}
      />
    </FormikStepper>,
  );
};

describe("CreateMethodology", () => {
  it("should display selected methodologies and versions", async () => {
    renderComponent(mockMethodologies);
    await waitFor(() => {
      expect(screen.getByTestId("methodology-input-0")).toHaveValue("Methodology1");
      expect(screen.getByTestId("version-input-0")).toHaveValue(1.1);
      expect(screen.getByTestId("methodology-input-1")).toHaveValue("Methodology2");
      expect(screen.getByTestId("version-input-1")).toHaveValue(2.2);
    });
  });

  it("should allow for addition of methodologies and versions", () => {
    renderComponent(mockMethodologies);
    const button = screen.getByRole("button", { name: /Add Methodology/i });
    expect(button).toBeInTheDocument();
    expect(button).not.toBeDisabled();
  });

  it("should require at least one methodology", async () => {
    renderComponent();
    await waitFor(() => {
      expect(screen.queryByTestId("methodology-input-0")).not.toBeInTheDocument();
      expect(screen.queryByTestId("version-input-0")).not.toBeInTheDocument();
      expect(screen.getByText("Please add a methodology")).toBeInTheDocument();
    });
  });
});
