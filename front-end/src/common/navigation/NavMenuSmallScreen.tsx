import { Menu as MenuIcon } from "@mui/icons-material";
import { IconButton, Menu, MenuItem } from "@mui/material";
import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import { NavbarRoute } from "../header/Header";

export const NavMenuSmallScreen = ({ validNavbarRoutes }: { validNavbarRoutes: NavbarRoute[] }) => {
  const navigate = useNavigate();
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const open = Boolean(anchorEl);

  const handleClick = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };
  const handleClose = () => {
    setAnchorEl(null);
  };
  const clickItem = (path: string) => {
    navigate(path);
    handleClose();
  };

  return (
    <nav style={{ display: "flex" }}>
      <IconButton onClick={handleClick} sx={{ color: "white" }}>
        <MenuIcon />
      </IconButton>
      <Menu
        anchorEl={anchorEl}
        open={open}
        onClose={handleClose}
        slotProps={{
          paper: {
            style: {
              maxHeight: 48 * 6.5,
              width: "22ch",
            },
          },
        }}
      >
        {validNavbarRoutes.map((route) => (
          <MenuItem key={route.path} selected={false} onClick={() => clickItem(route.path)}>
            {route.title}
          </MenuItem>
        ))}
      </Menu>
      <Link
        to="/"
        style={{
          color: "#fff",
          display: "flex",
          outline: 0,
          fontSize: "18px",
          alignItems: "center",
          fontWeight: 600,
          fontFamily: "Open Sans, Verdana, Geneva, Tahoma, sans-serif",
          marginLeft: "7px",
          textTransform: "uppercase",
          textAlign: "left",
        }}
      >
        Assurance Platform
      </Link>
    </nav>
  );
};
