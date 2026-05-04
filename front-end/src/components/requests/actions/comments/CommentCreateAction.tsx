import AddCommentIcon from "@mui/icons-material/AddComment";
import { Button } from "@mui/material";
import { useContext, useState } from "react";
import { toast } from "react-toastify";

import { GSRequest } from "../../../../common/request/GSRequest";
import { AuthContext } from "../../../Auth/Login/AuthProvider";
import RequestCommentModal from "../../comments/RequestCommentModal";
import "../../RequestsView.scss";

export const CommentCreateAction = ({
  request,
  updateRequest,
  disabled,
}: {
  request: GSRequest;
  updateRequest: () => void;
  disabled: boolean;
}) => {
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const authContext = useContext(AuthContext);

  const handleOpenModal = () => {
    if (!authContext.user?.account) {
      toast.error("No platform account associated with your account");
      return;
    }
    if (!authContext.user.account.emailAddress) {
      toast.error("No Email Address linked to your Account");
      return;
    }
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    updateRequest();
  };

  return (
    <>
      <Button
        data-testid="add-comment-button"
        disabled={disabled}
        onClick={handleOpenModal}
        variant="outlined"
        className="action-button"
      >
        <AddCommentIcon />
        <span className="action-text">Add Comment</span>
      </Button>
      {isModalOpen && authContext.user?.account && (
        <RequestCommentModal
          request={request}
          account={authContext.user.account}
          isOpen={isModalOpen}
          onClose={handleCloseModal}
          data-testid="comment-dialog"
        />
      )}
    </>
  );
};
