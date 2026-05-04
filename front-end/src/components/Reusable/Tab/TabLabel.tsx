import { Badge, Box } from "@mui/material";

interface TabLabelProps {
  text: string;
  count: number;
}

export const TabLabel: React.FC<TabLabelProps> = ({ text, count }: TabLabelProps) => {
  return (
    <Badge badgeContent={count} color="primary">
      <Box sx={{ mr: count == 0 ? 0 : 1 }}>{text}</Box>
    </Badge>
  );
};
