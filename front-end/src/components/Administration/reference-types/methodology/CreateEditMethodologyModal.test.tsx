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

    const activeInput = screen.getByRole("checkbox", { name: "Active" });
    fireEvent.click(activeInput);

    fireEvent.click(screen.getByText("Create"));

    await waitFor(() => {
      expect(createMethodology as jest.Mock).toHaveBeenCalledWith({
        description: "This is my description",
        active: true,
        paa: true,
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
      expect(screen.getByRole("checkbox", { name: "Active" })).toHaveProperty("checked", true);
    });

    fireEvent.click(screen.getByText("Edit"));

    await waitFor(() => {
      expect(updateMethodology as jest.Mock).toHaveBeenCalledWith({
        id: 1,
        description: "Methodology1",
        active: true,
        paa: true,
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
      expect(screen.getByRole("checkbox", { name: "Active" })).toHaveProperty("checked", true);
    });

    fireEvent.click(screen.getByTestId("methodology-delete-button"));

    await waitFor(() => {
      expect(screen.getByText("Confirm Delete")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByTestId("confirm-delete-button"));

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

  it("should allow for toggling of the PAA checkbox", async () => {
    render(
      <CreateEditMethodologyModal
        open={true}
        onClose={() => null}
        methodology={{
          ...mockActiveReferenceTypes.Methodology[0],
          inUse: true,
          paa: true,
        }}
      />,
    );

    await waitFor(() => {
      expect(screen.getByLabelText("PAA (Paris Agreement Alignment)")).toHaveProperty("checked", true);
    });

    const paaInput = screen.getByLabelText("PAA (Paris Agreement Alignment)");
    fireEvent.click(paaInput);

    await waitFor(() => {
      expect(screen.getByLabelText("PAA (Paris Agreement Alignment)")).toHaveProperty("checked", false);
    });
  });

  it("should default the PAA checkbox to true when creating a new methodology", async () => {
    render(<CreateEditMethodologyModal open={true} onClose={() => null} />);

    await waitFor(() => {
      expect(screen.getByLabelText("PAA (Paris Agreement Alignment)")).toHaveProperty("checked", true);
    });
  });

  it("should default the PAA checkbox to the methodology value when editing", async () => {
    render(
      <CreateEditMethodologyModal
        open={true}
        onClose={() => null}
        methodology={{
          ...mockActiveReferenceTypes.Methodology[0],
          inUse: true,
          paa: false,
        }}
      />,
    );

    await waitFor(() => {
      expect(screen.getByLabelText("PAA (Paris Agreement Alignment)")).toHaveProperty("checked", false);
    });
  });

  it("should prevent submission if description is removed on edit", async () => {
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
    });

    const descriptionInput = screen.getByLabelText("Description *");
    fireEvent.change(descriptionInput, { target: { value: "" } });

    await waitFor(() => {
      expect(screen.getByText("Edit")).toBeDisabled();
    });
  });
});
