import { LocalFileHeader } from "./zip-streaming/structures/LocalFileHeader";
import { ZipDataUtils } from "./zip-streaming/ZipDataUtils";

export class ZipStreamFileCounter {
  // Explicitly set the generic type argument to ArrayBuffer
  private recentDataBuffer: Uint8Array<ArrayBuffer> = new Uint8Array();

  constructor(private readonly onFileNameObserved: (fileName: string) => void) {
    this.onFileNameObserved = onFileNameObserved;
  }
  public scanForPotentialFileHeaders(additionalBytes: Uint8Array) {
    this.recentDataBuffer = this.concatenateBuffers(this.recentDataBuffer, additionalBytes);
    this.scanRecentDataBufferForFileHeaders();
  }

  private concatenateBuffers(buffer1: Uint8Array<ArrayBuffer>, buffer2: Uint8Array): Uint8Array<ArrayBuffer> {
    const concatenated = new Uint8Array(buffer1.length + buffer2.length);
    concatenated.set(buffer1);
    concatenated.set(buffer2, buffer1.length);
    return concatenated;
  }

  private scanRecentDataBufferForFileHeaders() {
    while (this.recentDataBuffer.length > ZipDataUtils.FILE_HEADER_LENGTH_EXCLUDING_VARIABLE_DATA) {
      const possibleHeaderStart = this.readBytesFromRecentDataBuffer(ZipDataUtils.FILE_HEADER_SIGNATURE.length);
      if (!ZipDataUtils.isByteArrayFileHeaderStart(possibleHeaderStart)) {
        this.popBytesFromRecentDataBuffer(1);
        continue;
      }

      const staticHeaderBytes = this.readBytesFromRecentDataBuffer(
        ZipDataUtils.FILE_HEADER_LENGTH_EXCLUDING_VARIABLE_DATA,
      );
      const fullHeaderLength = ZipDataUtils.getFullLengthOfHeaderIncludingVariableData(staticHeaderBytes);
      if (this.recentDataBuffer.length < fullHeaderLength) {
        break;
      }

      const localFileHeader = new LocalFileHeader(this.recentDataBuffer);
      this.onFileNameObserved(localFileHeader.fileName);

      this.popBytesFromRecentDataBuffer(fullHeaderLength);
    }
  }

  private readBytesFromRecentDataBuffer(numberOfBytes: number) {
    return this.recentDataBuffer.subarray(0, numberOfBytes);
  }

  private popBytesFromRecentDataBuffer(numberOfBytes: number) {
    this.recentDataBuffer = this.recentDataBuffer.subarray(numberOfBytes);
  }
}
