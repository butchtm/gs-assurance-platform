import EditIcon from "@mui/icons-material/Edit";
import { IconButton } from "@mui/material";
import { useState } from "react";

import RequestCommentModifyModal from "../../comments/RequestCommentModifyModal";
import { RequestComment } from "../../comments/RequestComments";
import "../../RequestsView.scss";

export const CommentModifyAction = (props: { comment: RequestComment; updateRequest: () => void }) => {
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleOpenModal = () => {
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    props.updateRequest();
  };

  return (
    <>
      <IconButton data-testid="comment-edit-action" onClick={handleOpenModal}>
        <EditIcon data-testid="comment-edit-icon" />
      </IconButton>

      {isModalOpen && (
        <RequestCommentModifyModal comment={props.comment} isOpen={isModalOpen} onClose={handleCloseModal} />
      )}
    </>
  );
};
