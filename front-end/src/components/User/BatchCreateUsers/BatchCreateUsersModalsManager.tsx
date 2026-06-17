import { isAxiosError } from "axios";
import React, { useEffect, useRef, useState } from "react";
import { toast } from "react-toastify";

import { BatchUserResponse, getBatchStatus, uploadBatchUsers } from "../User";

import { BatchCreateUsersModal } from "./BatchCreateUsersModal";
import { BatchUsersSummaryModal } from "./BatchUsersSummaryModal";

export const BatchCreateUsersModalsManager: React.FC<{
  isOpen: boolean;
  onClose: () => void;
  refreshUsers: () => void;
}> = ({ isOpen, onClose, refreshUsers }) => {
  const [isBatchCreateUsersModalOpen, setIsBatchCreateUsersModalOpen] = useState<boolean>(false);
  const [isBatchUsersSummaryModalOpen, setIsBatchUsersSummaryModalOpen] = useState<boolean>(false);
  const [isCreatingBatch, setIsCreatingBatch] = useState<boolean>(true);
  const batchPoller = useRef<number | undefined>();
  const [batchUserResponse, setBatchUserResponse] = useState<BatchUserResponse>();

  useEffect(() => {
    if (isOpen) {
      setIsBatchCreateUsersModalOpen(true);
      setIsBatchUsersSummaryModalOpen(false);
    } else {
      setIsBatchCreateUsersModalOpen(false);
      setIsBatchUsersSummaryModalOpen(false);
    }
  }, [isOpen]);

  const handleBatchedUsersUploaded = (csvFile: File): void => {
    setIsBatchCreateUsersModalOpen(false);
    setIsBatchUsersSummaryModalOpen(true);
    processBatch(csvFile);
  };

  const processBatch = async (batchUsersCsv: File) => {
    setIsCreatingBatch(true);
    try {
      const response = await uploadBatchUsers(batchUsersCsv);
      batchPoller.current = setInterval(async () => {
        await checkBatchStatus(response.id);
      }, 1000);
    } catch (error) {
      setIsCreatingBatch(false);
      if (isAxiosError(error) && error.response?.data.detail) {
        toast.error("Unable to upload users. " + error.response?.data.detail);
      } else {
        toast.error("Unable to upload users");
      }
    }
  };

  const checkBatchStatus = async (batchId: number) => {
    try {
      const status = await getBatchStatus(batchId);
      if (status.complete) {
        clearInterval(batchPoller.current);
        setBatchUserResponse(status);
        setIsCreatingBatch(false);
        if (status.successCount > 0) {
          refreshUsers();
        }
      }
    } catch {
      throw Error("Unable to get batch status");
    }
  };

  return (
    <>
      <BatchCreateUsersModal
        isOpen={isBatchCreateUsersModalOpen}
        onClose={onClose}
        onBatchedUsersUploaded={handleBatchedUsersUploaded}
      />
      <BatchUsersSummaryModal
        isOpen={isBatchUsersSummaryModalOpen}
        onClose={onClose}
        isLoading={isCreatingBatch}
        batchUserResponse={batchUserResponse}
        onBatchedUsersUploaded={handleBatchedUsersUploaded}
      />
    </>
  );
};
