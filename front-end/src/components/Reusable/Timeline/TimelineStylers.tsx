import { StepConnector, stepConnectorClasses, StepIconProps, styled } from "@mui/material";

export const TimelineConnector = styled(StepConnector)(() => ({
  [`&.${stepConnectorClasses.alternativeLabel}`]: {
    top: 11,
    left: "calc(-50%)",
    right: "calc(50%)",
  },
  [`&.${stepConnectorClasses.completed}, &.${stepConnectorClasses.active}`]: {
    [`& .${stepConnectorClasses.line}`]: {
      borderColor: "var(--color-accent-teal-border)",
    },
  },
  [`& .${stepConnectorClasses.line}`]: {
    borderColor: "var(--color-timeline-line-border)",
    borderTopWidth: "10px",
  },
}));

export const TimelineStartStepIconRoot = styled((props: StepIconProps) => {
  return <div className={props.className} />;
})(() => ({ backgroundColor: "var(--color-accent-teal-border)", width: "8px", height: "32px", zIndex: 1 }));

export const TimelineStepIconRoot = styled("div")<{
  isCurrent: boolean;
  isCompleted: boolean;
  isOverdue: boolean;
  isSkipped: boolean;
}>(() => ({
  color: "#fff",
  backgroundColor: "var(--color-surface-step-base)",
  borderRadius: "999px",
  display: "flex",
  width: "32px",
  height: "32px",
  alignItems: "center",
  justifyContent: "center",
  zIndex: 1,
  variants: [
    {
      props: (props) => props.isCompleted,
      style: {
        backgroundColor: "var(--color-accent-teal-border)",
      },
    },
    {
      props: (props) => props.isCurrent,
      style: {
        border: "3px solid white",
        backgroundColor: "var(--color-accent-teal-border)",
        boxSizing: "content-box",
        marginTop: "-3px",
        boxShadow: "0 0 0 .2rem var(--color-accent-teal-border)",
      },
    },
    {
      props: (props) => !props.isCompleted && !props.isOverdue,
      style: {
        "& .MuiSvgIcon-root": {
          fontSize: "11px",
        },
      },
    },
    {
      props: (props) => props.isCurrent && !props.isCompleted && props.isOverdue,
      style: {
        boxShadow: "0 0 0 .2rem var(--color-error-alert)",
      },
    },
    {
      props: (props) => !props.isCompleted && props.isOverdue && props.isCurrent,
      style: {
        backgroundColor: "var(--color-error-alert)",
      },
    },
    {
      props: (props) => props.isSkipped,
      style: {
        backgroundColor: "var(--color-accent-teal-border)",
      },
    },
  ],
}));
