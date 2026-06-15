DROP SCHEMA IF EXISTS staging CASCADE;
CREATE SCHEMA staging;

CREATE TABLE staging."BaseProjectDocument"
AS SELECT * FROM scdatacopy."BaseProjectDocument";

CREATE TABLE staging."BaseProjectReview"
AS SELECT * FROM scdatacopy."BaseProjectReview";

CREATE TABLE staging."Bundles"
AS SELECT * FROM scdatacopy."Bundles";

CREATE TABLE staging."DocumentReview"
AS SELECT * FROM scdatacopy."DocumentReview";

CREATE TABLE staging."Documents"
AS SELECT * FROM scdatacopy."Documents";

CREATE TABLE staging."EnergyProjectsDetails"
AS SELECT * FROM scdatacopy."EnergyProjectsDetails";

CREATE TABLE staging."Invoices"
AS SELECT * FROM scdatacopy."Invoices";

CREATE TABLE staging."LUFProjectsDetails"
AS SELECT * FROM scdatacopy."LUFProjectsDetails";

CREATE TABLE staging."ProjectStateChangeLog"
AS SELECT * FROM scdatacopy."ProjectStateChangeLog";

CREATE TABLE staging."Projects"
AS SELECT * FROM scdatacopy."Projects";

CREATE TABLE staging."ReviewStateChangeLog"
AS SELECT * FROM scdatacopy."ReviewStateChangeLog";

CREATE TABLE staging."Reviews"
AS SELECT * FROM scdatacopy."Reviews";

CREATE TABLE staging."vwAccounts"
AS SELECT * FROM scdatacopy."vwAccounts";

CREATE TABLE staging."vwCertificationFeedbacks"
AS SELECT * FROM scdatacopy."vwCertificationFeedbacks";

CREATE TABLE staging."vwCertificationProject"
AS SELECT * FROM scdatacopy."vwCertificationProject";

CREATE TABLE staging."vwCertificationReview"
AS SELECT * FROM scdatacopy."vwCertificationReview";

CREATE TABLE staging."vwVVBReview"
AS SELECT * FROM scdatacopy."vwVVBReview";