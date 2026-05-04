import { CircularProgress, Typography } from "@mui/material";
import { CanceledError } from "axios";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";

import { TimelineActivity } from "../../../common/request/GSRequest";
import { Timeline } from "../../Reusable/Timeline/Timeline";
import { TimelineNode } from "../../Reusable/Timeline/TimelineNodeDisplay";
import { getCurrentPathwayGroupTimeline } from "../Requests";

export const PathwayGroupTimeline = ({ requestId, refreshRequest }: { requestId: number; refreshRequest: boolean }) => {
  const [timelineNodes, setTimelineNodes] = useState<TimelineNode[]>();
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const abortController = new AbortController();

    const toTimelineNodes = (timelineActivities: TimelineActivity[]): TimelineNode[] => {
      if (timelineActivities) {
        return timelineActivities.map((activity) => toTimelineNode(activity));
      }
      return [];
    };

    const toTimelineNode = (activity: TimelineActivity): TimelineNode => {
      return {
        index: activity.index,
        name: activity.name,
        startDate: activity.startDate,
        dueDate: activity.dueDate,
        endDate: activity.endDate,
        completed: activity.completed,
        current: activity.current,
        skipped: activity.skipped,
        customComponent: toExternalResponsibilityComponent(activity.externalResponsibility),
      };
    };

    const toExternalResponsibilityComponent = (external: boolean): JSX.Element => {
      if (!external) {
        return <></>;
      }
      return (
        <>
          <br />
          <Typography variant="caption" color="gray">
            (External)
          </Typography>
        </>
      );
    };

    const fetchCurrentPathwayGroupTimeline = async () => {
      try {
        const response = await getCurrentPathwayGroupTimeline(requestId, abortController.signal);
        setTimelineNodes(toTimelineNodes(response));
      } catch (error) {
        if (!(error instanceof CanceledError)) {
          toast.error("Failed fetching pathway group timeline");
        }
      }
      if (!abortController.signal.aborted) {
        setIsLoading(false);
      }
    };
    fetchCurrentPathwayGroupTimeline();

    return () => {
      abortController.abort();
    };
  }, [requestId, refreshRequest]);

  if (isLoading) return <CircularProgress />;

  if (!timelineNodes) return <>Error loading data</>;

  return <Timeline timelineNodes={timelineNodes} noNodesMessage="No current activities" />;
};
