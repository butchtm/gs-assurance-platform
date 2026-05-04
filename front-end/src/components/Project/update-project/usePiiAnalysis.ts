import { useEffect, useState } from "react";

import { analyseText, ProbablePii, TextAnalysis } from "../../../common/piiAnalysis/PiiAnalysis";
import { Project, ProjectStatus } from "../../../common/project/Project";

interface PiiAnalysisState {
  probablePii: ProbablePii[];
  isAnalyzing: boolean;
  error: string | null;
}

interface PiiAnalysisFields {
  name?: string;
  description?: string;
  notes?: string;
}

export const usePiiAnalysis = (fields: PiiAnalysisFields, project?: Project) => {
  const [piiState, setPiiState] = useState<PiiAnalysisState>({
    probablePii: [],
    isAnalyzing: false,
    error: null,
  });

  const runPiiAnalysis = async (fieldsToAnalyze: PiiAnalysisFields) => {
    const textToAnalyse = [];

    if (fieldsToAnalyze.name?.trim()) {
      textToAnalyse.push({ field: "title", text: fieldsToAnalyze.name });
    }
    if (fieldsToAnalyze.description?.trim()) {
      textToAnalyse.push({ field: "description", text: fieldsToAnalyze.description });
    }
    if (fieldsToAnalyze.notes?.trim()) {
      textToAnalyse.push({ field: "notes", text: fieldsToAnalyze.notes });
    }

    if (textToAnalyse.length === 0) {
      setPiiState({
        probablePii: [],
        isAnalyzing: false,
        error: null,
      });
      return;
    }

    const analysisPayload: TextAnalysis = {
      fieldsToAnalyse: textToAnalyse,
    };

    setPiiState((prev) => ({ ...prev, isAnalyzing: true, error: null }));

    try {
      const result = await analyseText(analysisPayload);
      setPiiState({
        probablePii: result.probablePii || [],
        isAnalyzing: false,
        error: null,
      });
      return result;
    } catch (error) {
      console.error("PII Analysis failed:", error);
      setPiiState({
        probablePii: [],
        isAnalyzing: false,
        error: "Failed to analyze text for PII",
      });
    }
  };

  useEffect(() => {
    if (project?.projectStatus.code === ProjectStatus.New) {
      const debounceTimer = setTimeout(() => {
        runPiiAnalysis(fields);
      }, 500);

      return () => clearTimeout(debounceTimer);
    }
  }, [fields.name, fields.description, fields.notes]);

  return {
    ...piiState,
    hasPii: piiState.probablePii.length > 0,
    getPiiForField: (fieldName: string) => piiState.probablePii.filter((pii) => pii.field === fieldName),
  };
};
