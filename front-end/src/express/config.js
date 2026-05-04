module.exports = {
  PORT: 3001,
  ALLOWED_ORIGINS: ["http://localhost:3000", "http://localhost:8080", "http://localhost:3001"],
  // eslint-disable-next-line no-undef
  PROXY_TARGET: process.env.PROXY_TARGET ?? "http://localhost:8080",
};
