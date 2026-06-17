--
-- PostgreSQL database dump
--

-- Dumped from database version 15.4
-- Dumped by pg_dump version 16.0

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: scdatacopy; Type: SCHEMA; Schema: -; Owner: postgres
--

DROP SCHEMA IF EXISTS scdatacopy CASCADE;
CREATE SCHEMA scdatacopy;


ALTER SCHEMA scdatacopy OWNER TO postgres;

--
-- Name: BaseProjectDocument; Type: FOREIGN TABLE; Schema: scdatacopy; Owner: postgres
--

CREATE FOREIGN TABLE scdatacopy."BaseProjectDocument" (
    "DocumentsID" uuid,
    "ProjectsID" integer
)
SERVER scdatacopy
OPTIONS (
    schema_name 'dbo',
    table_name 'BaseProjectDocument'
);
ALTER FOREIGN TABLE scdatacopy."BaseProjectDocument" ALTER COLUMN "DocumentsID" OPTIONS (
    column_name 'DocumentsID'
);
ALTER FOREIGN TABLE scdatacopy."BaseProjectDocument" ALTER COLUMN "ProjectsID" OPTIONS (
    column_name 'ProjectsID'
);


ALTER FOREIGN TABLE scdatacopy."BaseProjectDocument" OWNER TO postgres;

--
-- Name: BaseProjectReview; Type: FOREIGN TABLE; Schema: scdatacopy; Owner: postgres
--

CREATE FOREIGN TABLE scdatacopy."BaseProjectReview" (
    "ProjectsID" integer,
    "ReviewsID" integer
)
SERVER scdatacopy
OPTIONS (
    schema_name 'dbo',
    table_name 'BaseProjectReview'
);
ALTER FOREIGN TABLE scdatacopy."BaseProjectReview" ALTER COLUMN "ProjectsID" OPTIONS (
    column_name 'ProjectsID'
);
ALTER FOREIGN TABLE scdatacopy."BaseProjectReview" ALTER COLUMN "ReviewsID" OPTIONS (
    column_name 'ReviewsID'
);


ALTER FOREIGN TABLE scdatacopy."BaseProjectReview" OWNER TO postgres;

--
-- Name: Bundles; Type: FOREIGN TABLE; Schema: scdatacopy; Owner: postgres
--

CREATE FOREIGN TABLE scdatacopy."Bundles" (
    "ID" integer,
    "Name" character varying(255),
    "MasterProjectCategory" integer,
    "MasterProjectID" integer
)
SERVER scdatacopy
OPTIONS (
    schema_name 'dbo',
    table_name 'Bundles'
);
ALTER FOREIGN TABLE scdatacopy."Bundles" ALTER COLUMN "ID" OPTIONS (
    column_name 'ID'
);
ALTER FOREIGN TABLE scdatacopy."Bundles" ALTER COLUMN "Name" OPTIONS (
    column_name 'Name'
);
ALTER FOREIGN TABLE scdatacopy."Bundles" ALTER COLUMN "MasterProjectCategory" OPTIONS (
    column_name 'MasterProjectCategory'
);
ALTER FOREIGN TABLE scdatacopy."Bundles" ALTER COLUMN "MasterProjectID" OPTIONS (
    column_name 'MasterProjectID'
);


ALTER FOREIGN TABLE scdatacopy."Bundles" OWNER TO postgres;

--
-- Name: DocumentReview; Type: FOREIGN TABLE; Schema: scdatacopy; Owner: postgres
--

CREATE FOREIGN TABLE scdatacopy."DocumentReview" (
    "DocumentsID" uuid,
    "ReviewsID" integer
)
SERVER scdatacopy
OPTIONS (
    schema_name 'dbo',
    table_name 'DocumentReview'
);
ALTER FOREIGN TABLE scdatacopy."DocumentReview" ALTER COLUMN "DocumentsID" OPTIONS (
    column_name 'DocumentsID'
);
ALTER FOREIGN TABLE scdatacopy."DocumentReview" ALTER COLUMN "ReviewsID" OPTIONS (
    column_name 'ReviewsID'
);


ALTER FOREIGN TABLE scdatacopy."DocumentReview" OWNER TO postgres;

--
-- Name: Documents; Type: FOREIGN TABLE; Schema: scdatacopy; Owner: postgres
--

CREATE FOREIGN TABLE scdatacopy."Documents" (
    "ID" uuid,
    "ContainerName" character varying(450),
    "FileName" character varying(1024),
    "FileSize" bigint,
    "Hash" bytea,
    "Access" integer,
    "Category" integer,
    "DocumentType" integer,
    "ContentType" text,
    "CreatedDate" timestamp(6) without time zone,
    "LastModifiedDate" timestamp(6) without time zone,
    "IsLatest" smallint
)
SERVER scdatacopy
OPTIONS (
    schema_name 'dbo',
    table_name 'Documents'
);
ALTER FOREIGN TABLE scdatacopy."Documents" ALTER COLUMN "ID" OPTIONS (
    column_name 'ID'
);
ALTER FOREIGN TABLE scdatacopy."Documents" ALTER COLUMN "ContainerName" OPTIONS (
    column_name 'ContainerName'
);
ALTER FOREIGN TABLE scdatacopy."Documents" ALTER COLUMN "FileName" OPTIONS (
    column_name 'FileName'
);
ALTER FOREIGN TABLE scdatacopy."Documents" ALTER COLUMN "FileSize" OPTIONS (
    column_name 'FileSize'
);
ALTER FOREIGN TABLE scdatacopy."Documents" ALTER COLUMN "Hash" OPTIONS (
    column_name 'Hash'
);
ALTER FOREIGN TABLE scdatacopy."Documents" ALTER COLUMN "Access" OPTIONS (
    column_name 'Access'
);
ALTER FOREIGN TABLE scdatacopy."Documents" ALTER COLUMN "Category" OPTIONS (
    column_name 'Category'
);
ALTER FOREIGN TABLE scdatacopy."Documents" ALTER COLUMN "DocumentType" OPTIONS (
    column_name 'DocumentType'
);
ALTER FOREIGN TABLE scdatacopy."Documents" ALTER COLUMN "ContentType" OPTIONS (
    column_name 'ContentType'
);
ALTER FOREIGN TABLE scdatacopy."Documents" ALTER COLUMN "CreatedDate" OPTIONS (
    column_name 'CreatedDate'
);
ALTER FOREIGN TABLE scdatacopy."Documents" ALTER COLUMN "LastModifiedDate" OPTIONS (
    column_name 'LastModifiedDate'
);
ALTER FOREIGN TABLE scdatacopy."Documents" ALTER COLUMN "IsLatest" OPTIONS (
    column_name 'IsLatest'
);


ALTER FOREIGN TABLE scdatacopy."Documents" OWNER TO postgres;

--
-- Name: EnergyProjectsDetails; Type: FOREIGN TABLE; Schema: scdatacopy; Owner: postgres
--

CREATE FOREIGN TABLE scdatacopy."EnergyProjectsDetails" (
    "ProjectLinkInOtherScheme" text,
    "CreditingPeriodEndDateOtherSchemes" timestamp(6) without time zone,
    "CreditingPeriodStartDateOtherSchemes" timestamp(6) without time zone,
    "TransitionApprovalProcedure" integer,
    "TransitionFromOtherScheme" integer,
    "TransitionPathway" integer,
    "ID" integer
)
SERVER scdatacopy
OPTIONS (
    schema_name 'dbo',
    table_name 'EnergyProjectsDetails'
);
ALTER FOREIGN TABLE scdatacopy."EnergyProjectsDetails" ALTER COLUMN "ProjectLinkInOtherScheme" OPTIONS (
    column_name 'ProjectLinkInOtherScheme'
);
ALTER FOREIGN TABLE scdatacopy."EnergyProjectsDetails" ALTER COLUMN "CreditingPeriodEndDateOtherSchemes" OPTIONS (
    column_name 'CreditingPeriodEndDateOtherSchemes'
);
ALTER FOREIGN TABLE scdatacopy."EnergyProjectsDetails" ALTER COLUMN "CreditingPeriodStartDateOtherSchemes" OPTIONS (
    column_name 'CreditingPeriodStartDateOtherSchemes'
);
ALTER FOREIGN TABLE scdatacopy."EnergyProjectsDetails" ALTER COLUMN "TransitionApprovalProcedure" OPTIONS (
    column_name 'TransitionApprovalProcedure'
);
ALTER FOREIGN TABLE scdatacopy."EnergyProjectsDetails" ALTER COLUMN "TransitionFromOtherScheme" OPTIONS (
    column_name 'TransitionFromOtherScheme'
);
ALTER FOREIGN TABLE scdatacopy."EnergyProjectsDetails" ALTER COLUMN "TransitionPathway" OPTIONS (
    column_name 'TransitionPathway'
);
ALTER FOREIGN TABLE scdatacopy."EnergyProjectsDetails" ALTER COLUMN "ID" OPTIONS (
    column_name 'ID'
);


ALTER FOREIGN TABLE scdatacopy."EnergyProjectsDetails" OWNER TO postgres;

--
-- Name: Invoices; Type: FOREIGN TABLE; Schema: scdatacopy; Owner: postgres
--

CREATE FOREIGN TABLE scdatacopy."Invoices" (
    "ID" integer,
    "Amount" numeric(18,2),
    "Status" integer,
    "IsArchived" smallint,
    "StatusChangedAt" timestamp(6) without time zone,
    "CustomerEmail" text,
    "LastManualCheck" timestamp(6) without time zone
)
SERVER scdatacopy
OPTIONS (
    schema_name 'dbo',
    table_name 'Invoices'
);
ALTER FOREIGN TABLE scdatacopy."Invoices" ALTER COLUMN "ID" OPTIONS (
    column_name 'ID'
);
ALTER FOREIGN TABLE scdatacopy."Invoices" ALTER COLUMN "Amount" OPTIONS (
    column_name 'Amount'
);
ALTER FOREIGN TABLE scdatacopy."Invoices" ALTER COLUMN "Status" OPTIONS (
    column_name 'Status'
);
ALTER FOREIGN TABLE scdatacopy."Invoices" ALTER COLUMN "IsArchived" OPTIONS (
    column_name 'IsArchived'
);
ALTER FOREIGN TABLE scdatacopy."Invoices" ALTER COLUMN "StatusChangedAt" OPTIONS (
    column_name 'StatusChangedAt'
);
ALTER FOREIGN TABLE scdatacopy."Invoices" ALTER COLUMN "CustomerEmail" OPTIONS (
    column_name 'CustomerEmail'
);
ALTER FOREIGN TABLE scdatacopy."Invoices" ALTER COLUMN "LastManualCheck" OPTIONS (
    column_name 'LastManualCheck'
);


ALTER FOREIGN TABLE scdatacopy."Invoices" OWNER TO postgres;

--
-- Name: LUFProjectsDetails; Type: FOREIGN TABLE; Schema: scdatacopy; Owner: postgres
--

CREATE FOREIGN TABLE scdatacopy."LUFProjectsDetails" (
    "ARSoilCarbon" smallint,
    "EligibleAreaHa" integer,
    "SmallHolder" smallint,
    "TotalAreaHa" integer,
    "LastActivity" timestamp(6) without time zone,
    "Notes" text,
    "CertifiedDesignDate" timestamp(6) without time zone,
    "CertifiedProjectDate" timestamp(6) without time zone,
    "LastDesignCertificationRenewalDate" timestamp(6) without time zone,
    "LastPerformanceReviewDate" timestamp(6) without time zone,
    "ID" integer
)
SERVER scdatacopy
OPTIONS (
    schema_name 'dbo',
    table_name 'LUFProjectsDetails'
);
ALTER FOREIGN TABLE scdatacopy."LUFProjectsDetails" ALTER COLUMN "ARSoilCarbon" OPTIONS (
    column_name 'ARSoilCarbon'
);
ALTER FOREIGN TABLE scdatacopy."LUFProjectsDetails" ALTER COLUMN "EligibleAreaHa" OPTIONS (
    column_name 'EligibleAreaHa'
);
ALTER FOREIGN TABLE scdatacopy."LUFProjectsDetails" ALTER COLUMN "SmallHolder" OPTIONS (
    column_name 'SmallHolder'
);
ALTER FOREIGN TABLE scdatacopy."LUFProjectsDetails" ALTER COLUMN "TotalAreaHa" OPTIONS (
    column_name 'TotalAreaHa'
);
ALTER FOREIGN TABLE scdatacopy."LUFProjectsDetails" ALTER COLUMN "LastActivity" OPTIONS (
    column_name 'LastActivity'
);
ALTER FOREIGN TABLE scdatacopy."LUFProjectsDetails" ALTER COLUMN "Notes" OPTIONS (
    column_name 'Notes'
);
ALTER FOREIGN TABLE scdatacopy."LUFProjectsDetails" ALTER COLUMN "CertifiedDesignDate" OPTIONS (
    column_name 'CertifiedDesignDate'
);
ALTER FOREIGN TABLE scdatacopy."LUFProjectsDetails" ALTER COLUMN "CertifiedProjectDate" OPTIONS (
    column_name 'CertifiedProjectDate'
);
ALTER FOREIGN TABLE scdatacopy."LUFProjectsDetails" ALTER COLUMN "LastDesignCertificationRenewalDate" OPTIONS (
    column_name 'LastDesignCertificationRenewalDate'
);
ALTER FOREIGN TABLE scdatacopy."LUFProjectsDetails" ALTER COLUMN "LastPerformanceReviewDate" OPTIONS (
    column_name 'LastPerformanceReviewDate'
);
ALTER FOREIGN TABLE scdatacopy."LUFProjectsDetails" ALTER COLUMN "ID" OPTIONS (
    column_name 'ID'
);


ALTER FOREIGN TABLE scdatacopy."LUFProjectsDetails" OWNER TO postgres;

--
-- Name: ProjectStateChangeLog; Type: FOREIGN TABLE; Schema: scdatacopy; Owner: postgres
--

CREATE FOREIGN TABLE scdatacopy."ProjectStateChangeLog" (
    "ID" integer,
    "PreviousState" integer,
    "NextState" integer,
    "BaseProjectID" integer
)
SERVER scdatacopy
OPTIONS (
    schema_name 'dbo',
    table_name 'ProjectStateChangeLog'
);
ALTER FOREIGN TABLE scdatacopy."ProjectStateChangeLog" ALTER COLUMN "ID" OPTIONS (
    column_name 'ID'
);
ALTER FOREIGN TABLE scdatacopy."ProjectStateChangeLog" ALTER COLUMN "PreviousState" OPTIONS (
    column_name 'PreviousState'
);
ALTER FOREIGN TABLE scdatacopy."ProjectStateChangeLog" ALTER COLUMN "NextState" OPTIONS (
    column_name 'NextState'
);
ALTER FOREIGN TABLE scdatacopy."ProjectStateChangeLog" ALTER COLUMN "BaseProjectID" OPTIONS (
    column_name 'BaseProjectID'
);


ALTER FOREIGN TABLE scdatacopy."ProjectStateChangeLog" OWNER TO postgres;

--
-- Name: Projects; Type: FOREIGN TABLE; Schema: scdatacopy; Owner: postgres
--

CREATE FOREIGN TABLE scdatacopy."Projects" (
    "ID" integer,
    "GSID" text,
    "Status" smallint,
    "Name" character varying(255),
    "Description" text,
    "ProgrammeOfActivities" integer,
    "ProjectLocation" text,
    "ProjectStartDate" timestamp(6) without time zone,
    "ProjectCategory" integer,
    "RegistrationType" integer,
    "HasProjectAssets" smallint,
    "ProjectAssets" text,
    "CurrentStandardVersion" integer,
    "CarbonStream" integer,
    "ProjectDeveloperName" text,
    "ProjectDeveloperEmail" text,
    "ProjectSize" integer,
    "ZohoId" text,
    "CurrentReviewID" integer,
    "ProjectState_ProjectState" integer,
    "ProjectTypes" text,
    "CreditingPeriod1StartDate" timestamp(6) without time zone,
    "CreditingPeriod1EndDate" timestamp(6) without time zone,
    "CreditingPeriod2StartDate" timestamp(6) without time zone,
    "CreditingPeriod2EndDate" timestamp(6) without time zone,
    "CreditingPeriod3StartDate" timestamp(6) without time zone,
    "CreditingPeriod3EndDate" timestamp(6) without time zone,
    "BundleID" integer,
    "DesignCertificationDate" timestamp(6) without time zone,
    "DesignCertificationDateManualSet" smallint,
    "Methodologies" text,
    "FeeSchedule" integer,
    "SDGs" text,
    "EstimatedAnnualUnits" integer,
    "MasterProjectID" integer,
    "CityTownCommunity" text,
    "RegionStateProvince" text,
    "StreetAndNumber" text,
    "ListingDate" timestamp(6) without time zone,
    "ListingDateManualSet" smallint,
    "ParentProjectID" integer
)
SERVER scdatacopy
OPTIONS (
    schema_name 'dbo',
    table_name 'Projects'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "ID" OPTIONS (
    column_name 'ID'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "GSID" OPTIONS (
    column_name 'GSID'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "Status" OPTIONS (
    column_name 'Status'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "Name" OPTIONS (
    column_name 'Name'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "Description" OPTIONS (
    column_name 'Description'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "ProgrammeOfActivities" OPTIONS (
    column_name 'ProgrammeOfActivities'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "ProjectLocation" OPTIONS (
    column_name 'ProjectLocation'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "ProjectStartDate" OPTIONS (
    column_name 'ProjectStartDate'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "ProjectCategory" OPTIONS (
    column_name 'ProjectCategory'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "RegistrationType" OPTIONS (
    column_name 'RegistrationType'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "HasProjectAssets" OPTIONS (
    column_name 'HasProjectAssets'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "ProjectAssets" OPTIONS (
    column_name 'ProjectAssets'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "CurrentStandardVersion" OPTIONS (
    column_name 'CurrentStandardVersion'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "CarbonStream" OPTIONS (
    column_name 'CarbonStream'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "ProjectDeveloperName" OPTIONS (
    column_name 'ProjectDeveloperName'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "ProjectDeveloperEmail" OPTIONS (
    column_name 'ProjectDeveloperEmail'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "ProjectSize" OPTIONS (
    column_name 'ProjectSize'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "ZohoId" OPTIONS (
    column_name 'ZohoId'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "CurrentReviewID" OPTIONS (
    column_name 'CurrentReviewID'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "ProjectState_ProjectState" OPTIONS (
    column_name 'ProjectState_ProjectState'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "ProjectTypes" OPTIONS (
    column_name 'ProjectTypes'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "CreditingPeriod1StartDate" OPTIONS (
    column_name 'CreditingPeriod1StartDate'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "CreditingPeriod1EndDate" OPTIONS (
    column_name 'CreditingPeriod1EndDate'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "CreditingPeriod2StartDate" OPTIONS (
    column_name 'CreditingPeriod2StartDate'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "CreditingPeriod2EndDate" OPTIONS (
    column_name 'CreditingPeriod2EndDate'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "CreditingPeriod3StartDate" OPTIONS (
    column_name 'CreditingPeriod3StartDate'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "CreditingPeriod3EndDate" OPTIONS (
    column_name 'CreditingPeriod3EndDate'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "BundleID" OPTIONS (
    column_name 'BundleID'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "DesignCertificationDate" OPTIONS (
    column_name 'DesignCertificationDate'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "DesignCertificationDateManualSet" OPTIONS (
    column_name 'DesignCertificationDateManualSet'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "Methodologies" OPTIONS (
    column_name 'Methodologies'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "FeeSchedule" OPTIONS (
    column_name 'FeeSchedule'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "SDGs" OPTIONS (
    column_name 'SDGs'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "EstimatedAnnualUnits" OPTIONS (
    column_name 'EstimatedAnnualUnits'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "MasterProjectID" OPTIONS (
    column_name 'MasterProjectID'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "CityTownCommunity" OPTIONS (
    column_name 'CityTownCommunity'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "RegionStateProvince" OPTIONS (
    column_name 'RegionStateProvince'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "StreetAndNumber" OPTIONS (
    column_name 'StreetAndNumber'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "ListingDate" OPTIONS (
    column_name 'ListingDate'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "ListingDateManualSet" OPTIONS (
    column_name 'ListingDateManualSet'
);
ALTER FOREIGN TABLE scdatacopy."Projects" ALTER COLUMN "ParentProjectID" OPTIONS (
    column_name 'ParentProjectID'
);


ALTER FOREIGN TABLE scdatacopy."Projects" OWNER TO postgres;

--
-- Name: ReviewStateChangeLog; Type: FOREIGN TABLE; Schema: scdatacopy; Owner: postgres
--

CREATE FOREIGN TABLE scdatacopy."ReviewStateChangeLog" (
    "ID" integer,
    "ChangedBy" uuid,
    "ChangedAt" timestamp(6) without time zone,
    "PreviousState" integer,
    "NextState" integer,
    "ReviewID" integer
)
SERVER scdatacopy
OPTIONS (
    schema_name 'dbo',
    table_name 'ReviewStateChangeLog'
);
ALTER FOREIGN TABLE scdatacopy."ReviewStateChangeLog" ALTER COLUMN "ID" OPTIONS (
    column_name 'ID'
);
ALTER FOREIGN TABLE scdatacopy."ReviewStateChangeLog" ALTER COLUMN "ChangedBy" OPTIONS (
    column_name 'ChangedBy'
);
ALTER FOREIGN TABLE scdatacopy."ReviewStateChangeLog" ALTER COLUMN "ChangedAt" OPTIONS (
    column_name 'ChangedAt'
);
ALTER FOREIGN TABLE scdatacopy."ReviewStateChangeLog" ALTER COLUMN "PreviousState" OPTIONS (
    column_name 'PreviousState'
);
ALTER FOREIGN TABLE scdatacopy."ReviewStateChangeLog" ALTER COLUMN "NextState" OPTIONS (
    column_name 'NextState'
);
ALTER FOREIGN TABLE scdatacopy."ReviewStateChangeLog" ALTER COLUMN "ReviewID" OPTIONS (
    column_name 'ReviewID'
);


ALTER FOREIGN TABLE scdatacopy."ReviewStateChangeLog" OWNER TO postgres;

--
-- Name: Reviews; Type: FOREIGN TABLE; Schema: scdatacopy; Owner: postgres
--

CREATE FOREIGN TABLE scdatacopy."Reviews" (
    "ID" integer,
    "EndDate" timestamp(6) without time zone,
    "BookingDate" timestamp(6) without time zone,
    "ReviewType" integer,
    "Index" integer,
    "ReviewName" character varying(150),
    "ReviewState_LastStateChangeDate" timestamp(6) without time zone,
    "ReviewState_CurrentState" integer,
    "ReviewCreditingPeriod" integer,
    "InvoiceID" integer,
    "SameReviewTypeCount" integer,
    "ReviewDocumentStatus" integer,
    "AssignedExternalReviewer" uuid,
    "AssignedQAReviewer" uuid,
    "ReplacementReviewId" integer,
    "EmissionReductionAmount" integer,
    "ManuallyAdded" smallint
)
SERVER scdatacopy
OPTIONS (
    schema_name 'dbo',
    table_name 'Reviews'
);
ALTER FOREIGN TABLE scdatacopy."Reviews" ALTER COLUMN "ID" OPTIONS (
    column_name 'ID'
);
ALTER FOREIGN TABLE scdatacopy."Reviews" ALTER COLUMN "EndDate" OPTIONS (
    column_name 'EndDate'
);
ALTER FOREIGN TABLE scdatacopy."Reviews" ALTER COLUMN "BookingDate" OPTIONS (
    column_name 'BookingDate'
);
ALTER FOREIGN TABLE scdatacopy."Reviews" ALTER COLUMN "ReviewType" OPTIONS (
    column_name 'ReviewType'
);
ALTER FOREIGN TABLE scdatacopy."Reviews" ALTER COLUMN "Index" OPTIONS (
    column_name 'Index'
);
ALTER FOREIGN TABLE scdatacopy."Reviews" ALTER COLUMN "ReviewName" OPTIONS (
    column_name 'ReviewName'
);
ALTER FOREIGN TABLE scdatacopy."Reviews" ALTER COLUMN "ReviewState_LastStateChangeDate" OPTIONS (
    column_name 'ReviewState_LastStateChangeDate'
);
ALTER FOREIGN TABLE scdatacopy."Reviews" ALTER COLUMN "ReviewState_CurrentState" OPTIONS (
    column_name 'ReviewState_CurrentState'
);
ALTER FOREIGN TABLE scdatacopy."Reviews" ALTER COLUMN "ReviewCreditingPeriod" OPTIONS (
    column_name 'ReviewCreditingPeriod'
);
ALTER FOREIGN TABLE scdatacopy."Reviews" ALTER COLUMN "InvoiceID" OPTIONS (
    column_name 'InvoiceID'
);
ALTER FOREIGN TABLE scdatacopy."Reviews" ALTER COLUMN "SameReviewTypeCount" OPTIONS (
    column_name 'SameReviewTypeCount'
);
ALTER FOREIGN TABLE scdatacopy."Reviews" ALTER COLUMN "ReviewDocumentStatus" OPTIONS (
    column_name 'ReviewDocumentStatus'
);
ALTER FOREIGN TABLE scdatacopy."Reviews" ALTER COLUMN "AssignedExternalReviewer" OPTIONS (
    column_name 'AssignedExternalReviewer'
);
ALTER FOREIGN TABLE scdatacopy."Reviews" ALTER COLUMN "AssignedQAReviewer" OPTIONS (
    column_name 'AssignedQAReviewer'
);
ALTER FOREIGN TABLE scdatacopy."Reviews" ALTER COLUMN "ReplacementReviewId" OPTIONS (
    column_name 'ReplacementReviewId'
);
ALTER FOREIGN TABLE scdatacopy."Reviews" ALTER COLUMN "EmissionReductionAmount" OPTIONS (
    column_name 'EmissionReductionAmount'
);
ALTER FOREIGN TABLE scdatacopy."Reviews" ALTER COLUMN "ManuallyAdded" OPTIONS (
    column_name 'ManuallyAdded'
);


ALTER FOREIGN TABLE scdatacopy."Reviews" OWNER TO postgres;

--
-- Name: vwAccounts; Type: FOREIGN TABLE; Schema: scdatacopy; Owner: postgres
--

CREATE FOREIGN TABLE scdatacopy."vwAccounts" (
    id character varying(4000),
    "Account_Name" character varying(4000),
    "Account_Type" character varying(4000),
    "Created_Time" character varying(4000),
    "Address_1" character varying(4000),
    "Shipping_Code" character varying(4000),
    "Active" character varying(4000),
    "Postal_Code" character varying(4000)
)
SERVER scdatacopy
OPTIONS (
    schema_name 'dbo',
    table_name 'vwAccounts'
);
ALTER FOREIGN TABLE scdatacopy."vwAccounts" ALTER COLUMN id OPTIONS (
    column_name 'id'
);
ALTER FOREIGN TABLE scdatacopy."vwAccounts" ALTER COLUMN "Account_Name" OPTIONS (
    column_name 'Account_Name'
);
ALTER FOREIGN TABLE scdatacopy."vwAccounts" ALTER COLUMN "Account_Type" OPTIONS (
    column_name 'Account_Type'
);
ALTER FOREIGN TABLE scdatacopy."vwAccounts" ALTER COLUMN "Created_Time" OPTIONS (
    column_name 'Created_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwAccounts" ALTER COLUMN "Address_1" OPTIONS (
    column_name 'Address_1'
);
ALTER FOREIGN TABLE scdatacopy."vwAccounts" ALTER COLUMN "Shipping_Code" OPTIONS (
    column_name 'Shipping_Code'
);
ALTER FOREIGN TABLE scdatacopy."vwAccounts" ALTER COLUMN "Active" OPTIONS (
    column_name 'Active'
);
ALTER FOREIGN TABLE scdatacopy."vwAccounts" ALTER COLUMN "Postal_Code" OPTIONS (
    column_name 'Postal_Code'
);


ALTER FOREIGN TABLE scdatacopy."vwAccounts" OWNER TO postgres;

--
-- Name: vwCertificationFeedbacks; Type: FOREIGN TABLE; Schema: scdatacopy; Owner: postgres
--

CREATE FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" (
    "Total_Cost" character varying(4000),
    "PlndStartDTQA" character varying(4000),
    "Secondary_Email" character varying(4000),
    "CO_From_Review" character varying(4000),
    "File_Upload_URL_External" character varying(4000),
    "Modified_By" character varying(4000),
    "Email" character varying(4000),
    "IR_Name" character varying(4000),
    "Accept_assignment_IR" character varying(4000),
    "Feedback_Round" character varying(4000),
    "Issuance_Number" character varying(4000),
    "Start_Date_Planned" character varying(4000),
    "Notes1" character varying(8000),
    "ProgramType" character varying(4000),
    "Project_Name" character varying(4000),
    "GS_Notes" character varying(4000),
    "Feedback_Uploaded_to_SC_app" character varying(4000),
    "End_Date_Planned_GS" character varying(4000),
    "Considered_for_action" character varying(4000),
    "IR_Date_CO" character varying(4000),
    "End_Date_Planned_IR" character varying(4000),
    "Independent_Reviewer" character varying(4000),
    "Name" character varying(4000),
    "CO_Email" character varying(4000),
    "GS_Comments" character varying(4000),
    "COI_Date_CO" character varying(4000),
    "Conflict_Y_N_IR" character varying(4000),
    "Conflict_Y_N_CO" character varying(4000),
    "Locked__s" character varying(4000),
    "MP_End" character varying(4000),
    "Working_Hour" character varying(4000),
    "Exchange_Rate" character varying(4000),
    realocation character varying(4000),
    "Project_Status" character varying(4000),
    "OldEndDatePP" character varying(4000),
    "GS_TAC_Comments" character varying(4000),
    "IR_Officer_Email" character varying(4000),
    "TestCO" character varying(4000),
    "OldEndDateGS" character varying(4000),
    "User_Related_Activity_Time" character varying(4000),
    "Work_for_the_project_IR" character varying(4000),
    "File_Upload_URL_internal" character varying(4000),
    "Owner" character varying(4000),
    "End_Date_Planned" character varying(4000),
    "ActualStartDTQA" character varying(4000),
    "ActualEndDTQA" character varying(4000),
    "Response_of_the_Expert" character varying(4000),
    "Mail_send_to_ROE" character varying(4000),
    "TestPD" character varying(4000),
    "NotificationToQA" character varying(4000),
    "Project_Type" character varying(4000),
    "OldEndDateTAC" character varying(4000),
    "CR_Message" character varying(4000),
    "Created_Time" character varying(4000),
    "End_Date_Actual" character varying(4000),
    "Notification_GS" character varying(4000),
    "End_Date_Actual_GS" character varying(4000),
    "COI_Officer_Email" character varying(4000),
    "Layout" character varying(4000),
    "End_Date_Plnd_TAC" character varying(4000),
    "OldReviewTLRating" character varying(4000),
    "Record_Approval_Status" character varying(4000),
    "Send_notification_to_QA_Expert" character varying(4000),
    "NotificationFeedbackComplete" character varying(4000),
    "Last_Activity_Time" character varying(4000),
    "COI_Expert_Name" character varying(4000),
    "Review_Type" character varying(4000),
    "TestExpert" character varying(4000),
    "Design_Certification_Renewal" character varying(4000),
    "Expert" character varying(4000),
    "PP_Contact" character varying(4000),
    "Tag" character varying(4000),
    "NotificationPPFeedbackUploaded" character varying(4000),
    "Unsubscribed_Time" character varying(4000),
    "Remarks" character varying(4000),
    "URL_ID" character varying(4000),
    "Specify_your_work_for_the_project" character varying(4000),
    "Accept_assignment_share_review_feedback" character varying(4000),
    id character varying(4000),
    "Project_Scale" character varying(4000),
    "MP_Start" character varying(4000),
    "Combined_Review" character varying(4000),
    "System_Modified_Time" character varying(4000),
    "Technical_Expert" character varying(4000),
    "Work_for_the_project_CO" character varying(4000),
    "Date_of_Acceptance" character varying(4000),
    "Reviewer_Timeline_Rating" character varying(4000),
    "Check_Status" character varying(4000),
    "Folder_ID" character varying(4000),
    conflict_of_interest_with_this_project character varying(4000),
    "Created_By" character varying(4000),
    "Stream" character varying(4000),
    "Email_Opt_Out" character varying(4000),
    "Delayed_Days" character varying(4000),
    "Project_Developer" character varying(4000),
    "Type_of_Review" character varying(4000),
    "Start_Date_Actual" character varying(4000),
    "OldEndDateEx" character varying(4000),
    "activeStatus" character varying(4000),
    "Modified_Time" character varying(4000),
    "GSID" character varying(4000),
    "IR_Email" character varying(4000),
    "Is_Record_Duplicate" character varying(4000),
    "Round_Status" character varying(4000),
    "LAST_ACTION_TIME" character varying(4000),
    "Tigger_URL_Mail" character varying(4000),
    "COI_Officer_Name" character varying(4000),
    "Accept_assignment_CO" character varying(4000),
    "LAST_ACTION" character varying(4000),
    "User_Type" character varying(4000),
    "Notification_TACStart" character varying(4000),
    "PlndEndDTQA" character varying(4000),
    "Reason_of_Delay" character varying(4000),
    "Certification_Officer" character varying(4000),
    "End_Date_Planned_PP_Mandatory" character varying(4000),
    "Currency" character varying(4000),
    "User_Modified_Time" character varying(4000),
    "Delayed" character varying(4000),
    "Unsubscribed_Mode" character varying(4000),
    "End_Date_Planned_PP" character varying(4000),
    "PPDelay" character varying(4000),
    "Send_Date_to_ROE_allocation" character varying(4000),
    "Start_Date_Actual_PP" character varying(4000),
    "LAST_SENT_TIME" character varying(4000),
    "COI_Expert_Mail" character varying(4000),
    "Notification_PPStart" character varying(4000),
    "Total_Hours" character varying(4000),
    "Date_response_received" character varying(4000),
    "End_Date_Actual_PP" character varying(4000),
    "System_Related_Activity_Time" character varying(4000),
    "Record_Image" character varying(4000),
    "End_Date_Act_TAC" character varying(4000)
)
SERVER scdatacopy
OPTIONS (
    schema_name 'dbo',
    table_name 'vwCertificationFeedbacks'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Total_Cost" OPTIONS (
    column_name 'Total_Cost'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "PlndStartDTQA" OPTIONS (
    column_name 'PlndStartDTQA'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Secondary_Email" OPTIONS (
    column_name 'Secondary_Email'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "CO_From_Review" OPTIONS (
    column_name 'CO_From_Review'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "File_Upload_URL_External" OPTIONS (
    column_name 'File_Upload_URL_External'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Modified_By" OPTIONS (
    column_name 'Modified_By'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Email" OPTIONS (
    column_name 'Email'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "IR_Name" OPTIONS (
    column_name 'IR_Name'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Accept_assignment_IR" OPTIONS (
    column_name 'Accept_assignment_IR'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Feedback_Round" OPTIONS (
    column_name 'Feedback_Round'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Issuance_Number" OPTIONS (
    column_name 'Issuance_Number'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Start_Date_Planned" OPTIONS (
    column_name 'Start_Date_Planned'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Notes1" OPTIONS (
    column_name 'Notes1'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "ProgramType" OPTIONS (
    column_name 'ProgramType'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Project_Name" OPTIONS (
    column_name 'Project_Name'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "GS_Notes" OPTIONS (
    column_name 'GS_Notes'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Feedback_Uploaded_to_SC_app" OPTIONS (
    column_name 'Feedback_Uploaded_to_SC_app'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "End_Date_Planned_GS" OPTIONS (
    column_name 'End_Date_Planned_GS'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Considered_for_action" OPTIONS (
    column_name 'Considered_for_action'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "IR_Date_CO" OPTIONS (
    column_name 'IR_Date_CO'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "End_Date_Planned_IR" OPTIONS (
    column_name 'End_Date_Planned_IR'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Independent_Reviewer" OPTIONS (
    column_name 'Independent_Reviewer'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Name" OPTIONS (
    column_name 'Name'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "CO_Email" OPTIONS (
    column_name 'CO_Email'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "GS_Comments" OPTIONS (
    column_name 'GS_Comments'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "COI_Date_CO" OPTIONS (
    column_name 'COI_Date_CO'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Conflict_Y_N_IR" OPTIONS (
    column_name 'Conflict_Y_N_IR'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Conflict_Y_N_CO" OPTIONS (
    column_name 'Conflict_Y_N_CO'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Locked__s" OPTIONS (
    column_name 'Locked__s'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "MP_End" OPTIONS (
    column_name 'MP_End'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Working_Hour" OPTIONS (
    column_name 'Working_Hour'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Exchange_Rate" OPTIONS (
    column_name 'Exchange_Rate'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN realocation OPTIONS (
    column_name 'realocation'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Project_Status" OPTIONS (
    column_name 'Project_Status'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "OldEndDatePP" OPTIONS (
    column_name 'OldEndDatePP'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "GS_TAC_Comments" OPTIONS (
    column_name 'GS_TAC_Comments'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "IR_Officer_Email" OPTIONS (
    column_name 'IR_Officer_Email'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "TestCO" OPTIONS (
    column_name 'TestCO'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "OldEndDateGS" OPTIONS (
    column_name 'OldEndDateGS'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "User_Related_Activity_Time" OPTIONS (
    column_name 'User_Related_Activity_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Work_for_the_project_IR" OPTIONS (
    column_name 'Work_for_the_project_IR'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "File_Upload_URL_internal" OPTIONS (
    column_name 'File_Upload_URL_internal'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Owner" OPTIONS (
    column_name 'Owner'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "End_Date_Planned" OPTIONS (
    column_name 'End_Date_Planned'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "ActualStartDTQA" OPTIONS (
    column_name 'ActualStartDTQA'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "ActualEndDTQA" OPTIONS (
    column_name 'ActualEndDTQA'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Response_of_the_Expert" OPTIONS (
    column_name 'Response_of_the_Expert'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Mail_send_to_ROE" OPTIONS (
    column_name 'Mail_send_to_ROE'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "TestPD" OPTIONS (
    column_name 'TestPD'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "NotificationToQA" OPTIONS (
    column_name 'NotificationToQA'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Project_Type" OPTIONS (
    column_name 'Project_Type'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "OldEndDateTAC" OPTIONS (
    column_name 'OldEndDateTAC'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "CR_Message" OPTIONS (
    column_name 'CR_Message'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Created_Time" OPTIONS (
    column_name 'Created_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "End_Date_Actual" OPTIONS (
    column_name 'End_Date_Actual'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Notification_GS" OPTIONS (
    column_name 'Notification_GS'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "End_Date_Actual_GS" OPTIONS (
    column_name 'End_Date_Actual_GS'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "COI_Officer_Email" OPTIONS (
    column_name 'COI_Officer_Email'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Layout" OPTIONS (
    column_name 'Layout'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "End_Date_Plnd_TAC" OPTIONS (
    column_name 'End_Date_Plnd_TAC'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "OldReviewTLRating" OPTIONS (
    column_name 'OldReviewTLRating'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Record_Approval_Status" OPTIONS (
    column_name 'Record_Approval_Status'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Send_notification_to_QA_Expert" OPTIONS (
    column_name 'Send_notification_to_QA_Expert'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "NotificationFeedbackComplete" OPTIONS (
    column_name 'NotificationFeedbackComplete'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Last_Activity_Time" OPTIONS (
    column_name 'Last_Activity_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "COI_Expert_Name" OPTIONS (
    column_name 'COI_Expert_Name'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Review_Type" OPTIONS (
    column_name 'Review_Type'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "TestExpert" OPTIONS (
    column_name 'TestExpert'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Design_Certification_Renewal" OPTIONS (
    column_name 'Design_Certification_Renewal'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Expert" OPTIONS (
    column_name 'Expert'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "PP_Contact" OPTIONS (
    column_name 'PP_Contact'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Tag" OPTIONS (
    column_name 'Tag'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "NotificationPPFeedbackUploaded" OPTIONS (
    column_name 'NotificationPPFeedbackUploaded'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Unsubscribed_Time" OPTIONS (
    column_name 'Unsubscribed_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Remarks" OPTIONS (
    column_name 'Remarks'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "URL_ID" OPTIONS (
    column_name 'URL_ID'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Specify_your_work_for_the_project" OPTIONS (
    column_name 'Specify_your_work_for_the_project'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Accept_assignment_share_review_feedback" OPTIONS (
    column_name 'Accept_assignment_share_review_feedback'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN id OPTIONS (
    column_name 'id'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Project_Scale" OPTIONS (
    column_name 'Project_Scale'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "MP_Start" OPTIONS (
    column_name 'MP_Start'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Combined_Review" OPTIONS (
    column_name 'Combined_Review'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "System_Modified_Time" OPTIONS (
    column_name 'System_Modified_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Technical_Expert" OPTIONS (
    column_name 'Technical_Expert'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Work_for_the_project_CO" OPTIONS (
    column_name 'Work_for_the_project_CO'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Date_of_Acceptance" OPTIONS (
    column_name 'Date_of_Acceptance'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Reviewer_Timeline_Rating" OPTIONS (
    column_name 'Reviewer_Timeline_Rating'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Check_Status" OPTIONS (
    column_name 'Check_Status'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Folder_ID" OPTIONS (
    column_name 'Folder_ID'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN conflict_of_interest_with_this_project OPTIONS (
    column_name 'conflict_of_interest_with_this_project'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Created_By" OPTIONS (
    column_name 'Created_By'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Stream" OPTIONS (
    column_name 'Stream'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Email_Opt_Out" OPTIONS (
    column_name 'Email_Opt_Out'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Delayed_Days" OPTIONS (
    column_name 'Delayed_Days'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Project_Developer" OPTIONS (
    column_name 'Project_Developer'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Type_of_Review" OPTIONS (
    column_name 'Type_of_Review'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Start_Date_Actual" OPTIONS (
    column_name 'Start_Date_Actual'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "OldEndDateEx" OPTIONS (
    column_name 'OldEndDateEx'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "activeStatus" OPTIONS (
    column_name 'activeStatus'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Modified_Time" OPTIONS (
    column_name 'Modified_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "GSID" OPTIONS (
    column_name 'GSID'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "IR_Email" OPTIONS (
    column_name 'IR_Email'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Is_Record_Duplicate" OPTIONS (
    column_name 'Is_Record_Duplicate'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Round_Status" OPTIONS (
    column_name 'Round_Status'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "LAST_ACTION_TIME" OPTIONS (
    column_name 'LAST_ACTION_TIME'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Tigger_URL_Mail" OPTIONS (
    column_name 'Tigger_URL_Mail'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "COI_Officer_Name" OPTIONS (
    column_name 'COI_Officer_Name'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Accept_assignment_CO" OPTIONS (
    column_name 'Accept_assignment_CO'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "LAST_ACTION" OPTIONS (
    column_name 'LAST_ACTION'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "User_Type" OPTIONS (
    column_name 'User_Type'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Notification_TACStart" OPTIONS (
    column_name 'Notification_TACStart'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "PlndEndDTQA" OPTIONS (
    column_name 'PlndEndDTQA'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Reason_of_Delay" OPTIONS (
    column_name 'Reason_of_Delay'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Certification_Officer" OPTIONS (
    column_name 'Certification_Officer'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "End_Date_Planned_PP_Mandatory" OPTIONS (
    column_name 'End_Date_Planned_PP_Mandatory'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Currency" OPTIONS (
    column_name 'Currency'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "User_Modified_Time" OPTIONS (
    column_name 'User_Modified_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Delayed" OPTIONS (
    column_name 'Delayed'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Unsubscribed_Mode" OPTIONS (
    column_name 'Unsubscribed_Mode'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "End_Date_Planned_PP" OPTIONS (
    column_name 'End_Date_Planned_PP'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "PPDelay" OPTIONS (
    column_name 'PPDelay'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Send_Date_to_ROE_allocation" OPTIONS (
    column_name 'Send_Date_to_ROE_allocation'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Start_Date_Actual_PP" OPTIONS (
    column_name 'Start_Date_Actual_PP'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "LAST_SENT_TIME" OPTIONS (
    column_name 'LAST_SENT_TIME'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "COI_Expert_Mail" OPTIONS (
    column_name 'COI_Expert_Mail'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Notification_PPStart" OPTIONS (
    column_name 'Notification_PPStart'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Total_Hours" OPTIONS (
    column_name 'Total_Hours'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Date_response_received" OPTIONS (
    column_name 'Date_response_received'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "End_Date_Actual_PP" OPTIONS (
    column_name 'End_Date_Actual_PP'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "System_Related_Activity_Time" OPTIONS (
    column_name 'System_Related_Activity_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "Record_Image" OPTIONS (
    column_name 'Record_Image'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" ALTER COLUMN "End_Date_Act_TAC" OPTIONS (
    column_name 'End_Date_Act_TAC'
);


ALTER FOREIGN TABLE scdatacopy."vwCertificationFeedbacks" OWNER TO postgres;

--
-- Name: vwCertificationProject; Type: FOREIGN TABLE; Schema: scdatacopy; Owner: postgres
--

CREATE FOREIGN TABLE scdatacopy."vwCertificationProject" (
    "Missing_Documents" character varying(4000),
    publicdocs character varying(4000),
    "GSVersion" character varying(4000),
    "Transition_Pathway" character varying(4000),
    "Project_Developer" character varying(4000),
    "System_Modified_Time" character varying(4000),
    "Active" character varying(4000),
    "CR_3_End_Date" character varying(4000),
    "Documents_in_Public_View" character varying(4000),
    "Is_Record_Duplicate" character varying(4000),
    "Program_Type" character varying(4000),
    "GSID" character varying(4000),
    "Modified_Time" character varying(4000),
    "Unsubscribed_Mode" character varying(4000),
    "Design_Certification_Date" character varying(4000),
    "Owner" character varying(4000),
    "Project_Start_Date" character varying(4000),
    "System_Related_Activity_Time" character varying(4000),
    "CR_1_End_Date" character varying(4000),
    "Email" character varying(4000),
    "CR_Start_date_Other_schemes" character varying(4000),
    "SC_CreatedDate" character varying(4000),
    "Estimated_Annual_Units" character varying(4000),
    "Project_Scale" character varying(4000),
    "Secondary_Email" character varying(4000),
    "Record_Approval_Status" character varying(4000),
    "Created_Time" character varying(4000),
    "LAST_SENT_TIME" character varying(4000),
    id character varying(4000),
    "Last_Activity_Time" character varying(4000),
    "Email_ROE" character varying(4000),
    "VPA_Bundle_ID" character varying(4000),
    "Email_PD" character varying(4000),
    "Layout" character varying(4000),
    "Methodologies" character varying(4000),
    "Project_Type" character varying(4000),
    "CR_End_date_Other_schemes" character varying(4000),
    "Project_Information_in_Public_View" character varying(4000),
    "CR_2_Start_Date" character varying(4000),
    "LAST_ACTION_TIME" character varying(4000),
    "Additional_Notes" character varying(4000),
    "WorkDrive_Link" character varying(4000),
    "User_Modified_Time" character varying(4000),
    "Incorrect_Details" character varying(4000),
    "User_Related_Activity_Time" character varying(4000),
    "Fee_Schedule" character varying(4000),
    "Modified_By" character varying(4000),
    "Transition_Date" character varying(4000),
    "Country" character varying(4000),
    "Email_IR" character varying(4000),
    "Status" character varying(4000),
    "CR_3_Start_Date" character varying(4000),
    "Project_link_in_other_scheme" character varying(4000),
    "Created_By" character varying(4000),
    "Locked__s" character varying(4000),
    "SDGImpactText" character varying(4000),
    "Email_Opt_Out" character varying(4000),
    "Name" character varying(4000),
    "VPA_Bundle_Name" character varying(4000),
    "Unsubscribed_Time" character varying(4000),
    "Currency" character varying(4000),
    "Transition_from_other_scheme" character varying(4000),
    "LAST_ACTION" character varying(4000),
    "Stream" character varying(4000),
    "Record_Image" character varying(4000),
    "Email_CO" character varying(4000),
    "Workdrive_ID" character varying(4000),
    "Sample_Date" character varying(4000),
    "Registration_Type" character varying(4000),
    "Transition_approval_procedure" character varying(4000),
    "CR_1_Start_Date" character varying(4000),
    "CR_2_End_Date" character varying(4000),
    "Exchange_Rate" character varying(4000),
    "Internal_Verification" character varying(4000),
    "Tag" character varying(4000),
    "Selected_for_Sampling_Y_N" character varying(4000),
    "SDG_Impacts" character varying(4000)
)
SERVER scdatacopy
OPTIONS (
    schema_name 'dbo',
    table_name 'vwCertificationProject'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Missing_Documents" OPTIONS (
    column_name 'Missing_Documents'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN publicdocs OPTIONS (
    column_name 'publicdocs'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "GSVersion" OPTIONS (
    column_name 'GSVersion'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Transition_Pathway" OPTIONS (
    column_name 'Transition_Pathway'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Project_Developer" OPTIONS (
    column_name 'Project_Developer'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "System_Modified_Time" OPTIONS (
    column_name 'System_Modified_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Active" OPTIONS (
    column_name 'Active'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "CR_3_End_Date" OPTIONS (
    column_name 'CR_3_End_Date'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Documents_in_Public_View" OPTIONS (
    column_name 'Documents_in_Public_View'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Is_Record_Duplicate" OPTIONS (
    column_name 'Is_Record_Duplicate'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Program_Type" OPTIONS (
    column_name 'Program_Type'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "GSID" OPTIONS (
    column_name 'GSID'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Modified_Time" OPTIONS (
    column_name 'Modified_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Unsubscribed_Mode" OPTIONS (
    column_name 'Unsubscribed_Mode'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Design_Certification_Date" OPTIONS (
    column_name 'Design_Certification_Date'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Owner" OPTIONS (
    column_name 'Owner'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Project_Start_Date" OPTIONS (
    column_name 'Project_Start_Date'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "System_Related_Activity_Time" OPTIONS (
    column_name 'System_Related_Activity_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "CR_1_End_Date" OPTIONS (
    column_name 'CR_1_End_Date'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Email" OPTIONS (
    column_name 'Email'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "CR_Start_date_Other_schemes" OPTIONS (
    column_name 'CR_Start_date_Other_schemes'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "SC_CreatedDate" OPTIONS (
    column_name 'SC_CreatedDate'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Estimated_Annual_Units" OPTIONS (
    column_name 'Estimated_Annual_Units'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Project_Scale" OPTIONS (
    column_name 'Project_Scale'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Secondary_Email" OPTIONS (
    column_name 'Secondary_Email'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Record_Approval_Status" OPTIONS (
    column_name 'Record_Approval_Status'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Created_Time" OPTIONS (
    column_name 'Created_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "LAST_SENT_TIME" OPTIONS (
    column_name 'LAST_SENT_TIME'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN id OPTIONS (
    column_name 'id'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Last_Activity_Time" OPTIONS (
    column_name 'Last_Activity_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Email_ROE" OPTIONS (
    column_name 'Email_ROE'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "VPA_Bundle_ID" OPTIONS (
    column_name 'VPA_Bundle_ID'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Email_PD" OPTIONS (
    column_name 'Email_PD'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Layout" OPTIONS (
    column_name 'Layout'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Methodologies" OPTIONS (
    column_name 'Methodologies'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Project_Type" OPTIONS (
    column_name 'Project_Type'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "CR_End_date_Other_schemes" OPTIONS (
    column_name 'CR_End_date_Other_schemes'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Project_Information_in_Public_View" OPTIONS (
    column_name 'Project_Information_in_Public_View'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "CR_2_Start_Date" OPTIONS (
    column_name 'CR_2_Start_Date'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "LAST_ACTION_TIME" OPTIONS (
    column_name 'LAST_ACTION_TIME'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Additional_Notes" OPTIONS (
    column_name 'Additional_Notes'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "WorkDrive_Link" OPTIONS (
    column_name 'WorkDrive_Link'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "User_Modified_Time" OPTIONS (
    column_name 'User_Modified_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Incorrect_Details" OPTIONS (
    column_name 'Incorrect_Details'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "User_Related_Activity_Time" OPTIONS (
    column_name 'User_Related_Activity_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Fee_Schedule" OPTIONS (
    column_name 'Fee_Schedule'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Modified_By" OPTIONS (
    column_name 'Modified_By'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Transition_Date" OPTIONS (
    column_name 'Transition_Date'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Country" OPTIONS (
    column_name 'Country'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Email_IR" OPTIONS (
    column_name 'Email_IR'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Status" OPTIONS (
    column_name 'Status'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "CR_3_Start_Date" OPTIONS (
    column_name 'CR_3_Start_Date'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Project_link_in_other_scheme" OPTIONS (
    column_name 'Project_link_in_other_scheme'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Created_By" OPTIONS (
    column_name 'Created_By'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Locked__s" OPTIONS (
    column_name 'Locked__s'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "SDGImpactText" OPTIONS (
    column_name 'SDGImpactText'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Email_Opt_Out" OPTIONS (
    column_name 'Email_Opt_Out'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Name" OPTIONS (
    column_name 'Name'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "VPA_Bundle_Name" OPTIONS (
    column_name 'VPA_Bundle_Name'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Unsubscribed_Time" OPTIONS (
    column_name 'Unsubscribed_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Currency" OPTIONS (
    column_name 'Currency'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Transition_from_other_scheme" OPTIONS (
    column_name 'Transition_from_other_scheme'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "LAST_ACTION" OPTIONS (
    column_name 'LAST_ACTION'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Stream" OPTIONS (
    column_name 'Stream'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Record_Image" OPTIONS (
    column_name 'Record_Image'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Email_CO" OPTIONS (
    column_name 'Email_CO'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Workdrive_ID" OPTIONS (
    column_name 'Workdrive_ID'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Sample_Date" OPTIONS (
    column_name 'Sample_Date'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Registration_Type" OPTIONS (
    column_name 'Registration_Type'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Transition_approval_procedure" OPTIONS (
    column_name 'Transition_approval_procedure'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "CR_1_Start_Date" OPTIONS (
    column_name 'CR_1_Start_Date'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "CR_2_End_Date" OPTIONS (
    column_name 'CR_2_End_Date'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Exchange_Rate" OPTIONS (
    column_name 'Exchange_Rate'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Internal_Verification" OPTIONS (
    column_name 'Internal_Verification'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Tag" OPTIONS (
    column_name 'Tag'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "Selected_for_Sampling_Y_N" OPTIONS (
    column_name 'Selected_for_Sampling_Y_N'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" ALTER COLUMN "SDG_Impacts" OPTIONS (
    column_name 'SDG_Impacts'
);


ALTER FOREIGN TABLE scdatacopy."vwCertificationProject" OWNER TO postgres;

--
-- Name: vwCertificationReview; Type: FOREIGN TABLE; Schema: scdatacopy; Owner: postgres
--

CREATE FOREIGN TABLE scdatacopy."vwCertificationReview" (
    "Record_Approval_Status" character varying(4000),
    "Country" character varying(4000),
    "Registration_Type" character varying(4000),
    "Date_Feed_Form_Upload" character varying(4000),
    "OfficerEmaiID" character varying(4000),
    "Certification_Officer_Rating" character varying(4000),
    "reviewNumber" character varying(4000),
    "Date_of_Acceptance" character varying(4000),
    "System_Modified_Time" character varying(4000),
    "I_have_filled_in_VVB_rating_for_review" character varying(4000),
    "Review_conclusion" character varying(4000),
    "Review_Status" character varying(4000),
    "I_have_validated_all_documents_in_SC_app" character varying(4000),
    "Planned_End_DT_Review" character varying(4000),
    "Created_Time" character varying(4000),
    "Total_Timeline_Rating" character varying(4000),
    "ReviewRemarks" character varying(4000),
    "I_confirm_my_availability_and_suitability_for_this" character varying(4000),
    "Combined_Review" character varying(4000),
    "GSID_for_other_VPAs" character varying(4000),
    id character varying(4000),
    "Locked__s" character varying(4000),
    "Project_Name" character varying(4000),
    "Notes_for_Certification_Officers" character varying(4000),
    "IR_Mail" character varying(4000),
    "Record_Image" character varying(4000),
    "Exchange_Rate" character varying(4000),
    "Owner" character varying(4000),
    "Review_Group" character varying(4000),
    "Certification_Officer" character varying(4000),
    "LAST_ACTION_TIME" character varying(4000),
    "ExpertEmailID" character varying(4000),
    "Project_Stream" character varying(4000),
    "VPA_Bundle_ID" character varying(4000),
    "Name" character varying(4000),
    "User_Related_Activity_Time" character varying(4000),
    "Created_On" character varying(4000),
    "Project_Developer" character varying(4000),
    "Expedition_fee" character varying(4000),
    "Risk_Analysis" character varying(4000),
    "Platform_review_id" character varying(4000),
    "Feedback_Rating" character varying(4000),
    "Email_Opt_Out" character varying(4000),
    "Version" character varying(4000),
    "External_Expert" character varying(4000),
    "Actual_Start_Date" character varying(4000),
    "Specify_your_work_for_the_project" character varying(4000),
    "Tag" character varying(4000),
    "VVB_Score" character varying(4000),
    "Created_By" character varying(4000),
    "Project_Scale" character varying(4000),
    "Independent_Reviewer" character varying(4000),
    "Overall_performance_6" character varying(4000),
    "VVB_Organization" character varying(4000),
    "Expert_QA" character varying(4000),
    "Currency" character varying(4000),
    "Layout" character varying(4000),
    "Validated_By" character varying(4000),
    "Notes1" character varying(4000),
    "Planned_Start_DT_Review" character varying(4000),
    "Unsubscribed_Time" character varying(4000),
    "Total_Feedback_Rounds" character varying(4000),
    certification_review_unique_id character varying(4000),
    "Additional_Round" character varying(4000),
    "Reason_for_reopening_the_review" character varying(4000),
    "Review_Type" character varying(4000),
    "Delayed_Days" character varying(4000),
    "ScoreVVB" character varying(4000),
    "Non_Availability_Reason" character varying(4000),
    "Unsubscribed_Mode" character varying(4000),
    "PP_Mail" character varying(4000),
    "Is_Record_Duplicate" character varying(4000),
    "Reviewer_Name" character varying(4000),
    "ROE_Assigned" character varying(4000),
    "GSID" character varying(4000),
    "Modified_By" character varying(4000),
    "Modified_Time" character varying(4000),
    "Type_of_Review" character varying(4000),
    "Declaration_Feedback" character varying(4000),
    "ProgramType" character varying(4000),
    "Project_Status" character varying(4000),
    publicdocs character varying(4000),
    "LAST_ACTION" character varying(4000),
    "Email" character varying(4000),
    "VolumeofCredit" character varying(4000),
    "notificationobjectobserverYES" character varying(4000),
    "System_Related_Activity_Time" character varying(4000),
    "Actual_End_Date" character varying(4000),
    "DateSyncwithSC" character varying(4000),
    "Average_Reviewer_Rating" character varying(4000),
    "Secondary_Email" character varying(4000),
    "User_Modified_Time" character varying(4000),
    "Last_Activity_Time" character varying(4000),
    "Delayed" character varying(4000),
    "Master_VPA_to_others" character varying(4000),
    "LAST_SENT_TIME" character varying(4000),
    "syncwithSCapp" character varying(4000),
    "RevNamewithYearMonth" character varying(4000),
    "Project_Type" character varying(4000),
    "Notes_VVB_Score" character varying(4000),
    conflict_of_interest_with_this_project character varying(4000)
)
SERVER scdatacopy
OPTIONS (
    schema_name 'dbo',
    table_name 'vwCertificationReview'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Record_Approval_Status" OPTIONS (
    column_name 'Record_Approval_Status'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Country" OPTIONS (
    column_name 'Country'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Registration_Type" OPTIONS (
    column_name 'Registration_Type'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Date_Feed_Form_Upload" OPTIONS (
    column_name 'Date_Feed_Form_Upload'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "OfficerEmaiID" OPTIONS (
    column_name 'OfficerEmaiID'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Certification_Officer_Rating" OPTIONS (
    column_name 'Certification_Officer_Rating'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "reviewNumber" OPTIONS (
    column_name 'reviewNumber'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Date_of_Acceptance" OPTIONS (
    column_name 'Date_of_Acceptance'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "System_Modified_Time" OPTIONS (
    column_name 'System_Modified_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "I_have_filled_in_VVB_rating_for_review" OPTIONS (
    column_name 'I_have_filled_in_VVB_rating_for_review'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Review_conclusion" OPTIONS (
    column_name 'Review_conclusion'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Review_Status" OPTIONS (
    column_name 'Review_Status'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "I_have_validated_all_documents_in_SC_app" OPTIONS (
    column_name 'I_have_validated_all_documents_in_SC_app'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Planned_End_DT_Review" OPTIONS (
    column_name 'Planned_End_DT_Review'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Created_Time" OPTIONS (
    column_name 'Created_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Total_Timeline_Rating" OPTIONS (
    column_name 'Total_Timeline_Rating'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "ReviewRemarks" OPTIONS (
    column_name 'ReviewRemarks'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "I_confirm_my_availability_and_suitability_for_this" OPTIONS (
    column_name 'I_confirm_my_availability_and_suitability_for_this'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Combined_Review" OPTIONS (
    column_name 'Combined_Review'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "GSID_for_other_VPAs" OPTIONS (
    column_name 'GSID_for_other_VPAs'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN id OPTIONS (
    column_name 'id'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Locked__s" OPTIONS (
    column_name 'Locked__s'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Project_Name" OPTIONS (
    column_name 'Project_Name'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Notes_for_Certification_Officers" OPTIONS (
    column_name 'Notes_for_Certification_Officers'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "IR_Mail" OPTIONS (
    column_name 'IR_Mail'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Record_Image" OPTIONS (
    column_name 'Record_Image'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Exchange_Rate" OPTIONS (
    column_name 'Exchange_Rate'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Owner" OPTIONS (
    column_name 'Owner'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Review_Group" OPTIONS (
    column_name 'Review_Group'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Certification_Officer" OPTIONS (
    column_name 'Certification_Officer'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "LAST_ACTION_TIME" OPTIONS (
    column_name 'LAST_ACTION_TIME'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "ExpertEmailID" OPTIONS (
    column_name 'ExpertEmailID'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Project_Stream" OPTIONS (
    column_name 'Project_Stream'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "VPA_Bundle_ID" OPTIONS (
    column_name 'VPA_Bundle_ID'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Name" OPTIONS (
    column_name 'Name'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "User_Related_Activity_Time" OPTIONS (
    column_name 'User_Related_Activity_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Created_On" OPTIONS (
    column_name 'Created_On'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Project_Developer" OPTIONS (
    column_name 'Project_Developer'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Expedition_fee" OPTIONS (
    column_name 'Expedition_fee'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Risk_Analysis" OPTIONS (
    column_name 'Risk_Analysis'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Platform_review_id" OPTIONS (
    column_name 'Platform_review_id'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Feedback_Rating" OPTIONS (
    column_name 'Feedback_Rating'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Email_Opt_Out" OPTIONS (
    column_name 'Email_Opt_Out'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Version" OPTIONS (
    column_name 'Version'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "External_Expert" OPTIONS (
    column_name 'External_Expert'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Actual_Start_Date" OPTIONS (
    column_name 'Actual_Start_Date'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Specify_your_work_for_the_project" OPTIONS (
    column_name 'Specify_your_work_for_the_project'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Tag" OPTIONS (
    column_name 'Tag'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "VVB_Score" OPTIONS (
    column_name 'VVB_Score'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Created_By" OPTIONS (
    column_name 'Created_By'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Project_Scale" OPTIONS (
    column_name 'Project_Scale'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Independent_Reviewer" OPTIONS (
    column_name 'Independent_Reviewer'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Overall_performance_6" OPTIONS (
    column_name 'Overall_performance_6'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "VVB_Organization" OPTIONS (
    column_name 'VVB_Organization'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Expert_QA" OPTIONS (
    column_name 'Expert_QA'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Currency" OPTIONS (
    column_name 'Currency'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Layout" OPTIONS (
    column_name 'Layout'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Validated_By" OPTIONS (
    column_name 'Validated_By'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Notes1" OPTIONS (
    column_name 'Notes1'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Planned_Start_DT_Review" OPTIONS (
    column_name 'Planned_Start_DT_Review'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Unsubscribed_Time" OPTIONS (
    column_name 'Unsubscribed_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Total_Feedback_Rounds" OPTIONS (
    column_name 'Total_Feedback_Rounds'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN certification_review_unique_id OPTIONS (
    column_name 'certification_review_unique_id'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Additional_Round" OPTIONS (
    column_name 'Additional_Round'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Reason_for_reopening_the_review" OPTIONS (
    column_name 'Reason_for_reopening_the_review'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Review_Type" OPTIONS (
    column_name 'Review_Type'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Delayed_Days" OPTIONS (
    column_name 'Delayed_Days'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "ScoreVVB" OPTIONS (
    column_name 'ScoreVVB'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Non_Availability_Reason" OPTIONS (
    column_name 'Non_Availability_Reason'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Unsubscribed_Mode" OPTIONS (
    column_name 'Unsubscribed_Mode'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "PP_Mail" OPTIONS (
    column_name 'PP_Mail'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Is_Record_Duplicate" OPTIONS (
    column_name 'Is_Record_Duplicate'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Reviewer_Name" OPTIONS (
    column_name 'Reviewer_Name'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "ROE_Assigned" OPTIONS (
    column_name 'ROE_Assigned'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "GSID" OPTIONS (
    column_name 'GSID'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Modified_By" OPTIONS (
    column_name 'Modified_By'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Modified_Time" OPTIONS (
    column_name 'Modified_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Type_of_Review" OPTIONS (
    column_name 'Type_of_Review'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Declaration_Feedback" OPTIONS (
    column_name 'Declaration_Feedback'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "ProgramType" OPTIONS (
    column_name 'ProgramType'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Project_Status" OPTIONS (
    column_name 'Project_Status'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN publicdocs OPTIONS (
    column_name 'publicdocs'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "LAST_ACTION" OPTIONS (
    column_name 'LAST_ACTION'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Email" OPTIONS (
    column_name 'Email'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "VolumeofCredit" OPTIONS (
    column_name 'VolumeofCredit'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "notificationobjectobserverYES" OPTIONS (
    column_name 'notificationobjectobserverYES'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "System_Related_Activity_Time" OPTIONS (
    column_name 'System_Related_Activity_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Actual_End_Date" OPTIONS (
    column_name 'Actual_End_Date'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "DateSyncwithSC" OPTIONS (
    column_name 'DateSyncwithSC'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Average_Reviewer_Rating" OPTIONS (
    column_name 'Average_Reviewer_Rating'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Secondary_Email" OPTIONS (
    column_name 'Secondary_Email'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "User_Modified_Time" OPTIONS (
    column_name 'User_Modified_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Last_Activity_Time" OPTIONS (
    column_name 'Last_Activity_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Delayed" OPTIONS (
    column_name 'Delayed'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Master_VPA_to_others" OPTIONS (
    column_name 'Master_VPA_to_others'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "LAST_SENT_TIME" OPTIONS (
    column_name 'LAST_SENT_TIME'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "syncwithSCapp" OPTIONS (
    column_name 'syncwithSCapp'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "RevNamewithYearMonth" OPTIONS (
    column_name 'RevNamewithYearMonth'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Project_Type" OPTIONS (
    column_name 'Project_Type'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN "Notes_VVB_Score" OPTIONS (
    column_name 'Notes_VVB_Score'
);
ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" ALTER COLUMN conflict_of_interest_with_this_project OPTIONS (
    column_name 'conflict_of_interest_with_this_project'
);


ALTER FOREIGN TABLE scdatacopy."vwCertificationReview" OWNER TO postgres;

--
-- Name: vwVVBReview; Type: FOREIGN TABLE; Schema: scdatacopy; Owner: postgres
--

CREATE FOREIGN TABLE scdatacopy."vwVVBReview" (
    "Currency" character varying(4000),
    "CPF_Email" character varying(4000),
    "GSID" character varying(4000),
    "Invoice_Paid" character varying(4000),
    "Service_Request" character varying(4000),
    "CPF_approved" character varying(4000),
    "City_Postcode" character varying(4000),
    "Starting_Date_of_the_project_activity" character varying(4000),
    "Potential_assessment_start" character varying(4000),
    "Validator_Verifier_Name" character varying(4000),
    "Crediting_Period_Number" character varying(4000),
    "If_Verification_which_Monitoring_Period1" character varying(4000),
    "Final_decision_performed" character varying(4000),
    "Review_Stage" character varying(4000),
    "Boundary_Host_Country_ies" character varying(4000),
    "Visit_Planned" character varying(4000),
    "CPF_Visitor_Remarks" character varying(4000),
    "LAST_ACTION" character varying(4000),
    "Modified_By" character varying(4000),
    "CPF_Assignment" character varying(4000),
    "Draft_report_planned" character varying(4000),
    "Site_visit_performed" character varying(4000),
    "External_request_received_date" character varying(4000),
    "Legal_Form" character varying(4000),
    "Closure_reason" character varying(4000),
    "Unsubscribed_Time" character varying(4000),
    "Exchange_Rate" character varying(4000),
    "VPA_Bundle_Project_Titles" character varying(4000),
    "Final_decision_planned" character varying(4000),
    id character varying(4000),
    "LAST_SENT_TIME" character varying(4000),
    "MSA_Response" character varying(4000),
    "External_request_received" character varying(4000),
    "Project_Developer" character varying(4000),
    "Type_of_site_visit" character varying(4000),
    "CPF_Dat_of_previous_visit" character varying(4000),
    "Assessment_DR_performed" character varying(4000),
    "Potential_submission_to_Final_decision" character varying(4000),
    "Street_No" character varying(4000),
    "Service_Date" character varying(4000),
    "Owner" character varying(4000),
    "Surname_Given_Name" character varying(4000),
    "Record_Approval_Status" character varying(4000),
    "CPF_Date_of_Approval" character varying(4000),
    "Site_visit_planned" character varying(4000),
    "E_Mail" character varying(4000),
    "Platform_VVB_Service_id" character varying(4000),
    "SOW_Authorized_Representative" character varying(4000),
    "Reason_s_for_change" character varying(4000),
    "VVB_Service_unique_id" character varying(4000),
    "CPF_SC_service_details" character varying(4000),
    "Service_Number" character varying(4000),
    "Validating_VVB" character varying(4000),
    "Location_of_each_individual_project" character varying(4000),
    "Scheme" character varying(4000),
    "Date_of_Submission" character varying(4000),
    "Layout" character varying(4000),
    "Draft_report_to_client_performed" character varying(4000),
    "Draft_report_to_client_planned" character varying(4000),
    "CPF_Visitor_Name" character varying(4000),
    "Single_project1" character varying(4000),
    "DR_from_client_planned" character varying(4000),
    "Modified_Time" character varying(4000),
    "Country_Province" character varying(4000),
    "Created_Time" character varying(4000),
    "Additional_Tool_s_used" character varying(4000),
    "Previous_Periodic_Verification_done_by_which_VVB" character varying(4000),
    "Client_Company_name" character varying(4000),
    "Project_Language_is_English" character varying(4000),
    "Review_Type" character varying(4000),
    "CPF_Project_Type_Others" character varying(4000),
    "Is_Record_Duplicate" character varying(4000),
    "Service_Booked_on" character varying(4000),
    "MSA_Exists" character varying(4000),
    "Scheme_Service" character varying(4000),
    "Which_Periodic_Verification_2nd_3rd_4th" character varying(4000),
    "Physical_site_visit" character varying(4000),
    "All_VPA_Projects" character varying(4000),
    "Invoice_status" character varying(4000),
    "System_Modified_Time" character varying(4000),
    "Visit_Performed" character varying(4000),
    "VPA_Bundle_Name" character varying(4000),
    "DR_from_client_performed" character varying(4000),
    "WorkDrive_Link" character varying(4000),
    "Desk_review_performed" character varying(4000),
    "CPF_Approver" character varying(4000),
    "Team_change_after_project_initiation" character varying(4000),
    "Team_Leader_Name" character varying(4000),
    "CPF_PDD_MR_Other_Consultant" character varying(4000),
    "CPF_Project_Titles" character varying(4000),
    "Assignment_Type" character varying(4000),
    "Documents_Uploaded" character varying(4000),
    "Created_By" character varying(4000),
    "Cancelled_by" character varying(4000),
    "CPF_Project_Type" character varying(4000),
    "User_Modified_Time" character varying(4000),
    "POA1" character varying(4000),
    "SOW_Date" character varying(4000),
    "Record_Image" character varying(4000),
    "BookingNumonPlatform1" character varying(4000),
    "Team_Nomination_Completed" character varying(4000),
    "Error_Message" character varying(4000),
    "Last_Activity_Time" character varying(4000),
    "Host_Country_Expert_Name" character varying(4000),
    "Date_of_Cancellation" character varying(4000),
    "CPE_Received" character varying(4000),
    "Tag" character varying(4000),
    "BookingNumonPlatform" character varying(4000),
    "Review_Number" character varying(4000),
    "Secondary_Email" character varying(4000),
    "Certification_Project" character varying(4000),
    "TR_of_Final_report_performed" character varying(4000),
    "Street_Number" character varying(4000),
    "Project_Name" character varying(4000),
    "PDD_MR_Other_Prepared_By" character varying(4000),
    "Accounts_Country" character varying(4000),
    "Project_Dev_Contact" character varying(4000),
    "POA_ID_VVB" character varying(4000),
    "Email_Opt_Out" character varying(4000),
    "Status" character varying(4000),
    "Technical_Expert_Name" character varying(4000),
    "Locked__s" character varying(4000),
    "System_Related_Activity_Time" character varying(4000),
    "Assessment_DR_planned" character varying(4000),
    "COI_Free" character varying(4000),
    "Email" character varying(4000),
    "Site_visit_days" character varying(4000),
    "RequestedService" character varying(4000),
    "SOW_Received" character varying(4000),
    "Sectoral_Scope" character varying(4000),
    "TR_of_Final_report_planned" character varying(4000),
    "Add_any_additional_relevant_information" character varying(4000),
    "CPF_Visit_By" character varying(4000),
    "Country" character varying(4000),
    "Number_of_individual_project_s" character varying(4000),
    "Name" character varying(4000),
    "Short_description_of_the_proposed_project_activity" character varying(4000),
    "Request_for_reg_issue_date" character varying(4000),
    "Specify" character varying(4000),
    "Name_any_other_documents_uploaded" character varying(4000),
    "External_request_resolved" character varying(4000),
    "TR_of_draft_report_planned" character varying(4000),
    "ID_Reference_Number1" character varying(4000),
    "Postcode_City" character varying(4000),
    "Material_is_defined_as_5_of_overall_emissions" character varying(4000),
    "VVB_Service_signed_document_URL" character varying(4000),
    "User_Related_Activity_Time" character varying(4000),
    "Draft_report_performed" character varying(4000),
    "Conflict_of_Interest" character varying(4000),
    "Phone" character varying(4000),
    "Travel_time_between_sites" character varying(4000),
    "Methodology_ies_Version" character varying(4000),
    "LAST_ACTION_TIME" character varying(4000),
    "Team_Leader" character varying(4000),
    "Business_Expenses" character varying(4000),
    "Scale" character varying(4000),
    "External_request_resolved_date" character varying(4000),
    "TR_of_draft_report_performed" character varying(4000),
    "Possible_date_of_document_submission" character varying(4000),
    "CPF_Previous_visit" character varying(4000),
    "Number_of_production_lines_at_each_project_site" character varying(4000),
    "Desk_review_planned" character varying(4000),
    "Other_Project_IDs" character varying(4000),
    "Team_COI_Free" character varying(4000),
    "Trainee_Name" character varying(4000),
    "CPF_Multiple_IDs" character varying(4000),
    "Unsubscribed_Mode" character varying(4000)
)
SERVER scdatacopy
OPTIONS (
    schema_name 'dbo',
    table_name 'vwVVBReview'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Currency" OPTIONS (
    column_name 'Currency'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "CPF_Email" OPTIONS (
    column_name 'CPF_Email'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "GSID" OPTIONS (
    column_name 'GSID'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Invoice_Paid" OPTIONS (
    column_name 'Invoice_Paid'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Service_Request" OPTIONS (
    column_name 'Service_Request'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "CPF_approved" OPTIONS (
    column_name 'CPF_approved'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "City_Postcode" OPTIONS (
    column_name 'City_Postcode'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Starting_Date_of_the_project_activity" OPTIONS (
    column_name 'Starting_Date_of_the_project_activity'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Potential_assessment_start" OPTIONS (
    column_name 'Potential_assessment_start'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Validator_Verifier_Name" OPTIONS (
    column_name 'Validator_Verifier_Name'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Crediting_Period_Number" OPTIONS (
    column_name 'Crediting_Period_Number'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "If_Verification_which_Monitoring_Period1" OPTIONS (
    column_name 'If_Verification_which_Monitoring_Period1'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Final_decision_performed" OPTIONS (
    column_name 'Final_decision_performed'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Review_Stage" OPTIONS (
    column_name 'Review_Stage'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Boundary_Host_Country_ies" OPTIONS (
    column_name 'Boundary_Host_Country_ies'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Visit_Planned" OPTIONS (
    column_name 'Visit_Planned'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "CPF_Visitor_Remarks" OPTIONS (
    column_name 'CPF_Visitor_Remarks'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "LAST_ACTION" OPTIONS (
    column_name 'LAST_ACTION'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Modified_By" OPTIONS (
    column_name 'Modified_By'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "CPF_Assignment" OPTIONS (
    column_name 'CPF_Assignment'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Draft_report_planned" OPTIONS (
    column_name 'Draft_report_planned'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Site_visit_performed" OPTIONS (
    column_name 'Site_visit_performed'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "External_request_received_date" OPTIONS (
    column_name 'External_request_received_date'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Legal_Form" OPTIONS (
    column_name 'Legal_Form'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Closure_reason" OPTIONS (
    column_name 'Closure_reason'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Unsubscribed_Time" OPTIONS (
    column_name 'Unsubscribed_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Exchange_Rate" OPTIONS (
    column_name 'Exchange_Rate'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "VPA_Bundle_Project_Titles" OPTIONS (
    column_name 'VPA_Bundle_Project_Titles'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Final_decision_planned" OPTIONS (
    column_name 'Final_decision_planned'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN id OPTIONS (
    column_name 'id'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "LAST_SENT_TIME" OPTIONS (
    column_name 'LAST_SENT_TIME'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "MSA_Response" OPTIONS (
    column_name 'MSA_Response'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "External_request_received" OPTIONS (
    column_name 'External_request_received'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Project_Developer" OPTIONS (
    column_name 'Project_Developer'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Type_of_site_visit" OPTIONS (
    column_name 'Type_of_site_visit'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "CPF_Dat_of_previous_visit" OPTIONS (
    column_name 'CPF_Dat_of_previous_visit'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Assessment_DR_performed" OPTIONS (
    column_name 'Assessment_DR_performed'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Potential_submission_to_Final_decision" OPTIONS (
    column_name 'Potential_submission_to_Final_decision'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Street_No" OPTIONS (
    column_name 'Street_No'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Service_Date" OPTIONS (
    column_name 'Service_Date'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Owner" OPTIONS (
    column_name 'Owner'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Surname_Given_Name" OPTIONS (
    column_name 'Surname_Given_Name'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Record_Approval_Status" OPTIONS (
    column_name 'Record_Approval_Status'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "CPF_Date_of_Approval" OPTIONS (
    column_name 'CPF_Date_of_Approval'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Site_visit_planned" OPTIONS (
    column_name 'Site_visit_planned'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "E_Mail" OPTIONS (
    column_name 'E_Mail'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Platform_VVB_Service_id" OPTIONS (
    column_name 'Platform_VVB_Service_id'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "SOW_Authorized_Representative" OPTIONS (
    column_name 'SOW_Authorized_Representative'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Reason_s_for_change" OPTIONS (
    column_name 'Reason_s_for_change'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "VVB_Service_unique_id" OPTIONS (
    column_name 'VVB_Service_unique_id'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "CPF_SC_service_details" OPTIONS (
    column_name 'CPF_SC_service_details'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Service_Number" OPTIONS (
    column_name 'Service_Number'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Validating_VVB" OPTIONS (
    column_name 'Validating_VVB'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Location_of_each_individual_project" OPTIONS (
    column_name 'Location_of_each_individual_project'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Scheme" OPTIONS (
    column_name 'Scheme'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Date_of_Submission" OPTIONS (
    column_name 'Date_of_Submission'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Layout" OPTIONS (
    column_name 'Layout'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Draft_report_to_client_performed" OPTIONS (
    column_name 'Draft_report_to_client_performed'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Draft_report_to_client_planned" OPTIONS (
    column_name 'Draft_report_to_client_planned'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "CPF_Visitor_Name" OPTIONS (
    column_name 'CPF_Visitor_Name'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Single_project1" OPTIONS (
    column_name 'Single_project1'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "DR_from_client_planned" OPTIONS (
    column_name 'DR_from_client_planned'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Modified_Time" OPTIONS (
    column_name 'Modified_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Country_Province" OPTIONS (
    column_name 'Country_Province'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Created_Time" OPTIONS (
    column_name 'Created_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Additional_Tool_s_used" OPTIONS (
    column_name 'Additional_Tool_s_used'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Previous_Periodic_Verification_done_by_which_VVB" OPTIONS (
    column_name 'Previous_Periodic_Verification_done_by_which_VVB'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Client_Company_name" OPTIONS (
    column_name 'Client_Company_name'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Project_Language_is_English" OPTIONS (
    column_name 'Project_Language_is_English'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Review_Type" OPTIONS (
    column_name 'Review_Type'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "CPF_Project_Type_Others" OPTIONS (
    column_name 'CPF_Project_Type_Others'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Is_Record_Duplicate" OPTIONS (
    column_name 'Is_Record_Duplicate'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Service_Booked_on" OPTIONS (
    column_name 'Service_Booked_on'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "MSA_Exists" OPTIONS (
    column_name 'MSA_Exists'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Scheme_Service" OPTIONS (
    column_name 'Scheme_Service'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Which_Periodic_Verification_2nd_3rd_4th" OPTIONS (
    column_name 'Which_Periodic_Verification_2nd_3rd_4th'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Physical_site_visit" OPTIONS (
    column_name 'Physical_site_visit'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "All_VPA_Projects" OPTIONS (
    column_name 'All_VPA_Projects'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Invoice_status" OPTIONS (
    column_name 'Invoice_status'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "System_Modified_Time" OPTIONS (
    column_name 'System_Modified_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Visit_Performed" OPTIONS (
    column_name 'Visit_Performed'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "VPA_Bundle_Name" OPTIONS (
    column_name 'VPA_Bundle_Name'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "DR_from_client_performed" OPTIONS (
    column_name 'DR_from_client_performed'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "WorkDrive_Link" OPTIONS (
    column_name 'WorkDrive_Link'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Desk_review_performed" OPTIONS (
    column_name 'Desk_review_performed'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "CPF_Approver" OPTIONS (
    column_name 'CPF_Approver'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Team_change_after_project_initiation" OPTIONS (
    column_name 'Team_change_after_project_initiation'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Team_Leader_Name" OPTIONS (
    column_name 'Team_Leader_Name'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "CPF_PDD_MR_Other_Consultant" OPTIONS (
    column_name 'CPF_PDD_MR_Other_Consultant'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "CPF_Project_Titles" OPTIONS (
    column_name 'CPF_Project_Titles'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Assignment_Type" OPTIONS (
    column_name 'Assignment_Type'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Documents_Uploaded" OPTIONS (
    column_name 'Documents_Uploaded'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Created_By" OPTIONS (
    column_name 'Created_By'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Cancelled_by" OPTIONS (
    column_name 'Cancelled_by'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "CPF_Project_Type" OPTIONS (
    column_name 'CPF_Project_Type'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "User_Modified_Time" OPTIONS (
    column_name 'User_Modified_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "POA1" OPTIONS (
    column_name 'POA1'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "SOW_Date" OPTIONS (
    column_name 'SOW_Date'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Record_Image" OPTIONS (
    column_name 'Record_Image'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "BookingNumonPlatform1" OPTIONS (
    column_name 'BookingNumonPlatform1'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Team_Nomination_Completed" OPTIONS (
    column_name 'Team_Nomination_Completed'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Error_Message" OPTIONS (
    column_name 'Error_Message'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Last_Activity_Time" OPTIONS (
    column_name 'Last_Activity_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Host_Country_Expert_Name" OPTIONS (
    column_name 'Host_Country_Expert_Name'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Date_of_Cancellation" OPTIONS (
    column_name 'Date_of_Cancellation'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "CPE_Received" OPTIONS (
    column_name 'CPE_Received'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Tag" OPTIONS (
    column_name 'Tag'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "BookingNumonPlatform" OPTIONS (
    column_name 'BookingNumonPlatform'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Review_Number" OPTIONS (
    column_name 'Review_Number'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Secondary_Email" OPTIONS (
    column_name 'Secondary_Email'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Certification_Project" OPTIONS (
    column_name 'Certification_Project'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "TR_of_Final_report_performed" OPTIONS (
    column_name 'TR_of_Final_report_performed'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Street_Number" OPTIONS (
    column_name 'Street_Number'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Project_Name" OPTIONS (
    column_name 'Project_Name'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "PDD_MR_Other_Prepared_By" OPTIONS (
    column_name 'PDD_MR_Other_Prepared_By'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Accounts_Country" OPTIONS (
    column_name 'Accounts_Country'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Project_Dev_Contact" OPTIONS (
    column_name 'Project_Dev_Contact'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "POA_ID_VVB" OPTIONS (
    column_name 'POA_ID_VVB'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Email_Opt_Out" OPTIONS (
    column_name 'Email_Opt_Out'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Status" OPTIONS (
    column_name 'Status'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Technical_Expert_Name" OPTIONS (
    column_name 'Technical_Expert_Name'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Locked__s" OPTIONS (
    column_name 'Locked__s'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "System_Related_Activity_Time" OPTIONS (
    column_name 'System_Related_Activity_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Assessment_DR_planned" OPTIONS (
    column_name 'Assessment_DR_planned'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "COI_Free" OPTIONS (
    column_name 'COI_Free'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Email" OPTIONS (
    column_name 'Email'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Site_visit_days" OPTIONS (
    column_name 'Site_visit_days'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "RequestedService" OPTIONS (
    column_name 'RequestedService'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "SOW_Received" OPTIONS (
    column_name 'SOW_Received'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Sectoral_Scope" OPTIONS (
    column_name 'Sectoral_Scope'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "TR_of_Final_report_planned" OPTIONS (
    column_name 'TR_of_Final_report_planned'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Add_any_additional_relevant_information" OPTIONS (
    column_name 'Add_any_additional_relevant_information'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "CPF_Visit_By" OPTIONS (
    column_name 'CPF_Visit_By'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Country" OPTIONS (
    column_name 'Country'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Number_of_individual_project_s" OPTIONS (
    column_name 'Number_of_individual_project_s'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Name" OPTIONS (
    column_name 'Name'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Short_description_of_the_proposed_project_activity" OPTIONS (
    column_name 'Short_description_of_the_proposed_project_activity'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Request_for_reg_issue_date" OPTIONS (
    column_name 'Request_for_reg_issue_date'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Specify" OPTIONS (
    column_name 'Specify'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Name_any_other_documents_uploaded" OPTIONS (
    column_name 'Name_any_other_documents_uploaded'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "External_request_resolved" OPTIONS (
    column_name 'External_request_resolved'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "TR_of_draft_report_planned" OPTIONS (
    column_name 'TR_of_draft_report_planned'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "ID_Reference_Number1" OPTIONS (
    column_name 'ID_Reference_Number1'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Postcode_City" OPTIONS (
    column_name 'Postcode_City'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Material_is_defined_as_5_of_overall_emissions" OPTIONS (
    column_name 'Material_is_defined_as_5_of_overall_emissions'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "VVB_Service_signed_document_URL" OPTIONS (
    column_name 'VVB_Service_signed_document_URL'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "User_Related_Activity_Time" OPTIONS (
    column_name 'User_Related_Activity_Time'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Draft_report_performed" OPTIONS (
    column_name 'Draft_report_performed'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Conflict_of_Interest" OPTIONS (
    column_name 'Conflict_of_Interest'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Phone" OPTIONS (
    column_name 'Phone'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Travel_time_between_sites" OPTIONS (
    column_name 'Travel_time_between_sites'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Methodology_ies_Version" OPTIONS (
    column_name 'Methodology_ies_Version'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "LAST_ACTION_TIME" OPTIONS (
    column_name 'LAST_ACTION_TIME'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Team_Leader" OPTIONS (
    column_name 'Team_Leader'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Business_Expenses" OPTIONS (
    column_name 'Business_Expenses'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Scale" OPTIONS (
    column_name 'Scale'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "External_request_resolved_date" OPTIONS (
    column_name 'External_request_resolved_date'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "TR_of_draft_report_performed" OPTIONS (
    column_name 'TR_of_draft_report_performed'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Possible_date_of_document_submission" OPTIONS (
    column_name 'Possible_date_of_document_submission'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "CPF_Previous_visit" OPTIONS (
    column_name 'CPF_Previous_visit'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Number_of_production_lines_at_each_project_site" OPTIONS (
    column_name 'Number_of_production_lines_at_each_project_site'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Desk_review_planned" OPTIONS (
    column_name 'Desk_review_planned'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Other_Project_IDs" OPTIONS (
    column_name 'Other_Project_IDs'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Team_COI_Free" OPTIONS (
    column_name 'Team_COI_Free'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Trainee_Name" OPTIONS (
    column_name 'Trainee_Name'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "CPF_Multiple_IDs" OPTIONS (
    column_name 'CPF_Multiple_IDs'
);
ALTER FOREIGN TABLE scdatacopy."vwVVBReview" ALTER COLUMN "Unsubscribed_Mode" OPTIONS (
    column_name 'Unsubscribed_Mode'
);


ALTER FOREIGN TABLE scdatacopy."vwVVBReview" OWNER TO postgres;

--
-- PostgreSQL database dump complete
--