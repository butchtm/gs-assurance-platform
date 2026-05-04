import { apiClient } from "../../components/APIClient/Client";
import { Project } from "../project/Project";

import { PiiAction } from "./PiiAction";

export interface TextAnalysis {
  fieldsToAnalyse: FieldTextAnalysis[];
}

interface FieldTextAnalysis {
  field: string;
  text: string;
}

export interface TextAnalysisResponse {
  probablePii: ProbablePii[];
}

export interface ProbablePii {
  entity: string;
  probability: string;
  identifiedString: string;
  field?: string;
}

export interface PiiMetadata {
  piiPresent: boolean;
  action: PiiAction | null;
  piiDetail: string[];
}

export const analyseText = (textToAnalyse: TextAnalysis): Promise<TextAnalysisResponse> => {
  return apiClient.makeRequest("/pii-analysis", "POST", JSON.stringify(textToAnalyse));
};

export const publicAnalyseText = (textToAnalyse: TextAnalysis): Promise<TextAnalysisResponse> => {
  return apiClient.makeRequest("/public/pii-analysis", "POST", JSON.stringify(textToAnalyse));
};

export const removeDuplicatePii = (pii: ProbablePii[]): ProbablePii[] => {
  const noDuplicatesPii: ProbablePii[] = [];
  pii.forEach((p) => {
    if (!noDuplicatesPii.some((d) => d.identifiedString === p.identifiedString)) {
      noDuplicatesPii.push(p);
    }
  });
  return noDuplicatesPii;
};

export const highProbability = "var(--color-pii-high)";
export const mediumProbability = "var(--color-pii-medium)";
export const lowProbability = "var(--color-pii-low)";

export const mapResultToColour = (result: string | undefined) => {
  if (result === "HIGH") {
    return highProbability;
  }
  if (result === "MEDIUM") {
    return mediumProbability;
  }
  if (result === "LOW") {
    return lowProbability;
  }
};

export const piiHelperText = (
  <>
    PII, or Personally Identifiable Information, is any data that can be used to identify an individual, such as names,
    email addresses and phone numbers.
    <br />
    <br />
    It is important because mishandling PII can lead to to privacy breaches, identity theft, and legal consequences.
    Protecting PII is essential for maintaining trust, complying with regulations, and ensuring data security.
    <br />
    <br />
    Always review your input to include only the necessary information and avoid sharing sensitive data unless
    absolutely required.
  </>
);

export const publicSwitchDisabledPiiHelperText = (
  <>This Document cannot be made public due to it currently being scanned for PII or containing PII.</>
);

export const piiForReviewHelperText = (
  <>This text has been identified as potentially containing PII. Please review it carefully.</>
);

export const piiWithColourHelperText = (
  <>
    {piiHelperText}
    <br />
    <br />
    <table style={{ textAlign: "center", borderCollapse: "collapse" }}>
      <tr>
        <th>Colour</th>
        <th>Probability of being PII</th>
      </tr>
      <tr style={{ color: highProbability }}>
        <td>Purple</td>
        <td>High</td>
      </tr>
      <tr style={{ color: mediumProbability }}>
        <td>Red</td>
        <td>Medium</td>
      </tr>
      <tr style={{ color: lowProbability }}>
        <td>Orange</td>
        <td>Low</td>
      </tr>
    </table>
  </>
);

export const checkPiiToolTip = (validFields: boolean): string => {
  if (!validFields) {
    return "Please fill out fields before checking for PII";
  }
  return "";
};

export const submitCommentToolTip = (
  hasCheckedPii: boolean,
  existsProbablePii: boolean,
  understandsPii: boolean,
): string => {
  if (!hasCheckedPii) {
    return "Please check comment for PII before submitting";
  }
  if (existsProbablePii && !understandsPii) {
    return "Please confirm you understand this comment contains PII before submitting";
  }
  return "";
};

export const fieldIncludesPii = (project: Project, field: string) => {
  if (!project?.piiMetadata?.piiPresent) {
    return false;
  }
  if (!project.piiMetadata?.piiDetail) {
    return false;
  }
  return project.piiMetadata.piiDetail.some((detail) => detail === field);
};
