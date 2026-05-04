import { Box, Button, FormControl, InputLabel, MenuItem, Modal, Select, TextField, Typography } from "@mui/material";
import React, { useEffect, useState } from "react";
import { toast } from "react-toastify";

import { ReferenceType, useReferenceTypes } from "../../common/data-types/DataReferenceTypes";
import { GSRequest } from "../../common/request/GSRequest";

import { getInvoice, updateInvoice } from "./Invoice";

interface UpdateInvoiceStatusModalProps {
  request: GSRequest;
  isOpen: boolean;
  onClose: (statusId?: number) => void;
}

const UpdateInvoiceStatusModal: React.FC<UpdateInvoiceStatusModalProps> = ({ request, isOpen, onClose }) => {
  const [selectedStatus, setSelectedStatus] = useState<number>();
  const [currentAmount, setCurrentAmount] = useState<number>();
  const { activeReferenceTypes } = useReferenceTypes();

  useEffect(() => {
    const fetchInvoice = async () => {
      if (request.invoiceId) {
        const invoiceResponse = await getInvoice(request.invoiceId);

        if (invoiceResponse) {
          setSelectedStatus(invoiceResponse.invoiceStatusId);
          setCurrentAmount(invoiceResponse.amount);
        }
      }
    };
    fetchInvoice();
  }, [request]);

  const handleSubmit = () => {
    if (selectedStatus !== null && request.invoiceId) {
      updateInvoice(
        {
          invoiceStatusId: selectedStatus,
          amount: currentAmount,
        },
        request.invoiceId,
      );
      toast.success("Invoice Updated Successfully");
      onClose(selectedStatus);
    } else {
      onClose();
    }
  };

  return (
    <Modal open={isOpen} onClose={() => onClose()}>
      <Box style={{ backgroundColor: "white", width: 600, padding: 20, borderRadius: 8 }}>
        <Typography variant="h6" component="h2" sx={{ mb: 2 }}>
          Update Invoice Status
        </Typography>
        <form>
          <FormControl fullWidth margin="normal">
            <InputLabel htmlFor="update-invoice-select">Invoice Status</InputLabel>
            <Select
              label="Invoice Status"
              inputProps={{
                id: "update-invoice-select",
              }}
              value={selectedStatus ?? ""}
              onChange={(e) => setSelectedStatus(Number(e.target.value))}
              data-testid="update-invoice-status-select"
            >
              {activeReferenceTypes.InvoiceStatus?.map((status: ReferenceType) => {
                return (
                  <MenuItem value={status.id} key={status.id} data-testid={`invoice-status-option-${status.id}`}>
                    {status.description}
                  </MenuItem>
                );
              })}
            </Select>
          </FormControl>
          <TextField
            label="Invoice Amount"
            type="number"
            value={currentAmount ?? ""}
            onChange={(e) => setCurrentAmount(Number(e.target.value))}
            fullWidth
            margin="normal"
            inputProps={{ "data-testid": "invoice-amount-input" }}
          />
        </form>
        <div style={{ display: "flex", justifyContent: "space-between", paddingTop: "10px" }}>
          <Button variant="outlined" onClick={() => onClose()}>
            Cancel
          </Button>
          <Button variant="contained" onClick={handleSubmit}>
            Save
          </Button>
        </div>
      </Box>
    </Modal>
  );
};

export default UpdateInvoiceStatusModal;
