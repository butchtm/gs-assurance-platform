import { Alert, Box, Button, CircularProgress, Grid, Paper, Typography } from "@mui/material";
import { RJSFSchema, UiSchema } from "@rjsf/utils";
import React, { useEffect, useState } from "react";

import { JsonSchemaForm } from "../../Reusable/JsonSchemaForm/JsonSchemaForm";

import formSchemasData from "./formSchemas.json";

const mockApiService = {
  // Simulate fetching enum options from database
  fetchEnumOptions: async (): Promise<{
    skills: string[];
    experience: string[];
    favoriteColors: string[];
    preferredLanguages: string[];
  }> => {
    await new Promise((resolve) => setTimeout(resolve, 500));

    // Mock enum data from database
    return {
      skills: [
        "JavaScript",
        "TypeScript",
        "React",
        "Node.js",
        "Python",
        "Java",
        "C#",
        "Go",
        "Rust",
        "Angular",
        "Vue.js",
        "Docker",
      ],
      experience: ["Intern", "Junior", "Mid-level", "Senior", "Lead", "Principal"],
      favoriteColors: [
        "Red",
        "Blue",
        "Green",
        "Yellow",
        "Purple",
        "Orange",
        "Pink",
        "Brown",
        "Black",
        "White",
        "Gray",
        "Turquoise",
        "Crimson",
        "Navy",
        "Lime",
        "Indigo",
      ],
      preferredLanguages: [
        "JavaScript",
        "TypeScript",
        "Python",
        "Java",
        "C#",
        "C++",
        "Go",
        "Rust",
        "PHP",
        "Ruby",
        "Swift",
        "Kotlin",
        "Dart",
        "Scala",
        "Haskell",
        "F#",
        "Clojure",
      ],
    };
  },

  fetchFormData: async (_userId: string): Promise<TestFormData> => {
    await new Promise((resolve) => setTimeout(resolve, 1000));

    const mockData: TestFormData = {
      firstName: "Jane",
      lastName: "Smith",
      email: "jane.smith@example.com",
      age: 28,
      bio: "Full-stack developer with 5 years of experience building web applications.",
      skills: ["JavaScript", "TypeScript", "React", "Node.js"],
      experience: "Advanced",
      favoriteColors: ["Purple", "Turquoise"],
      preferredLanguages: ["TypeScript", "Python", "Go"],
      birthDate: "1996-07-22",
      startDate: "2025-03-01",
      settings: {
        newsletter: true,
        notifications: false,
      },
    };

    return mockData;
  },

  saveFormData: async (_formData: TestFormData): Promise<{ success: boolean; id: string; message: string }> => {
    await new Promise((resolve) => setTimeout(resolve, 800));

    const isSuccess = Math.random() > 0.1; // 90% success rate

    if (isSuccess) {
      return {
        success: true,
        id: `user_${Date.now()}`,
        message: "Form data saved successfully to database!",
      };
    } else {
      throw new Error("Failed to save data to database. Please try again.");
    }
  },
};

interface TestFormData extends Record<string, unknown> {
  firstName?: string;
  lastName?: string;
  email?: string;
  age?: number;
  bio?: string;
  skills?: string[];
  experience?: string;
  favoriteColors?: string[];
  preferredLanguages?: string[];
  birthDate?: string;
  startDate?: string;
  settings?: {
    newsletter?: boolean;
    notifications?: boolean;
  };
}

// Load schemas from JSON file
const schema: RJSFSchema = formSchemasData.schema as RJSFSchema;
const uiSchema: UiSchema = formSchemasData.uiSchema as UiSchema;
const initialFormData: TestFormData = formSchemasData.initialFormData as TestFormData;

const JsonSchemaFormTest: React.FC = () => {
  const [formData, setFormData] = useState<TestFormData>(initialFormData);
  const [submittedData, setSubmittedData] = useState<TestFormData | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [apiMessage, setApiMessage] = useState<{ type: "success" | "error"; text: string } | null>(null);
  const [dynamicSchema, setDynamicSchema] = useState<RJSFSchema>(schema);
  const [isSchemaLoading, setIsSchemaLoading] = useState(true);

  useEffect(() => {
    const loadEnumOptions = async () => {
      try {
        const enumOptions = await mockApiService.fetchEnumOptions();

        // Build dynamic schema with database-loaded enums
        const updatedSchema: RJSFSchema = {
          ...schema,
          properties: {
            ...schema.properties,
            skills: {
              ...(schema.properties?.skills as Record<string, unknown>),
              items: {
                type: "string",
                enum: enumOptions.skills,
              },
            },
            experience: {
              ...(schema.properties?.experience as Record<string, unknown>),
              enum: enumOptions.experience,
            },
            favoriteColors: {
              ...(schema.properties?.favoriteColors as Record<string, unknown>),
              items: {
                type: "string",
                enum: enumOptions.favoriteColors,
              },
            },
            preferredLanguages: {
              ...(schema.properties?.preferredLanguages as Record<string, unknown>),
              items: {
                type: "string",
                enum: enumOptions.preferredLanguages,
              },
            },
          },
        };

        setDynamicSchema(updatedSchema);
        setApiMessage({ type: "success", text: "Schema options loaded from database successfully!" });
      } catch (error) {
        setApiMessage({ type: "error", text: "Failed to load schema options from database." });
        console.error("Failed to load enum options:", error);
      } finally {
        setIsSchemaLoading(false);
      }
    };

    loadEnumOptions();
  }, []);

  useEffect(() => {
    const loadFormData = async () => {
      setIsLoading(true);
      setApiMessage(null);
      try {
        const data = await mockApiService.fetchFormData("user123");
        setFormData(data);
        setApiMessage({ type: "success", text: "Form data loaded from database successfully!" });
      } catch (error) {
        setApiMessage({ type: "error", text: "Failed to load form data from database." });
        console.error("Failed to load form data:", error);
      } finally {
        setIsLoading(false);
      }
    };

    loadFormData();
  }, []);

  const handleSubmit = async (data: { formData?: TestFormData }) => {
    if (data.formData) {
      setIsSaving(true);
      setApiMessage(null);
      try {
        const response = await mockApiService.saveFormData(data.formData);
        setSubmittedData(data.formData);
        setApiMessage({ type: "success", text: response.message });
      } catch (error) {
        setApiMessage({
          type: "error",
          text: error instanceof Error ? error.message : "Failed to save form data.",
        });
        console.error("Failed to save form data:", error);
      } finally {
        setIsSaving(false);
      }
    }
  };

  const handleChange = (data: { formData?: TestFormData }) => {
    if (data.formData) {
      setFormData(data.formData);
    }
  };

  const handleLoadNewData = async () => {
    setIsLoading(true);
    setApiMessage(null);
    setSubmittedData(null);
    try {
      const data = await mockApiService.fetchFormData("user456");
      setFormData(data);
      setApiMessage({ type: "success", text: "New form data loaded from database!" });
    } catch (error) {
      setApiMessage({ type: "error", text: "Failed to load new form data from database." });
      console.error("Failed to load form data:", error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleRefreshEnumOptions = async () => {
    setIsSchemaLoading(true);
    setApiMessage(null);
    try {
      const enumOptions = await mockApiService.fetchEnumOptions();

      // Rebuild schema with fresh enum options
      const updatedSchema: RJSFSchema = {
        ...schema,
        properties: {
          ...schema.properties,
          skills: {
            ...(schema.properties?.skills as Record<string, unknown>),
            items: {
              type: "string",
              enum: enumOptions.skills,
            },
          },
          experience: {
            ...(schema.properties?.experience as Record<string, unknown>),
            enum: enumOptions.experience,
          },
          favoriteColors: {
            ...(schema.properties?.favoriteColors as Record<string, unknown>),
            items: {
              type: "string",
              enum: enumOptions.favoriteColors,
            },
          },
          preferredLanguages: {
            ...(schema.properties?.preferredLanguages as Record<string, unknown>),
            items: {
              type: "string",
              enum: enumOptions.preferredLanguages,
            },
          },
        },
      };

      setDynamicSchema(updatedSchema);
      setApiMessage({ type: "success", text: "Enum options refreshed from database!" });
    } catch (error) {
      setApiMessage({ type: "error", text: "Failed to refresh enum options from database." });
      console.error("Failed to refresh enum options:", error);
    } finally {
      setIsSchemaLoading(false);
    }
  };

  if (isLoading || isSchemaLoading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="200px">
        <CircularProgress />
        <Typography variant="body1" sx={{ ml: 2 }}>
          {isSchemaLoading ? "Loading form schema from database..." : "Loading form data from database..."}
        </Typography>
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="h5" component="h2" gutterBottom>
        JsonSchemaForm Component with Mock API Integration
      </Typography>

      {apiMessage && (
        <Alert severity={apiMessage.type} sx={{ mb: 3 }}>
          {apiMessage.text}
        </Alert>
      )}

      <Box sx={{ mb: 3, display: "flex", gap: 2 }}>
        <Button variant="outlined" onClick={handleLoadNewData} disabled={isLoading || isSchemaLoading}>
          Load New Data from API
        </Button>
        <Button variant="outlined" onClick={handleRefreshEnumOptions} disabled={isLoading || isSchemaLoading}>
          Refresh Enum Options
        </Button>
      </Box>

      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Typography variant="h6" gutterBottom>
            Contained Button Variant
          </Typography>
          <JsonSchemaForm<TestFormData>
            schema={dynamicSchema}
            uiSchema={uiSchema}
            formData={formData}
            title="Form with Contained Button"
            onSubmit={handleSubmit}
            onChange={handleChange}
            showErrorList={false}
            liveValidate={true}
            submitButtonText={isSaving ? "Saving..." : "Save to Database (Contained)"}
            submitButtonVariant="contained"
            disabled={isSaving}
          />
        </Grid>

        <Grid item xs={12} md={6}>
          <Typography variant="h6" gutterBottom>
            Outlined Button Variant
          </Typography>
          <JsonSchemaForm<TestFormData>
            schema={dynamicSchema}
            uiSchema={uiSchema}
            formData={formData}
            title="Form with Outlined Button"
            onSubmit={handleSubmit}
            onChange={handleChange}
            showErrorList={false}
            liveValidate={true}
            submitButtonText={isSaving ? "Saving..." : "Save to Database (Outlined)"}
            submitButtonVariant="outlined"
            disabled={isSaving}
          />
        </Grid>

        <Grid item xs={12}>
          {submittedData && (
            <Paper elevation={1} sx={{ p: 2 }}>
              <Typography variant="h6" gutterBottom>
                Last Saved Data
              </Typography>
              <Alert severity="success" sx={{ mb: 2 }}>
                Form data saved to database successfully!
              </Alert>
              <pre
                style={{
                  fontSize: "12px",
                  overflow: "auto",
                  maxHeight: "200px",
                  backgroundColor: "#f5f5f5",
                  padding: "10px",
                  borderRadius: "4px",
                }}
              >
                {JSON.stringify(submittedData, null, 2)}
              </pre>
            </Paper>
          )}
        </Grid>
      </Grid>
    </Box>
  );
};

export default JsonSchemaFormTest;
