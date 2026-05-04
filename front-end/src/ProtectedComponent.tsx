import React, { useContext, useEffect, useState } from "react";

import { PermissionGroup } from "./PermissionGroup";
import { AuthContext } from "./components/Auth/Login/AuthProvider";

interface ProtectedComponentProps {
  children: React.ReactNode;
  roles?: PermissionGroup[];
}

const ProtectedComponent: React.FC<ProtectedComponentProps> = ({ children, roles }) => {
  const authContext = useContext(AuthContext);
  const [hasAccess, setHasAccess] = useState(false);

  useEffect(() => {
    setHasAccess(checkCurrentUserAccess());
  }, [authContext.isAuthenticated, roles, authContext.user]);

  const checkCurrentUserAccess = (): boolean => {
    if (!authContext.isAuthenticated) {
      return false;
    }
    if (!roles || roles.length == 0) {
      return true;
    }
    return roles.some((role) => authContext.user?.groups.includes(role));
  };

  if (!authContext.isAuthenticated || !hasAccess) {
    return null;
  }

  return <>{children}</>;
};

export default ProtectedComponent;
