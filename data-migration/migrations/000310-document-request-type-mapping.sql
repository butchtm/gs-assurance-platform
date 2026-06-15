CREATE TABLE gs.document_category (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.document_category (id, code, description)
VALUES
    (0,'PD_DOCUMENTS','Project Developer Documents'),
    (1,'SUPPORT_DOCUMENTS','Support Documents'),
    (2,'VVB_DOCUMENTS','VVB Documents'),
    (3,'REVIEW_DOCUMENTS','Review Documents');

CREATE TABLE pg_temp.confluence_request_type_mapping (
  confluence_request_type varchar NOT NULL,
  request_type_id int4 NOT NULL
);

INSERT INTO pg_temp.confluence_request_type_mapping (
    confluence_request_type,
    request_type_id
) VALUES
 ('General',0)
,('Preliminary Review',1)
,('General',2)
,('Design Certification Project',3)
,('General',4)
,('Performance Certification | Project',5)
,('General',6)
,('Design Change | With VVB',7)
,('General',8)
,('Design Certification Renewal | Project',9)
,('General',10)
,('General',11)
,('General',12)
,('Preliminary Review',13)
,('General',14)
,('Preliminary Review',15)
,('Preliminary Review',16)
;

CREATE TABLE pg_temp.confluence_document_type_mapping (
  confluence_document_type varchar NOT NULL,
  document_type_id int4 NOT NULL
);

INSERT INTO pg_temp.confluence_document_type_mapping (
    confluence_document_type,
    document_type_id
) VALUES
 ('CDM PDD',0)
,('GS Project Design Document',1)
,('Cover letter',2)
,('Terms and Conditions',3)
,('ODA Declaration',4)
,('ER calculation',5)
,('CDM Validation Report',6)
,('Validation report',7)
,('CDM Verificaiton Report',9)
,('Monitoring Report',10)
,('Verification Report',11)
,('Monitoring Report',12)
,('PoA Design Consultation Report',13)
,('Validation report',15)
,('Emission Reduction estimation',16)
,('Cover letter',17)
,('Transition Request Form',19)
,('LSC invitation letter',20)
,('LSC Meeting Attendance List',21)
,('EIA/ESIA report',22)
,('Stakeholder Evaluation Forms',23)
,('Stakeholder Consultation Report',24)
,('Baseline survey report',25)
,('Kitchen Surveys',26)
,('EIA/ESIA report',29)
,('SDG tool',47)
,('Project annual report form',48)
,('Preliminary Review request Form',49)
;

CREATE TABLE pg_temp.document_type_category (
    document_type varchar,
    document_category varchar,
    PRIMARY KEY (document_type)
);

INSERT INTO pg_temp.document_type_category (
    document_type,
    document_category
) VALUES
 ('GS Project Design Document','PD_DOCUMENTS')
,('Emission Reduction estimation','PD_DOCUMENTS')
,('Stakeholder Consultation Report','PD_DOCUMENTS')
,('Preliminary Review request Form','PD_DOCUMENTS')
,('Monitoring Report','PD_DOCUMENTS')
,('ER calculation','PD_DOCUMENTS')
,('Project annual report form','PD_DOCUMENTS')
,('Cover letter','PD_DOCUMENTS')
,('ODA Declaration','PD_DOCUMENTS')
,('Terms and Conditions','PD_DOCUMENTS')
,('PoA-DD','SUPPORT_DOCUMENTS')
,('VPA-DD','SUPPORT_DOCUMENTS')
,('PoA Design Consultation Report','SUPPORT_DOCUMENTS')
,('SDG tool','SUPPORT_DOCUMENTS')
,('Transition Request Form','SUPPORT_DOCUMENTS')
,('CDM PDD','SUPPORT_DOCUMENTS')
,('CDM Validation Report','SUPPORT_DOCUMENTS')
,('CDM Verificaiton Report','SUPPORT_DOCUMENTS')
,('LSC invitation letter','SUPPORT_DOCUMENTS')
,('LSC Meeting Attendance List','SUPPORT_DOCUMENTS')
,('EIA/ESIA report','SUPPORT_DOCUMENTS')
,('Stakeholder Evaluation Forms','SUPPORT_DOCUMENTS')
,('Baseline survey report','SUPPORT_DOCUMENTS')
,('Kitchen Surveys','SUPPORT_DOCUMENTS')
,('Safeguarding principles','SUPPORT_DOCUMENTS')
,('Validation report','VVB_DOCUMENTS')
,('Verification Report','VVB_DOCUMENTS')
,('Review feedback form','REVIEW_DOCUMENTS')
;

CREATE TABLE pg_temp.request_type_document_type (
    request_type varchar,
    document_type varchar,
    mandatory bool,
    PRIMARY KEY (request_type, document_type)
);

INSERT INTO pg_temp.request_type_document_type (
    request_type,
    document_type,
    mandatory
) VALUES
 ('Preliminary Review','Stakeholder Consultation Report',true)
,('Preliminary Review','Preliminary Review request Form',true)
,('Preliminary Review','Cover letter',true)
,('Preliminary Review','ODA Declaration',true)
,('Preliminary Review','Terms and Conditions',true)
,('Preliminary Review','Transition Request Form',true)
,('Preliminary Review','CDM PDD',true)
,('Preliminary Review','LSC invitation letter',true)
,('Preliminary Review','LSC Meeting Attendance List',true)
,('Preliminary Review','EIA/ESIA report',true)
,('Preliminary Review','Stakeholder Evaluation Forms',true)
,('Preliminary Review','Review feedback form',true)
,('Design Certification Project','GS Project Design Document',true)
,('Design Certification Project','Emission Reduction estimation',true)
,('Design Certification Project','Stakeholder Consultation Report',true)
,('Design Certification Project','SDG tool',true)
,('Design Certification Project','CDM PDD',true)
,('Design Certification Project','CDM Validation Report',true)
,('Design Certification Project','Baseline survey report',true)
,('Design Certification Project','Kitchen Surveys',true)
,('Design Certification Project','Safeguarding principles',true)
,('Design Certification Project','Validation report',true)
,('Design Certification Project','Review feedback form',true)
,('Design Certification PoA with Real Case VPA','Emission Reduction estimation',true)
,('Design Certification PoA with Real Case VPA','Stakeholder Consultation Report',true)
,('Design Certification PoA with Real Case VPA','PoA-DD',true)
,('Design Certification PoA with Real Case VPA','VPA-DD',true)
,('Design Certification PoA with Real Case VPA','PoA Design Consultation Report',true)
,('Design Certification PoA with Real Case VPA','SDG tool',true)
,('Design Certification PoA with Real Case VPA','CDM PDD',true)
,('Design Certification PoA with Real Case VPA','CDM Validation Report',true)
,('Design Certification PoA with Real Case VPA','Baseline survey report',true)
,('Design Certification PoA with Real Case VPA','Kitchen Surveys',true)
,('Design Certification PoA with Real Case VPA','Safeguarding principles',true)
,('Design Certification PoA with Real Case VPA','Validation report',true)
,('Design Certification PoA with Real Case VPA','Review feedback form',true)
,('Inclusion Real Case VPA','Emission Reduction estimation',true)
,('Inclusion Real Case VPA','Stakeholder Consultation Report',true)
,('Inclusion Real Case VPA','VPA-DD',true)
,('Inclusion Real Case VPA','Baseline survey report',true)
,('Inclusion Real Case VPA','Kitchen Surveys',true)
,('Inclusion Real Case VPA','Safeguarding principles',true)
,('Inclusion Real Case VPA','Validation report',true)
,('Inclusion Real Case VPA','Review feedback form',true)
,('Inclusion regular VPA','Emission Reduction estimation',true)
,('Inclusion regular VPA','Stakeholder Consultation Report',true)
,('Inclusion regular VPA','VPA-DD',true)
,('Inclusion regular VPA','Baseline survey report',true)
,('Inclusion regular VPA','Kitchen Surveys',true)
,('Inclusion regular VPA','Safeguarding principles',true)
,('Inclusion regular VPA','Review feedback form',true)
,('Performance Certification | Project','Monitoring Report',true)
,('Performance Certification | Project','ER calculation',true)
,('Performance Certification | Project','SDG tool',true)
,('Performance Certification | Project','CDM Verificaiton Report',true)
,('Performance Certification | Project','Kitchen Surveys',true)
,('Performance Certification | Project','Verification Report',true)
,('Performance Certification | Project','Review feedback form',true)
,('Performance Certification | VPA','Monitoring Report',true)
,('Performance Certification | VPA','ER calculation',true)
,('Performance Certification | VPA','SDG tool',true)
,('Performance Certification | VPA','CDM Verificaiton Report',true)
,('Performance Certification | VPA','Kitchen Surveys',true)
,('Performance Certification | VPA','Verification Report',true)
,('Performance Certification | VPA','Review feedback form',true)
,('Design Certification Renewal | Project','GS Project Design Document',true)
,('Design Certification Renewal | Project','Emission Reduction estimation',true)
,('Design Certification Renewal | Project','Baseline survey report',true)
,('Design Certification Renewal | Project','Kitchen Surveys',true)
,('Design Certification Renewal | Project','Safeguarding principles',true)
,('Design Certification Renewal | Project','Validation report',true)
,('Design Certification Renewal | Project','Review feedback form',true)
,('Design Certification Renewal | PoA with Real Case VPA','Emission Reduction estimation',true)
,('Design Certification Renewal | PoA with Real Case VPA','PoA-DD',true)
,('Design Certification Renewal | PoA with Real Case VPA','VPA-DD',true)
,('Design Certification Renewal | PoA with Real Case VPA','Baseline survey report',true)
,('Design Certification Renewal | PoA with Real Case VPA','Kitchen Surveys',true)
,('Design Certification Renewal | PoA with Real Case VPA','Safeguarding principles',true)
,('Design Certification Renewal | PoA with Real Case VPA','Validation report',true)
,('Design Certification Renewal | PoA with Real Case VPA','Review feedback form',true)
,('Renewal | Real Case VPA','Emission Reduction estimation',true)
,('Renewal | Real Case VPA','VPA-DD',true)
,('Renewal | Real Case VPA','Baseline survey report',true)
,('Renewal | Real Case VPA','Kitchen Surveys',true)
,('Renewal | Real Case VPA','Safeguarding principles',true)
,('Renewal | Real Case VPA','Validation report',true)
,('Renewal | Real Case VPA','Review feedback form',true)
,('Renewal | Regular VPA','Emission Reduction estimation',true)
,('Renewal | Regular VPA','VPA-DD',true)
,('Renewal | Regular VPA','Baseline survey report',true)
,('Renewal | Regular VPA','Kitchen Surveys',true)
,('Renewal | Regular VPA','Safeguarding principles',true)
,('Renewal | Regular VPA','Review feedback form',true)
,('Design Change | With VVB','GS Project Design Document',true)
,('Design Change | With VVB','Validation report',true)
,('Design Change | With VVB','Review feedback form',true)
,('Design Change | Without VVB','GS Project Design Document',true)
,('Design Change | Without VVB','Review feedback form',true)
,('Issuance','Review feedback form',true)
,('General','Review feedback form',true)
,('Annual Report','Project annual report form',true)
,('Inclusion regular VPA','Validation report',false)
,('Renewal | Regular VPA','Validation report',false)
;

-- association of document types with categories
-- we could extend the document_type ref table but that would break the generic enum type in the backend
CREATE TABLE gs.document_type_category (
    document_type_id int4 NOT NULL,
    document_category_id int4 NOT NULL,
    PRIMARY KEY (document_type_id)
);

ALTER TABLE gs.document_type_category
ADD FOREIGN KEY (document_type_id) REFERENCES gs.document_type ON DELETE RESTRICT;

ALTER TABLE gs.document_type_category
ADD FOREIGN KEY (document_category_id) REFERENCES gs.document_category ON DELETE RESTRICT;

INSERT INTO gs.document_type_category (document_type_id, document_category_id)
SELECT
  dtm.document_type_id,
  dc.id
FROM
  pg_temp.document_type_category dtc
INNER JOIN
  pg_temp.confluence_document_type_mapping dtm
ON
  dtc.document_type = dtm.confluence_document_type
LEFT JOIN
  gs.document_category dc
ON
  dtc.document_category = dc.code;

CREATE TABLE gs.request_type_document_type (
    request_type_id int4 NOT NULL,
    document_type_id int4 NOT NULL,
    mandatory bool NOT NULL,
    PRIMARY KEY (request_type_id, document_type_id)
);

ALTER TABLE gs.request_type_document_type
ADD FOREIGN KEY (request_type_id) REFERENCES gs.request_type ON DELETE RESTRICT;

ALTER TABLE gs.request_type_document_type
ADD FOREIGN KEY (document_type_id) REFERENCES gs.document_type ON DELETE RESTRICT;

INSERT INTO gs.request_type_document_type (request_type_id, document_type_id, mandatory)
SELECT
  rtm.request_type_id,
  dtm.document_type_id,
  rd.mandatory
FROM
  pg_temp.request_type_document_type rd
INNER JOIN
  pg_temp.confluence_request_type_mapping rtm
ON
  rd.request_type = rtm.confluence_request_type
INNER JOIN
  pg_temp.confluence_document_type_mapping dtm
ON
  rd.document_type = dtm.confluence_document_type;
