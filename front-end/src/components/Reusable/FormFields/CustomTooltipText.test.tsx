import { fireEvent, render, screen } from "@testing-library/react";

import CustomTooltipText, { CustomTooltip } from "./CustomTooltipText";

describe("CustomTooltipText Component", () => {
  it("renders help icon when type is HELP", () => {
    render(<CustomTooltipText text="Help text" type={CustomTooltip.HELP} />);

    expect(screen.getByTestId("help-icon")).toBeInTheDocument();
  });

  it("renders warning icon when type is WARNING", () => {
    render(<CustomTooltipText text="Warning text" type={CustomTooltip.WARNING} />);

    expect(screen.getByTestId("warning-icon")).toBeInTheDocument();
  });

  it("displays popover with text when icon is clicked", () => {
    render(<CustomTooltipText text="This is helpful information" type={CustomTooltip.HELP} />);

    const helpIcon = screen.getByTestId("help-icon");
    fireEvent.click(helpIcon);

    expect(screen.getByText("This is helpful information")).toBeInTheDocument();
  });
});
