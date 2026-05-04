import { Tab, Tabs, useMediaQuery } from "@mui/material";
import { useContext, useEffect, useState } from "react";
import { Link, useLocation } from "react-router-dom";

import { AuthContext } from "../../components/Auth/Login/AuthProvider";
import { NavbarRoute } from "../header/Header";

export const NavMenu = ({ validNavbarRoutes }: { validNavbarRoutes: NavbarRoute[] }) => {
  const location = useLocation();
  const authContext = useContext(AuthContext);
  const [selectedTabPath, setSelectedTabPath] = useState<string | boolean>(false);

  useEffect(() => {
    const route = validNavbarRoutes.find((route) => route.path == location.pathname);
    setSelectedTabPath(route?.path ?? false);
  }, [authContext.isAuthenticated, location]);

  const isBelow1100 = useMediaQuery("(max-width:1100px)");
  const isBelow900 = useMediaQuery("(max-width:900px)");
  const isMediumScreen = isBelow1100 && !isBelow900;

  const tabFontSize = isMediumScreen ? "11px" : "13px";
  const tabPadding = isMediumScreen ? "12px 0.6vw" : "21px 1vw";
  const tabMinWidth = isMediumScreen ? 0 : undefined;

  return (
    <Tabs
      sx={{
        "& .MuiTab-textColorPrimary": {
          color: "#fff",
        },
        "& .MuiTabs-indicator": {
          backgroundColor: "white",
          height: "3px",
        },
        "& .MuiTab-root": {
          padding: tabPadding,
          textTransform: "uppercase",
          fontSize: tabFontSize,
          minWidth: tabMinWidth,
          flexShrink: 1,
        },
      }}
      value={selectedTabPath}
    >
      {validNavbarRoutes.map((route) => (
        <Tab
          key={route.path}
          sx={{
            "&.Mui-selected": {
              color: "#fff",
              fontWeight: "bold",
            },
          }}
          value={route.path}
          label={route.title}
          component={Link}
          to={route.path}
        />
      ))}
    </Tabs>
  );
};
