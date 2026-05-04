import ReactMarkdown from "react-markdown";
import { type DescriptionFieldProps } from "@rjsf/utils";
import { Typography } from "@mui/material";
import React from "react";

const aTemplate = ({ href, children }: { href?: string; children?: React.ReactNode }) => (
  <a
    href={href ?? "#"}
    target="_blank"
    rel="noopener noreferrer"
    style={{ color: "#1976d2", textDecoration: "underline" }}
  >
    {children}
  </a>
);

const pTemplate = ({ children }: { children?: React.ReactNode }) => (
  <Typography variant="caption" sx={{ mt: "5px" }}>
    {children}
  </Typography>
);

function DescriptionFieldTemplate(props: DescriptionFieldProps) {
  const { description } = props;
  return (
    <ReactMarkdown
      components={{
        a: aTemplate,
        p: pTemplate,
      }}
    >
      {String(description)}
    </ReactMarkdown>
  );
}

export default DescriptionFieldTemplate;
