import { ReferenceType } from "../data-types/DataReferenceTypes";
import { PiiActionCode } from "../piiAnalysis/PiiAction";

export class DocumentCategoryType {
  static readonly REQUIRED = new DocumentCategoryType(1, "Required Documents");
  static readonly OTHER = new DocumentCategoryType(2, "Other Documents");
  static readonly SUPPORT = new DocumentCategoryType(3, "Support Documents");

  private constructor(
    public readonly id: number,
    public readonly description: string,
  ) {}

  public toString() {
    return this.id.toString();
  }
}

export enum DocumentPiiScanStatus {
  IN_PROGRESS = "IN_PROGRESS",
  COMPLETED_NO_PII = "COMPLETED_NO_PII",
  COMPLETED_PII_FOUND = "COMPLETED_PII_FOUND",
  FAILED = "FAILED",
}

export interface GSDocument {
  id: string;
  containerName: string;
  fileName: string;
  fileSize: number;
  hashValue: string;
  access: boolean;
  category: boolean;
  documentTypeId: number;
  contentType: string;
  documentCategoryType: number;
  latest: boolean;
  modifiedTimestamp: string;
  uploadedTimestamp: string;
  piiScanStatus?: DocumentPiiScanStatus;
  piiAction?: PiiActionCode;
  isMissing?: boolean;
  uploadedByAccountType?: ReferenceType;
}
