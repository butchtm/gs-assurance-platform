-- Filter out Projects missing requests
CREATE TABLE gs_dq.projects_missing_requests
AS
SELECT
  p.*
FROM
  staging."Projects" p
LEFT JOIN
  staging."Reviews" r
ON
  p."CurrentReviewID" = r."ID"
WHERE
  r."ID" IS NULL
;

-- Filter out Projects related to Projects missing requests
-- Caveats:
--   1) projects may appear here and in gs_dq.projects_missing_requests
--   2) single level check only
CREATE TABLE gs_dq.projects_missing_referenced_project
AS
SELECT
  p.*
FROM
  staging."Projects" p
INNER JOIN
  gs_dq.projects_missing_requests rp
ON
  p."MasterProjectID" = rp."ID"
UNION
SELECT
  p.*
FROM
  staging."Projects" p
INNER JOIN
  gs_dq.projects_missing_requests rp
ON
  p."ParentProjectID" = rp."ID"
;

-- Filter out LUF Projects which relate to Projects with DQ issues
CREATE TABLE gs_dq.luf_projects_with_dqi_project
AS
SELECT
  lp.*
FROM
  staging."LUFProjectsDetails" lp
WHERE
  "ID" IN (SELECT "ID" FROM gs_dq.projects_missing_requests) OR
  "ID" IN (SELECT "ID" FROM gs_dq.projects_missing_referenced_project);

-- Filter out Energy Projects which relate to Projects with DQ issues
CREATE TABLE gs_dq.energy_projects_with_dqi_project
AS
SELECT
  lp.*
FROM
  staging."EnergyProjectsDetails" lp
WHERE
  "ID" IN (SELECT "ID" FROM gs_dq.projects_missing_requests) OR
  "ID" IN (SELECT "ID" FROM gs_dq.projects_missing_referenced_project);

INSERT INTO gs.project
(	id,
	gsid,
	status,
	name,
	description,
	programme_of_activities_id,
	project_start_date,
	project_category_id,
	registration_type_id,
	has_project_assets,
	current_standard_version_id,
	carbon_stream_id,
	project_developer_name,
	project_developer_email,
	project_size_id,
	current_request_id,
	project_status_id,
	bundle_id,
	design_certification_date,
	design_certification_date_manual_set,
	fee_schedule_id,
	estimated_annual_units,
	master_project_id,
	city_town_community,
	region_state_province,
	street_and_number,
	listing_date,
	listing_date_manual_set,
	parent_project_id,
	migrated
)
SELECT 
	"ID",
	"GSID",
	CASE WHEN "Status" = 0 THEN false
	     WHEN "Status" = 1 THEN true
	END,
	"Name",
	"Description",
	"ProgrammeOfActivities",
	"ProjectStartDate",
	"ProjectCategory",
	"RegistrationType",
	CASE WHEN "HasProjectAssets" = 0 THEN false
	     WHEN "HasProjectAssets" = 1 THEN true
	END,
	"CurrentStandardVersion",
	"CarbonStream",
	"ProjectDeveloperName",
	"ProjectDeveloperEmail",
	"ProjectSize",
	"CurrentReviewID",
	"ProjectState_ProjectState",
	"BundleID",
	"DesignCertificationDate",
	CASE WHEN "DesignCertificationDateManualSet" = 0 THEN false
	     WHEN "DesignCertificationDateManualSet" = 1 THEN true
	END,
	"FeeSchedule",
	"EstimatedAnnualUnits",
	"MasterProjectID",
	"CityTownCommunity",
	"RegionStateProvince",
	"StreetAndNumber",
	"ListingDate",
	CASE WHEN "ListingDateManualSet" = 0 THEN false
	     WHEN "ListingDateManualSet" = 1 THEN true
	END,
	"ParentProjectID",
	true
FROM
  staging."Projects"
WHERE
  "ID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_requests) AND
  "ID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_referenced_project);

INSERT INTO
  gs.project_project_location
(
	project_id,
	project_location_id
)
SELECT
  "ID",
  CAST(STRING_TO_TABLE("ProjectLocation", ';') AS int4)
FROM
  staging."Projects"
WHERE
  "ID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_requests) AND
  "ID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_referenced_project);


INSERT INTO
  gs.project_project_asset
(
	project_id,
	project_asset_id
)
SELECT
  "ID",
  CAST(STRING_TO_TABLE("ProjectAssets", ';') AS int4)
FROM
  staging."Projects"
WHERE
  "ID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_requests) AND
  "ID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_referenced_project);


INSERT INTO
  gs.project_project_type
(
	project_id,
	project_type_id
)
SELECT
  "ID",
  CAST(STRING_TO_TABLE("ProjectTypes", ';') AS int4)
FROM
  staging."Projects"
WHERE
  "ID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_requests) AND
  "ID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_referenced_project);


INSERT INTO
  gs.project_methodology
(
	project_id,
	methodology_id
)
SELECT
  "ID",
  CAST(STRING_TO_TABLE("Methodologies", ';') AS int4)
FROM
  staging."Projects"
WHERE
  "ID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_requests) AND
  "ID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_referenced_project);


-- project 1661 has duplicate sdgs (12;12;13) so we dedupe
INSERT INTO
  gs.project_sustainable_development_goal
(
	project_id,
	sustainable_development_goal_id
)
SELECT DISTINCT
  "ID",
  CAST(STRING_TO_TABLE("SDGs", ';') AS int4)
FROM
  staging."Projects"
WHERE
  "ID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_requests) AND
  "ID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_referenced_project);


INSERT INTO
  gs.project_crediting_period
(
	project_id,
	period_id,
	start_date,
	end_date
)
SELECT
  "ID",
  1,
  "CreditingPeriod1StartDate",
  "CreditingPeriod1EndDate"
FROM
  staging."Projects"
WHERE
  "ID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_requests) AND
  "ID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_referenced_project) AND
  ("CreditingPeriod1StartDate" IS NOT NULL OR
   "CreditingPeriod1EndDate" IS NOT NULL)
UNION ALL
SELECT
  "ID",
  2,
  "CreditingPeriod2StartDate",
  "CreditingPeriod2EndDate"
FROM
  staging."Projects"
WHERE
  "ID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_requests) AND
  "ID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_referenced_project) AND
  ("CreditingPeriod2StartDate" IS NOT NULL OR
   "CreditingPeriod2EndDate" IS NOT NULL)
UNION ALL
SELECT
  "ID",
  3,
  "CreditingPeriod3StartDate",
  "CreditingPeriod3EndDate"
FROM
  staging."Projects"
WHERE
  "ID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_requests) AND
  "ID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_referenced_project) AND
  ("CreditingPeriod3StartDate" IS NOT NULL OR
   "CreditingPeriod3EndDate" IS NOT NULL);


INSERT INTO gs.luf_project
(
	project_id,
	ar_soil_carbon,
	eligible_area_ha,
	small_holder,
	total_area_ha,
	last_activity,
	notes,
	certified_design_date,
	certified_project_date,
	last_design_certification_renewal_date,
	last_performance_review_date
)
SELECT
	"ID",
	CASE WHEN "ARSoilCarbon" = 0 THEN false
	     WHEN "ARSoilCarbon" = 1 THEN true
	END,
	"EligibleAreaHa" ,
	CASE WHEN "SmallHolder" = 0 THEN false
	     WHEN "SmallHolder" = 1 THEN true
	END,
	"TotalAreaHa",
	"LastActivity",
	"Notes",
	"CertifiedDesignDate",
	"CertifiedProjectDate",
	"LastDesignCertificationRenewalDate",
	"LastPerformanceReviewDate"
FROM
  staging."LUFProjectsDetails"
WHERE
  "ID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_requests) AND
  "ID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_referenced_project);


INSERT INTO gs.energy_project
(
	project_id,
	project_link_in_other_scheme,
	crediting_period_start_date_other_schemes,
	crediting_period_end_date_other_schemes,
	transition_approval_procedure,
	transition_from_other_scheme,
	transition_pathway

)
SELECT
	"ID",
	"ProjectLinkInOtherScheme",
	"CreditingPeriodStartDateOtherSchemes",
	"CreditingPeriodEndDateOtherSchemes",
	"TransitionApprovalProcedure",
	"TransitionFromOtherScheme",
	"TransitionPathway"
FROM
  staging."EnergyProjectsDetails"
WHERE
  "ID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_requests) AND
  "ID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_referenced_project);
