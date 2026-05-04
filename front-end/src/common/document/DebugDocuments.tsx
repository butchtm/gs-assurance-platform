import { RequiredDocumentsResponse } from "../../components/requests/Requests";
import { mockActiveReferenceTypes } from "../data-types/DebugReferenceTypes";
import { PiiActionCode } from "../piiAnalysis/PiiAction";

import { DocumentPiiScanStatus, GSDocument } from "./GSDocument";

export const mockDocuments: GSDocument[] = [
  {
    id: "doc1",
    containerName: "Container1",
    fileName: "Document1.pdf",
    fileSize: 1024, // Size in KB
    hashValue: "abcdef123456",
    access: true,
    category: true,
    documentTypeId: 1,
    contentType: "application/pdf",
    documentCategoryType: 1,
    latest: true,
    modifiedTimestamp: "2023-01-01T12:00:00.000Z",
    uploadedTimestamp: "2023-01-01T10:00:00.000Z",
    piiScanStatus: DocumentPiiScanStatus.COMPLETED_PII_FOUND,
    piiAction: PiiActionCode.NEW,
  },
  {
    id: "doc11",
    containerName: "Container1",
    fileName: "OldDocument1.pdf",
    fileSize: 1024, // Size in KB
    hashValue: "abcdef123456",
    access: true,
    category: true,
    documentTypeId: 1,
    contentType: "application/pdf",
    documentCategoryType: 1,
    latest: true,
    modifiedTimestamp: "2022-01-01T12:00:00.000Z",
    uploadedTimestamp: "2022-01-01T10:00:00.000Z",
    piiScanStatus: DocumentPiiScanStatus.IN_PROGRESS,
  },
  {
    id: "doc2",
    containerName: "Container2",
    fileName: "Document2.docx",
    fileSize: 2048, // Size in KB
    hashValue: "123456abcdef",
    access: false,
    category: false,
    documentTypeId: 2,
    contentType: "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    documentCategoryType: 2,
    latest: false,
    modifiedTimestamp: "2023-02-02T18:00:00.000Z",
    uploadedTimestamp: "2023-02-02T10:00:00.000Z",
    piiScanStatus: DocumentPiiScanStatus.COMPLETED_NO_PII,
  },
  {
    id: "doc3",
    containerName: "Container3",
    fileName: "Image3.png",
    fileSize: 512, // Size in KB
    hashValue: "fedcba654321",
    access: true,
    category: true,
    documentTypeId: 3,
    contentType: "image/png",
    documentCategoryType: 2,
    latest: true,
    modifiedTimestamp: "2023-03-03T12:00:00.000Z",
    uploadedTimestamp: "2023-03-03T10:00:00.000Z",
    piiScanStatus: DocumentPiiScanStatus.FAILED,
  },
  {
    id: "doc4",
    containerName: "Container4",
    fileName: "Image4.png",
    fileSize: 512, // Size in KB
    hashValue: "fedcba654321",
    access: false,
    category: true,
    documentTypeId: 4,
    contentType: "image/png",
    documentCategoryType: 2,
    latest: true,
    modifiedTimestamp: "2023-03-03T12:00:00.000Z",
    uploadedTimestamp: "2023-03-03T10:00:00.000Z",
  },
];

export const mockPiiDocuments: GSDocument[] = [
  {
    id: "doc1",
    containerName: "Container1",
    fileName: "Document1.pdf",
    fileSize: 1024, // Size in KB
    hashValue: "abcdef123456",
    access: false,
    category: true,
    documentTypeId: 1,
    contentType: "application/pdf",
    documentCategoryType: 1,
    latest: true,
    modifiedTimestamp: "2023-01-01T12:00:00.000Z",
    uploadedTimestamp: "2023-01-01T10:00:00.000Z",
    piiScanStatus: DocumentPiiScanStatus.COMPLETED_PII_FOUND,
    piiAction: PiiActionCode.NEW,
  },
  {
    id: "doc2",
    containerName: "Container2",
    fileName: "Document2.pdf",
    fileSize: 1024, // Size in KB
    hashValue: "abcdef123456",
    access: false,
    category: true,
    documentTypeId: 1,
    contentType: "application/pdf",
    documentCategoryType: 1,
    latest: true,
    modifiedTimestamp: "2023-01-01T12:00:00.000Z",
    uploadedTimestamp: "2023-01-01T10:00:00.000Z",
    piiScanStatus: DocumentPiiScanStatus.COMPLETED_PII_FOUND,
    piiAction: PiiActionCode.ACCEPTED,
  },
  {
    id: "doc3",
    containerName: "Container3",
    fileName: "Document3.pdf",
    fileSize: 1024, // Size in KB
    hashValue: "abcdef123456",
    access: false,
    category: true,
    documentTypeId: 1,
    contentType: "application/pdf",
    documentCategoryType: 1,
    latest: true,
    modifiedTimestamp: "2023-01-01T12:00:00.000Z",
    uploadedTimestamp: "2023-01-01T10:00:00.000Z",
    piiScanStatus: DocumentPiiScanStatus.COMPLETED_NO_PII,
    piiAction: PiiActionCode.NO_PII_FOUND,
  },
];

export const mockRequiredDocumentsResponse: RequiredDocumentsResponse = {
  OTHER: [
    {
      requestTypeId: 1,
      documentTypeId: 4,
      documentDisplayCategoryId: 301,
      documentTypeDescription: "Other Document Type 1",
      documentDisplayCategoryCode: "ODC1",
    },
    {
      requestTypeId: 2,
      documentTypeId: 3,
      documentDisplayCategoryId: 302,
      documentTypeDescription: "Other Document Type 2",
      documentDisplayCategoryCode: "ODC2",
    },
  ],
  REQUIRED: [
    {
      requestTypeId: 1,
      documentTypeId: 1,
      documentDisplayCategoryId: 303,
      documentTypeDescription: "Required Document Type 1",
      documentDisplayCategoryCode: "RDC1",
    },
    {
      requestTypeId: 2,
      documentTypeId: 2,
      documentDisplayCategoryId: 304,
      documentTypeDescription: "Required Document Type 2",
      documentDisplayCategoryCode: "RDC2",
      formType: mockActiveReferenceTypes.DigitalFormType[0],
    },
  ],
  SUPPORTING: [],
};

export const documentPiiResults = {
  content: [
    {
      entity: "ADDRESS",
      probability: "HIGH",
      pageNumber: 1,
      occurrences: 2,
    },
    {
      entity: "PHONE",
      probability: "HIGH",
      pageNumber: 2,
      occurrences: 4,
    },
    {
      entity: "ADDRESS",
      probability: "HIGH",
      pageNumber: 3,
      occurrences: 1,
    },
    {
      entity: "EMAIL",
      probability: "HIGH",
      pageNumber: 5,
      occurrences: 4,
    },
    {
      entity: "NAME",
      probability: "HIGH",
      pageNumber: 5,
      occurrences: 1,
    },
  ],
  pageSize: 5,
  pageNumber: 1,
  totalPages: 2,
  totalElements: 9,
};

export const documentPiiResultsWithNoPageNumber = {
  content: [
    {
      entity: "ADDRESS",
      probability: "HIGH",
      occurrences: 2,
    },
    {
      entity: "PHONE",
      probability: "HIGH",
      occurrences: 4,
    },
  ],
  pageSize: 5,
  pageNumber: 1,
  totalPages: 1,
  totalElements: 3,
};

export const documentPiiResultsWithLocations = {
  content: [
    {
      entity: "ADDRESS",
      probability: "HIGH",
      location: "Sheet1!A2",
      occurrences: 2,
    },
    {
      entity: "PHONE",
      probability: "HIGH",
      location: "Sheet2!B2",
      occurrences: 4,
    },
  ],
  pageSize: 5,
  pageNumber: 1,
  totalPages: 1,
  totalElements: 3,
};
