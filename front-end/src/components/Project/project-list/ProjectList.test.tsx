import React from "react";
import { screen } from "@testing-library/react";

import { mockActiveReferenceTypes } from "@/common/data-types/DebugReferenceTypes";
import { Project } from "@/common/project/Project";
import { renderWithProviders } from "@/test/test-utils";

import { getProjectPage } from "../Project";

import { ProjectList } from "./ProjectList";

type ProjectListColumn = {
  header: string;
  accessorFn?: (row: Project) => React.ReactNode;
  accessorKey?: keyof Project;
};

jest.mock("react-helmet-async", () => ({
  Helmet: ({ children }: { children: React.ReactNode }) => <>{children}</>,
}));

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => jest.fn(),
}));

jest.mock("../Project", () => ({
  getProjectPage: jest.fn(),
}));

jest.mock("../../../common/filterable-sortable-table/FilterableSortableTable", () => ({
  FilterableSortableTable: ({ columns, data }: { columns: ProjectListColumn[]; data: Project[] }) => (
    <>
      {columns.map((column) => (
        <span key={column.header}>{column.header}</span>
      ))}
      {data.flatMap((row) =>
        columns.map((column) => (
          <span key={`${row.id}-${column.header}`}>
            {column.accessorFn?.(row) ?? (column.accessorKey ? String(row[column.accessorKey] ?? "") : "")}
          </span>
        )),
      )}
    </>
  ),
}));

jest.mock("./CreateProjectButton", () => ({
  CreateProjectButton: () => <button>Create Project</button>,
}));

const crossLinkProject = {
  id: 910106,
  gsid: "GS910106",
  name: "GS910102-Demo-Real-Case-VPA",
  projectClassificationId: 10,
  projectStatus: { id: 1, code: "NEW", description: "New" },
  countries: [],
  projectTypes: [],
} as unknown as Project;

const referenceOverrides = {
  activeReferenceTypes: mockActiveReferenceTypes,
  allReferenceTypes: {
    ...mockActiveReferenceTypes,
    ProjectClassification: [
      ...mockActiveReferenceTypes.ProjectClassification,
      {
        id: 10,
        code: "REAL_CASE_VPA_CROSS_LINKAGE",
        description: "Real Case VPA (cross linkage)",
        active: false,
      },
    ],
  },
};

describe("ProjectList", () => {
  beforeEach(() => {
    (getProjectPage as jest.Mock).mockResolvedValue({
      content: [crossLinkProject],
      totalElements: 1,
      totalPages: 1,
      currentPage: 1,
    });
  });

  it.each([
    { role: "Admin", isAdminUser: true, isArmUser: false },
    { role: "ARM", isAdminUser: false, isArmUser: true },
  ])("shows raw cross-link project classification labels to $role users", async ({ isAdminUser, isArmUser }) => {
    renderWithProviders(<ProjectList />, {
      providerOverrides: {
        auth: {
          isAdminUser: jest.fn().mockReturnValue(isAdminUser),
          isArmUser: jest.fn().mockReturnValue(isArmUser),
          isProjectDeveloperUser: jest.fn().mockReturnValue(false),
        },
        reference: referenceOverrides,
      },
    });

    await screen.findByText("GS910106");

    expect(screen.getByText("Project Classification")).toBeInTheDocument();
    expect(screen.getByText("Real Case VPA (cross linkage)")).toBeInTheDocument();
  });

  it("keeps Project Classification hidden for Project Developer users", async () => {
    renderWithProviders(<ProjectList />, {
      providerOverrides: {
        auth: {
          isAdminUser: jest.fn().mockReturnValue(false),
          isArmUser: jest.fn().mockReturnValue(false),
          isProjectDeveloperUser: jest.fn().mockReturnValue(true),
        },
        reference: referenceOverrides,
      },
    });

    await screen.findByText("GS910106");

    expect(screen.queryByText("Project Classification")).not.toBeInTheDocument();
    expect(screen.queryByText("Real Case VPA (cross linkage)")).not.toBeInTheDocument();
  });
});
