import { render, screen } from "@testing-library/react";
import { Formik } from "formik";

import { CustomFormSelect } from "./CustomFormSelect";

const mockOptions = [
  { value: 1, label: "Option 1" },
  { value: 2, label: "Option 2" },
  { value: 3, label: "Option 3" },
];

describe("CustomFormSelect Component", () => {
  it("renders correctly with label and options", () => {
    render(
      <Formik initialValues={{ selectField: "" }} onSubmit={jest.fn()}>
        <CustomFormSelect name="selectField" label="Test Select" options={mockOptions} />
      </Formik>,
    );

    expect(screen.getByTestId("selectField-select")).toBeInTheDocument();
  });

  it("displays error message when touched and has error", () => {
    render(
      <Formik
        initialValues={{ selectField: "" }}
        initialErrors={{ selectField: "Required field" }}
        initialTouched={{ selectField: true }}
        onSubmit={jest.fn()}
      >
        <CustomFormSelect name="selectField" label="Test Select" options={mockOptions} />
      </Formik>,
    );

    expect(screen.getByText("Required field")).toBeInTheDocument();
  });

  it("renders disabled when disabled prop is true", () => {
    render(
      <Formik initialValues={{ selectField: "" }} onSubmit={jest.fn()}>
        <CustomFormSelect name="selectField" label="Test Select" options={mockOptions} disabled={true} />
      </Formik>,
    );

    const select = screen.getByTestId("selectField-select");
    expect(select).toBeInTheDocument();
  });

  it("displays warning icon when warningText is provided", () => {
    render(
      <Formik initialValues={{ selectField: "" }} onSubmit={jest.fn()}>
        <CustomFormSelect name="selectField" label="Test Select" options={mockOptions} warningText="Warning message" />
      </Formik>,
    );

    expect(screen.getByTestId("warning-icon")).toBeInTheDocument();
  });
});
