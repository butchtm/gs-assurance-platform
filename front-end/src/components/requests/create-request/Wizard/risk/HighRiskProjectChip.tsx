import { WarningAmber } from "@mui/icons-material";
import { Chip, Tooltip } from "@mui/material";

interface ItemProps {
  className: string;
  disabled: boolean;
  "data-tag-index": number;
  tabIndex: -1;
  onDelete: (event: unknown) => void;
}

export const HighRiskProjectChip = ({
  key,
  label,
  itemProps,
}: {
  key: number;
  label: string;
  itemProps: ItemProps;
}) => {
  return (
    <Chip
      key={key}
      label={label}
      icon={
        <Tooltip title={"This project has been deemed high risk"}>
          <WarningAmber sx={{ fontSize: 20, color: (theme) => theme.palette.secondary.main }} />
        </Tooltip>
      }
      sx={{
        backgroundColor: "var(--color-risk-high-bg)",
      }}
      {...itemProps}
    />
  );
};
