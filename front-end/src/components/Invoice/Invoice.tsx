import { ReferenceType } from "../../common/project/Project";
import { apiClient } from "../APIClient/Client";

export interface Invoice {
  id: number;
  amount: number;
  status: ReferenceType;
  invoiceStatusId: number;
  isArchived: boolean;
  statusChangedAt: string;
  customerEmail: string;
  lastManualCheck: string;
  requestId: number;
}

interface InvoiceCreatePayload {
  requestId: number;
  invoiceStatusId: number;
  amount: number;
}

export const updateInvoice = async (payload: Partial<Invoice>, invoiceId: number) => {
  await apiClient.makeRequest(`/invoices/${invoiceId}`, "PATCH", JSON.stringify(payload));
};

export const createInvoice = async (payload: Partial<InvoiceCreatePayload>) => {
  await apiClient.makeRequest(`/invoices`, "POST", JSON.stringify(payload));
};

export async function getInvoice(invoiceId: number): Promise<Invoice> {
  try {
    const response = await apiClient.makeRequest("/invoices/" + invoiceId, "GET");
    const data = await response;
    return data as Invoice;
  } catch (error) {
    console.error("Failed to fetch invoice", error);
    throw error;
  }
}
