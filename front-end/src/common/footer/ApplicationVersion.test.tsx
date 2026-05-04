jest.mock("../env/env", () => ({
  getGitTag: jest.fn(),
  getEnvironment: jest.fn(),
}));

import { render, screen, waitFor } from "@testing-library/react";

import { apiClient } from "../../components/APIClient/Client";
import { getEnvironment, getGitTag } from "../env/env";

import { ApplicationVersions } from "./ApplicationVersions";

jest.mock("../../components/APIClient/Client", () => ({
  apiClient: {
    makeRequest: jest.fn(),
  },
}));

describe("ApplicationVersions", () => {
  beforeEach(() => {
    (apiClient.makeRequest as jest.Mock).mockResolvedValue({
      git: {
        commit: {
          id: {
            describe: "1.12.0",
          },
        },
      },
    });
  });

  it("should display not available if no version found", async () => {
    (getGitTag as jest.Mock).mockReturnValue(undefined);
    (getEnvironment as jest.Mock).mockReturnValue("jest-test");

    render(<ApplicationVersions isOpen={true} onClose={() => ""} />);

    await waitFor(() => {
      expect(screen.getByText("Not available")).toBeInTheDocument();
      expect(screen.getByText("1.12.0")).toBeInTheDocument();
    });
  });

  it("should display not available if no environment found", async () => {
    (getGitTag as jest.Mock).mockReturnValue("1.20.0");
    (getEnvironment as jest.Mock).mockReturnValue(undefined);

    render(<ApplicationVersions isOpen={true} onClose={() => ""} />);

    await waitFor(() => {
      expect(screen.getByText("Not available")).toBeInTheDocument();
      expect(screen.getByText("1.12.0")).toBeInTheDocument();
    });
  });

  it("should display the versions", async () => {
    (getGitTag as jest.Mock).mockReturnValue("1.20.0");

    render(<ApplicationVersions isOpen={true} onClose={() => ""} />);
    await waitFor(() => {
      expect(screen.getByText("1.20.0")).toBeInTheDocument();
      expect(screen.getByText("1.12.0")).toBeInTheDocument();
    });
  });

  it("should fetch the back-end version from the API", async () => {
    (getGitTag as jest.Mock).mockReturnValue("1.20.0");

    render(<ApplicationVersions isOpen={true} onClose={() => ""} />);
    await waitFor(() => {
      expect(apiClient.makeRequest).toHaveBeenCalledWith("/actuator/info", "GET");
    });
  });
});
