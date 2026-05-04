import { ZipStreamFileCounter } from "./ZipStreamFileCounter";

export class ZipStreamProcessor {
  private receivedLength = 0;
  private readonly zipStreamFileCounter: ZipStreamFileCounter;

  constructor(
    private readonly onDownloadFinished: () => void,
    private readonly onProgressUpdate: (numberOfBytesDownloaded: number) => void,
    onFileNameObserved: (fileName: string) => void,
  ) {
    this.onDownloadFinished = onDownloadFinished;
    this.onProgressUpdate = onProgressUpdate;
    this.zipStreamFileCounter = new ZipStreamFileCounter(onFileNameObserved);
  }

  public createStream(reader: ReadableStreamDefaultReader<Uint8Array>): ReadableStream<string> {
    return new ReadableStream({
      start: (controller) => {
        this.pushData(reader, controller);
      },
    });
  }

  private async pushData(
    reader: ReadableStreamDefaultReader<Uint8Array>,
    controller: ReadableStreamDefaultController<unknown>,
  ) {
    const { done, value } = await reader.read();
    if (done) {
      controller.close();
      this.onDownloadFinished();
      return;
    }

    this.zipStreamFileCounter.scanForPotentialFileHeaders(value);

    this.receivedLength += value.length;
    this.onProgressUpdate(this.receivedLength);

    controller.enqueue(value);
    this.pushData(reader, controller);
  }
}
