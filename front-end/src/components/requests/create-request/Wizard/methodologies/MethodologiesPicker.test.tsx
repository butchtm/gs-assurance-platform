import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { Formik } from "formik";

import { MethodologiesPicker } from "./MethodologiesPicker";

const mockMethodology = {
  creditingPeriodId: 1,
  methodologyId: 2,
  methodologyVersion: 1.1,
};

const mockMethodologyOption = {
  value: mockMethodology.methodologyId,
  label: "Mock Methodology 2",
};

describe("MethodologiesPicker", () => {
  it("should allow for selection of a methodology and input of a version", async () => {
    render(
      <Formik initialValues={{ creditingPeriodId: 1 }} onSubmit={jest.fn()}>
        <MethodologiesPicker
          methodologies={[mockMethodology]}
          methodologyOptions={[mockMethodologyOption]}
          allDisabled={false}
          creditingPeriodId={1}
          fieldName={"methodologyConfigurations"}
        />
      </Formik>,
    );
    await waitFor(() => {
      expect(screen.getByTestId("methodology-input-0")).toBeInTheDocument();
      expect(screen.getByTestId("version-input-0")).toBeInTheDocument();
    });
  });
  it("should be able to add a methodology", () => {
    render(
      <Formik
        initialValues={{ creditingPeriodId: 1, methodologyConfigurations: [mockMethodology] }}
        onSubmit={jest.fn()}
      >
        <MethodologiesPicker
          methodologies={[mockMethodology]}
          methodologyOptions={[mockMethodologyOption]}
          allDisabled={false}
          creditingPeriodId={1}
          fieldName={"methodologyConfigurations"}
        />
      </Formik>,
    );
    const add = screen.getByRole("button", { name: "Add Methodology" });
    expect(add).toBeInTheDocument();
    expect(add).not.toBeDisabled();
  });

  it("should be able to remove a methodology", () => {
    render(
      <Formik
        initialValues={{ creditingPeriodId: 1, methodologyConfigurations: [mockMethodology] }}
        onSubmit={jest.fn()}
      >
        <MethodologiesPicker
          methodologies={[mockMethodology]}
          methodologyOptions={[mockMethodologyOption]}
          allDisabled={false}
          creditingPeriodId={1}
          fieldName={"methodologyConfigurations"}
        />
      </Formik>,
    );
    const remove = screen.getByTestId("remove-0");
    expect(remove).toBeInTheDocument();
    expect(remove).not.toBeDisabled();
  });

  it("should disable the adding of methodologies if there are blank values", () => {
    render(
      <Formik initialValues={{ creditingPeriodId: 1, methodologyConfigurations: [] }} onSubmit={jest.fn()}>
        <MethodologiesPicker
          methodologies={[
            {
              methodologyId: "",
              methodologyVersion: 0,
              creditingPeriodId: 1,
            },
          ]}
          methodologyOptions={[
            {
              value: "",
              label: "",
            },
          ]}
          allDisabled={false}
          creditingPeriodId={1}
          fieldName={"methodologyConfigurations"}
        />
      </Formik>,
    );
    const add = screen.getByRole("button", { name: "Add Methodology" });
    expect(add).toBeInTheDocument();
    expect(add).toBeDisabled();
  });

  it("should show warning message for methodology version 0", async () => {
    render(
      <Formik
        initialValues={{
          creditingPeriodId: 1,
          methodologyConfigurations: [
            {
              methodologyId: 1,
              methodologyVersion: 0,
              creditingPeriodId: 1,
            },
          ],
        }}
        onSubmit={jest.fn()}
        validate={() => ({})}
      >
        {({ submitForm }) => (
          <>
            <MethodologiesPicker
              methodologies={[
                {
                  methodologyId: 1,
                  methodologyVersion: 0,
                  creditingPeriodId: 1,
                },
              ]}
              methodologyOptions={[
                {
                  value: 1,
                  label: "Mock Methodology 1",
                },
              ]}
              allDisabled={false}
              creditingPeriodId={1}
              fieldName="methodologyConfigurations"
            />
            <button onClick={submitForm}>Submit</button>
          </>
        )}
      </Formik>,
    );

    fireEvent.click(screen.getByRole("button", { name: "Submit" }));

    expect(
      await screen.findByText("Methodology Version 0 should only be used for unapproved methodologies"),
    ).toBeInTheDocument();
  });

  it("should show combined error message when methodologyId and methodologyVersion are empty", async () => {
    render(
      <Formik
        initialValues={{
          creditingPeriodId: 1,
          methodologyConfigurations: [
            {
              methodologyId: "",
              methodologyVersion: null,
              creditingPeriodId: 1,
            },
          ],
        }}
        validate={(values) => {
          const errs: {
            methodologyConfigurations?: Array<{
              methodologyId?: string;
              methodologyVersion?: string;
            }>;
          } = {};

          errs.methodologyConfigurations = values.methodologyConfigurations.map((cfg) => {
            const e: { methodologyId?: string; methodologyVersion?: string } = {};

            if (!cfg.methodologyId) {
              e.methodologyId = "Required";
            }
            if (cfg.methodologyVersion == null || Number.isNaN(cfg.methodologyVersion)) {
              e.methodologyVersion = "Required";
            }

            return e;
          });

          return errs;
        }}
        onSubmit={jest.fn()}
      >
        {({ submitForm }) => (
          <>
            <MethodologiesPicker
              methodologies={[
                {
                  methodologyId: "",
                  methodologyVersion: null,
                  creditingPeriodId: 1,
                },
              ]}
              methodologyOptions={[
                {
                  value: "",
                  label: "",
                },
              ]}
              allDisabled={false}
              creditingPeriodId={1}
              fieldName="methodologyConfigurations"
            />
            <button onClick={submitForm}>Submit</button>
          </>
        )}
      </Formik>,
    );

    // Submit twice to get submitCount > 1 - simulate the user clicking next twice
    fireEvent.click(screen.getByRole("button", { name: "Submit" }));
    fireEvent.click(screen.getByRole("button", { name: "Submit" }));

    expect(await screen.findByText("Methodology and version number required")).toBeInTheDocument();
  });
});
