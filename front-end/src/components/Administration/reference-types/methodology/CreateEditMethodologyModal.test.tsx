import { fireEvent, render, screen, waitFor } from "@testing-library/react";

import { createMethodology, deleteMethodology, updateMethodology } from "../ReferenceTypes";
import { mockActiveReferenceTypes } from "../../../../common/data-types/DebugReferenceTypes";

import { CreateEditMethodologyModal } from "./CreateEditMethodologyModal";

jest.mock("../ReferenceTypes", () => ({
  createMethodology: jest.fn(),
  updateMethodology: jest.fn(),
  deleteMethodology: jest.fn(),
}));

describe("CreateEditMethodologyModal", () => {
  it("should allow for creation of a methodology", async () => {
    render(<CreateEditMethodologyModal open={true} onClose={() => null} />);

    expect(screen.getByText("Create Methodology")).toBeInTheDocument();
    const descriptionInput = screen.getByLabelText("Description *");
    fireEvent.change(descriptionInput, { target: { value: "This is my description" } });

    const activeInput = screen.getByRole("checkbox");
    fireEvent.click(activeInput);

    fireEvent.click(screen.getByText("Create"));

    await waitFor(() => {
      expect(createMethodology as jest.Mock).toHaveBeenCalledWith({
        description: "This is my description",
        active: true,
      });
    });
  });

  it("should prevent submission of a blank methodology", async () => {
    render(<CreateEditMethodologyModal open={true} onClose={() => null} />);

    await waitFor(() => {
      expect(screen.getByLabelText("Description *")).toHaveValue("");
      expect(screen.getByText("Create")).toBeDisabled();
    });
  });

  it("should allow for editing of a methodology", async () => {
    render(
      <CreateEditMethodologyModal
        open={true}
        onClose={() => null}
        methodology={{
          ...mockActiveReferenceTypes.Methodology[0],
          inUse: true,
        }}
      />,
    );

    await waitFor(() => {
      expect(screen.getByLabelText("Description *")).toHaveValue("Methodology1");
      expect(screen.getByRole("checkbox")).toHaveProperty("checked", true);
    });

    fireEvent.click(screen.getByText("Edit"));

    await waitFor(() => {
      expect(updateMethodology as jest.Mock).toHaveBeenCalledWith({
        id: 1,
        description: "Methodology1",
        active: true,
      });
    });
  });

  it("should allow for deletion of unused methodologies", async () => {
    render(
      <CreateEditMethodologyModal
        open={true}
        onClose={() => null}
        methodology={{
          ...mockActiveReferenceTypes.Methodology[0],
          inUse: false,
        }}
      />,
    );

    await waitFor(() => {
      expect(screen.getByLabelText("Description *")).toHaveValue("Methodology1");
      expect(screen.getByRole("checkbox")).toHaveProperty("checked", true);
    });

    fireEvent.click(screen.getByText("Delete"));

    await waitFor(() => {
      expect(screen.getByText("Confirm Delete")).toBeInTheDocument();
    });

    fireEvent.click(screen.getAllByText("Delete")[1]);

    await waitFor(() => {
      expect(deleteMethodology as jest.Mock).toHaveBeenCalledWith(1);
    });
  });

  it("should prevent deletion of methodologies in use", async () => {
    render(
      <CreateEditMethodologyModal
        open={true}
        onClose={() => null}
        methodology={{
          ...mockActiveReferenceTypes.Methodology[0],
          inUse: true,
        }}
      />,
    );
    await waitFor(() => {
      expect(screen.getByText("Delete")).toBeDisabled();
      expect(screen.getByText("Methodologies in use cannot be deleted")).toBeInTheDocument();
    });
  });
});
