import KeyboardArrowDownIcon from "@mui/icons-material/KeyboardArrowDown";
import KeyboardArrowUpIcon from "@mui/icons-material/KeyboardArrowUp";
import { Button, Menu, MenuItem } from "@mui/material";
import { useContext, useState } from "react";
import { Link } from "react-router-dom";

import ProtectedComponent from "../../ProtectedComponent";
import { PermissionGroup } from "../../PermissionGroup";
import { AuthContext } from "../../components/Auth/Login/AuthProvider";
import UserMultiFactorSettingsModal from "../../components/Auth/Settings/UserMultiFactorSettingsModal";

export const UserMenu = () => {
  const authContext = useContext(AuthContext);
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [isMfaModalOpen, setIsMfaModalOpen] = useState(false);
  const open = Boolean(anchorEl);

  const buildUserRolesText = (): string => {
    if (!authContext.isAuthenticated || !authContext.user || authContext.user.groups.length == 0) {
      return "None";
    }
    return authContext.user.groups.join(", ");
  };

  const handleLogoutClick = async () => {
    handleClose();
    await authContext.logout();
  };

  const handleMfaSettingsClick = () => {
    handleClose();
    setIsMfaModalOpen(true);
  };

  const handleMfaModalClose = () => {
    setIsMfaModalOpen(false);
  };

  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  if (!authContext.isAuthenticated) {
    return (
      <Link
        style={{
          color: "white",
          textDecoration: "none",
          padding: "10px",
          textTransform: "uppercase",
          fontFamily: "Gotham Rounded Book, Roboto, Helvetica, Arial, sans-serif",
          fontSize: "13px",
        }}
        to="/login"
      >
        Login
      </Link>
    );
  }

  return (
    <>
      <Button
        sx={{
          color: "white",
          fontSize: "13px",
          cursor: "pointer",
          border: "none",
          marginRight: "0",
        }}
        aria-expanded={open ? "true" : undefined}
        aria-haspopup="true"
        disableRipple
        onClick={handleClick}
        endIcon={open ? <KeyboardArrowUpIcon /> : <KeyboardArrowDownIcon />}
      >
        {authContext.user?.givenName} {authContext.user?.familyName}&nbsp;
      </Button>
      <Menu
        anchorEl={anchorEl}
        open={open}
        onClose={handleClose}
        anchorOrigin={{
          vertical: "bottom",
          horizontal: "right",
        }}
      >
        <MenuItem disabled>
          Platform Account:&nbsp;<span>{authContext.user?.account?.name ?? "None"}</span>
        </MenuItem>
        <MenuItem disabled>
          Roles:&nbsp;<span>{buildUserRolesText()}</span>
        </MenuItem>
        <ProtectedComponent roles={[PermissionGroup.GS_ADMIN]}>
          <MenuItem onClick={handleMfaSettingsClick}>MFA Settings</MenuItem>
        </ProtectedComponent>
        <MenuItem onClick={handleLogoutClick}>Logout</MenuItem>
      </Menu>
      <UserMultiFactorSettingsModal isOpen={isMfaModalOpen} onClose={handleMfaModalClose} />
    </>
  );
};
