import { Update } from "@mui/icons-material";
import { Button } from "@mui/material";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";

import { ReferenceType, useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { GSRequest } from "../../../../common/request/GSRequest";
import { createInvoice } from "../../../Invoice/Invoice";
import "../../RequestsView.scss";

export const CreateInvoiceAction = (props: { request: GSRequest; updateRequest: () => void }) => {
  const [hasInvoice, setHasInvoice] = useState<boolean>(false);
  const { activeReferenceTypes } = useReferenceTypes();

  useEffect(() => {
    if (props.request.invoiceId !== null && props.request.invoiceId !== undefined) {
      setHasInvoice(true);
    }
  }, [props.request]);

  const submitInvoice = async () => {
    const requestId = props.request.id;

    const invoiceNotTriggeredStatus = activeReferenceTypes.InvoiceStatus.find(
      (status: ReferenceType) => status.code === "NOT_TRIGGERED",
    )?.id;

    const defaultInvoicePayload = {
      requestId: requestId,
      invoiceStatusId: invoiceNotTriggeredStatus,
      amount: 0,
    };

    try {
      await createInvoice(defaultInvoicePayload);
    } catch {
      toast.error("Failed to create invoice");
    } finally {
      props.updateRequest();
      toast.success("Invoice Created Successfully");
    }
  };

  return (
    <>
      {!hasInvoice && (
        <Button variant="outlined" className="action-button" onClick={submitInvoice}>
          <Update />
          <span className="action-text">Create Invoice</span>
        </Button>
      )}
    </>
  );
};
