import { render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";

import { mockTransitionDetail } from "../../../common/project/DebugProject";

import TransitioningProjectInformation from "./TransitioningProjectInformation";

describe(TransitioningProjectInformation, () => {
  it("renders all of the fields of the transition detail", async () => {
    render(
      <MemoryRouter>
        <TransitioningProjectInformation transitionDetail={mockTransitionDetail} />
      </MemoryRouter>,
    );

    await waitFor(() => {
      expect(screen.getByText("Transition Scheme")).toBeInTheDocument();
      expect(screen.getByText("TRANSITION FROM OTHER SCHEME")).toBeInTheDocument();
      expect(screen.getByText("Mock Other Scheme")).toBeInTheDocument();
      expect(screen.getByText("TRANSITION PATHWAY")).toBeInTheDocument();
      expect(screen.getByText("Mock Pathway")).toBeInTheDocument();
      expect(screen.getByText("CREDITING PERIOD OTHER SCHEME")).toBeInTheDocument();
      expect(screen.getByText("18/09/2024 - 19/09/2024")).toBeInTheDocument();
      expect(screen.getByText("TRANSITION APPROVAL PROCEDURE")).toBeInTheDocument();
      expect(screen.getByText("Mock Approval Procedure")).toBeInTheDocument();
      expect(screen.getByText("PROJECT LINK IN OTHER SCHEME")).toBeInTheDocument();
      expect(screen.getByText("test.com")).toBeInTheDocument();
    });
  });
});
