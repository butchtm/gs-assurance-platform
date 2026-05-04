import { render, screen } from "@testing-library/react";
import { Formik } from "formik";

import { CustomAutoComplete } from "./CustomAutoComplete";

const mockOptions = [
  { value: 1, label: "Option 1" },
  { value: 2, label: "Option 2" },
  { value: 3, label: "Option 3" },
];

describe("CustomAutoComplete Component", () => {
  it("renders correctly with label", () => {
    render(
      <Formik initialValues={{ testField: null }} onSubmit={jest.fn()}>
        <CustomAutoComplete name="testField" label="Test Field" options={mockOptions} />
      </Formik>,
    );

    expect(screen.getByTestId("Test Field-select")).toBeInTheDocument();
  });

  it("displays error message when touched and has error", async () => {
    render(
      <Formik
        initialValues={{ testField: null }}
        initialErrors={{ testField: "Required field" }}
        initialTouched={{ testField: true }}
        onSubmit={jest.fn()}
      >
        <CustomAutoComplete name="testField" label="Test Field" options={mockOptions} />
      </Formik>,
    );

    expect(await screen.findByText("Required field")).toBeInTheDocument();
  });

  it("renders with required field indicator", () => {
    render(
      <Formik initialValues={{ testField: null }} onSubmit={jest.fn()}>
        <CustomAutoComplete name="testField" label="Test Field" options={mockOptions} required={true} />
      </Formik>,
    );

    expect(screen.getByTestId("Test Field-select")).toBeInTheDocument();
  });

  it("displays warning icon when warningText is provided", () => {
    render(
      <Formik initialValues={{ testField: null }} onSubmit={jest.fn()}>
        <CustomAutoComplete name="testField" label="Test Field" options={mockOptions} warningText="Warning message" />
      </Formik>,
    );

    expect(screen.getByTestId("warning-icon")).toBeInTheDocument();
  });
});
