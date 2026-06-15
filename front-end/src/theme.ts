import "@fontsource/open-sans/400.css";
import { createTheme, styled } from "@mui/material/styles";

const theme = createTheme({
  palette: {
    primary: {
      main: "rgb(0, 186, 190)",
    },
    secondary: {
      main: "rgb(33, 33, 33)",
    },
    success: {
      main: "rgb(46, 125, 50)",
      light: "rgb(209, 250, 209)",
    },
    warning: {
      main: "rgb(237, 108, 2)",
      light: "rgb(255, 220, 189)",
    },
    error: {
      main: "rgb(211, 47, 47)",
      light: "rgb(255, 184, 184)",
    },
  },
  typography: {
    fontFamily: "Open Sans, Verdana, Geneva, Tahoma, sans-serif",
    h1: {
      fontSize: "2rem",
    },
    body1: {
      fontSize: "16px",
    },
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: "7px",
          border: "1px solid",
          transition: "background-color 0.3s, color 0.3s",
          "&.edit-button": {
            marginLeft: "10px",
            color: "#00babe",
            textTransform: "none",
            padding: "2px 6px",
            border: "0px solid",
            "&:hover": {
              color: "#2a9191",
              backgroundColor: "transparent",
            },
          },
        },
      },
    },
    MuiTab: {
      styleOverrides: {
        root: {
          textTransform: "none",
          color: "black",
          fontSize: "16px",
          "&.Mui-selected": {
            color: "teal",
          },
        },
      },
    },
    MuiTabs: {
      styleOverrides: {
        indicator: {
          backgroundColor: "teal",
        },
      },
    },
    MuiBadge: {
      styleOverrides: {
        badge: {
          color: "white",
        },
      },
    },
    MuiTableCell: {
      styleOverrides: {
        root: {
          padding: "5px !important",
          borderBottom: "1px solid #d3d3d3",
        },
        head: {
          color: "grey",
          textTransform: "uppercase",
          fontWeight: "700 !important",
          fontSize: "13px !important",
          fontFamily: "Open Sans, Verdana, Geneva, Tahoma, sans-serif",
        },
      },
    },
    MuiFormLabel: {
      styleOverrides: {
        root: {
          "&.Mui-focused": {
            color: "(rgb (0, 186, 190))",
          },
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          "& .MuiOutlinedInput-root": {
            "& fieldset": {
              borderColor: "rgba(0, 0, 0, 0.87)",
            },
            "&:hover fieldset": {
              borderColor: "rgb(0, 186, 190)",
            },
            "&.Mui-focused fieldset": {
              borderColor: "rgb(0, 186, 190)",
            },
          },
          "& .MuiInputLabel-root": {
            fontWeight: "300",
            "&.Mui-focused": {
              color: "rgb(0, 186, 190)",
            },
          },
        },
      },
    },
    MuiModal: {
      styleOverrides: {
        root: {
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
        },
      },
    },
    MuiAutocomplete: {
      styleOverrides: {
        tag: {
          maxWidth: "600px",
        },
      },
    },
  },
});

const GlobalStylesComponent = styled("div")({
  "--border-color-table-row": "#e0e0e0",
  "--border-color-admin-collapsed": "#bebebe",
  "--color-text-bold": "#616161",
  "--color-text-dark-gray": "#2E2E2E",
  "--color-cookie-consent-bg": "#f8f9fa",
  "--color-cookie-consent-text": "#333333",
  "--color-cookie-consent-button-bg": "#007bff",
  "--color-cookie-consent-button-hover-bg": "#0056b3",
  "--color-text-white": "#ffffff",
  "--shadow-soft": "0px 0px 10px rgba(0, 0, 0, 0.1)",
  "--color-risk-high-bg": "#ffdad6",
  "--color-risk-high-border": "#BA1A1A",
  "--color-text-pii-warning-action": "#dd4242",
  "--color-pii-high": "#9932cc",
  "--color-pii-medium": "#e50000",
  "--color-pii-low": "#d43900",
  "--color-button-disabled-bg": "#e0e0e0",
  "--color-button-disabled-text": "#9e9e9e",
  "--color-accent-teal-dark": "#088b8d",
  "--color-text-muted": "#6c6c6c",
  "--color-warning-soft": "#CF4A02",
  "--color-surface-card": "#f4f4f4",
  "--border-color-card": "#dddddd",
  "--color-chip-bg-neutral": "#e0e0e0",
  "--color-text-black": "#000000",
  "--color-error-surface-light": "#ffe6e6",
  "--color-scrollbar-track": "#f1f1f1",
  "--color-risk-high-thumb-hover": "#8B0000",
  "--color-pii-tooltip-warning": "#8A2500",
  "--color-accent-teal-border": "#00AEB8",
  "--color-surface-accent-teal-tint": "#ebfafa",
  "--color-text-muted-dark": "#595959",
  "--color-table-header": "#9e9e9e",
  "--color-accent-teal-primary": "#00babe",
  "--color-accent-teal-medium": "#2a9191",
  "--color-accent-teal-light": "#92caca",
  "--color-timeline-line-border": "#eaeaf0",
  "--color-surface-step-base": "#d3d3d3",
  "--color-error-alert": "#ff0000",
  "--color-access-public-bg": "#46b446",
  "--color-access-private-bg": "#dd4242",
  "--color-surface-neutral": "#e2e2e2",
  "--color-surface-deadline-default": "#d3d3d3",
  "--color-tag-default": "#d3d3d3",
  "--color-tag-paa": "#00AEFF",
  // APP CONTAINER
  ".App": {
    textAlign: "center",
    fontFamily: "Open Sans, Verdana, Geneva, Tahoma, sans-serif",
    display: "flex",
    flexDirection: "column",
    minHeight: "100vh",
  },
  // CONTENT CONTAINER
  ".content": {
    display: "flex",
    flexDirection: "column",
    flex: 1,
    marginBottom: "15px",
  },
  // BODY CONTENT
  ".body": {
    position: "relative",
    flexGrow: 1,
    backgroundColor: "rgb(255, 255, 255)",
  },
  // BODY ELEMENT
  body: {
    margin: 0,
    padding: 0,
    display: "flex",
    flexDirection: "column",
    minHeight: "100vh",
  },

  // OTHER BUTTONS
  ".other-button, .pagination-button, .wizard-buttons button": {
    backgroundColor: "transparent",
    fontWeight: 600,
    border: "1px solid",
    borderRadius: "7px",
    padding: "5px 10px",
    cursor: "pointer",
    transition: "background-color 0.3s, color 0.3s",
    marginRight: "5px",
  },
  // TABLE HEADINGS
  ".table-headings": {
    color: "#000",
    fontWeight: "bold",
    textWrap: "balance",
  },
  // TABLE ROWS
  ".table-row": {
    "&:hover": {
      backgroundColor: "#e0e0e0 !important",
    },
  },
  ".request-table": {
    padding: "10px",
  },
  // MODAL OVERLAY
  ".modal-overlay": {
    position: "fixed",
    top: 0,
    left: 0,
    width: "100%",
    height: "100%",
    background: "rgba(0, 0, 0, 0.5)",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
  },
  // MODAL
  ".modal": {
    background: "white",
    width: "600px",
    padding: "20px",
    borderRadius: "8px",
  },
  // MODAL HEADER
  ".modal-header": {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
  },
  // VERTICAL FLEXBOX
  ".vertical-flexbox": {
    display: "flex",
    flexDirection: "column",
  },
  // HORIZONTAL FLEXBOX
  ".horizontal-flexbox": {
    display: "flex",
  },
  // BUTTON BAR
  ".button-bar": {
    margin: "1%",
  },
  // CHANGE PAGE BUTTON
  ".change-page-button": {
    marginLeft: "1%",
  },
  // TICK ICON AND CROSS ICON
  ".tickIcon, .crossIcon": {
    color: "black",
    fontWeight: "bold",
  },
  // FULL-WIDTH ELEMENT
  ".full-width": {
    width: "100%",
    maxWidth: "none",
  },
  // ENVELOPE ICON
  ".envelope-icon svg": {
    transform: "scale(1.2)",
    color: "#088b8d",
    marginLeft: "10px",
  },
  // ENVELOPE ICON ON HOVER
  ".envelope-icon:hover": {
    color: "#00babe",
  },
  // WIZARD CARD
  ".wizard-card": {
    marginTop: "50px",
    padding: "20px",
    borderRadius: "10px",
    boxShadow: "0px 0px 10px rgba(0, 0, 0, 0.1)",
  },
  // WIZARD CARD HEADER
  ".wizard-card h4": {
    fontSize: "24px",
    marginBottom: "10px",
    color: "#4a6666",
  },
  // WIZARD CARD SUBTITLE
  ".wizard-card .subtitle1": {
    fontSize: "16px",
    color: "#666",
  },
  // WIZARD FIELD
  ".wizard-field": {
    marginTop: "10px",
  },
  // WIZARD BUTTONS
  ".wizard-buttons": {
    display: "flex",
    justifyContent: "flex-end",
    marginTop: "20px",
  },
  // WIZARD BUTTONS ON HOVER
  ".wizard-buttons button:hover": {
    backgroundColor: "#4a6666",
    color: "#fff",
  },
  // MUI FORM LABEL IN INPUT FIELDS
  ".input-fields .MuiFormLabel-root": {
    fontWeight: 300,
  },
  // MUI INPUT BASE IN INPUT FIELDS
  ".input-fields .MuiInputBase-root": {
    backgroundColor: "#fff",
    border: "0.5px solid #ececec",
    borderRadius: "3px",
  },
  // MUI INPUT BASE INPUT IN INPUT FIELDS
  ".input-fields .MuiInputBase-input": {
    color: "#4a6666",
  },
  // MUI FORM HELPER TEXT IN INPUT FIELDS
  ".input-fields .MuiFormHelperText-root": {
    fontSize: "12px",
    color: "#ff0000",
  },
  // CUSTOM INPUT LABEL WHEN FOCUSED
  ".custom-input label.Mui-focused": {
    color: "green",
  },
  // SELECT FIELD IN INPUT FIELDS
  ".input-fields .select-field": {
    width: "100%",
  },
  // VIEW CONTAINER
  ".view-container": {
    marginLeft: "65px",
    marginRight: "65px",
    marginTop: "80px",
  },
  // VIEW CONTAINER TITLE
  ".view-container .title": {
    width: "100%",
    minHeight: "80px",
    backgroundImage: "linear-gradient(to right, #00babe, #00a8b5)",
    color: "white",
    textAlign: "center",
    padding: "4vh 0",
    fontSize: "clamp(1rem, 2vw, 1.5rem)",
    fontWeight: "bold",
    boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
    textShadow: "0 2px 4px rgba(0, 0, 0, 0.2)",
    borderRadius: "10px",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
  },
  // VIEW CONTAINER TABLE
  ".view-container table": {
    width: "100%",
  },
  // VIEW CONTAINER DETAIL ITEM HEADING
  ".view-container .detail-item-heading": {
    marginBottom: "20px",
    marginTop: "0px",
    marginLeft: "30px",
    fontSize: "14px",
    textAlign: "left",
    color: "#6c6c6c",
  },
  ".row-text": {
    textAlign: "left",
    fontSize: "13px !important",
    fontWeight: "400",
    fontFamily: "Open Sans, Verdana, Geneva, Tahoma, sans-serif",
  },
  ".project-name": {
    color: "#616161",
    fontSize: "13px !important",
    fontWeight: "700",
    fontFamily: "Open Sans, Verdana, Geneva, Tahoma, sans-serif",
  },
  // PAGE OPTIONS CONTAINER
  ".page-options-container": {
    flexDirection: "row-reverse",
    display: "flex",
    "& p": {
      marginRight: "5px",
      color: "blue",
      borderBottom: "1px solid blue",
      cursor: "pointer",
    },
  },
  // CREATE PROJECT BUTTON
  ".create-project-btn": {
    margin: "1rem",
  },
  // SEARCH CONTAINER
  ".search-container": {
    display: "flex",
    justifyContent: "flex-end",
    alignItems: "center",
    width: "100%",
    padding: "20px 20px 0 20px",
  },
  ".create-and-search": {
    display: "flex",
    alignItems: "center",
  },
  // SEARCH FIELD
  ".search-field": {
    marginRight: "30px",
  },
  ".centered-text": {
    textAlign: "center",
    fontSize: "16px",
  },
  ".clickable": {
    cursor: "pointer",
    color: "blue !important",
    textDecoration: "underline",
  },
  // PROJECT CONSULTATIONS
  ".projects-container": {
    padding: "0px",
  },
  ".projects-header": {
    marginBottom: "20px",
  },
  ".table-cell": {
    padding: "10px",
  },
  ".full-screen-message-container": {
    position: "absolute",
    top: "50%",
    width: "100%",
    transform: "translateY(-50%)",
  },
  ".fullscreen-form-container": {
    minHeight: "75dvh",
    padding: "1em",
    display: "flex",
    flexDirection: "column",
    justifyContent: "center",
    alignItems: "center",
    boxSizing: "border-box",
  },
  // BATCH USER CREATION
  ".user-fields-list-item": {
    listStyleType: "circle",
  },
});

export { GlobalStylesComponent, theme };
