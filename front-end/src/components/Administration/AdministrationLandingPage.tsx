import ChevronLeftIcon from "@mui/icons-material/ChevronLeft";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import { Box, Divider, Drawer, IconButton, List, styled, Typography, useTheme } from "@mui/material";
import { useState } from "react";
import { Helmet } from "react-helmet-async";

import { PermissionGroup } from "../../PermissionGroup";
import ProtectedComponent from "../../ProtectedComponent";
import { isFormSandboxEnabled } from "../../common/env/env";
import { FormOptionList } from "../Forms/FormOptionList";
import { SimpleFormWrapper } from "../Forms/simple-form/SimpleFormWrapper";
import { StepperFormWrapper } from "../Forms/stepper-form/StepperFormWrapper";

import { ReferenceTypeList } from "./reference-types/ReferenceTypeList";
import { Methodologies } from "./reference-types/methodology/Methodologies";
import { ReportingOptionList } from "./reporting/ReportingOptionList";
import { StatusReport } from "./reporting/status-report/StatusReport";

export enum PageOption {
  METHODOLOGY = "Methodology",
  STATUS_REPORT = "Status Report",
  SIMPLE_FORM = "Simple Form",
  STEPPER_FORM = "Stepper Form",
}

const drawerWidth = 200;

const Main = styled("main", { shouldForwardProp: (prop) => prop !== "open" })<{
  open?: boolean;
}>(({ theme }) => ({
  display: "flex",
  justifyContent: "space-between",
  transition: theme.transitions.create("margin", {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.leavingScreen,
  }),
  marginLeft: 0,
  variants: [
    {
      props: ({ open }) => open,
      style: {
        display: "flex",
        justifyContent: "space-between",
        transition: theme.transitions.create("margin", {
          easing: theme.transitions.easing.easeOut,
          duration: theme.transitions.duration.enteringScreen,
        }),
        marginLeft: `${drawerWidth}px`,
      },
    },
  ],
}));

const Content = styled("div", { shouldForwardProp: (prop) => prop !== "open" })<{
  open?: boolean;
}>(() => ({
  width: "100%",
  margin: "20px 20px 20px 0",
  variants: [
    {
      props: ({ open }) => open,
      style: {
        width: "100%",
        margin: "20px",
      },
    },
  ],
}));

export const AdministrationLandingPage = () => {
  const theme = useTheme();
  const [open, setOpen] = useState<boolean>(true);
  const [page, setPage] = useState<PageOption>();

  return (
    <>
      <Helmet>
        <title>Administration</title>
      </Helmet>
      <Box id="drawer-container">
        <Drawer
          sx={{
            width: drawerWidth,
            flexShrink: 0,
            "& .MuiDrawer-paper": {
              width: drawerWidth,
              boxSizing: "border-box",
              zIndex: 1050,
            },
          }}
          variant="persistent"
          anchor="left"
          open={open}
          PaperProps={{ style: { position: "absolute" } }}
          ModalProps={{
            container: document.getElementById("drawer-container"),
            style: { position: "absolute" },
          }}
        >
          <Box>
            <IconButton data-testid="close-drawer" onClick={() => setOpen(!open)} sx={{ float: "right" }}>
              {theme.direction === "ltr" ? <ChevronLeftIcon /> : <ChevronRightIcon />}
            </IconButton>
          </Box>
          <Divider sx={{ width: "95%", alignSelf: "center" }} />
          <List>
            <ProtectedComponent roles={[PermissionGroup.GS_ADMIN]}>
              <ReferenceTypeList onSelect={(referenceType: PageOption) => setPage(referenceType)} currentPage={page} />
            </ProtectedComponent>
            <ReportingOptionList
              onSelect={(reportingOption: PageOption) => setPage(reportingOption)}
              currentPage={page}
            />
            {isFormSandboxEnabled() && (
              <ProtectedComponent roles={[PermissionGroup.GS_ADMIN]}>
                <FormOptionList onSelect={(formOption: PageOption) => setPage(formOption)} currentPage={page} />
              </ProtectedComponent>
            )}
          </List>
        </Drawer>
      </Box>
      <Main open={open}>
        <Box
          sx={[
            {
              border: "1px solid var(--border-color-admin-collapsed)",
              height: "fit-content",
            },
            open && { display: "none" },
          ]}
        >
          <IconButton aria-label="open drawer" data-testid="open-drawer" onClick={() => setOpen(!open)}>
            <ChevronRightIcon />
          </IconButton>
        </Box>
        <Content open={open}>
          {page == PageOption.METHODOLOGY && <Methodologies />}
          {page == PageOption.STATUS_REPORT && <StatusReport />}
          {page == PageOption.SIMPLE_FORM && <SimpleFormWrapper />}
          {page == PageOption.STEPPER_FORM && <StepperFormWrapper />}
          {!page && <Typography variant="h6">Please select an administrative function</Typography>}
        </Content>
      </Main>
    </>
  );
};
