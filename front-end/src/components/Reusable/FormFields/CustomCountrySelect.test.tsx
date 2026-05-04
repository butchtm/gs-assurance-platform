import { render, screen } from "@testing-library/react";
import { Formik } from "formik";

import { CustomCountrySelect } from "./CustomCountrySelect";

jest.mock("../../../common/data-types/DataReferenceTypes", () => ({
  useReferenceTypes: () => ({
    activeReferenceTypes: {
      ProjectLocation: [
        { id: 1, description: "United States", code: "US" },
        { id: 2, description: "Canada", code: "CA" },
        { id: 3, description: "United Kingdom", code: "GB" },
      ],
    },
  }),
}));

describe("CustomCountrySelect Component", () => {
  it("renders correctly with label", () => {
    render(
      <Formik initialValues={{ countries: [] }} onSubmit={jest.fn()}>
        <CustomCountrySelect name="countries" label="Select Countries" />
      </Formik>,
    );

    expect(screen.getByLabelText("Select Countries")).toBeInTheDocument();
  });

  it("renders with empty initial value", () => {
    render(
      <Formik initialValues={{ countries: [] }} onSubmit={jest.fn()}>
        <CustomCountrySelect name="countries" label="Select Countries" />
      </Formik>,
    );

    const input = screen.getByTestId("countries-input") as HTMLInputElement;
    expect(input.value).toBe("");
  });

  it("displays countries with flags", () => {
    render(
      <Formik initialValues={{ countries: [] }} onSubmit={jest.fn()}>
        <CustomCountrySelect name="countries" label="Select Countries" />
      </Formik>,
    );

    expect(screen.getByLabelText("Select Countries")).toBeInTheDocument();
  });

  it("displays warning icon when warningText is provided", () => {
    render(
      <Formik initialValues={{ countries: [] }} onSubmit={jest.fn()}>
        <CustomCountrySelect name="countries" label="Select Countries" warningText="Warning message" />
      </Formik>,
    );

    expect(screen.getByTestId("warning-icon")).toBeInTheDocument();
  });
});
