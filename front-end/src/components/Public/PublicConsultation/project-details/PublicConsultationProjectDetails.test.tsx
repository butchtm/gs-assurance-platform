import { render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";

import { mockProjectInConsultation } from "../../../../common/project/DebugProject";

import PublicConsultationProjectDetails from "./PublicConsultationProjectDetails";

describe("PublicConsultationProjectDetails", () => {
  it("correctly displays the project details", async () => {
    render(
      <MemoryRouter>
        <PublicConsultationProjectDetails project={mockProjectInConsultation} />
      </MemoryRouter>,
    );
    await waitFor(() => {
      expect(screen.getByText("Project Characteristics")).toBeInTheDocument();
      expect(screen.getByText("Project Type")).toBeInTheDocument();
      expect(screen.getByText("TypeDescription1")).toBeInTheDocument();

      expect(screen.getByText("Project Impact")).toBeInTheDocument();
      expect(screen.getByText("Carbon Stream")).toBeInTheDocument();
      expect(screen.getByText("Carbon 1")).toBeInTheDocument();
      expect(screen.getByText("Estimated Annual Emission Reduction/Removal")).toBeInTheDocument();
      expect(screen.getByText("100,000")).toBeInTheDocument();

      expect(screen.getByText("Certification Milestones")).toBeInTheDocument();
      expect(screen.getByText("Project Start Date")).toBeInTheDocument();
      expect(screen.getByText("01/08/2024")).toBeInTheDocument();
      expect(screen.getByText("Design Certification Date")).toBeInTheDocument();
      expect(screen.getByText("02/08/2024")).toBeInTheDocument();
    });
  });
});
