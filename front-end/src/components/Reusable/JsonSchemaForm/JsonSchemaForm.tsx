import { Box, Button, Paper, Typography } from "@mui/material";
import { default as Form } from "@rjsf/mui";
import { RJSFSchema, UiSchema } from "@rjsf/utils";
import validator from "@rjsf/validator-ajv8";

import { widgets } from "../../Forms/widgets/Widgets";
import { StepFormData } from "../../Forms/Forms";

interface JsonSchemaFormProps<T = StepFormData> {
  schema: RJSFSchema;
  uiSchema?: UiSchema;
  formData?: T;
  title?: string;
  onSubmit?: (data: { formData?: T }) => void;
  onChange?: (data: { formData?: T }) => void;
  showErrorList?: false | "top" | "bottom";
  liveValidate?: boolean;
  disabled?: boolean;
  readonly?: boolean;
  submitButtonText?: string;
  submitButtonVariant?: "contained" | "outlined" | "text";
  showTitle?: boolean;
  elevation?: number;
  className?: string;
}

const JsonSchemaForm = <T extends StepFormData>({
  schema,
  uiSchema = {},
  formData,
  title,
  onSubmit,
  onChange,
  showErrorList = false,
  liveValidate = true,
  disabled = false,
  readonly = false,
  submitButtonText = "Submit",
  submitButtonVariant = "contained",
  showTitle = true,
  elevation = 1,
  className,
}: JsonSchemaFormProps<T>) => {
  const handleSubmit = (data: { formData?: T }) => {
    if (onSubmit) {
      onSubmit(data);
    }
  };

  const handleChange = (data: { formData?: T }) => {
    if (onChange) {
      onChange(data);
    }
  };

  const formContent = (
    <Form
      schema={schema}
      uiSchema={uiSchema}
      formData={formData}
      onChange={handleChange}
      onSubmit={handleSubmit}
      validator={validator}
      widgets={widgets}
      showErrorList={showErrorList}
      liveValidate={liveValidate}
      disabled={disabled}
      readonly={readonly}
    >
      {submitButtonText && (
        <Box sx={{ mt: 2 }}>
          <Button type="submit" variant={submitButtonVariant}>
            {submitButtonText}
          </Button>
        </Box>
      )}
    </Form>
  );

  if (elevation === 0) {
    return (
      <Box className={className}>
        {showTitle && title && (
          <Typography variant="h6" gutterBottom>
            {title}
          </Typography>
        )}
        {formContent}
      </Box>
    );
  }

  return (
    <Paper elevation={elevation} sx={{ p: 2 }} className={className}>
      {showTitle && title && (
        <Typography variant="h6" gutterBottom>
          {title}
        </Typography>
      )}
      {formContent}
    </Paper>
  );
};

export { JsonSchemaForm };
