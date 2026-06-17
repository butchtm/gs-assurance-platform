import { render, screen } from "@testing-library/react";

import { helperTexts } from "./HelperText";

describe("create project helperTexts", () => {
  it("explains cross linkage in the project classification help text", () => {
    render(helperTexts.projectClassification());

    expect(screen.getByText(/Cross linkage means the VPA's PD account is different/)).toBeInTheDocument();
    expect(screen.getByText(/Regular Case VPA's PD account is different/)).toBeInTheDocument();
  });
});
