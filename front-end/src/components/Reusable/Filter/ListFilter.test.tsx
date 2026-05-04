import { fireEvent, render, screen, waitFor } from "@testing-library/react";

import { ReferenceType } from "../../../common/project/Project";

import { ListFilter } from "./ListFilter";

const mockFilterOptions: ReferenceType[] = [
  { id: 1, description: "Open", code: "open" },
  { id: 2, description: "Closed", code: "closed" },
];

describe("ListFilter Component", () => {
  it("renders Filter button and opens popover on click", async () => {
    render(
      <ListFilter
        fieldName="projectStatusId"
        options={mockFilterOptions}
        label="Project Status"
        onFilterChange={jest.fn()}
      />,
    );

    const filterButton = screen.getByRole("button");
    expect(filterButton).toBeInTheDocument();

    fireEvent.click(filterButton);

    await waitFor(() => {
      expect(screen.getByText("Project Status")).toBeInTheDocument();
    });
  });

  it("calls onFilterChange with correct arguments when a filter option is selected", () => {
    const mockOnFilterChange = jest.fn();
    render(
      <ListFilter
        fieldName="projectStatusId"
        options={mockFilterOptions}
        label="Project Status"
        onFilterChange={mockOnFilterChange}
      />,
    );

    fireEvent.click(screen.getByRole("button"));

    const radioButton = screen.getByLabelText("Open");
    fireEvent.click(radioButton);

    expect(mockOnFilterChange).toHaveBeenCalledWith({ projectStatusId: 1 });
  });

  it("does not render when given no options", () => {
    render(<ListFilter fieldName="projectStatusId" options={[]} label="Project Status" onFilterChange={jest.fn()} />);

    const filterButton = screen.queryByRole("button");
    expect(filterButton).toBeNull();
  });

  it("handles multiple filter changes correctly", () => {
    const mockOnFilterChange = jest.fn();
    render(
      <ListFilter
        fieldName="projectStatusId"
        options={mockFilterOptions}
        label="Project Status"
        onFilterChange={mockOnFilterChange}
      />,
    );

    fireEvent.click(screen.getByRole("button"));

    const firstOption = screen.getByLabelText("Open");
    fireEvent.click(firstOption);
    const secondOption = screen.getByLabelText("Closed");
    fireEvent.click(secondOption);

    expect(mockOnFilterChange).toHaveBeenCalledWith({ projectStatusId: 2 });
  });
});
