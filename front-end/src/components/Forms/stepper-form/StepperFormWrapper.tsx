import Box from "@mui/material/Box";
import Paper from "@mui/material/Paper";
import Typography from "@mui/material/Typography";

import StepperFormTest from "./StepperFormTest";

export const StepperFormWrapper = () => {
  return (
    <Box>
      <Paper elevation={2} sx={{ p: 3, mb: 3 }}>
        <Typography variant="h5" component="h2" gutterBottom>
          Multi-Step JSON Schema Form
        </Typography>
        <Typography variant="body2" color="text.secondary" paragraph>
          Advanced stepper form with multiple steps, dynamic enum loading, and form state management
        </Typography>
        <StepperFormTest />
      </Paper>
    </Box>
  );
};
