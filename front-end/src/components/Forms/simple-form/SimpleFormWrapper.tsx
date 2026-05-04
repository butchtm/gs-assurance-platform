import Box from "@mui/material/Box";
import Paper from "@mui/material/Paper";
import Typography from "@mui/material/Typography";

import JsonSchemaFormTest from "./JsonSchemaFormTest";

export const SimpleFormWrapper = () => {
  return (
    <Box p={3}>
      <Typography variant="h4" component="h1" gutterBottom>
        Form Sandbox - Development Testing Area
      </Typography>

      <Paper elevation={2} sx={{ p: 3, mb: 3 }}>
        <Typography variant="h5" component="h2" gutterBottom>
          Simple JSON Schema Form
        </Typography>
        <Typography variant="body2" color="text.secondary" paragraph>
          Basic form with mock API integration for loading and saving data
        </Typography>
        <JsonSchemaFormTest />
      </Paper>
    </Box>
  );
};
