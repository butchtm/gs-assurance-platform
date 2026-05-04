import { Button } from "@mui/material";
import { useState } from "react";

import { GSRequest } from "../../../common/request/GSRequest";

import ViewBatchProjectsModal from "./ViewBatchProjectsModal";

interface ViewBatchProjectsActionProps {
  request: GSRequest;
}
const ViewBatchProjectsAction = ({ request }: ViewBatchProjectsActionProps) => {
  const [isModalOpen, setModalOpen] = useState(false);

  const handleOpenModal = () => {
    setModalOpen(true);
  };

  const handleCloseModal = () => {
    setModalOpen(false);
  };

  return (
    <>
      <Button onClick={handleOpenModal} variant="outlined" className="action-button" data-testid="open-btn">
        <span className="action-text">{`View ${request.legacyBatchProjectIds?.length} projects in this request`}</span>
      </Button>

      <ViewBatchProjectsModal isOpen={isModalOpen} onClose={handleCloseModal} request={request} />
    </>
  );
};

export default ViewBatchProjectsAction;
