import { useContext } from "react";
import { Navigate } from "react-router-dom";

import { AuthContext } from "./components/Auth/Login/AuthProvider";
import { PermissionGroup } from "./PermissionGroup";

export const ProtectedRoute = ({ element, roles }: { element: JSX.Element; roles?: PermissionGroup[] }) => {
  const authContext = useContext(AuthContext);

  const userHasRequiredRoles = (): boolean => {
    if (!roles) return true;
    return roles.some((role) => authContext.user?.groups.includes(role));
  };

  if (!authContext.isAuthenticated) {
    return <Navigate replace to="/login" />;
  }

  if (!userHasRequiredRoles()) {
    return <Navigate replace to="/unauthorized" />;
  }

  return element;
};
