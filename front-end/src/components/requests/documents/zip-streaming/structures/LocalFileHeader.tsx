class GeneralPurposeBitFlag {
  isEncrypted: boolean;
  compressionOption: boolean;
  hasDataDescriptor: boolean;
  isStrongEncryption: boolean;
  isUtf8: boolean;

  constructor() {
    this.isEncrypted = false;
    this.compressionOption = false;
    this.hasDataDescriptor = false;
    this.isStrongEncryption = false;
    this.isUtf8 = false;
  }

  static fromUint8Array(data: Uint8Array): GeneralPurposeBitFlag {
    const flag = new GeneralPurposeBitFlag();
    const byte = data[0]; // Assuming the flag is in the first byte

    flag.isEncrypted = (byte & 0x01) !== 0;
    flag.compressionOption = (byte & 0x02) !== 0;
    flag.hasDataDescriptor = (byte & 0x08) !== 0;
    flag.isStrongEncryption = (byte & 0x40) !== 0;
    flag.isUtf8 = (byte & 0x0800) !== 0;

    return flag;
  }
}

export class LocalFileHeader {
  private static readonly SIGNATURE_OFFSET = 0;
  private static readonly MIN_VERSION_OFFSET = 4;
  private static readonly BIT_FLAG_OFFSET = 6;
  private static readonly COMPRESSION_OFFSET = 8;
  private static readonly MOD_TIME_OFFSET = 10;
  private static readonly MOD_DATE_OFFSET = 12;
  private static readonly CRC32_OFFSET = 14;
  private static readonly COMPRESSED_SIZE_OFFSET = 18;
  private static readonly UNCOMPRESSED_SIZE_OFFSET = 22;
  private static readonly FILE_NAME_LENGTH_OFFSET = 26;
  private static readonly EXTRA_FIELD_LENGTH_OFFSET = 28;
  private static readonly FILE_NAME_OFFSET = 30;

  public signature: string;
  public minimumVersionForExtraction: number;
  public generalPurposeBitFlag: GeneralPurposeBitFlag;
  public compression: number;
  public fileLastModificationTime: number;
  public fileLastModificationDate: number;
  public CRC32: Uint8Array;
  public compressedSize: number;
  public uncompressedSize: number;
  public fileNameLength: number;
  public extraFieldLength: number;
  public fileName: string;
  public extraField: Uint8Array;

  constructor(fullHeaderBytes: Uint8Array) {
    const headerDataView = new DataView(fullHeaderBytes.buffer, fullHeaderBytes.byteOffset, fullHeaderBytes.byteLength);
    const textDecoder = new TextDecoder("utf-8");

    this.signature = this.decodeString(fullHeaderBytes, LocalFileHeader.SIGNATURE_OFFSET, 4, textDecoder);
    this.minimumVersionForExtraction = headerDataView.getUint16(LocalFileHeader.MIN_VERSION_OFFSET, true);
    this.generalPurposeBitFlag = GeneralPurposeBitFlag.fromUint8Array(
      fullHeaderBytes.subarray(LocalFileHeader.BIT_FLAG_OFFSET, LocalFileHeader.BIT_FLAG_OFFSET + 2),
    );
    this.compression = headerDataView.getUint16(LocalFileHeader.COMPRESSION_OFFSET, true);
    this.fileLastModificationTime = headerDataView.getUint16(LocalFileHeader.MOD_TIME_OFFSET, true);
    this.fileLastModificationDate = headerDataView.getUint16(LocalFileHeader.MOD_DATE_OFFSET, true);
    this.CRC32 = fullHeaderBytes.subarray(LocalFileHeader.CRC32_OFFSET, LocalFileHeader.CRC32_OFFSET + 4);
    this.compressedSize = headerDataView.getUint32(LocalFileHeader.COMPRESSED_SIZE_OFFSET, true);
    this.uncompressedSize = headerDataView.getUint32(LocalFileHeader.UNCOMPRESSED_SIZE_OFFSET, true);
    this.fileNameLength = headerDataView.getUint16(LocalFileHeader.FILE_NAME_LENGTH_OFFSET, true);
    this.extraFieldLength = headerDataView.getUint16(LocalFileHeader.EXTRA_FIELD_LENGTH_OFFSET, true);
    this.fileName = this.decodeString(
      fullHeaderBytes,
      LocalFileHeader.FILE_NAME_OFFSET,
      this.fileNameLength,
      textDecoder,
    );

    const extraFieldOffset = LocalFileHeader.FILE_NAME_OFFSET + this.fileNameLength;
    this.extraField = fullHeaderBytes.subarray(extraFieldOffset, extraFieldOffset + this.extraFieldLength);
  }

  private decodeString(bytes: Uint8Array, start: number, length: number, decoder: TextDecoder): string {
    return decoder.decode(bytes.subarray(start, start + length));
  }
}
