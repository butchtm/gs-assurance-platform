import React, { useContext, useEffect, useState } from "react";
import { useLocation } from "react-router-dom";

import { AuthContext } from "../../components/Auth/Login/AuthProvider";
import { PermissionGroup } from "../../PermissionGroup";
import { NavMenuSmallScreen } from "../navigation/NavMenuSmallScreen";
import { UserMenu } from "../navigation/UserMenu";
import { getUnreadMessages, UnreadMessages } from "../../components/requests/conversation/Conversation";
import { NavMenuNormalScreen } from "../navigation/NavMenuNormalScreen";

import { UnreadMessagesNotifications } from "./UnreadMessagesNotifications";

export interface NavbarRoute {
  title: string;
  path: string;
  access?: PermissionGroup[];
  public?: boolean;
}

const navbarRoutes: NavbarRoute[] = [
  {
    title: "My Requests",
    path: "/my-requests",
    access: [
      PermissionGroup.GS_REVIEWER,
      PermissionGroup.GS_REGISTRY,
      PermissionGroup.PROJECT_DEVELOPER,
      PermissionGroup.ARM_TEAM,
      PermissionGroup.TAC,
      PermissionGroup.VVB,
      PermissionGroup.FINANCE,
    ],
  },
  {
    title: "All Requests",
    path: "/all-requests",
    access: [PermissionGroup.ARM_TEAM, PermissionGroup.GS_ADMIN],
  },
  {
    title: "Dashboard",
    path: "/common-dashboard",
    access: [PermissionGroup.ARM_TEAM, PermissionGroup.GS_ADMIN, PermissionGroup.SUPPORT],
  },
  {
    title: "Projects",
    path: "/projects",
    access: [
      PermissionGroup.GS_ADMIN,
      PermissionGroup.GS_REVIEWER,
      PermissionGroup.GS_REGISTRY,
      PermissionGroup.ARM_TEAM,
      PermissionGroup.VVB,
      PermissionGroup.TAC,
      PermissionGroup.FINANCE,
      PermissionGroup.SUPPORT,
    ],
  },
  {
    title: "Projects",
    path: "/project-trees",
    access: [PermissionGroup.PROJECT_DEVELOPER],
  },
  {
    title: "Platform Accounts",
    path: "/accounts",
    access: [PermissionGroup.GS_ADMIN, PermissionGroup.ARM_TEAM],
  },
  {
    title: "Users",
    path: "/users",
    access: [PermissionGroup.GS_ADMIN],
  },
  {
    title: "Consultations",
    path: "/public-consultations",
    public: true,
  },
  {
    title: "Admin",
    path: "/administration",
    access: [PermissionGroup.GS_ADMIN, PermissionGroup.ARM_TEAM],
  },
];

const checkIsScreenSmall = (): boolean => {
  return window.innerWidth < 900;
};

export const Header: React.FC = () => {
  const [navigationEnabled, setNavigationEnabled] = useState<boolean>(false);
  const location = useLocation();
  const authContext = useContext(AuthContext);
  const [validNavbarRoutes, setValidNavbarRoutes] = useState<NavbarRoute[]>([]);
  const [isScreenSmall, setIsScreenSmall] = useState(checkIsScreenSmall());
  const [unreadMessages, setUnreadMessages] = useState<UnreadMessages[]>([]);

  const isArmPDOrVVB = authContext.isArmUser() || authContext.isProjectDeveloperUser() || authContext.isVvbUser();

  const fetchUnreadMessages = async () => {
    try {
      const response = await getUnreadMessages();
      if (response) {
        setUnreadMessages(response);
      }
    } catch (error) {
      console.error("Unexpected error retrieving unread message count:", error);
    }
  };

  useEffect(() => {
    const canSeeTab = (route: NavbarRoute): boolean => {
      if (route.public) {
        return true;
      }
      if (!authContext.isAuthenticated) {
        return false;
      }
      if (!route.access || route.access.length == 0) {
        return true;
      }
      return route.access.some((requiredRole) => authContext.user?.groups.includes(requiredRole));
    };

    if (authContext.isAuthenticated && isArmPDOrVVB) {
      fetchUnreadMessages();
    }

    setValidNavbarRoutes(navbarRoutes.filter(canSeeTab));
  }, [authContext.isAuthenticated, location]);

  useEffect(() => {
    const handleWindowResize = (): void => {
      setIsScreenSmall(checkIsScreenSmall());
    };

    window.addEventListener("resize", handleWindowResize);

    return () => {
      window.removeEventListener("resize", handleWindowResize);
    };
  }, []);

  const onUrlChange = (): void => {
    setNavigationEnabled(!location.pathname.includes("project-documents"));
  };
  useEffect(onUrlChange, [location]);

  if (!navigationEnabled) return <></>;

  return (
    <header
      style={{
        backgroundColor: "#212121",
        boxShadow: "rgba(0, 0, 0, 0.15) 0px 1px 3px 0px, rgba(0, 0, 0, 0.22) 1px 3px 5px 0px",
        boxSizing: "border-box",
        display: "flex",
        justifyContent: "space-between",
        gridTemplateColumns: "1fr auto 1fr",
        alignItems: "center",
        height: "auto",
        padding: "0% 1%",
        position: "sticky" as const,
        top: "0",
        width: "100%",
        zIndex: 1100,
      }}
    >
      {isScreenSmall ? (
        <NavMenuSmallScreen validNavbarRoutes={validNavbarRoutes} />
      ) : (
        <NavMenuNormalScreen validNavbarRoutes={validNavbarRoutes} />
      )}
      <div
        style={{
          display: "flex",
          justifyContent: "flex-end",
          textAlign: "right",
          alignItems: "center",
        }}
      >
        {authContext.isAuthenticated && isArmPDOrVVB && <UnreadMessagesNotifications unreadMessages={unreadMessages} />}
        <UserMenu />
      </div>
    </header>
  );
};
