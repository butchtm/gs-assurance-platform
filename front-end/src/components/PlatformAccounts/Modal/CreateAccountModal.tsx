import { Button, Dialog, DialogActions, DialogContent, DialogTitle } from "@mui/material";
import React, { useState } from "react";
import { toast } from "react-toastify";

import ConfirmCloseDialog from "../../../common/dialog/ConfirmCloseDialog";
import { createAccount } from "../PlatformAccount";
import { PlatformAccount } from "../PlatformAccountTypes";

import { PlatformAccountsForm } from "./PlatformAccountsForm";

export const CreateAccountModal: React.FC<{
  isOpen: boolean;
  onClose: () => void;
}> = ({ isOpen, onClose }) => {
  const [openConfirmDialog, setOpenConfirmDialog] = useState(false);
  const [platformAccount, setPlatformAccount] = useState<PlatformAccount>();
  const [selectedUserIds, setSelectedUserIds] = useState<number[]>([]);
  const [platformAccountIsValid, setPlatformAccountIsValid] = useState<boolean>(false);

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
      await createAccount({
        name: platformAccount?.name,
        emailAddress: platformAccount?.emailAddress,
        active: platformAccount?.active,
        address: platformAccount?.address,
        accountTypeId: platformAccount?.accountType.id,
        userIds: selectedUserIds,
      });
      toast.success("Account Created Successfully!");

      onClose();
    } catch (error) {
      console.error("Error submitting account form: ", error);
    }
  };

  return (
    <>
      <Dialog fullWidth open={isOpen} onClose={handleCloseAttempt}>
        <DialogTitle>Create Account</DialogTitle>
        <DialogContent>
          <PlatformAccountsForm
            onChange={setPlatformAccount}
            onChangeSelectedUserIds={setSelectedUserIds}
            onValidationChange={setPlatformAccountIsValid}
          />
        </DialogContent>

        <DialogActions>
          <Button onClick={handleCloseAttempt}>Cancel</Button>
          <Button onClick={handleSubmit} disabled={!platformAccountIsValid} variant="contained">
            Create
          </Button>
        </DialogActions>
      </Dialog>
      <ConfirmCloseDialog isOpen={openConfirmDialog} onConfirm={handleConfirmClose} onCancel={handleCancelClose} />
    </>
  );
};
