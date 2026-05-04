import { Check } from "@mui/icons-material";
import { Box, Button, CircularProgress, StepIconProps, StepLabel, Stepper } from "@mui/material";
import { Form, Formik, FormikConfig, FormikHelpers } from "formik";
import React, { Children, createContext, isValidElement, ReactNode, useContext, useEffect, useState } from "react";

import { FormikStepperCustomStep, FormikStepperStepIconRoot } from "./FormikStepperStylers";

interface FormikStepperProps<Values> extends FormikConfig<Values> {
  children: ReactNode;
  // eslint-disable-next-line
  onSubmit: (values: Values, helpers: FormikHelpers<Values>) => Promise<any>;
  changeStep?: number;
  setToLastStep?: boolean;
  enableAll?: boolean;
}

// eslint-disable-next-line
const FormikStepContext = createContext({ setStep: (step: number) => {} });

export function useFormikStepContext() {
  return useContext(FormikStepContext);
}

export const FormikStepper = <Values extends object>({
  children,
  onSubmit,
  changeStep,
  setToLastStep,
  enableAll,
  ...props
}: FormikStepperProps<Values>) => {
  const [currentStep, setCurrentStep] = useState<number>(0);
  const [visitedPages, setVisitedPages] = useState<number[]>([]);

  useEffect(() => {
    if (changeStep !== undefined && changeStep !== currentStep) {
      setCurrentStep(changeStep);
    }
    if (setToLastStep) {
      setCurrentStep(childrenArray.length - 1);
    }
  }, [changeStep]);

  useEffect(() => {
    if (!visitedPages.includes(currentStep)) {
      setVisitedPages([currentStep, ...visitedPages]);
    }
  }, [currentStep]);

  const onClickBack = () => {
    setCurrentStep(currentStep - 1);
    scrollToStep(currentStep - 1);
  };

  const isStepVisited = (step: number) => {
    return visitedPages.includes(step);
  };

  const onClickStep = (step: number) => {
    if (!isStepVisited(step) && !enableAll) return;
    scrollToStep(step);
    setCurrentStep(step);
  };

  const scrollToStep = (step: number) => {
    const stepElement = document.getElementById(`${step}`);
    const stepperElement = document.getElementById("stepper");
    if (stepElement && stepperElement) {
      const absoluteLeft = stepElement.offsetLeft + stepElement.clientWidth / 2;
      const middle = absoluteLeft - stepperElement.clientWidth / 2;
      stepperElement.scrollTo({
        left: middle,
        behavior: "smooth",
      });
    }
  };

  const childrenArray = Children.toArray(children).filter(isValidElement) as React.ReactElement[];
  const currentChild = childrenArray[currentStep];
  const isLastStep = currentStep === childrenArray.length - 1;
  const contextValue = { setStep: setCurrentStep };
  const currentValidationSchema = currentChild.props.validationSchema;

  const getStepIcon = (stepIconProps: StepIconProps) => {
    if (stepIconProps.completed && !stepIconProps.active) {
      return <Check />;
    }
    return stepIconProps.icon;
  };

  const createStepIcon = (stepIconProps: StepIconProps) => {
    return (
      <FormikStepperStepIconRoot stepIconProps={stepIconProps}>{getStepIcon(stepIconProps)}</FormikStepperStepIconRoot>
    );
  };

  return (
    <FormikStepContext.Provider value={contextValue}>
      <Formik<Values>
        {...props}
        validationSchema={currentValidationSchema}
        onSubmit={async (values, helpers) => {
          if (isLastStep) {
            await onSubmit(values, helpers);
          } else {
            setCurrentStep(currentStep + 1);
            scrollToStep(currentStep + 1);
            helpers.setTouched({});
          }
        }}
      >
        {({ isSubmitting }) => (
          <Form autoComplete="off">
            <Stepper
              alternativeLabel
              activeStep={currentStep}
              sx={{ overflowX: `auto`, paddingTop: "5px" }}
              id="stepper"
            >
              {childrenArray.map((child, index) => (
                <FormikStepperCustomStep
                  key={child.key}
                  completed={(isStepVisited(index) || (!!enableAll && enableAll)) && index !== currentStep}
                  disabled={!isStepVisited(index) && !enableAll}
                  onClick={() => onClickStep(index)}
                  id={index.toString()}
                >
                  <StepLabel StepIconComponent={createStepIcon}>{child.props.label}</StepLabel>
                </FormikStepperCustomStep>
              ))}
            </Stepper>

            {currentChild}

            <Box className="wizard-buttons">
              <Button disabled={isSubmitting || currentStep === 0} onClick={onClickBack}>
                Back
              </Button>

              {isLastStep ? (
                <Button
                  startIcon={isSubmitting ? <CircularProgress size="1rem" /> : undefined}
                  disabled={isSubmitting}
                  type="submit"
                >
                  Finish
                </Button>
              ) : (
                <Button type="submit">Next</Button>
              )}
            </Box>
          </Form>
        )}
      </Formik>
    </FormikStepContext.Provider>
  );
};
