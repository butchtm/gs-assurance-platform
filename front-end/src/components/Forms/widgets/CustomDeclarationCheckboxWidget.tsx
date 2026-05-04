import { type WidgetProps } from "@rjsf/utils";
import { Checkbox, FormControlLabel, Box, List, ListItem, ListItemText, FormGroup, Typography } from "@mui/material";

interface CustomDeclarationCheckboxWidgetOptions {
  points: string[];
  summary: string;
}

type CheckboxWithDescriptionWidgetProps = WidgetProps & {
  options: WidgetProps["options"] & CustomDeclarationCheckboxWidgetOptions;
};

export default function CustomDeclarationCheckboxWidget(props: CheckboxWithDescriptionWidgetProps) {
  const options = props.options;
  const points = options.points;
  const summary = options.summary;
  return (
    <Box>
      <FormGroup>
        <FormControlLabel
          control={
            <Checkbox
              id={props.id}
              checked={!!props.value}
              required={props.required}
              disabled={props.disabled || props.readonly}
              onChange={(event) => props.onChange(event.target.checked)}
            />
          }
          label={props.label}
          labelPlacement="end"
        />
      </FormGroup>
      {points && (
        <List sx={{ listStyle: "decimal", pl: 4 }}>
          {points.map((point) => (
            <ListItem sx={{ display: "list-item" }} key={point}>
              <ListItemText>{point}</ListItemText>
            </ListItem>
          ))}
        </List>
      )}
      {summary && <Typography>{summary}</Typography>}
    </Box>
  );
}
