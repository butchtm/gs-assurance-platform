import { render, waitFor, screen, fireEvent } from "@testing-library/react";

import AddPublicConsultationComment from "./AddPublicConsultationComment";

describe("AddPublicConsultationComment", () => {
  it("should open modal for adding a comment", async () => {
    render(<AddPublicConsultationComment requestId={1}></AddPublicConsultationComment>);
    await waitFor(() => {
      fireEvent.click(screen.getByText("Add Comment"));
      expect(screen.getByTestId("modal-close-icon")).toBeInTheDocument();
    });
  });
});
