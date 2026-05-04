import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { HelmetProvider } from "react-helmet-async";

import {
  mockMultiRequestProjectInConsultation,
  mockSingleBatchedRequestProjectInConsultation,
  mockSingleRequestProjectInConsultation,
} from "../../../../common/project/DebugProject";
import * as consultationAPI from "../PublicConsultation";

import PublicConsultationList from "./PublicConsultationList";

jest.mock("../PublicConsultation", () => ({
  getPublicConsultation: jest.fn(),
}));

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => jest.fn(),
}));

describe("PublicConsultationList", () => {
  beforeEach(() => {
    (consultationAPI.getPublicConsultation as jest.Mock).mockResolvedValue({
      data: [
        mockMultiRequestProjectInConsultation,
        mockSingleRequestProjectInConsultation,
        mockSingleBatchedRequestProjectInConsultation,
      ],
      totalPages: 1,
    });
  });

  it("fetches projects and updates state on render", async () => {
    const fetchSpy = jest.spyOn(consultationAPI, "getPublicConsultation");
    render(
      <HelmetProvider>
        <PublicConsultationList />
      </HelmetProvider>,
    );
    await waitFor(() => {
      expect(fetchSpy).toHaveBeenCalled();
      expect(screen.getByText("Test Multi Request Project")).toBeInTheDocument();
      expect(screen.getByText("Test Single Request Project")).toBeInTheDocument();
      expect(screen.getByText("Test Batched Request Project")).toBeInTheDocument();
    });
  });

  it("navigates to project info page on button click", async () => {
    render(
      <HelmetProvider>
        <PublicConsultationList />
      </HelmetProvider>,
    );
    await waitFor(() => {
      expect(screen.getByText("Test Multi Request Project")).toBeInTheDocument();
    });
  });

  it("displays all expandable rows as expanded by default", async () => {
    render(
      <HelmetProvider>
        <PublicConsultationList />
      </HelmetProvider>,
    );

    await waitFor(() => {
      expect(screen.getByText("Test Multi Request Project")).toBeInTheDocument();
      expect(screen.getByText("Test Multi Request 1")).toBeInTheDocument();
      expect(screen.getByText("Test Multi Request 2")).toBeInTheDocument();
      expect(screen.getByText("Test Multi Request 3")).toBeInTheDocument();
    });
  });

  it("can collapse all rows", async () => {
    render(
      <HelmetProvider>
        <PublicConsultationList />
      </HelmetProvider>,
    );

    await waitFor(() => {
      expect(screen.getByText("Test Multi Request Project")).toBeInTheDocument();
      expect(screen.getByText("Test Multi Request 1")).toBeInTheDocument();
      expect(screen.getByText("Test Multi Request 2")).toBeInTheDocument();
      expect(screen.getByText("Test Multi Request 3")).toBeInTheDocument();
    });

    await waitFor(() => {
      fireEvent.click(screen.getByTestId("consultation-expand-all-row"));
    });

    await waitFor(() => {
      expect(screen.getByText("Test Multi Request Project")).toBeInTheDocument();
      expect(screen.queryByText("Test Multi Request 1")).not.toBeInTheDocument();
      expect(screen.queryByText("Test Multi Request 2")).not.toBeInTheDocument();
      expect(screen.queryByText("Test Multi Request 3")).not.toBeInTheDocument();
    });
  });

  it("can collapse each project individually", async () => {
    render(
      <HelmetProvider>
        <PublicConsultationList />
      </HelmetProvider>,
    );

    await waitFor(() => {
      expect(screen.getByText("Test Multi Request Project")).toBeInTheDocument();
      expect(screen.getByText("Test Multi Request 1")).toBeInTheDocument();
      expect(screen.getByText("Test Multi Request 2")).toBeInTheDocument();
      expect(screen.getByText("Test Multi Request 3")).toBeInTheDocument();
    });

    await waitFor(() => {
      fireEvent.click(screen.getByTestId("consultation-expand-row"));
    });

    await waitFor(() => {
      expect(screen.getByText("Test Multi Request Project")).toBeInTheDocument();
      expect(screen.queryByText("Test Multi Request 1")).not.toBeInTheDocument();
      expect(screen.queryByText("Test Multi Request 2")).not.toBeInTheDocument();
      expect(screen.queryByText("Test Multi Request 3")).not.toBeInTheDocument();
    });
  });

  it("displays all information of projects with one request", async () => {
    render(
      <HelmetProvider>
        <PublicConsultationList />
      </HelmetProvider>,
    );

    // Collapse all expandable rows to only show single requests
    await waitFor(() => {
      fireEvent.click(screen.getByTestId("consultation-expand-all-row"));
    });

    await waitFor(() => {
      expect(screen.getByText("Test Single Request Project")).toBeInTheDocument();
      expect(screen.getByText("Test Single Request")).toBeInTheDocument();
    });
  });

  it("displays and correctly formats all country fields", async () => {
    render(
      <HelmetProvider>
        <PublicConsultationList />
      </HelmetProvider>,
    );

    await waitFor(() => {
      expect(screen.getByText("Andorra, Bolivia")).toBeInTheDocument();
      expect(screen.getByText("Colombia")).toBeInTheDocument();
      expect(screen.getByText("UK, USA")).toBeInTheDocument();
    });
  });

  it("displays request methodologies if present OR methodologies from crediting period on request if not present, against request rows for a project with multiple requests", async () => {
    render(
      <HelmetProvider>
        <PublicConsultationList />
      </HelmetProvider>,
    );

    await waitFor(() => {
      expect(screen.getAllByText("Crediting Period Methodology 1")).toHaveLength(2);
      expect(screen.getByText("Request Methodology 1")).toBeInTheDocument();
      expect(screen.getByText("Request Methodology 2")).toBeInTheDocument();
    });
  });

  it("displays request methodologies if present, for a project with a single request", async () => {
    render(
      <HelmetProvider>
        <PublicConsultationList />
      </HelmetProvider>,
    );

    await waitFor(() => {
      expect(screen.getByText("Request Methodology 3")).toBeInTheDocument();
    });
  });

  it("displays methodologies from crediting period on request if request methodologies not present, for a project with a single request", async () => {
    render(
      <HelmetProvider>
        <PublicConsultationList />
      </HelmetProvider>,
    );

    await waitFor(() => {
      expect(screen.getByText("Crediting Period Methodology 2")).toBeInTheDocument();
      expect(screen.getByText("Crediting Period Methodology 3")).toBeInTheDocument();
      expect(screen.getByText("Crediting Period Methodology 4")).toBeInTheDocument();
    });
  });

  it("displays correctly whether a request is batched", async () => {
    render(
      <HelmetProvider>
        <PublicConsultationList />
      </HelmetProvider>,
    );

    await waitFor(() => {
      expect(screen.getByText("Test Batched Request Project")).toBeInTheDocument();
      expect(screen.getByText("Test Request Batch")).toBeInTheDocument();
      expect(screen.getByText("Yes")).toBeInTheDocument();
    });
  });
});
