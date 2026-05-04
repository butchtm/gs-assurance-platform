import { render, screen } from "@testing-library/react";

import { mockRequestConsultations } from "../../../common/request/DebugRequests";

import { RequestCommentsListConsultationInfo } from "./RequestCommentsListConsultationInfo";

describe("RequestCommentsListConsultationInfo", () => {
  afterEach(() => {
    jest.clearAllMocks();
  });

  it("should not render consultations if undefined", () => {
    render(<RequestCommentsListConsultationInfo consultations={undefined} />);

    expect(screen.queryByText("Public Consultation")).not.toBeInTheDocument();
    expect(screen.queryByText("Stakeholder Consultation")).not.toBeInTheDocument();
  });

  it("should not render consultations if empty", () => {
    render(<RequestCommentsListConsultationInfo consultations={[]} />);

    expect(screen.queryByText("Public Consultation")).not.toBeInTheDocument();
    expect(screen.queryByText("Stakeholder Consultation")).not.toBeInTheDocument();
  });

  it("renders public consultation correctly", () => {
    render(<RequestCommentsListConsultationInfo consultations={mockRequestConsultations} />);

    const element = screen.getByTestId("comment-public-consultation-dates");
    expect(element.textContent).toBe("Public Consultation - 24/04/2025 to 08/05/2025");
  });
});
