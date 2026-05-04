import { isAxiosError } from "axios";
import { toast } from "react-toastify";

export function handleApiError(error: unknown): void {
  if (isAxiosError(error)) {
    if (error?.response?.data?.detail) {
      toast.error(error.response.data.detail);
      return;
    }
  }
  toast.error("Error submitting.");
}
