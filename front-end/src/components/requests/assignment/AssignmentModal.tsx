import {
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
} from "@mui/material";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";

import { useReferenceTypes, getReferenceTypeById } from "../../../common/data-types/DataReferenceTypes";
import { isClosedStatus } from "../../../common/request/GSRequest";
import { PlatformAccount } from "../../PlatformAccounts/PlatformAccountTypes";

import { AccountAssignmentRow } from "./AccountAssignmentRow";
import {
  Assignment,
  AssignmentStatus,
  AssignmentType,
  assignRequest,
  getAllAccountsByAssignmentType,
  getRequestAssignments,
  getAssignmentTypeByRequestType,
  updateRequestAssignment,
} from "./Assignment";

interface AccountWithStatus {
  account: PlatformAccount | null;
  status: AssignmentStatus | null;
}

interface AccountsByAssignmentType {
  [assignmentTypeId: string]: PlatformAccount[];
}

interface MappedAssignmentAccounts {
  [assignmentTypeId: number]: AccountWithStatus;
}

export const AssignmentModal = ({
  isOpen,
  onClose,
  requestId,
  requestProjectName,
  requestTypeId,
  requestTypeDescription,
  requestProjectDeveloperName,
  requestStatusId,
  readonlyOnOpen,
  handleOpenSummaryModal,
  onSave,
}: {
  isOpen: boolean;
  onClose: () => void;
  requestId: number;
  requestProjectName: string;
  requestTypeId: number;
  requestTypeDescription: string;
  requestProjectDeveloperName?: string;
  requestStatusId: number;
  readonlyOnOpen?: boolean;
  handleOpenSummaryModal?: () => void;
  onSave?: () => void;
}) => {
  const [accountsByType, setAccountsByType] = useState<AccountsByAssignmentType>({});
  const [assignments, setAssignments] = useState<Partial<Assignment>[]>();
  const [assignmentTypes, setAssignmentTypes] = useState<AssignmentType[]>([]);
  const [selectedAssignmentAccounts, setSelectedAssignmentAccounts] = useState<MappedAssignmentAccounts>({});
  const [overrideAssignmentTypes, setOverrideAssignmentTypes] = useState<AssignmentType[]>([]);
  const [requiredAssignmentTypes, setRequiredAssignmentTypes] = useState<AssignmentType[]>([]);
  const [isMappingComplete, setIsMappingComplete] = useState(false);
  const [dataLoaded, setDataLoaded] = useState(false);
  const [loading, setLoading] = useState(false);
  const { activeReferenceTypes } = useReferenceTypes();
  const [readonly, setReadonly] = useState(readonlyOnOpen ?? false);

  const isEditable = (requestStatusId: number) => {
    const requestStatus = getReferenceTypeById(requestStatusId, activeReferenceTypes.RequestStatus);
    return !isClosedStatus(requestStatus);
  };

  useEffect(() => {
    if (!isOpen) return;

    const fetchData = async () => {
      setLoading(true);

      if (activeReferenceTypes?.AssignmentType) {
        const requestTypeAssignmentTypeMap = await getAssignmentTypeByRequestType(requestTypeId);

        const assignmentTypesData: AssignmentType[] = activeReferenceTypes.AssignmentType.filter((assignmentType) =>
          requestTypeAssignmentTypeMap.some(
            (requestTypeAssignmentType) => requestTypeAssignmentType.assignmentTypeId === assignmentType.id,
          ),
        );
        const requiredAssignmentTypesData: AssignmentType[] = activeReferenceTypes.AssignmentType.filter(
          (assignmentType) =>
            requestTypeAssignmentTypeMap.some(
              (requestTypeAssignmentType) =>
                requestTypeAssignmentType.mandatory === true &&
                requestTypeAssignmentType.assignmentTypeId === assignmentType.id,
            ),
        );
        const assignmentOverrideTypes = activeReferenceTypes.AssignmentType.filter(
          (assignmentType) =>
            !assignmentTypesData.some((assignmentTypesData) => assignmentTypesData.id === assignmentType.id),
        );

        setAssignmentTypes(assignmentTypesData);
        setRequiredAssignmentTypes(requiredAssignmentTypesData);
        setOverrideAssignmentTypes(assignmentOverrideTypes);
      }

      const assignmentsData = await getRequestAssignments(requestId);
      setAssignments(assignmentsData);

      const promises: Promise<PlatformAccount[]>[] = [];
      for (const assignmentType of activeReferenceTypes.AssignmentType) {
        promises[assignmentType.id] = getAllAccountsByAssignmentType(assignmentType.id);
      }
      const accountsByType = await Promise.all(promises);
      const newAccountsByType: AccountsByAssignmentType = {};
      for (const assignmentType of activeReferenceTypes.AssignmentType) {
        newAccountsByType[assignmentType.id] = accountsByType[assignmentType.id];
      }

      setAccountsByType(newAccountsByType);
      setLoading(false);
      setDataLoaded(true);
    };

    fetchData();
  }, [isOpen, requestId, requestTypeId]);

  useEffect(() => {
    if (!assignments || !Object.values(accountsByType).some((accounts) => accounts.length > 0)) {
      return;
    }

    const mappedData = assignments.reduce<Record<number, AccountWithStatus>>((assignmentIdToAccountMap, assignment) => {
      const assignmentStatus = activeReferenceTypes.AssignmentStatus.find(
        (as) => as.id === assignment.assignmentStatus?.id,
      );

      if (assignment?.assignmentType?.id !== undefined) {
        const assignmentTypeId = assignment.assignmentType.id;
        const account = accountsByType[assignmentTypeId]?.find((a) => a.id === assignment.account?.id);
        if (account) {
          assignmentIdToAccountMap[assignmentTypeId] = {
            account: account,
            status: assignmentStatus || null,
          };
        }
      }
      return assignmentIdToAccountMap;
    }, {});

    setSelectedAssignmentAccounts((prevState) => {
      return { ...prevState, ...mappedData };
    });
  }, [assignments, accountsByType, activeReferenceTypes.AssignmentStatus]);

  useEffect(() => {
    if (!dataLoaded) return;

    const hasAccounts = Object.values(accountsByType).some((accounts) => accounts.length > 0);
    const hasAssignments = assignments && assignments.length > 0;

    if (hasAccounts && hasAssignments) {
      mapAssignmentsToAccounts();
    }

    setIsMappingComplete(true);
  }, [assignments, accountsByType, dataLoaded]);

  const mapAssignmentsToAccounts = () => {
    const assignmentAccountMap = assignments?.reduce(
      (map, assignment) => {
        if (!assignment?.assignmentType?.id) {
          return map;
        }

        const account = accountsByType[assignment.assignmentType.id].find(
          (accountItem) => accountItem.id === assignment.account?.id,
        );
        const assignmentStatus = activeReferenceTypes.AssignmentStatus.find(
          (as) => as.id === assignment.assignmentStatus?.id,
        );

        if (account) {
          map[assignment.assignmentType.id] = {
            account: account,
            status: assignmentStatus || null,
          };
        }

        return map;
      },
      {} as Record<number, AccountWithStatus>,
    );

    setSelectedAssignmentAccounts((prevState) => {
      return { ...prevState, ...(assignmentAccountMap || {}) };
    });
  };

  const getUpdatedAssignments = () => {
    return Object.entries(selectedAssignmentAccounts).reduce((assignmentsMap, [typeId, accountWithStatus]) => {
      let assignmentType = assignmentTypes.find((type) => type.id === Number(typeId));
      if (!assignmentType) {
        assignmentType = overrideAssignmentTypes.find((type) => type.id === Number(typeId));
      }

      const assignmentStatus = activeReferenceTypes.AssignmentStatus.find(
        (status) => status.code === accountWithStatus?.status?.code,
      );

      if (!assignmentType || !assignmentStatus || !accountWithStatus.account?.id) {
        if (!accountWithStatus.account?.id) {
          toast.error(`Please choose an account for ${assignmentType?.description}`);
        }
        if (!assignmentStatus) {
          toast.error(`Please choose an assignment status for ${assignmentType?.description}`);
        }
        return assignmentsMap;
      }

      const existingAssignment = assignments?.find((a) => a.assignmentType?.id === assignmentType?.id);

      const assignmentPayload: Partial<Assignment> = {
        assignmentTypeId: assignmentType.id,
        account: accountWithStatus.account,
        requestId: requestId,
        assignmentStatusId: assignmentStatus.id,
        assignmentId: existingAssignment?.id,
      };

      if (
        !existingAssignment ||
        existingAssignment.account?.id !== accountWithStatus.account.id ||
        existingAssignment.assignmentStatus?.id !== assignmentStatus.id
      ) {
        assignmentsMap.push(assignmentPayload);
      }

      return assignmentsMap;
    }, [] as Partial<Assignment>[]);
  };

  const handleSave = async () => {
    try {
      const updatedAssignments = getUpdatedAssignments();
      if (!updatedAssignments.length) return;

      const updatePromises = updatedAssignments.map((assignment) => {
        const { assignmentId, ...assignmentWithoutId } = assignment;
        return assignmentId ? updateRequestAssignment(assignmentId, assignmentWithoutId) : assignRequest(assignment);
      });

      await Promise.all(updatePromises);

      toast.success("Assignments updated");

      if (onSave) {
        onSave();
      }
      onClose();
    } catch (error) {
      console.error("Error saving assignments:", error);
      toast.error("Error saving assignments");
    }
  };

  const openSummaryModal = () => {
    if (handleOpenSummaryModal) {
      handleOpenSummaryModal();
    }
    onClose();
  };

  const renderAssignments = (assignmentTypesToRender: AssignmentType[]) => {
    if (loading || !isMappingComplete) {
      return <CircularProgress color="inherit" size={20} />;
    }

    assignmentTypesToRender = assignmentTypesToRender.filter(
      (type) => type.code !== "STAKEHOLDER_CONSULTATION" && type.code !== "COMPLETENESS_CHECK",
    );

    if (assignmentTypesToRender.length == 0) {
      return <p>None to display for this request type.</p>;
    }

    if (readonly) {
      return (
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Type</TableCell>
              <TableCell>Assignee</TableCell>
              <TableCell>Status</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {assignmentTypesToRender.map((assignmentType) => (
              <TableRow key={assignmentType.id}>
                <TableCell>{assignmentType.description}</TableCell>
                <TableCell>{selectedAssignmentAccounts[assignmentType.id]?.account?.name}</TableCell>
                <TableCell>
                  {selectedAssignmentAccounts[assignmentType.id]?.status?.description.toUpperCase()}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      );
    }

    return assignmentTypesToRender.map((assignmentType) => (
      <AccountAssignmentRow
        key={assignmentType.id}
        label={assignmentType.description}
        assignmentTypeId={assignmentType.id}
        accountOptions={accountsByType[assignmentType.id]}
        onAccountAssignmentChanged={(newAccount, newStatus) => {
          setSelectedAssignmentAccounts((prevState) => ({
            ...prevState,
            [assignmentType.id]: {
              account: newAccount,
              status: newStatus,
            },
          }));
        }}
        onAccountAssignmentStatusChanged={(newStatus) => {
          setSelectedAssignmentAccounts((prevState) => ({
            ...prevState,
            [assignmentType.id]: {
              account: prevState[assignmentType.id]?.account,
              status: newStatus,
            },
          }));
        }}
        defaultAccount={selectedAssignmentAccounts[assignmentType.id]?.account || undefined}
        defaultStatus={selectedAssignmentAccounts[assignmentType.id]?.status || undefined}
        required={requiredAssignmentTypes.some(
          (requiredAssignmentType) => requiredAssignmentType.id === assignmentType.id,
        )}
      />
    ));
  };

  return (
    <Dialog maxWidth="lg" open={isOpen} onClose={onClose}>
      <DialogTitle>{requestProjectName}</DialogTitle>

      <DialogContent sx={{ minWidth: "575px" }}>
        <div>Request Type: {requestTypeDescription}</div>
        {requestProjectDeveloperName && <div>Project Developer: {requestProjectDeveloperName}</div>}
        <div data-testid="request-type-assignments">
          <h3>Request Type Assignments</h3>
          {renderAssignments(assignmentTypes)}
        </div>
        <div data-testid="override-assignments">
          <h3>Override Assignments</h3>
          {renderAssignments(overrideAssignmentTypes)}
        </div>
      </DialogContent>

      {readonly && (
        <DialogActions>
          <Button onClick={onClose}>Close</Button>
          {isEditable(requestStatusId) && (
            <Button onClick={() => setReadonly(false)} variant="contained" data-testid="edit-assignments-button">
              Edit
            </Button>
          )}
        </DialogActions>
      )}

      {!readonly && (
        <DialogActions sx={{ justifyContent: "space-between" }}>
          <Button onClick={openSummaryModal} variant="contained" sx={{ mr: "12px" }}>
            Assignments Summary
          </Button>
          <div>
            <Button onClick={onClose} sx={{ marginRight: "8px" }}>
              Cancel
            </Button>
            <Button onClick={handleSave} variant="contained">
              Save
            </Button>
          </div>
        </DialogActions>
      )}
    </Dialog>
  );
};
