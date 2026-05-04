import { Add, Remove, ArrowUpward, ArrowDownward } from "@mui/icons-material";
import { Box, Divider, IconButton, Typography } from "@mui/material";
import { ArrayFieldTemplateProps } from "@rjsf/utils";

export const ArrayFieldTemplate = (props: ArrayFieldTemplateProps) => {
  return (
    <>
      <Box>
        <Typography variant="body2">{props.title}</Typography>
        {props.items.map((element) => (
          <Box key={element.key} sx={{ display: "flex" }}>
            <Box sx={{ width: "100%" }}>
              <Divider sx={{ fontSize: 14, color: "var(--color-text-muted)" }}>Item {element.index + 1}</Divider>
              {element.children}
            </Box>
            <Box sx={{ height: "fit-content", margin: "auto", display: "flex", flexDirection: "column" }}>
              <IconButton
                onClick={element.onReorderClick(element.index, element.index - 1)}
                color="primary"
                disabled={!element.hasMoveUp}
                aria-label={"Move up item " + element.index}
                data-testid={`move-up-item-${element.index}`}
              >
                <ArrowUpward />
              </IconButton>
              <IconButton
                onClick={element.onReorderClick(element.index, element.index + 1)}
                color="primary"
                disabled={!element.hasMoveDown}
                aria-label={"Move down item " + element.index}
                data-testid={`move-down-item-${element.index}`}
              >
                <ArrowDownward />
              </IconButton>
              <IconButton
                onClick={element.onDropIndexClick(element.index)}
                color="primary"
                disabled={!element.hasRemove}
                aria-label={"Remove item " + element.index}
                data-testid={`remove-item-${element.index}`}
              >
                <Remove />
              </IconButton>
            </Box>
          </Box>
        ))}
        {props.canAdd && (
          <Box sx={{ display: "flex", justifyContent: "center" }}>
            <IconButton onClick={props.onAddClick} color="primary" aria-label="Add item" data-testid="add-item">
              <Add />
            </IconButton>
          </Box>
        )}
      </Box>
    </>
  );
};
