import { ReferenceType } from "../data-types/DataReferenceTypes";

export interface GSDocumentScrap {
  requestTypeId: number;
  documentTypeId: number;
  documentDisplayCategoryId: number;
  documentTypeDescription: string;
  documentDisplayCategoryCode: string;
  formType?: ReferenceType | null;
}
