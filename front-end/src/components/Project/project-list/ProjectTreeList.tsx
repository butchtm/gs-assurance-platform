import { FormControlLabel, Switch } from "@mui/material";
import React, { useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

import { ReferenceType, useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";
import { BoldTextCell } from "../../../common/filterable-sortable-table/cell/BoldTextCell";
import { FilterableSortableTable } from "../../../common/filterable-sortable-table/FilterableSortableTable";
import { Filter } from "../../../common/filterable-sortable-table/model/Filter";
import { Project } from "../../../common/project/Project";
import DesignCertifiedSVG from "../../../img/design-certified.svg";
import CertifiedSVG from "../../../img/gs-certified.svg";
import ListedSVG from "../../../img/listed.svg";
import { apiClient } from "../../APIClient/Client";
import { AuthContext } from "../../Auth/Login/AuthProvider";
import { PaginationState } from "../../Reusable/Pagination";

import { CreateProjectButton } from "./CreateProjectButton";
import { MultilineReferenceTypesCell } from "./MultilineReferenceTypesCell";

interface ProjectTree extends Project {
  children: ProjectTree[];
}

function getProjectTreePage(
  page: number,
  filters?: Record<string, string | number | number[] | null> | null,
  pageSize?: number,
): Promise<{
  content: ProjectTree[];
  currentPage: number;
  totalPages: number;
  totalElements: number;
}> {
  const queryParams = new URLSearchParams({});
  if (filters) {
    Object.entries(filters).forEach(([key, value]) => {
      if (value) queryParams.set(key, value.toString());
    });
  }
  return apiClient.makeRequest("/projects/trees/pages", "GET", "{}", { page, pageSize, filters: queryParams });
}

export const ProjectTreeList: React.FC = () => {
  const [projects, setProjects] = useState<ProjectTree[]>([]);
  const [filters, setFilters] = useState<Filter[]>([]);
  const [globalFilter, setGlobalFilter] = useState("");
  const [paginationState, setPaginationState] = useState<PaginationState>({
    pageIndex: 0,
    pageSize: 25,
  });
  const [rowCount, setRowCount] = useState(0);
  const [isLoadingProjects, setIsLoadingProjects] = useState(true);
  const [filterByAccount, setFilterByAccount] = useState<boolean>(true);
  const referenceTypes = useReferenceTypes();
  const navigate = useNavigate();
  const authContext = useContext(AuthContext);

  const fetchProjects = async () => {
    setIsLoadingProjects(true);
    try {
      const statusFilter = filters.find((filter) => filter.field == "status")?.values as number[] | undefined;

      const projectFilters: Record<string, string | number | number[]> = {
        search: globalFilter,
      };

      if (statusFilter) {
        projectFilters.projectStatusId = statusFilter;
      }

      const userAccountId = authContext.user?.account?.id;
      if (authContext.isProjectDeveloperUser() && filterByAccount && userAccountId) {
        projectFilters.accountId = userAccountId;
      }

      const projectsData = await getProjectTreePage(
        paginationState.pageIndex + 1,
        projectFilters,
        paginationState.pageSize,
      );
      if (projectsData) {
        setProjects(projectsData.content);
        setRowCount(projectsData.totalElements);
      }
    } catch (error) {
      console.error("Error fetching projects:", error);
      toast.error("Failed to fetch projects");
    }
    setIsLoadingProjects(false);
  };

  useEffect(() => {
    fetchProjects();
  }, [paginationState.pageIndex, paginationState.pageSize, globalFilter, filterByAccount, filters]);

  const getProjectStatusIcon = (statusCode: string) => {
    switch (statusCode) {
      case "LISTED":
        return ListedSVG;
      case "DESIGN_CERTIFIED":
        return DesignCertifiedSVG;
      case "CERTIFIED_PROJECT":
        return CertifiedSVG;
      default:
        return null;
    }
  };

  const renderStatus = (projectStatus: ReferenceType) => {
    const statusSvg = getProjectStatusIcon(projectStatus.code);
    if (!statusSvg) return <>{projectStatus.description}</>;

    return (
      <img
        src={statusSvg}
        alt={projectStatus.description}
        style={{ height: "35px" }}
        title={projectStatus.description}
      />
    );
  };

  const canBeEdited = (project: Project) => {
    if (authContext.isAdminUser() || authContext.isArmUser()) {
      return true;
    }

    const userOwnsProject = authContext.isProjectDeveloperUser() && authContext.isUserOwnerOf(project.accountId);
    return userOwnsProject && project.projectStatus.code == "NEW";
  };

  const buildStatusFilterOptions = () => {
    return referenceTypes.activeReferenceTypes.ProjectState.map((projectState) => ({
      label: projectState.description,
      value: projectState.id,
    }));
  };

  return (
    <FilterableSortableTable
      columns={[
        {
          accessorKey: "gsid",
          header: "GS ID",
          enableColumnFilter: false,
        },
        {
          accessorKey: "name",
          header: "Name",
          enableColumnFilter: false,
          Cell: BoldTextCell,
        },
        {
          accessorKey: "status",
          accessorFn: (row) => renderStatus(row.projectStatus),
          header: "Status",
          filterSelectOptions: buildStatusFilterOptions(),
          filterVariant: "multi-select",
        },
        {
          accessorFn: (row) => row.countries.map((country) => country.name).join(", "),
          header: "Country",
          enableColumnFilter: false,
        },
        {
          accessorKey: "projectTypes",
          header: "Types",
          enableColumnFilter: false,
          Cell: MultilineReferenceTypesCell,
        },
      ]}
      data={projects}
      onPaginationChange={setPaginationState}
      topToolbarCustomActions={
        <>
          {authContext.isProjectDeveloperUser() && (
            <FormControlLabel
              control={
                <Switch
                  checked={filterByAccount}
                  onChange={(event) => setFilterByAccount(event.target.checked)}
                  aria-label="my-projects-switch"
                />
              }
              label="My projects"
              data-testid="my-projects-switch"
            />
          )}
        </>
      }
      topToolbarActions={<CreateProjectButton />}
      onFilterUpdate={setFilters}
      enablePagination={true}
      enableFilters={true}
      enableGlobalFilter={true}
      onGlobalFilterChange={setGlobalFilter}
      globalSearchPlaceholder="Name or Exact GSID"
      rowCount={rowCount}
      state={{
        pagination: paginationState,
        isLoading: isLoadingProjects,
      }}
      enableExpanding={true}
      initialState={{ expanded: true }}
      onRowClick={(_, row) => navigate(`/projects/${row.original.gsid}`)}
      getSubRows={(row) => row.children}
      rowActions={[
        {
          label: "View",
          action: (row) => navigate(`/projects/${row.gsid}`),
        },
        {
          label: "Edit",
          action: (row) => navigate("/update-project", { state: { projectId: row.id } }),
          visible: (row) => canBeEdited(row),
        },
      ]}
    />
  );
};
