import React from "react";
import { Helmet } from "react-helmet-async";

import { PermissionGroup } from "../../../PermissionGroup";
import ProtectedComponent from "../../../ProtectedComponent";

import ARMTeamMyRequests from "./ARMTeam/ARMTeamMyRequests";
import FinanceMyRequests from "./Finance/FinanceMyRequests";
import ProjectDeveloperMyRequests from "./ProjectDeveloper/ProjectDeveloperMyRequests";
import RegistryReviewerMyRequests from "./RegistryReviewer/RegistryReviewerMyRequests";
import TACMyRequests from "./TAC/TACMyRequests";
import VVBMyRequests from "./VVB/VVBMyRequests";

const MyRequests: React.FC = () => {
  return (
    <>
      <Helmet>
        <title>My Requests</title>
      </Helmet>
      <ProtectedComponent roles={[PermissionGroup.FINANCE]}>
        <FinanceMyRequests />
      </ProtectedComponent>
      <ProtectedComponent roles={[PermissionGroup.VVB]}>
        <VVBMyRequests />
      </ProtectedComponent>
      <ProtectedComponent roles={[PermissionGroup.ARM_TEAM]}>
        <ARMTeamMyRequests />
      </ProtectedComponent>
      <ProtectedComponent roles={[PermissionGroup.GS_REVIEWER, PermissionGroup.GS_REGISTRY]}>
        <RegistryReviewerMyRequests />
      </ProtectedComponent>
      <ProtectedComponent roles={[PermissionGroup.PROJECT_DEVELOPER]}>
        <ProjectDeveloperMyRequests />
      </ProtectedComponent>
      <ProtectedComponent roles={[PermissionGroup.TAC]}>
        <TACMyRequests />
      </ProtectedComponent>
    </>
  );
};

export default MyRequests;
