import { Button, Typography } from "@mui/material";

import { mapResultToColour, piiWithColourHelperText, ProbablePii } from "../../../../common/piiAnalysis/PiiAnalysis";
import CustomTooltipText, { CustomTooltip } from "../../../Reusable/FormFields/CustomTooltipText";

type stringOrNoValue = string | null | undefined;

export const ReviewValueLabelPair = ({
  label,
  value = "",
  onEdit = undefined,
  probablePii = undefined,
}: {
  label: string;
  value: stringOrNoValue;
  onEdit?: () => void;
  probablePii?: ProbablePii[];
}) => {
  const toNotSetIfNull = (input: stringOrNoValue) => {
    if (input) {
      return input;
    }
    return "Not Set";
  };

  const allWordsSplit = value ? value.split(/\b/) : [];

  const matchingPii = (word: string) => {
    return probablePii?.find((pii) =>
      pii.identifiedString.split(/\b/).some((splitPii) => {
        if (word == " ") {
          return false;
        }
        const result = word.toLowerCase().trim() === splitPii.toLowerCase().trim();
        return result;
      }),
    );
  };

  const containsPii = () => {
    const result = allWordsSplit.some((word) => matchingPii(word));
    return result;
  };

  const setReviewLabelText = () => {
    return containsPii()
      ? allWordsSplit
          ?.filter((word): word is string => typeof word === "string")
          .map((word, index) => {
            const matching = matchingPii(word);
            const key = matching ? `probable-${matching?.identifiedString}-${index}` : `non-pii-${word}-${index}`;

            return (
              <span
                key={key}
                style={{
                  color: matching ? mapResultToColour(matching.probability) : undefined,
                  fontWeight: matching ? "bold" : undefined,
                }}
              >
                {word}
              </span>
            );
          })
      : toNotSetIfNull(value);
  };

  return (
    <>
      <Typography variant="body2" gutterBottom align="left">
        <strong>{label}</strong>
      </Typography>
      <Typography variant="body2" gutterBottom align="left" style={{ wordBreak: "break-word", fontSize: "0.8rem" }}>
        <>
          {setReviewLabelText()}
          {containsPii() && value != "" ? (
            <CustomTooltipText text={piiWithColourHelperText} type={CustomTooltip.WARNING} />
          ) : null}
          {onEdit && (
            <Button className="edit-button" data-testid={label} onClick={() => onEdit()}>
              Edit
            </Button>
          )}
        </>
      </Typography>
    </>
  );
};
