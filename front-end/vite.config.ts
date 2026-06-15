import react from "@vitejs/plugin-react";
import { defineConfig, loadEnv } from "vite";
import checker from "vite-plugin-checker";
import tsconfigPaths from "vite-tsconfig-paths";
import { manualChunks } from "./chunking";

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, ".", "VITE_");

  return {
    server: {
      host: env.HOST || "0.0.0.0",
      port: parseInt(env.PORT || "3000", 10),
      open: true,
      proxy: {
        "/ui": {
          target: env.VITE_PROXY_UI_TARGET || "http://localhost:3001",
          rewrite: (path) => path.replace(/^\/ui/, ""),
        },
        "/api": {
          target: env.VITE_PROXY_TARGET || "http://localhost:8080",
          changeOrigin: true,
        },
      },
    },
    plugins: [
      react(),
      tsconfigPaths(),
      checker({
        typescript: true,
        eslint: {
          lintCommand: 'eslint "./src/**/*.{ts,tsx}"',
          useFlatConfig: true,
        },
      }),
    ],
    build: {
      rollupOptions: {
        output: {
          manualChunks(id) {
            return manualChunks(id);
          },
        },
      },
    },
    optimizeDeps: {
      include: [
        "@mui/material/Button",
        "@mui/material/Grid",
        "@mui/material/Typography",
        "@mui/icons-material/Close",
        "@mui/material/Tooltip",
        "@mui/material/Dialog",
        "@mui/material/DialogTitle",
        "@mui/material/DialogContent",
        "@mui/material/DialogActions",
        "@mui/material/TextField",
        "@mui/material/Checkbox",
        "@mui/material/FormControlLabel",
        "@mui/material/FormGroup",
        "@mui/material/FormControl",
        "@mui/material/FormLabel",
        "@mui/material/Select",
        "@mui/material/MenuItem",
        "@mui/material/InputLabel",
        "@mui/material/List",
        "@mui/material/ListItem",
        "@mui/material/ListItemText",
        "@mui/material/ListItemIcon",
        "@mui/material/ListItemSecondaryAction",
        "@mui/material/Avatar",
        "@mui/material/Chip",
        "@mui/material/Box",
        "@mui/material/Stack",
        "@mui/material/Alert",
        "@mui/material/AlertTitle",
        "@mui/material/Collapse",
        "@mui/material/Paper",
        "@mui/material/Card",
        "@mui/material/CardContent",
        "@mui/material/CardActions",
        "@mui/material/CardHeader",
        "@mui/material/CardMedia",
        "@mui/material/Modal",
        "@mui/material/Popover",
        "@mui/material/Slider",
        "@mui/material/Tab",
        "@mui/material/Tabs",
        "@mui/material/Tooltip",
      ],
    },
  };
});
