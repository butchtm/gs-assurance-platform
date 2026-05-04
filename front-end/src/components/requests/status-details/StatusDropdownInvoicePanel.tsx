import { AccountBalance, Paid } from "@mui/icons-material";
import { Grid } from "@mui/material";
import React, { useEffect, useState } from "react";

import { useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";
import { getInvoice, Invoice } from "../../Invoice/Invoice";
import { RequestField } from "../reusable/RequestField";

interface StatusDropdownInvoicePanelProps {
  invoiceId: number;
  refreshRequest: boolean;
}

export const StatusDropdownInvoicePanel: React.FC<StatusDropdownInvoicePanelProps> = ({
  invoiceId,
  refreshRequest,
}) => {
  const [invoice, setInvoice] = useState<Invoice>();
  const { activeReferenceTypes } = useReferenceTypes();

  useEffect(() => {
    const fetchInvoice = async () => {
      const response = await getInvoice(invoiceId);
      setInvoice(response);
    };
    fetchInvoice();
  }, [invoiceId, refreshRequest]);

  const getInvoiceStatus = (statusId: number) => {
    const status = activeReferenceTypes.InvoiceStatus.find((s) => s.id === statusId);
    return status ? status.description : "";
  };

  return invoice ? (
    <Grid container>
      <RequestField name="Invoice Status" value={getInvoiceStatus(invoice.invoiceStatusId)} icon={<AccountBalance />} />
      <RequestField name="Invoice Amount" value={invoice ? invoice.amount.toLocaleString() : ""} icon={<Paid />} />
    </Grid>
  ) : (
    <></>
  );
};
