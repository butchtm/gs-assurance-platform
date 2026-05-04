import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import "./CookieConsent.css";

const CookieConsent: React.FC = () => {
  const [showBanner, setShowBanner] = useState(false);

  useEffect(() => {
    const consent = localStorage.getItem("cookie-consent");
    if (!consent) {
      setShowBanner(true);
    }
  }, []);

  const acceptCookies = () => {
    localStorage.setItem("cookie-consent", "accepted");
    setShowBanner(false);
  };

  return showBanner ? (
    <div className="cookie-consent-banner" data-testid="cookie-consent">
      <p>
        We use cookies to analyse site traffic and to offer you a better experience. By continuing to use this website,
        you consent to the use of cookies in accordance with our <Link to="/privacy-policy">privacy policy</Link>
      </p>
      <button onClick={acceptCookies}>Accept</button>
    </div>
  ) : null;
};

export default CookieConsent;
