import "@testing-library/jest-dom";
import { TextDecoder, TextEncoder } from "util";

import { extend } from "dayjs";
import businessDays from "dayjs-business-days2";
import relativeTime from "dayjs/plugin/relativeTime";
import timezone from "dayjs/plugin/timezone";
import utc from "dayjs/plugin/utc";
import failOnConsole from "jest-fail-on-console";

extend(utc);
extend(timezone);
extend(businessDays);
extend(relativeTime);

failOnConsole({
  shouldFailOnLog: false,
  shouldFailOnInfo: true,
  shouldFailOnWarn: true,
  shouldFailOnError: true,
  shouldFailOnAssert: true,
  shouldFailOnDebug: true,
  silenceMessage(message) {
    return /React Router Future Flag Warning/.test(message);
  },
});

// Polyfill for TextEncoder
if (typeof global.TextEncoder === "undefined") {
  global.TextEncoder = TextEncoder;
}
if (typeof window.TextEncoder === "undefined") {
  window.TextEncoder = global.TextEncoder;
}

// Polyfill for TextDecoder
if (typeof global.TextDecoder === "undefined") {
  // @ts-expect-error Fix TextDecoder Polyfill for Jest
  global.TextDecoder = TextDecoder;
}
if (typeof window.TextDecoder === "undefined") {
  window.TextDecoder = global.TextDecoder;
}
