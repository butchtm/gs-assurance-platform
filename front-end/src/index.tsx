import { extend } from "dayjs";
import businessDays from "dayjs-business-days2";
import relativeTime from "dayjs/plugin/relativeTime";
import timezone from "dayjs/plugin/timezone";
import utc from "dayjs/plugin/utc";
import React from "react";
import { createRoot } from "react-dom/client";
import { HelmetProvider } from "react-helmet-async";

import App from "./App";
import "./index.css";
import reportWebVitals from "./reportWebVitals";

extend(utc);
extend(timezone);
extend(businessDays);
extend(relativeTime);

const root = createRoot(document.getElementById("root") as HTMLElement);
root.render(
  <React.StrictMode>
    <HelmetProvider>
      <App />
    </HelmetProvider>
  </React.StrictMode>,
);

reportWebVitals();
