import { ZipDataUtils } from "./ZipDataUtils";

describe("ZipDataUtils", () => {
  test("should detect file header start correctly", () => {
    const headerStart = new Uint8Array([0x50, 0x4b, 0x03, 0x04]);
    const notHeaderStart = new Uint8Array([0x00, 0x00, 0x00, 0x00]);
    expect(ZipDataUtils.isByteArrayFileHeaderStart(headerStart)).toBe(true);
    expect(ZipDataUtils.isByteArrayFileHeaderStart(notHeaderStart)).toBe(false);
  });

  test("should calculate full length of header including variable data correctly", () => {
    const headerBytes = new Uint8Array(30);
    const dataView = new DataView(headerBytes.buffer);
    const fileNameDataLength = 10;
    const extraFieldDataLength = 5;
    dataView.setUint16(26, fileNameDataLength, true);
    dataView.setUint16(28, extraFieldDataLength, true);

    const result = ZipDataUtils.getFullLengthOfHeaderIncludingVariableData(headerBytes);

    expect(result).toBe(
      fileNameDataLength + extraFieldDataLength + ZipDataUtils.FILE_HEADER_LENGTH_EXCLUDING_VARIABLE_DATA,
    );
  });
});
