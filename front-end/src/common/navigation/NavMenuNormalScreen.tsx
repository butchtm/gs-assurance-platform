import { Link } from "react-router-dom";

import LogoG from "../../img/gs-gated-logo.svg";
import { HeaderTitle } from "../header/HeaderTitle";
import { NavbarRoute } from "../header/Header";

import { NavMenu } from "./NavMenu";

export const NavMenuNormalScreen = ({ validNavbarRoutes }: { validNavbarRoutes: NavbarRoute[] }) => {
  return (
    <>
      <Link to="/" id="logo-anchor" style={{ display: "flex", textDecoration: "none" }}>
        <img src={LogoG} alt="Gold Standard" style={{ height: "40px", marginRight: "7px" }} />
        <HeaderTitle />
      </Link>
      <nav>
        <NavMenu validNavbarRoutes={validNavbarRoutes} />
      </nav>
    </>
  );
};
