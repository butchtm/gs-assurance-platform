import { useEffect, useState } from "react";
import AssignmentIcon from "@mui/icons-material/Assignment";

import { Assignment } from "../assignment/Assignment";
import { RequestField } from "../reusable/RequestField";
import { useReferenceTypes, getReferenceTypeByCode } from "../../../common/data-types/DataReferenceTypes";

export const AuditAssignmentField = ({ assignments }: { assignments: Assignment[] }) => {
  const { activeReferenceTypes } = useReferenceTypes();
  const [visible, setVisible] = useState<boolean>(false);
  const [value, setValue] = useState<string>("");

  const auditType = getReferenceTypeByCode("AUDIT", activeReferenceTypes.AssignmentType);

  useEffect(() => {
    const findAuditAssignments = (): Assignment[] => {
      return assignments.filter((assignment) => assignment.assignmentType.id === auditType?.id);
    };

    const auditAssignments = findAuditAssignments();
    setVisible(auditAssignments.length > 0);
    setValue(toValue(auditAssignments));
  }, [assignments]);

  const toValue = (assignments: Assignment[]) => {
    if (assignments.length < 1) {
      return "";
    }
    const assignment = assignments[0];
    if (!assignment?.account) {
      return "";
    }
    return assignment.account.name;
  };

  if (auditType && visible) {
    return <RequestField name={auditType.description} value={value} icon={<AssignmentIcon />} />;
  }
  return null;
};
