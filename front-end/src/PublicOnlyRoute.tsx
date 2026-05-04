import { useContext } from "react";
import { Navigate } from "react-router-dom";

import { AuthContext } from "./components/Auth/Login/AuthProvider";

export const PublicOnlyRoute = ({ element }: { element: JSX.Element }) => {
  const authContext = useContext(AuthContext);

  const getRedirectPath = () => {
    if (authContext.isAdminUser()) {
      return "/all-requests";
    }
    if (authContext.isSupportUser()) {
      return "/projects";
    }
    return "/my-requests";
  };

  if (authContext.isAuthenticated) {
    return <Navigate replace to={getRedirectPath()} />;
  }

  return <>{element}</>;
};
