import { Check, Circle } from "@mui/icons-material";
import PriorityHighIcon from "@mui/icons-material/PriorityHigh";
import FastForward from "@mui/icons-material/FastForward";
import { Box, Step, StepLabel, Typography } from "@mui/material";
import dayjs from "dayjs";

import { getDaysRemaining, getDaysRemainingFromToday } from "../../../common/DaysDifference";

import { DeadlineDaysWarning } from "./DeadlineDaysWarning";
import { TimelineStepIconRoot } from "./TimelineStylers";

export interface TimelineNode {
  index: number;
  name: string;
  startDate: string;
  dueDate: string;
  endDate?: string;
  completed: boolean;
  current: boolean;
  skipped: boolean;
  customComponent: JSX.Element;
}

export const TimelineNodeDisplay = ({ timelineNode }: { timelineNode: TimelineNode }) => {
  const getActivityDaysRemaining = (node: TimelineNode): number => {
    if (node.completed) {
      if (!node.endDate) {
        return 0;
      }
      return getDaysRemaining(node.dueDate, node.endDate);
    }
    return getDaysRemainingFromToday(node.dueDate);
  };

  const isStepOverDue = (node: TimelineNode): boolean => {
    if (!node.dueDate) {
      return false;
    }
    return getActivityDaysRemaining(node) < 0;
  };

  const getStepIcon = (node: TimelineNode) => {
    if (node.skipped) {
      return <FastForward />;
    }
    if (!node.completed && isStepOverDue(node)) {
      return <PriorityHighIcon />;
    }
    if (node.completed) {
      return <Check />;
    }
    return <Circle />;
  };

  const canDisplayCompleted = (node: TimelineNode): boolean => {
    if (node.completed && node.endDate) {
      return true;
    }
    return false;
  };

  const canDisplayDeadline = (node: TimelineNode): boolean => {
    if (!node.completed && node.dueDate) {
      return true;
    }
    return false;
  };

  const createStepIcon = (node: TimelineNode) => {
    return (
      <TimelineStepIconRoot
        isCurrent={node.current ?? false}
        isCompleted={node.completed ?? false}
        isOverdue={canDisplayDeadline(node) && isStepOverDue(node)}
        isSkipped={node.skipped ?? false}
      >
        {getStepIcon(node)}
      </TimelineStepIconRoot>
    );
  };

  const toCompletedLabel = (node: TimelineNode) => {
    if (node.skipped) {
      return "Skipped:";
    }
    return "Completed:";
  };

  return (
    <Step completed={timelineNode.completed} active={timelineNode.current}>
      <StepLabel StepIconComponent={() => createStepIcon(timelineNode)}>
        <Box
          sx={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
          }}
        >
          <p>
            {timelineNode.name}
            {timelineNode.customComponent}
          </p>
          {canDisplayCompleted(timelineNode) && (
            <Typography variant="caption" color="gray">
              <span>{toCompletedLabel(timelineNode)}</span>
              <br />
              <span>{dayjs.utc(timelineNode.endDate).format("DD/MM/YYYY")}</span>
            </Typography>
          )}
          {canDisplayDeadline(timelineNode) && (
            <Typography variant="caption" color="gray">
              <span>Deadline:</span>
              <br />
              <span>{dayjs.utc(timelineNode.dueDate).format("DD/MM/YYYY")}</span>
            </Typography>
          )}

          {(canDisplayDeadline(timelineNode) || isStepOverDue(timelineNode)) && (
            <DeadlineDaysWarning
              isCompleted={timelineNode.completed}
              daysRemaining={getActivityDaysRemaining(timelineNode)}
            />
          )}
        </Box>
      </StepLabel>
    </Step>
  );
};
