export class ZipDataUtils {
  public static readonly FILE_HEADER_LENGTH_EXCLUDING_VARIABLE_DATA = 30;
  public static readonly EOCD_LENGTH_EXCLUDING_VARIABLE_DATA = 22;
  public static readonly FILE_HEADER_SIGNATURE = String.fromCharCode(0x50, 0x4b, 0x03, 0x04);
  public static readonly EOCD_HEADER_SIGNATURE = String.fromCharCode(0x50, 0x4b, 0x05, 0x06);

  public static getFullLengthOfHeaderIncludingVariableData(headerBytes: Uint8Array): number {
    const headerDataView = new DataView(headerBytes.buffer, headerBytes.byteOffset, headerBytes.byteLength);
    const fileNameDataLength = headerDataView.getUint16(26, true);
    const extraFieldDataLength = headerDataView.getUint16(28, true);
    return ZipDataUtils.FILE_HEADER_LENGTH_EXCLUDING_VARIABLE_DATA + fileNameDataLength + extraFieldDataLength;
  }

  public static isByteArrayFileHeaderStart(bytes: Uint8Array): boolean {
    return new TextDecoder("utf-8").decode(bytes) == ZipDataUtils.FILE_HEADER_SIGNATURE;
  }
}
