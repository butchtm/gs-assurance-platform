import { TextField, Typography } from "@mui/material";

const ValidatedTextField = ({
  label,
  value,
  onChange,
  onBlur,
  error,
  errorText,
  type = "text",
  autoComplete,
  inputProps,
  testId,
}: {
  label: string;
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onBlur: () => void;
  error: boolean;
  errorText: string | null;
  type?: string;
  autoComplete?: string;
  inputProps?: object;
  testId?: string;
}) => (
  <>
    <TextField
      label={label}
      type={type}
      value={value}
      onChange={onChange}
      onBlur={onBlur}
      autoComplete={autoComplete}
      inputProps={inputProps}
      variant="outlined"
      margin="normal"
      fullWidth
      error={error}
      aria-invalid={error}
      aria-describedby={error ? `${testId}-error-text` : undefined}
      data-testid={testId}
    />
    {error && (
      <Typography id={`${testId}-error-text`} color="error" variant="body2" role="alert" sx={{ mt: -1, mb: 1 }}>
        {errorText}
      </Typography>
    )}
  </>
);

export default ValidatedTextField;
