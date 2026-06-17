/* eslint-env node */

const path = require("path");

const express = require("express");
const cors = require("cors");
const { createProxyMiddleware } = require("http-proxy-middleware");

const config = require("./config");

const app = express();
app.disable("x-powered-by");

app.use(
  cors({
    origin: function (origin, callback) {
      if (!origin) return callback(null, true);
      if (config.ALLOWED_ORIGINS.indexOf(origin) === -1) {
        return callback(
          new Error(`The CORS policy for this site does not allow access from the specified origin: ${origin}`),
          false,
        );
      }
      return callback(null, true);
    },
    credentials: true,
  }),
);

app.use(
  "/api",
  createProxyMiddleware({
    target: "http://localhost:8080/api",
    changeOrigin: true,
  }),
);

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

const buildPath = path.resolve(__dirname, "public");
console.log("Serving static files from:", buildPath);
app.use(
  express.static(buildPath, {
    etag: false,
    lastModified: false,
    maxAge: 0,
  }),
);

app.get(/(.*)/, (req, res) => {
  res.sendFile(path.join(buildPath, "index.html"));
});

app.listen(config.PORT, () => {
  console.log(`Server is running on port ${config.PORT}`);
});
