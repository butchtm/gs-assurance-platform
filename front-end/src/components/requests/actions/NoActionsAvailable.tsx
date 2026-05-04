import ReportIcon from "@mui/icons-material/Report";

export const NoActionsAvailable = () => {
  return (
    <div
      style={{
        display: "flex",
        justifyContent: "space-between",
        margin: "5px 10px",
        padding: "0 10px",
        borderRadius: "5px",
        backgroundColor: "lightgray",
      }}
    >
      <div style={{ display: "flex" }}>
        <div
          style={{
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            padding: "1rem 10px",
          }}
        >
          <ReportIcon />
        </div>
        <p>No Actions Available</p>
      </div>
    </div>
  );
};
