import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { BrowserRouter as Router } from "react-router-dom";

import { mockActiveReferenceTypes } from "../../../common/data-types/DebugReferenceTypes";
import mockProjects, { mockProjectForHierarchy, mockProjectHierarchyTree } from "../../../common/project/DebugProject";
import { ProjectBundleInfo } from "../../../common/project/Project";
import { ProjectTree } from "../Project";

import ViewProjectHierarchyTree from "./ViewProjectHierarchyTree";

jest.mock("../../../common/data-types/DataReferenceTypes", () => ({
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
    allReferenceTypes: mockActiveReferenceTypes,
  }),
  getReferenceValueDescriptionById: jest.fn(),
}));

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => jest.fn(),
}));

const mockBundlesForHierarchy: ProjectBundleInfo[] = [
  {
    id: 300,
    gsid: "GSID3300",
    name: "Bundle No. 300",
    currentRequest: {
      id: 1,
      name: "Request One",
    },
  },
  {
    id: 301,
    gsid: "GSID3301",
    name: "Bundle No. 301",
    currentRequest: {
      id: 2,
      name: "Request Two",
    },
  },
];

const mockBundleProjectHierarchyTree: ProjectTree = {
  gsid: "GS1",
  id: 1,
  name: "Test POA",
  classificationId: 7,
  children: [
    {
      gsid: "GS2",
      id: 2,
      name: "Test Real Case VPA 1",
      classificationId: 8,
      bundle: mockBundlesForHierarchy[1],
    },
    {
      gsid: "GS3",
      id: 3,
      name: "Test Real Case VPA 2",
      classificationId: 8,
      bundle: mockBundlesForHierarchy[0],
      children: [
        {
          gsid: "GS4",
          id: 4,
          name: "Test Regular Case VPA 1",
          bundle: mockBundlesForHierarchy[0],
          classificationId: 9,
        },
        {
          gsid: "GS5",
          id: 5,
          name: "Test Regular Case VPA 2",
          bundle: mockBundlesForHierarchy[0],
          classificationId: 9,
        },
      ],
    },
  ],
};

describe("ViewProjectHierarchyTree", () => {
  window.scrollTo = jest.fn();

  it("renders correctly", async () => {
    render(
      <ViewProjectHierarchyTree
        project={mockProjectForHierarchy}
        returnToProjectInfoCallback={jest.fn()}
        projectTree={mockProjectHierarchyTree}
      />,
    );
    await waitFor(() => {
      expect(screen.getByText("GS1")).toBeInTheDocument();
      expect(screen.getByText("Test POA")).toBeInTheDocument();
    });
  });

  it("filters projects based on search input", async () => {
    render(
      <ViewProjectHierarchyTree
        project={mockProjectForHierarchy}
        returnToProjectInfoCallback={jest.fn()}
        projectTree={mockProjectHierarchyTree}
      />,
    );

    fireEvent.click(screen.getByText("GS1"));

    await waitFor(() => {
      const searchInput = screen.getByTestId("vpa-search");
      fireEvent.change(searchInput, { target: { value: "Test Real Case VPA 1" } });
    });
    await waitFor(() => {
      expect(screen.queryByText("Test Real Case VPA 2")).not.toBeInTheDocument();
      expect(screen.getByText("Test Real Case VPA 1")).toBeInTheDocument();
    });
  });

  it("filters to project children based on search input", async () => {
    render(
      <ViewProjectHierarchyTree
        project={mockProjectForHierarchy}
        returnToProjectInfoCallback={jest.fn()}
        projectTree={mockProjectHierarchyTree}
      />,
    );

    fireEvent.click(screen.getByText("GS1"));

    await waitFor(() => {
      const searchInput = screen.getByTestId("vpa-search");
      fireEvent.change(searchInput, { target: { value: "Test Regular Case VPA 2" } });
    });

    await waitFor(() => {
      expect(screen.getByText("Test Regular Case VPA 2")).toBeInTheDocument();
      expect(screen.queryByText("Test Regular Case VPA 1")).not.toBeInTheDocument();
    });
  });

  it("navigates to project on individual project click", async () => {
    const mockReturnToProjectInfoCallback = jest.fn();

    render(
      <Router>
        <ViewProjectHierarchyTree
          project={mockProjects[2]}
          returnToProjectInfoCallback={mockReturnToProjectInfoCallback}
          projectTree={mockProjectHierarchyTree}
        />
      </Router>,
    );

    const viewButton = screen.getByText("View");
    fireEvent.click(viewButton);

    await waitFor(() => {
      expect(mockReturnToProjectInfoCallback).toHaveBeenCalled();
    });
  });

  it("renders bundle details for projects in bundle", async () => {
    render(
      <ViewProjectHierarchyTree
        project={mockProjectForHierarchy}
        returnToProjectInfoCallback={jest.fn()}
        projectTree={mockBundleProjectHierarchyTree}
      />,
    );

    fireEvent.click(screen.getByText("GS1"));
    await waitFor(() => {
      expect(screen.getByText("Bundle No. 300")).toBeInTheDocument();
      expect(screen.getByText("Bundle No. 301")).toBeInTheDocument();
    });
  });
});
