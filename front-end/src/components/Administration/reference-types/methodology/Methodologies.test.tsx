import { fireEvent, render, screen, waitFor, act } from "@testing-library/react";

import { getMethodologies } from "../ReferenceTypes";
import { mockActiveReferenceTypes } from "../../../../common/data-types/DebugReferenceTypes";

import { Methodologies } from "./Methodologies";

jest.mock("../ReferenceTypes", () => ({
  getMethodologies: jest.fn(),
}));

const mockMethodologyResponse = {
  totalElements: 3,
  content: [
    {
      ...mockActiveReferenceTypes.Methodology[0],
      inUse: true,
    },
    {
      ...mockActiveReferenceTypes.Methodology[1],
      inUse: true,
      paa: false,
    },
    {
      ...mockActiveReferenceTypes.Methodology[2],
      active: false,
      inUse: false,
    },
  ],
};

describe("Methodologies", () => {
  beforeEach(() => {
    (getMethodologies as jest.Mock).mockResolvedValue(mockMethodologyResponse);
  });
  it("should display a list of methodologies", async () => {
    render(<Methodologies />);
    await waitFor(() => {
      expect(screen.getByText("Description")).toBeInTheDocument();
      expect(screen.getByText("Methodology1")).toBeInTheDocument();
      expect(screen.getByText("Methodology2")).toBeInTheDocument();
      expect(screen.getByText("Methodology3")).toBeInTheDocument();

      expect(screen.getAllByText("Active").length).toBe(3);
      expect(screen.getByText("Inactive")).toBeInTheDocument();

      expect(screen.getByText("Actions")).toBeInTheDocument();
    });
  });

  it("should provide the ability to create a methodology", async () => {
    render(<Methodologies />);

    fireEvent.click(screen.getByText("Create"));

    await waitFor(() => {
      expect(screen.getByText("Create Methodology")).toBeInTheDocument();
    });
  });

  it("should provide the ability to edit a methodology", async () => {
    render(<Methodologies />);

    await waitFor(() => {
      expect(screen.getByText("Description")).toBeInTheDocument();
      expect(screen.getByText("Methodology1")).toBeInTheDocument();
    });

    fireEvent.click(screen.getAllByTestId("Edit")[0]);

    await waitFor(() => {
      expect(screen.getByText("Edit Methodology")).toBeInTheDocument();
    });
  });

  it("should display a PAA tag for methodologies that are PAA", async () => {
    render(<Methodologies />);

    await waitFor(() => {
      expect(screen.getByText("Methodology1")).toBeInTheDocument();
      expect(screen.getByText("Methodology2")).toBeInTheDocument();
      expect(screen.getByText("Methodology3")).toBeInTheDocument();

      expect(screen.getAllByTestId("paa-tag").length).toBe(2);
    });
  });

  it("should filter methodologies by PAA", async () => {
    render(<Methodologies />);

    await waitFor(() => {
      expect(screen.getByText("Methodology1")).toBeInTheDocument();
      expect(screen.getByText("Methodology2")).toBeInTheDocument();
      expect(screen.getByText("Methodology3")).toBeInTheDocument();
    });

    act(() => {
      fireEvent.click(screen.getByRole("button", { name: "PAA" }));
    });

    const checkbox = screen.getByRole("checkbox", { name: "Filter by PAA" });

    act(() => {
      fireEvent.click(checkbox);
    });

    expect(checkbox).toBeChecked();

    await waitFor(() => {
      expect(getMethodologies).toHaveBeenCalledWith(
        "",
        1,
        25,
        true, // filterByPAA
      );
    });
  });
});
