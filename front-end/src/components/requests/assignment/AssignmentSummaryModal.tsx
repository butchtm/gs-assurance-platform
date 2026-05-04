import { TabContext, TabList } from "@mui/lab";
import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Tab } from "@mui/material";
import dayjs from "dayjs";
import { useState } from "react";
import { toast } from "react-toastify";

import { ReferenceType, useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";
import { addWorkingDays } from "../../../common/DaysDifference";

import {
  AssignmentSummaryFilters,
  exportArmAssignmentSummaryReport,
  exportRoeAssignmentSummaryReport,
} from "./Assignment";
import { AssignmentSummaryArmTab } from "./AssignmentSummaryArmTab";
import { AssignmentSummaryRoETab } from "./AssignmentSummaryRoETab";

enum AssignmentSummaryModalTabs {
  ARM,
  RoE,
}

export const AssignmentSummaryModal = ({ isOpen, onClose }: { isOpen: boolean; onClose: () => void }) => {
  const [selectedTabType, setSelectedTabType] = useState<AssignmentSummaryModalTabs>(AssignmentSummaryModalTabs.ARM);

  const { activeReferenceTypes } = useReferenceTypes();
  const reviewAssignmentType = activeReferenceTypes.AssignmentType.find((s) => s.code === "REVIEW") as ReferenceType;
  const qaqcAssignmentType = activeReferenceTypes.AssignmentType.find((s) => s.code === "QA/QC") as ReferenceType;
  const [roeFilters, setRoeFilters] = useState<AssignmentSummaryFilters>({
    assignmentTypeId: reviewAssignmentType.id,
    qaqcAssignedToCallingAccount: false,
    lastSubmittedAfter: dayjs().utc().subtract(1, "month").startOf("day"),
    lastSubmittedBefore: dayjs().utc().endOf("day"),
    requestDueBefore: addWorkingDays(dayjs.utc(), 10).startOf("day"),
  });
  const [armFilters, setArmFilters] = useState<AssignmentSummaryFilters>({
    assignmentTypeId: qaqcAssignmentType.id,
    qaqcAssignedToCallingAccount: false,
    requestDueBefore: addWorkingDays(dayjs.utc(), 10).startOf("day"),
  });

  const handleExport = async () => {
    try {
      if (selectedTabType == AssignmentSummaryModalTabs.RoE) {
        await exportRoeAssignmentSummaryReport(roeFilters);
      } else {
        await exportArmAssignmentSummaryReport(armFilters);
      }
      toast.success("Assignment Summary report exported successfully.");
    } catch (error) {
      toast.error("Failed to export Assignment Summary report, please try again later.");
      console.error("Error exporting Assignment Summary report:", error);
    }
  };

  return (
    <Dialog maxWidth="xl" fullWidth open={isOpen} onClose={onClose}>
      <DialogTitle>Assignment Summary</DialogTitle>
      <DialogContent sx={{ minWidth: "60vw", paddingBottom: 0 }}>
        <Box sx={{ textAlign: "center" }}>
          <Box>
            <TabContext value={String(selectedTabType)}>
              <TabList onChange={(event, newTabType) => setSelectedTabType(newTabType)} variant="fullWidth">
                <Tab
                  label="Roster of Experts"
                  value={String(AssignmentSummaryModalTabs.RoE)}
                  data-testid="roe-summary-tab"
                />
                <Tab label="ARM Team" value={String(AssignmentSummaryModalTabs.ARM)} data-testid="arm-summary-tab" />
              </TabList>
            </TabContext>
          </Box>
          <>
            {selectedTabType == AssignmentSummaryModalTabs.RoE && (
              <AssignmentSummaryRoETab filters={roeFilters} onFilterChange={setRoeFilters} />
            )}
            {selectedTabType == AssignmentSummaryModalTabs.ARM && (
              <AssignmentSummaryArmTab filters={armFilters} onFilterChange={setArmFilters} />
            )}
          </>
        </Box>
      </DialogContent>
      <DialogActions sx={{ justifyContent: "space-between" }}>
        <Button variant="outlined" onClick={() => onClose()}>
          Close
        </Button>
        <Button variant="contained" onClick={handleExport} data-testid="export-summary-button">
          Export
        </Button>
      </DialogActions>
    </Dialog>
  );
};
