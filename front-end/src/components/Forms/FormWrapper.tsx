import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import { Button, Step, StepLabel, Stepper } from "@mui/material";
import { useState } from "react";
import validator from "@rjsf/validator-ajv8";
import { Form } from "@rjsf/mui";
import { RJSFValidationError } from "@rjsf/utils";
import { useNavigate } from "react-router-dom";

import { RequestForm, saveForm, StepFormData, submitForm } from "./Forms";
import { widgets } from "./widgets/Widgets";
import DescriptionFieldTemplate from "./templates/DescriptionFieldTemplate";
import { ArrayFieldTemplate } from "./templates/ArrayFieldTemplate";
import { ObjectFieldTemplate } from "./templates/ObjectFieldTemplate";
import { customValidate } from "./validators/CustomValidators";

export const FormWrapper = ({
  requestId,
  form,
  readonly,
}: {
  requestId: number;
  form: RequestForm;
  readonly?: boolean;
}) => {
  const [activeStep, setActiveStep] = useState(0);
  const [formData, setFormData] = useState<StepFormData[]>(form?.data ?? []);
  const navigate = useNavigate();

  const steps = form.schema.steps;

  const isLastStep = () => {
    return activeStep === steps.length - 1;
  };

  const getCurrentStepData = () => {
    return formData[activeStep] || {};
  };

  const handleStepChange = (data: { formData?: StepFormData }) => {
    if (data.formData) {
      const newFormData = [...formData];
      if (formData.length < activeStep) {
        newFormData.push(data.formData);
      } else {
        newFormData[activeStep] = data.formData;
      }
      setFormData(newFormData);
    }
  };

  const handleStepSubmit = async (data: { formData?: StepFormData }) => {
    handleStepChange(data);
    if (activeStep !== steps.length - 1) {
      handleNext();
    } else {
      await submitForm(requestId, formData, form.id);
      navigate(-1);
    }
  };

  const handleNext = () => {
    setActiveStep((prevActiveStep) => prevActiveStep + 1);
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };

  const handleSave = async () => {
    await saveForm(requestId, formData, form.id);
    navigate(-1);
  };

  const transformErrors = (errors: RJSFValidationError[]) => {
    return errors.map((error) => {
      if (error.name === "if") {
        error.message = "";
      } else {
        error.message = "This question is required";
      }
      return error;
    });
  };

  return (
    <Box sx={{ maxWidth: "45vw", margin: "auto", textAlign: "left", mt: "10px" }}>
      <Typography variant="h5" gutterBottom sx={{ textAlign: "center" }}>
        {form.type.description}
      </Typography>
      <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
        {steps.map((step) => (
          <Step key={step.title}>
            <StepLabel>{step.title}</StepLabel>
          </Step>
        ))}
      </Stepper>
      {steps[activeStep] && (
        <Form
          schema={steps[activeStep].schema}
          uiSchema={steps[activeStep].uiSchema}
          formData={getCurrentStepData()}
          onChange={handleStepChange}
          onSubmit={handleStepSubmit}
          validator={validator}
          widgets={widgets}
          showErrorList={false}
          liveValidate={false}
          noHtml5Validate={true}
          transformErrors={transformErrors}
          customValidate={customValidate}
          readonly={readonly ?? false}
          templates={{
            DescriptionFieldTemplate: DescriptionFieldTemplate,
            ArrayFieldTemplate: ArrayFieldTemplate,
            ObjectFieldTemplate: ObjectFieldTemplate,
          }}
        >
          <Box sx={{ mt: 2, display: "flex", justifyContent: "space-between" }}>
            <Button onClick={handleBack} disabled={activeStep === 0} variant={"outlined"}>
              Back
            </Button>
            {!readonly && (
              <Button onClick={handleSave} variant={"outlined"}>
                Save
              </Button>
            )}
            {!isLastStep() && (
              <Button type="submit" variant="outlined">
                Next
              </Button>
            )}
            {isLastStep() &&
              (readonly ? (
                <Button variant="contained" onClick={() => navigate(-1)}>
                  Return
                </Button>
              ) : (
                <Button type="submit" variant="contained">
                  Submit
                </Button>
              ))}
          </Box>
        </Form>
      )}
    </Box>
  );
};
