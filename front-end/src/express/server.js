/* eslint-disable @typescript-eslint/no-var-requires */
const express = require("express");
const cors = require("cors");

const config = require("./config");

const app = express();
app.disable("x-powered-by");

// eslint-disable-next-line no-undef
if (process.env.NODE_ENV !== "production" && process.env.NODE_ENV !== "test") {
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
}

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

app.listen(config.PORT, () => {
  console.log(`Server is running on port ${config.PORT}`);
});
