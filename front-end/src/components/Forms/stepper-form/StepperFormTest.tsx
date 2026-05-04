import { Alert, Box, Button, CircularProgress, Paper, Step, StepLabel, Stepper, Typography } from "@mui/material";
import { RJSFSchema, UiSchema } from "@rjsf/utils";
import React, { useEffect, useState } from "react";

import { JsonSchemaForm } from "../../Reusable/JsonSchemaForm/JsonSchemaForm";
import { CompleteFormData, StepConfig, StepFormData } from "../Forms";

import stepperFormData from "./stepperFormSchemas.json";

// Mock API service for stepper form
const stepperApiService = {
  // Fetch form data for all steps
  fetchAllStepsData: async (_userId: string): Promise<CompleteFormData> => {
    await new Promise((resolve) => setTimeout(resolve, 1200));

    return {
      step0: {
        firstName: "Alice",
        lastName: "Johnson",
        email: "alice.johnson@example.com",
        phone: "+1-555-0456",
        birthDate: "1992-08-22",
      },
      step1: {
        jobTitle: "Senior Full Stack Developer",
        company: "Innovation Labs",
        experience: "Senior",
        skills: ["JavaScript", "TypeScript", "React", "Node.js", "Python"],
        salary: 95000,
      },
      step2: {
        workLocation: "Remote",
        availableStartDate: "2025-02-15",
        willingToRelocate: true,
        interests: ["Frontend Development", "Backend Development", "Machine Learning"],
        bio: "Experienced full stack developer with a passion for creating scalable web applications and exploring new technologies.",
        notifications: {
          emailNotifications: true,
          smsNotifications: true,
          jobAlerts: true,
        },
      },
    };
  },

  // Save complete form data
  saveCompleteForm: async (_formData: CompleteFormData): Promise<{ success: boolean; message: string }> => {
    await new Promise((resolve) => setTimeout(resolve, 1000));

    const isSuccess = Math.random() > 0.1; // 90% success rate

    if (isSuccess) {
      return {
        success: true,
        message: "Multi-step form submitted successfully!",
      };
    } else {
      throw new Error("Failed to submit form. Please try again.");
    }
  },

  // Fetch enum options for dynamic fields
  fetchStepEnumOptions: async (): Promise<{
    experience: string[];
    skills: string[];
    workLocation: string[];
    interests: string[];
  }> => {
    await new Promise((resolve) => setTimeout(resolve, 600));

    return {
      experience: ["Intern", "Entry Level", "Junior", "Mid-level", "Senior", "Lead", "Principal", "Distinguished"],
      skills: [
        "JavaScript",
        "TypeScript",
        "React",
        "Vue.js",
        "Angular",
        "Node.js",
        "Python",
        "Java",
        "C#",
        "Go",
        "Rust",
        "Docker",
        "Kubernetes",
        "AWS",
        "GraphQL",
        "MongoDB",
        "PostgreSQL",
      ],
      workLocation: ["Remote", "On-site", "Hybrid", "No preference", "Flexible"],
      interests: [
        "Frontend Development",
        "Backend Development",
        "Full Stack Development",
        "Mobile Development",
        "DevOps",
        "Cloud Computing",
        "Data Science",
        "Machine Learning",
        "Artificial Intelligence",
        "Cybersecurity",
        "UI/UX Design",
        "Product Management",
        "Technical Writing",
        "Open Source",
      ],
    };
  },
};

interface EnumOptions {
  experience: string[];
  skills: string[];
  workLocation: string[];
  interests: string[];
}

interface SchemaProperty {
  properties?: Record<string, unknown>;
}

interface StepFromJson {
  title: string;
  description: string;
  schema: Record<string, unknown>;
  uiSchema: Record<string, unknown>;
}

// Helper functions to update schema enums
const updateProfessionalDetailsEnums = (schema: SchemaProperty, enumOptions: EnumOptions) => {
  if (schema.properties?.experience) {
    (schema.properties.experience as Record<string, unknown>).enum = enumOptions.experience;
  }
  if (schema.properties?.skills) {
    const skillsProperty = schema.properties.skills as Record<string, unknown>;
    if (skillsProperty?.items) {
      (skillsProperty.items as Record<string, unknown>).enum = enumOptions.skills;
    }
  }
};

const updatePreferencesEnums = (schema: SchemaProperty, enumOptions: EnumOptions) => {
  if (schema.properties?.workLocation) {
    (schema.properties.workLocation as Record<string, unknown>).enum = enumOptions.workLocation;
  }
  if (schema.properties?.interests) {
    const interestsProperty = schema.properties.interests as Record<string, unknown>;
    if (interestsProperty?.items) {
      (interestsProperty.items as Record<string, unknown>).enum = enumOptions.interests;
    }
  }
};

const updateSchemaWithEnums = (step: StepFromJson, index: number, enumOptions: EnumOptions): StepConfig => {
  const updatedSchema = JSON.parse(JSON.stringify(step.schema));

  if (index === 1) {
    updateProfessionalDetailsEnums(updatedSchema, enumOptions);
  } else if (index === 2) {
    updatePreferencesEnums(updatedSchema, enumOptions);
  }

  return {
    title: step.title,
    description: step.description,
    schema: updatedSchema as unknown as RJSFSchema,
    uiSchema: step.uiSchema as UiSchema,
  };
};

const StepperFormTest: React.FC = () => {
  const [activeStep, setActiveStep] = useState(0);
  const [formData, setFormData] = useState<CompleteFormData>({});
  const [isLoading, setIsLoading] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [isSchemaLoading, setIsSchemaLoading] = useState(true);
  const [apiMessage, setApiMessage] = useState<{ type: "success" | "error"; text: string } | null>(null);
  const [steps, setSteps] = useState<StepConfig[]>([]);
  const [isFormComplete, setIsFormComplete] = useState(false);
  const [submittedData, setSubmittedData] = useState<CompleteFormData | null>(null);

  // Load dynamic schema with enum options from database
  useEffect(() => {
    const loadDynamicSchema = async () => {
      try {
        const enumOptions = await stepperApiService.fetchStepEnumOptions();
        const dynamicSteps: StepConfig[] = stepperFormData.steps.map((step, index) =>
          updateSchemaWithEnums(step, index, enumOptions),
        );

        setSteps(dynamicSteps);
        setApiMessage({ type: "success", text: "Form schema loaded successfully!" });
      } catch (error) {
        setApiMessage({ type: "error", text: "Failed to load form schema." });
        console.error("Failed to load schema:", error);
      } finally {
        setIsSchemaLoading(false);
      }
    };

    loadDynamicSchema();
  }, []);

  useEffect(() => {
    if (!isSchemaLoading) {
      const loadFormData = async () => {
        setIsLoading(true);
        setApiMessage(null);
        try {
          const data = await stepperApiService.fetchAllStepsData("user123");
          setFormData(data);
          setApiMessage({ type: "success", text: "Form data loaded from database!" });
        } catch (error) {
          setApiMessage({ type: "error", text: "Failed to load form data." });
          console.error("Failed to load form data:", error);
          setFormData(stepperFormData.initialFormData as CompleteFormData);
        } finally {
          setIsLoading(false);
        }
      };

      loadFormData();
    }
  }, [isSchemaLoading]);

  const handleNext = () => {
    setActiveStep((prevActiveStep) => prevActiveStep + 1);
    setApiMessage(null);
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
    setApiMessage(null);
  };

  const handleStepSubmit = (data: { formData?: StepFormData }) => {
    if (data.formData) {
      const stepKey = `step${activeStep}`;
      setFormData((prev: CompleteFormData) => ({
        ...prev,
        [stepKey]: data.formData as StepFormData,
      }));

      if (activeStep === steps.length - 1) {
        // Final step - submit complete form
        handleCompleteFormSubmit({
          ...formData,
          [stepKey]: data.formData,
        });
      } else {
        handleNext();
      }
    }
  };

  const handleCompleteFormSubmit = async (completeData: CompleteFormData) => {
    setIsSaving(true);
    setApiMessage(null);
    try {
      const response = await stepperApiService.saveCompleteForm(completeData);
      setSubmittedData(completeData);
      setIsFormComplete(true);
      setApiMessage({ type: "success", text: response.message });
    } catch (error) {
      setApiMessage({
        type: "error",
        text: error instanceof Error ? error.message : "Failed to submit form.",
      });
      console.error("Failed to submit form:", error);
    } finally {
      setIsSaving(false);
    }
  };

  const handleStepChange = (data: { formData?: StepFormData }) => {
    if (data.formData) {
      const stepKey = `step${activeStep}`;
      setFormData((prev: CompleteFormData) => ({
        ...prev,
        [stepKey]: data.formData as StepFormData,
      }));
    }
  };

  const handleReset = () => {
    setActiveStep(0);
    setFormData(stepperFormData.initialFormData as CompleteFormData);
    setIsFormComplete(false);
    setSubmittedData(null);
    setApiMessage(null);
  };

  const getCurrentStepData = () => {
    const stepKey = `step${activeStep}`;
    return formData[stepKey] || {};
  };

  const getSubmitButtonText = () => {
    if (isSaving) {
      return "Submitting...";
    }
    if (activeStep === steps.length - 1) {
      return "Submit Form";
    }
    return "Next Step";
  };

  if (isLoading || isSchemaLoading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="200px">
        <CircularProgress />
        <Typography variant="body1" sx={{ ml: 2 }}>
          {isSchemaLoading ? "Loading form schema..." : "Loading form data..."}
        </Typography>
      </Box>
    );
  }

  if (isFormComplete) {
    return (
      <Box>
        <Typography variant="h4" component="h1" gutterBottom>
          Form Submission Complete!
        </Typography>

        {apiMessage && (
          <Alert severity={apiMessage.type} sx={{ mb: 3 }}>
            {apiMessage.text}
          </Alert>
        )}

        <Paper elevation={1} sx={{ p: 3, mb: 3 }}>
          <Typography variant="h6" gutterBottom>
            Submitted Data Summary
          </Typography>
          <pre
            style={{
              fontSize: "12px",
              overflow: "auto",
              maxHeight: "400px",
              backgroundColor: "#f5f5f5",
              padding: "15px",
              borderRadius: "4px",
              whiteSpace: "pre-wrap",
            }}
          >
            {JSON.stringify(submittedData, null, 2)}
          </pre>
        </Paper>

        <Button variant="contained" onClick={handleReset}>
          Start New Form
        </Button>
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="h4" component="h1" gutterBottom>
        Multi-Step Form with JSON Schema
      </Typography>

      {apiMessage && (
        <Alert severity={apiMessage.type} sx={{ mb: 3 }}>
          {apiMessage.text}
        </Alert>
      )}

      <Paper elevation={1} sx={{ p: 3 }}>
        {/* Stepper */}
        <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
          {steps.map((step) => (
            <Step key={step.title}>
              <StepLabel>{step.title}</StepLabel>
            </Step>
          ))}
        </Stepper>

        {/* Current Step Content */}
        {steps[activeStep] && (
          <Box>
            <Typography variant="h5" gutterBottom>
              {steps[activeStep].title}
            </Typography>
            <Typography variant="body2" color="textSecondary" sx={{ mb: 3 }}>
              {steps[activeStep].description}
            </Typography>

            <JsonSchemaForm<StepFormData>
              schema={steps[activeStep].schema}
              uiSchema={steps[activeStep].uiSchema}
              formData={getCurrentStepData()}
              onSubmit={handleStepSubmit}
              onChange={handleStepChange}
              showErrorList={false}
              liveValidate={true}
              submitButtonText={getSubmitButtonText()}
              submitButtonVariant="contained"
              disabled={isSaving}
            />

            {/* Navigation Buttons */}
            <Box sx={{ display: "flex", justifyContent: "space-between", mt: 3 }}>
              <Button onClick={handleBack} disabled={activeStep === 0 || isSaving} variant="outlined">
                Back
              </Button>
              <Typography variant="body2" color="textSecondary" sx={{ alignSelf: "center" }}>
                Step {activeStep + 1} of {steps.length}
              </Typography>
              <Box /> {/* Spacer */}
            </Box>
          </Box>
        )}
      </Paper>
    </Box>
  );
};

export default StepperFormTest;
