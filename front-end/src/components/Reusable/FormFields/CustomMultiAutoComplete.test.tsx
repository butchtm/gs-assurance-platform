import { render, screen } from "@testing-library/react";
import { Formik } from "formik";

import { CustomMultiAutoComplete } from "./CustomMultiAutoComplete";

const mockOptions = [
  { value: 1, label: "Option 1" },
  { value: 2, label: "Option 2" },
  { value: 3, label: "Option 3" },
];

describe("CustomMultiAutoComplete Component", () => {
  it("renders correctly with label", () => {
    render(
      <Formik initialValues={{ multiField: [] }} onSubmit={jest.fn()}>
        <CustomMultiAutoComplete name="multiField" label="Test Multi Select" options={mockOptions} />
      </Formik>,
    );

    expect(screen.getByTestId("Test Multi Select-input")).toBeInTheDocument();
  });

  it("renders correctly with selected values", () => {
    render(
      <Formik initialValues={{ multiField: [1, 2] }} onSubmit={jest.fn()}>
        <CustomMultiAutoComplete name="multiField" label="Test Multi Select" options={mockOptions} />
      </Formik>,
    );

    expect(screen.getByTestId("Test Multi Select-input")).toBeInTheDocument();
  });

  it("renders with required field indicator", () => {
    render(
      <Formik initialValues={{ multiField: [] }} onSubmit={jest.fn()}>
        <CustomMultiAutoComplete name="multiField" label="Test Multi Select" options={mockOptions} required={true} />
      </Formik>,
    );

    expect(screen.getByTestId("Test Multi Select-input")).toBeInTheDocument();
  });

  it("displays warning icon when warningText is provided", () => {
    render(
      <Formik initialValues={{ multiField: [] }} onSubmit={jest.fn()}>
        <CustomMultiAutoComplete
          name="multiField"
          label="Test Multi Select"
          options={mockOptions}
          warningText="Warning message"
        />
      </Formik>,
    );

    expect(screen.getByTestId("warning-icon")).toBeInTheDocument();
  });
});
