import { Update } from "@mui/icons-material";
import { Button } from "@mui/material";
import { useEffect, useState } from "react";

import { GSRequest } from "../../../../common/request/GSRequest";
import UpdateInvoiceStatusModal from "../../../Invoice/UpdateInvoiceStatusModal";
import "../../RequestsView.scss";

export const UpdateInvoiceStatusAction = (props: { request: GSRequest; updateRequest: () => void }) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [hasInvoice, setHasInvoice] = useState<boolean>(false);

  useEffect(() => {
    if (props.request.invoiceId !== null && props.request.invoiceId !== undefined) {
      setHasInvoice(true);
    }
  }, [props.request]);

  const handleOpenModal = () => {
    setIsModalOpen(true);
  };

  const handleCloseModal = (statusId?: number) => {
    if (statusId !== undefined && statusId !== null) {
      props.updateRequest();
    }
    setIsModalOpen(false);
  };

  return (
    <>
      {hasInvoice && (
        <Button variant="outlined" className="action-button" onClick={handleOpenModal}>
          <Update />
          <span className="action-text">Update Invoice Status</span>
        </Button>
      )}
      {isModalOpen && (
        <UpdateInvoiceStatusModal request={props.request} isOpen={isModalOpen} onClose={handleCloseModal} />
      )}
    </>
  );
};
