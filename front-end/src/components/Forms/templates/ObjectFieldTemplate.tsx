import { Box, Typography } from "@mui/material";
import { ObjectFieldTemplateProps } from "@rjsf/utils";

export const ObjectFieldTemplate = (props: ObjectFieldTemplateProps) => {
  return (
    <>
      {!!props.title && (
        <Typography sx={{ mb: "5px", color: "var(--color-text-bold)" }}>
          {props.required ? (
            <>
              {props.title} <span aria-hidden="true">*</span>
            </>
          ) : (
            props.title
          )}
        </Typography>
      )}
      {props.properties.map((element) => (
        <Box key={element.name} sx={{ pt: "16px", mb: "10px", width: "100%" }}>
          {element.content}
        </Box>
      ))}
    </>
  );
};
