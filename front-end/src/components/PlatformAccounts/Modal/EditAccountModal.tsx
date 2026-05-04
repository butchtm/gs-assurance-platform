import { Button, Dialog, DialogActions, DialogContent, DialogTitle } from "@mui/material";
import React, { useState } from "react";
import { toast } from "react-toastify";

import ConfirmCloseDialog from "../../../common/dialog/ConfirmCloseDialog";
import { updateAccount } from "../PlatformAccount";
import { PlatformAccount } from "../PlatformAccountTypes";

import { PlatformAccountsForm } from "./PlatformAccountsForm";

const EditAccountModal: React.FC<{
  isOpen: boolean;
  onClose: () => void;
  account: PlatformAccount;
}> = ({ isOpen, onClose, account }) => {
  const [openConfirmDialog, setOpenConfirmDialog] = useState(false);
  const [updatedPlatformAccount, setUpdatedPlatformAccount] = useState<PlatformAccount>();
  const [selectedUserIds, setSelectedUserIds] = useState<number[]>([]);
  const [changesAreValid, setChangesAreValid] = useState<boolean>(false);

  const handleCloseAttempt = () => {
    setOpenConfirmDialog(true);
  };

  const handleConfirmClose = () => {
    setOpenConfirmDialog(false);
    onClose();
  };

  const handleCancelClose = () => {
    setOpenConfirmDialog(false);
  };

  const handleSubmit = async () => {
    try {
      await updateAccount(
        {
          name: updatedPlatformAccount?.name,
          emailAddress: updatedPlatformAccount?.emailAddress,
          active: updatedPlatformAccount?.active,
          address: updatedPlatformAccount?.address,
          accountTypeId: updatedPlatformAccount?.accountType.id,
          userIds: selectedUserIds,
        },
        account.id,
      );
      toast.success("Account Updated Successfully!");

      onClose();
    } catch (error) {
      console.error("Error submitting account form: ", error);
    }
  };

  return (
    <>
      <Dialog fullWidth open={isOpen} onClose={handleCloseAttempt}>
        <DialogTitle>Edit Account</DialogTitle>
        <DialogContent>
          <PlatformAccountsForm
            defaultAccountValues={account}
            onChange={setUpdatedPlatformAccount}
            onChangeSelectedUserIds={setSelectedUserIds}
            onValidationChange={setChangesAreValid}
          />
        </DialogContent>

        <DialogActions>
          <Button onClick={handleCloseAttempt}>Cancel</Button>
          <Button onClick={handleSubmit} disabled={!changesAreValid} variant="contained">
            Update
          </Button>
        </DialogActions>
      </Dialog>
      <ConfirmCloseDialog isOpen={openConfirmDialog} onConfirm={handleConfirmClose} onCancel={handleCancelClose} />
    </>
  );
};

export default EditAccountModal;
