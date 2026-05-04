import { Link as MUILink } from "@mui/material";
import React, { useState } from "react";
import { Link } from "react-router-dom";
import "./Footer.css";

import Logo from "../../img/gs-logo-primary-white.svg";

import { ApplicationVersions } from "./ApplicationVersions";

const Footer: React.FC = () => {
  const [isApplicationVersionOpen, setIsApplicationVersionOpen] = useState<boolean>(false);

  const policyLinkStyle = {
    textDecoration: "none",
    color: "inherit",
    padding: "0 5px",
    cursor: "pointer",
  };

  return (
    <footer className="footer">
      <div className="footer__container">
        <div className="footer__column">
          <img src={Logo} alt="Gold Standard" height="40px" style={{ height: "40px", marginRight: "7px" }} />
        </div>
      </div>
      <div>
        <p className="copyright">
          &copy; {new Date().getFullYear()} Gold Standard. All rights reserved. |
          <Link to="/privacy-policy" style={policyLinkStyle}>
            Privacy Policy
          </Link>
          |
          <MUILink href="https://www.goldstandard.org/terms-and-conditions" target="_blank" style={policyLinkStyle}>
            Terms & Conditions
          </MUILink>
          |
          <Link target="_blank" rel="noreferrer" to="/accessibility-statement" style={policyLinkStyle}>
            Accessibility
          </Link>
          |
          <MUILink onClick={() => setIsApplicationVersionOpen(true)} sx={policyLinkStyle}>
            Version
          </MUILink>
        </p>
      </div>
      <ApplicationVersions isOpen={isApplicationVersionOpen} onClose={() => setIsApplicationVersionOpen(false)} />
    </footer>
  );
};

export default Footer;
