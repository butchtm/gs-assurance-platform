import { Button, Dialog, DialogActions, DialogContent, DialogTitle, LinearProgress, Typography } from "@mui/material";
import { throttle } from "lodash";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";

import { APIClient } from "../../APIClient/Client";

import { ZipStreamProcessor } from "./ZipStreamProcessor";

export const DownloadAllDocumentsModal = ({
  isOpen,
  projectId,
  requestId,
  displayCategoryId,
  onClose,
}: {
  isOpen: boolean;
  projectId: number;
  requestId: number;
  displayCategoryId: number;
  onClose: () => void;
}) => {
  const [dialogTitle, setDialogTitle] = useState("Starting download...");
  const [isDownloadFinished, setIsDownloadFinished] = useState(false);
  const [totalFiles, setTotalFiles] = useState<number>();
  const [currentFileName, setCurrentFileName] = useState("");
  const [bytesDownloadedCount, setBytesDownloadedCount] = useState(0);
  const [errorOccurred, setErrorOccurred] = useState(false);

  const preventPageChangeDuringDownload = () => {
    if (isDownloadFinished) return;

    const onBeforeUnload = (event: BeforeUnloadEvent) => {
      event.preventDefault();
    };
    window.addEventListener("beforeunload", onBeforeUnload);

    return () => {
      window.removeEventListener("beforeunload", onBeforeUnload);
    };
  };
  useEffect(preventPageChangeDuringDownload, [isDownloadFinished]);

  const updateProgress = throttle(
    (value: number) => {
      setBytesDownloadedCount(value);
    },
    500,
    { leading: true, trailing: true },
  );

  useEffect(() => {
    const abortController = new AbortController();
    const signal = abortController.signal;

    const startDocumentsDownloadReader = async () => {
      const response = await fetch(
        `/api/projects/${projectId}/requests/${requestId}/documents/${displayCategoryId}/download`,
        { signal },
      );
      return response;
    };

    let fileNameHeader: string;
    startDocumentsDownloadReader()
      .then((response) => {
        const contentLengthFileCountHeader = response.headers.get("Content-Length-File-Count");
        if (!contentLengthFileCountHeader) throw Error("Invalid file count");
        setTotalFiles(parseInt(contentLengthFileCountHeader));

        const contentDispositionHeader = response.headers.get("Content-Disposition");
        if (!contentDispositionHeader) throw Error("Invalid content disposition header");
        fileNameHeader = contentDispositionHeader.split("filename=")[1].split(";")[0];

        if (!response.body) throw Error("Download all failed");

        return response.body.getReader();
      })
      .then((reader) => {
        const zipStreamProcessor = new ZipStreamProcessor(
          () => setIsDownloadFinished(true),
          updateProgress,
          setCurrentFileName,
        );
        return zipStreamProcessor.createStream(reader);
      })
      .then((readableStream) => new Response(readableStream))
      .then(async (response) => {
        setDialogTitle(`Downloading ${fileNameHeader}`);
        return await response.blob();
      })
      .then((blob) => {
        const blobUrl = window.URL.createObjectURL(blob);
        APIClient.downloadBlob(blobUrl, fileNameHeader);
        setDialogTitle("Download complete");
      })
      .catch((error) => {
        if (abortController.signal.aborted) return;
        toast.error("Error downloading: ", error?.text ?? "Unknown");
        setDialogTitle("Download failed");
        setIsDownloadFinished(true);
        setErrorOccurred(true);
      });

    return () => abortController.abort("Interrupted All Documents download");
  }, [isOpen]);

  const humanReadableFileSize = (numberOfBytes: number): string => {
    const i = numberOfBytes == 0 ? 0 : Math.floor(Math.log(numberOfBytes) / Math.log(1024));
    return +(numberOfBytes / Math.pow(1024, i)).toFixed(2) * 1 + [" bytes", "kB", "MB", "GB", "TB"][i];
  };

  const getProgressBarVariant = (): React.ComponentProps<typeof LinearProgress>["variant"] => {
    if (errorOccurred || isDownloadFinished) {
      return "determinate";
    }
    return "indeterminate";
  };

  return (
    <Dialog maxWidth="lg" open={isOpen} onClose={() => isDownloadFinished && onClose()}>
      <DialogTitle>{dialogTitle}</DialogTitle>
      <DialogContent sx={{ minWidth: "520px" }}>
        <LinearProgress variant={getProgressBarVariant()} value={100} color={errorOccurred ? "error" : "primary"} />

        <Typography variant="caption">
          Current file: {currentFileName}
          <br />
          Total files: {totalFiles?.toLocaleString()}
          <br />
          Downloaded: {humanReadableFileSize(bytesDownloadedCount)}
        </Typography>
      </DialogContent>
      <DialogActions>
        <Button disabled={!isDownloadFinished} variant="contained" onClick={onClose} autoFocus>
          Done
        </Button>
      </DialogActions>
    </Dialog>
  );
};
