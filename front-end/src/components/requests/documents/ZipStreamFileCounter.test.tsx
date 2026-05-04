import { ZipStreamFileCounter } from "./ZipStreamFileCounter";

describe("ZipStreamFileCounter", () => {
  let zipStreamFileCounter: ZipStreamFileCounter;
  const onFileNameObserved = jest.fn();

  beforeEach(() => {
    zipStreamFileCounter = new ZipStreamFileCounter(onFileNameObserved);
    jest.resetAllMocks();
  });

  test("should process potential header data and update file count", () => {
    jest.mock("./zip-streaming/ZipDataUtils", () => ({
      isByteArrayFileHeaderStart: jest.fn().mockReturnValue(true),
      getFullLengthOfHeaderIncludingVariableData: jest.fn().mockReturnValue(42),
    }));

    const headerSignature = new Uint8Array([0x50, 0x4b, 0x03, 0x04, ...new Uint8Array(30)]);
    zipStreamFileCounter.scanForPotentialFileHeaders(headerSignature);

    expect(onFileNameObserved).toHaveBeenCalledWith("");
  });

  test("should not update file count if header signature is not found", () => {
    jest.mock("./zip-streaming/ZipDataUtils", () => ({
      isByteArrayFileHeaderStart: jest.fn().mockReturnValue(false),
    }));

    const nonHeaderData = new Uint8Array([0x00, 0x00, 0x00, 0x00]);
    zipStreamFileCounter.scanForPotentialFileHeaders(nonHeaderData);

    expect(onFileNameObserved).not.toHaveBeenCalled();
  });
});
