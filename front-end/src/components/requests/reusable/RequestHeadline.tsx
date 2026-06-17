import { Tag } from "@/common/tags";

interface RequestHeadlineProps {
  value: string;
  completedApproved?: boolean;
}

export const RequestHeadline: React.FC<RequestHeadlineProps> = ({ value, completedApproved }: RequestHeadlineProps) => {
  return <Tag label={value} variant={completedApproved ? "success" : "default"} />;
};
