import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { TreeItem, TreeView } from "@mui/x-tree-view";
import React, { useState } from "react";

import { Project } from "../../../common/project/Project";
import { ProjectTree } from "../Project";

import ViewProjectHierarchyRowLabel from "./ViewProjectHierarchyRowLabel";

interface ViewProjectHierarchyTreeProps {
  project: Project;
  returnToProjectInfoCallback: () => void;
  projectTree: ProjectTree;
}

const ViewProjectHierarchyTree: React.FC<ViewProjectHierarchyTreeProps> = ({
  project,
  returnToProjectInfoCallback,
  projectTree,
}): JSX.Element => {
  const [expanded, setExpanded] = useState<string[]>([]);
  const [searchTerm, setSearchTerm] = useState<string>("");

  const handleToggle = (event: React.SyntheticEvent, nodeIds: string[]) => {
    setExpanded(nodeIds);
  };

  const expandProject = (id: string) => {
    const updatedExpanded = [...expanded, id];
    setExpanded(updatedExpanded);
  };

  const isBundlePresent = (tree: ProjectTree): boolean => {
    if (tree.bundle) {
      return true;
    }
    if (tree.children) {
      const withBundle = tree.children.filter((c) => c.bundle != null);
      if (withBundle.length) {
        return true;
      }
    }
    return false;
  };

  const isSearchMatch = (item: ProjectTree): boolean => {
    if (searchTerm) {
      const unifiedSearchTerm = searchTerm.toLowerCase();
      if (item.name.toLowerCase().includes(unifiedSearchTerm) || item.gsid.toLowerCase().includes(unifiedSearchTerm)) {
        return true;
      }
      if (item.children) {
        return item.children.some((child) => isSearchMatch(child));
      }
      return false;
    }
    return true;
  };

  const renderTree = (projectItem: ProjectTree) => {
    const children = projectItem.children ? projectItem.children.filter((child) => isSearchMatch(child)) : [];
    if (searchTerm && children.length > 0 && !expanded.includes(projectItem.id.toString())) {
      expandProject(projectItem.id.toString());
    }
    return (
      <TreeItem
        key={projectItem.id}
        nodeId={projectItem.id.toString()}
        data-testid={"tree-item-" + projectItem.id}
        label={
          <ViewProjectHierarchyRowLabel
            projectTree={projectItem}
            isBundlePresent={isBundlePresent(projectTree)}
            returnToProjectInfoCallback={returnToProjectInfoCallback}
          />
        }
        sx={{ "& .MuiTreeItem-content": { minHeight: "48px", alignItems: "center" } }}
      >
        {children.map((child) => renderTree(child))}
      </TreeItem>
    );
  };

  return (
    <div key={projectTree.id}>
      {expanded.includes(projectTree.id.toString()) && (
        <div style={{ margin: "10px 0" }}>
          <input
            type="text"
            placeholder="Search..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{ width: "100%", padding: "10px" }}
            data-testid="vpa-search"
          />
        </div>
      )}
      <TreeView
        aria-label="project navigator"
        defaultCollapseIcon={<ExpandMoreIcon />}
        defaultExpandIcon={<ChevronRightIcon />}
        expanded={expanded}
        onNodeToggle={handleToggle}
        sx={{ height: expanded.length > 0 ? 600 : "auto", flexGrow: 1, overflowY: "auto" }}
        selected={(project.id ?? "").toString()}
      >
        {renderTree(projectTree)}
      </TreeView>
    </div>
  );
};

export default ViewProjectHierarchyTree;
