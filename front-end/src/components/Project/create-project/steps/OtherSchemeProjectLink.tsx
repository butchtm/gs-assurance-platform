import { Box, FormHelperText, TextField } from "@mui/material";
import { useField } from "formik";

export const OtherSchemeProjectLink = () => {
  const [field, meta, helpers] = useField("projectLinkInOtherScheme");

  return (
    <>
      <Box display="flex" alignItems="center">
        <TextField
          fullWidth
          label={
            <>
              Project Link in Other Scheme<span aria-hidden="true">*</span>
            </>
          }
          data-testid="project-link-in-other-scheme"
          error={meta.touched && Boolean(meta.error)}
          value={field.value}
          onChange={(e) => {
            helpers.setValue(e.target.value);
          }}
          aria-required
        />
      </Box>
      {meta.touched && Boolean(meta.error) ? (
        <FormHelperText error role="alert">
          {meta.error}
        </FormHelperText>
      ) : null}
    </>
  );
};
