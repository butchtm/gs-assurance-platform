import { fireEvent, render, screen, waitFor } from "@testing-library/react";

import { mockBundles } from "../../../common/project/DebugProject";
import { getProjectBundles } from "../Project";

import ViewProjectBundleModal from "./ViewProjectBundleModal";

jest.mock("../Project", () => ({
  getProjectBundles: jest.fn(),
}));

describe("ViewProjectBundleModal", () => {
  beforeEach(() => {
    (getProjectBundles as jest.Mock).mockResolvedValue({
      content: mockBundles,
      totalPages: 1,
    });
  });

  it("renders the modal when isOpen is true", async () => {
    render(<ViewProjectBundleModal projectBundleId={1} bundleName="test" isOpen={true} onClose={jest.fn()} />);

    await waitFor(() => {
      expect(screen.getByText("GSID001")).toBeInTheDocument();
      expect(screen.getByText("Project One")).toBeInTheDocument();
      expect(screen.getByText("Request One")).toBeInTheDocument();
    });
  });

  it("calls onClose when the modal is closed", async () => {
    const handleClose = jest.fn();
    render(<ViewProjectBundleModal projectBundleId={1} bundleName="test" isOpen={true} onClose={handleClose} />);

    fireEvent.click(screen.getByTestId("close-btn"));
    await waitFor(() => {
      expect(handleClose).toHaveBeenCalled();
    });
  });
});
