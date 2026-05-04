import { Step, StepIconProps, styled } from "@mui/material";

export const FormikStepperCustomStep = styled(Step)({
  minWidth: "100px",
  display: "flex",
  alignItems: "center",
  flexDirection: "column",
  "& .MuiStepLabel-label": {
    marginTop: "9px",
    padding: "2px 10px",
    borderRadius: "50px",
    display: "inline-block",
  },
  "& .MuiStepLabel-label.Mui-active": {
    background: "var(--color-accent-teal-primary)",
    color: "#fff !important",
  },
  "&:hover": {
    cursor: "pointer",
    "& .MuiStepLabel-label:not(.Mui-active):not(.Mui-disabled)": {
      background: "var(--color-accent-teal-light)",
      color: "#fff !important",
    },
    "& .MuiStepLabel-iconContainer:not(.Mui-active):not(.Mui-disabled) div": {
      background: "var(--color-accent-teal-light)",
    },
  },
});

export const FormikStepperStepIconRoot = styled("div")<{
  stepIconProps: StepIconProps;
}>(() => ({
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
  background: "rgba(0, 0, 0, 0.38)",
  fontSize: "small",
  color: "#000",
  borderRadius: "999px",
  userSelect: "none",
  width: "24px",
  height: "24px",
  zIndex: 1,
  variants: [
    {
      props: ({ stepIconProps }) => stepIconProps.active,
      style: {
        background: "var(--color-accent-teal-primary)",
        color: "#fff",
        border: "2px solid #ffffff",
        marginTop: "-1px",
        boxSizing: "content-box",
        boxShadow: "0px 0px 0px 0.1rem var(--color-accent-teal-primary)",
      },
    },
    {
      props: ({ stepIconProps }) => stepIconProps.completed,
      style: {
        backgroundColor: "var(--color-accent-teal-medium)",
        color: "#fff",
        "& .MuiSvgIcon-root": {
          fontSize: "20px",
        },
      },
    },
  ],
}));
