import { Box, Step, StepLabel, Stepper, Typography } from "@mui/material";
import dayjs from "dayjs";

import { TimelineNodeDisplay, TimelineNode } from "./TimelineNodeDisplay";
import { TimelineConnector, TimelineStartStepIconRoot } from "./TimelineStylers";

export const Timeline = ({
  timelineNodes,
  noNodesMessage,
}: {
  timelineNodes: TimelineNode[];
  noNodesMessage: string;
}) => {
  if (timelineNodes.length == 0) return <>{noNodesMessage}</>;

  const toStartedLabel = (node: TimelineNode) => {
    if (node.startDate) {
      return (
        <>
          Started:
          <br />
          {dayjs.utc(node.startDate).format("DD/MM/YYYY")}
        </>
      );
    }
    return <></>;
  };

  return (
    <Stepper alternativeLabel activeStep={1} connector={<TimelineConnector />}>
      <Step>
        <StepLabel StepIconComponent={TimelineStartStepIconRoot}>
          <Box
            sx={{
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
            }}
          >
            <Typography variant="caption" color="gray">
              {toStartedLabel(timelineNodes[0])}
            </Typography>
          </Box>
        </StepLabel>
      </Step>
      {timelineNodes.map((timelineNode) => {
        return <TimelineNodeDisplay key={timelineNode.index} timelineNode={timelineNode} />;
      })}
    </Stepper>
  );
};
