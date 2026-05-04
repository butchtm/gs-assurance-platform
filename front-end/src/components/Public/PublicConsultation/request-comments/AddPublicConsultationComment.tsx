import { Box, Button } from "@mui/material";
import React, { useState } from "react";

import PublicConsultationAddCommentModal from "./PublicConsultationAddCommentModal";

interface AddPublicConsultationCommentProps {
  requestId: number;
}

const AddPublicConsultationComment: React.FC<AddPublicConsultationCommentProps> = ({
  requestId,
}: AddPublicConsultationCommentProps): JSX.Element => {
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);

  const handleOpenModal = () => {
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
  };

  return (
    <Box display="flex" alignItems="center" paddingTop={"5px"}>
      <Button
        sx={{ color: (theme) => theme.palette.primary.main, height: "fit-content", marginLeft: "auto" }}
        onClick={handleOpenModal}
      >
        Add Comment
      </Button>
      {isModalOpen && (
        <PublicConsultationAddCommentModal requestId={requestId} isOpen={isModalOpen} onClose={handleCloseModal} />
      )}
    </Box>
  );
};

export default AddPublicConsultationComment;
