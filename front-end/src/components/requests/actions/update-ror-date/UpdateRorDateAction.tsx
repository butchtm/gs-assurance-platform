import UpdateIcon from "@mui/icons-material/Update";
import { Button } from "@mui/material";
import { useContext, useState } from "react";

import { GSRequest } from "../../../../common/request/GSRequest";
import { UpdateRorDateModal } from "../../assignment/UpdateRorDateModal";
import { useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { AuthContext } from "../../../Auth/Login/AuthProvider";

import "../../RequestsView.scss";

export const UpdateRorDateAction = ({
  request,
  updateRequest,
  disabled,
}: {
  request: GSRequest;
  updateRequest: () => void;
  disabled: boolean;
}) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const { activeReferenceTypes } = useReferenceTypes();
  const authContext = useContext(AuthContext);

  const handleOpenModal = () => {
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
  };

  if (!authContext.isAdminUser()) {
    return null;
  }

  if (request.currentRoundIndex == null) {
    return null;
  }

  const currentRequestRound = request.requestRounds?.find(
    (requestRound) => requestRound.roundIndex === request.currentRoundIndex,
  );

  const isRorClosed = () => {
    if (!currentRequestRound?.statusId || !activeReferenceTypes.RequestRoundStatus) {
      return false;
    }

    const closedStatus = activeReferenceTypes.RequestRoundStatus.find((status) => status.code === "CLOSED");
    return currentRequestRound.statusId === closedStatus?.id;
  };

  if (isRorClosed()) {
    return null;
  }

  return (
    <>
      <Button disabled={disabled} onClick={handleOpenModal} variant="outlined" className="action-button">
        <UpdateIcon />
        <span className="action-text">Update RoR Dates</span>
      </Button>

      {isModalOpen && (
        <UpdateRorDateModal
          isOpen={isModalOpen}
          onClose={handleCloseModal}
          requestId={request.id}
          requestProjectName={request.project?.name || ""}
          requestCurrentRoundIndex={request.currentRoundIndex}
          defaultRequestCurrentRoundStartDate={currentRequestRound?.startDate}
          defaultRequestCurrentRoundEndDate={currentRequestRound?.endDate}
          currentRorStatus={currentRequestRound?.statusId}
          updateRequest={updateRequest}
        />
      )}
    </>
  );
};
