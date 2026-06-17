DROP FUNCTION IF EXISTS pg_temp.is_upper;
CREATE FUNCTION
  pg_temp.is_upper(character_to_test character)
RETURNS
  boolean
LANGUAGE plpgsql
AS '
DECLARE
BEGIN
  RETURN character_to_test != lower(character_to_test);
END;'
;

-- Generate a random permutation of 1-based indices into a n-element array
-- Uses Fisher-Yates shuffle - see https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle
DROP FUNCTION IF EXISTS pg_temp.random_permute;
CREATE FUNCTION
  pg_temp.random_permute(n integer)
RETURNS
  integer[]
LANGUAGE plpgsql
AS ' 
DECLARE
  indices integer[];
  i integer;
  j integer;
  temp integer;
BEGIN
  FOR i IN 1..n LOOP
    indices[i] = i;
  END LOOP;
  FOR i IN REVERSE n..2 LOOP
    j := CAST(round(1 + random() * (i - 1)) AS integer);
    IF i != j THEN
      temp := indices[i];
      indices[i] := indices[j];
      indices[j] := temp;
    END IF;
  END LOOP;
  RETURN indices;
END;'
;

-- Randomly permute given string, optionally preserving the relative casing of the text
DROP FUNCTION IF EXISTS pg_temp.permute_string;
CREATE FUNCTION 
  pg_temp.permute_string(string_to_permute text, preserve_case boolean DEFAULT false)
RETURNS
  text
LANGUAGE plpgsql
AS ' 
DECLARE
  string_length integer = length(string_to_permute);
  permutation_indices integer[] = pg_temp.random_permute(string_length);
  i integer;
  replacement text;
  current text;
  permuted_string text = '''';
BEGIN
  FOR i IN 1..string_length LOOP
    replacement := substr(string_to_permute, permutation_indices[i], 1);
    IF preserve_case THEN
      current = substr(string_to_permute, i, 1);
      IF pg_temp.is_upper(current) THEN
        replacement := upper(replacement);
      ELSE
        replacement := lower(replacement);
      END IF;
    END IF;
    permuted_string := permuted_string || replacement;
  END LOOP;
  RETURN permuted_string;
END;'
;

-- Randomly permute each given word, optionally preserving the relative casing of the text
DROP FUNCTION IF EXISTS pg_temp.permute_words;
CREATE FUNCTION 
  pg_temp.permute_words(string_to_permute text, preserve_case boolean DEFAULT false)
RETURNS
  text
LANGUAGE plpgsql
AS ' 
DECLARE
  words text[] = string_to_array(string_to_permute, '' '');
  number_of_words integer = cardinality(words);
  i integer;
  permuted_words text[];
BEGIN
  FOR i in 1..number_of_words LOOP
    permuted_words[i] := pg_temp.permute_string(words[i], preserve_case);
  END LOOP;
  RETURN array_to_string(permuted_words, '' '');
END;'
;

-- Randomly permute elements of domain bar tld
DROP FUNCTION IF EXISTS pg_temp.permute_domain;
CREATE FUNCTION 
  pg_temp.permute_domain(domain_to_permute text, preserve_case boolean DEFAULT false)
RETURNS
  text
LANGUAGE plpgsql
AS ' 
DECLARE
  words text[] = string_to_array(domain_to_permute, ''.'');
  number_of_elements integer = cardinality(words);
  i integer;
  permuted_words text[];
BEGIN
  FOR i in 1..(number_of_elements - 1) LOOP
    permuted_words[i] := pg_temp.permute_string(words[i], preserve_case);
  END LOOP;
  RETURN array_to_string(permuted_words, ''.'');
END;'
;

CREATE TABLE pg_temp.string_cache(
  original text NOT NULL,
  sanitised text NOT NULL
);

CREATE UNIQUE INDEX ON pg_temp.string_cache (md5(original));

-- Cached permute_words
DROP FUNCTION IF EXISTS pg_temp.permute_words_cached;
CREATE FUNCTION 
  pg_temp.permute_words_cached(string_to_permute text, preserve_case boolean DEFAULT false)
RETURNS
  text
LANGUAGE plpgsql
AS ' 
DECLARE
  permuted_string text;
BEGIN
  SELECT 
    sanitised
  INTO
    permuted_string
  FROM
    pg_temp.string_cache
  WHERE
    original = string_to_permute;
  IF permuted_string IS NULL THEN
    permuted_string := pg_temp.permute_words(string_to_permute, preserve_case);
    INSERT INTO pg_temp.string_cache (
      original,
      sanitised
    ) VALUES (
      string_to_permute,
      permuted_string
    );
  END IF;
  RETURN permuted_string;
END;'
;

CREATE TABLE pg_temp.email_address_cache(
  original text NOT NULL,
  sanitised text NOT NULL,
  PRIMARY KEY (original)
);

-- Cached sanitise email
DROP FUNCTION IF EXISTS pg_temp.sanitise_email_cached;
CREATE FUNCTION
  pg_temp.sanitise_email_cached(email_address text)
RETURNS
  text
LANGUAGE plpgsql
AS ' 
DECLARE
  email_parts text[];
  sanitised_email text;
  sanitised_domain text = ''z'';
BEGIN
  SELECT 
    sanitised
  INTO
    sanitised_email
  FROM
    pg_temp.email_address_cache
  WHERE
    original = email_address;
  IF sanitised_email IS NULL THEN
    email_parts = string_to_array(email_address, ''@'');
    IF cardinality(email_parts) > 1 THEN
      sanitised_domain := pg_temp.permute_domain(email_parts[2]);
    END IF;
    sanitised_email := format(''%s@%s.example.com'', pg_temp.permute_string(email_parts[1]), sanitised_domain);
    INSERT INTO pg_temp.email_address_cache (
      original,
      sanitised
    ) VALUES (
      email_address,
      sanitised_email
    );
  END IF;
  return sanitised_email;
END;'
;

--
-- Sanitise Invoices.CustomerEmail
--
UPDATE staging."Invoices"
SET
  "CustomerEmail" = pg_temp.sanitise_email_cached("CustomerEmail")
WHERE
  "CustomerEmail" IS NOT NULL;

--
-- Sanitise Projects.ProjectDeveloperName
--
UPDATE staging."Projects"
SET
  "ProjectDeveloperName" = pg_temp.permute_words_cached("ProjectDeveloperName", true)
WHERE
  "ProjectDeveloperName" IS NOT NULL;

--
-- Sanitise Projects.ProjectDeveloperEmail
--
UPDATE staging."Projects"
SET
  "ProjectDeveloperEmail" = pg_temp.sanitise_email_cached("ProjectDeveloperEmail")
WHERE
  "ProjectDeveloperEmail" IS NOT NULL;

--
-- Sanitise vwAccounts.Account_Name
--
UPDATE staging."vwAccounts"
SET
  "Account_Name" = pg_temp.permute_words_cached("Account_Name", true)
WHERE
  "Account_Name" IS NOT NULL;

--
-- Sanitise vwAccounts.Address_1
--
UPDATE staging."vwAccounts"
SET
  "Address_1" = pg_temp.permute_words_cached("Address_1", true)
WHERE
  "Address_1" IS NOT NULL;

--
-- Sanitise vwAccounts.Postal_Code
--
UPDATE staging."vwAccounts"
SET
  "Postal_Code" = pg_temp.permute_words_cached("Postal_Code", true)
WHERE
  "Postal_Code" IS NOT NULL;

--
-- Sanitise vwCertificationFeedbacks.Secondary_Email
--
UPDATE staging."vwCertificationFeedbacks"
SET
  "Secondary_Email" = pg_temp.sanitise_email_cached("Secondary_Email")
WHERE
  "Secondary_Email" IS NOT NULL;

--
-- Sanitise vwCertificationFeedbacks.Email
--
UPDATE staging."vwCertificationFeedbacks"
SET
  "Email" = pg_temp.sanitise_email_cached("Email")
WHERE
  "Email" IS NOT NULL;

--
-- Sanitise vwCertificationFeedbacks.Project_Developer
--
UPDATE staging."vwCertificationFeedbacks"
SET
  "Project_Developer" = pg_temp.sanitise_email_cached("Project_Developer")
WHERE
  "Project_Developer" IS NOT NULL;

--
-- Sanitise vwCertificationFeedbacks.CO_Email
--
UPDATE staging."vwCertificationFeedbacks"
SET
  "CO_Email" = pg_temp.sanitise_email_cached("CO_Email")
WHERE
  "CO_Email" IS NOT NULL;

--
-- Sanitise vwCertificationFeedbacks.IR_Officer_Email
--
UPDATE staging."vwCertificationFeedbacks"
SET
  "IR_Officer_Email" = pg_temp.sanitise_email_cached("IR_Officer_Email")
WHERE
  "IR_Officer_Email" IS NOT NULL;

--
-- Sanitise vwCertificationFeedbacks.COI_Officer_Email
--
UPDATE staging."vwCertificationFeedbacks"
SET
  "COI_Officer_Email" = pg_temp.sanitise_email_cached("COI_Officer_Email")
WHERE
  "COI_Officer_Email" IS NOT NULL;

--
-- Sanitise vwCertificationFeedbacks.COI_Expert_Name
--
UPDATE staging."vwCertificationFeedbacks"
SET
  "COI_Expert_Name" = pg_temp.permute_words_cached("COI_Expert_Name", true)
WHERE
  "COI_Expert_Name" IS NOT NULL;

--
-- Sanitise vwCertificationFeedbacks.IR_Email
--
UPDATE staging."vwCertificationFeedbacks"
SET
  "IR_Email" = pg_temp.sanitise_email_cached("IR_Email")
WHERE
  "IR_Email" IS NOT NULL;

--
-- Sanitise vwCertificationFeedbacks.COI_Officer_Name
--
UPDATE staging."vwCertificationFeedbacks"
SET
  "COI_Officer_Name" = pg_temp.permute_words_cached("COI_Officer_Name", true)
WHERE
  "COI_Officer_Name" IS NOT NULL;

--
-- Sanitise vwCertificationFeedbacks.COI_Expert_Mail
--
UPDATE staging."vwCertificationFeedbacks"
SET
  "COI_Expert_Mail" = pg_temp.permute_words_cached("COI_Expert_Mail", true)
WHERE
  "COI_Expert_Mail" IS NOT NULL;

--
-- Sanitise vwCertificationProject.Email
--
UPDATE staging."vwCertificationProject"
SET
  "Email" = pg_temp.sanitise_email_cached("Email")
WHERE
  "Email" IS NOT NULL;

--
-- Sanitise vwCertificationProject.Secondary_Email
--
UPDATE staging."vwCertificationProject"
SET
  "Secondary_Email" = pg_temp.sanitise_email_cached("Secondary_Email")
WHERE
  "Secondary_Email" IS NOT NULL;

--
-- Sanitise vwCertificationProject.Email_ROE
--
UPDATE staging."vwCertificationProject"
SET
  "Email_ROE" = pg_temp.sanitise_email_cached("Email_ROE")
WHERE
  "Email_ROE" IS NOT NULL;

--
-- Sanitise vwCertificationProject.Email_PD
--
UPDATE staging."vwCertificationProject"
SET
  "Email_PD" = pg_temp.sanitise_email_cached("Email_PD")
WHERE
  "Email_PD" IS NOT NULL;

--
-- Sanitise vwCertificationProject.Email_IR
--
UPDATE staging."vwCertificationProject"
SET
  "Email_IR" = pg_temp.sanitise_email_cached("Email_IR")
WHERE
  "Email_IR" IS NOT NULL;

--
-- Sanitise vwCertificationProject.Email_CO
--
UPDATE staging."vwCertificationProject"
SET
  "Email_CO" = pg_temp.sanitise_email_cached("Email_CO")
WHERE
  "Email_CO" IS NOT NULL;

--
-- Sanitise vwCertificationReview.IR_Mail
--
UPDATE staging."vwCertificationReview"
SET
  "IR_Mail" = pg_temp.permute_words_cached("IR_Mail", true)
WHERE
  "IR_Mail" IS NOT NULL;

--
-- Sanitise vwCertificationReview.ExpertEmailID
--
UPDATE staging."vwCertificationReview"
SET
  "ExpertEmailID" = pg_temp.sanitise_email_cached("ExpertEmailID")
WHERE
  "ExpertEmailID" IS NOT NULL;

--
-- Sanitise vwCertificationReview.Reviewer_Name
--
UPDATE staging."vwCertificationReview"
SET
  "Reviewer_Name" = pg_temp.permute_words_cached("Reviewer_Name", true)
WHERE
  "Reviewer_Name" IS NOT NULL;

--
-- Sanitise vwCertificationReview.Email
--
UPDATE staging."vwCertificationReview"
SET
  "Email" = pg_temp.sanitise_email_cached("Email")
WHERE
  "Email" IS NOT NULL;

--
-- Sanitise vwCertificationReview.Secondary_Email
--
UPDATE staging."vwCertificationReview"
SET
  "Secondary_Email" = pg_temp.sanitise_email_cached("Secondary_Email")
WHERE
  "Secondary_Email" IS NOT NULL;

--
-- Sanitise vwCertificationFeedbacks.CO_From_Review
--
UPDATE staging."vwCertificationFeedbacks"
SET
  "CO_From_Review" = pg_temp.permute_words_cached("CO_From_Review", true)
WHERE
  "CO_From_Review" IS NOT NULL;

--
-- Sanitise vwVVBReview.CPF_Email
--
UPDATE staging."vwVVBReview"
SET
  "CPF_Email" = pg_temp.sanitise_email_cached("CPF_Email")
WHERE
  "CPF_Email" IS NOT NULL;

--
-- Sanitise vwVVBReview.Validator_Verifier_Name
--
UPDATE staging."vwVVBReview"
SET
  "Validator_Verifier_Name" = pg_temp.permute_words_cached("Validator_Verifier_Name", true)
WHERE
  "Validator_Verifier_Name" IS NOT NULL;

--
-- Sanitise vwVVBReview.Surname_Given_Name
--
UPDATE staging."vwVVBReview"
SET
  "Surname_Given_Name" = pg_temp.permute_words_cached("Surname_Given_Name", true)
WHERE
  "Surname_Given_Name" IS NOT NULL;

--
-- Sanitise vwVVBReview.E_Mail
--
UPDATE staging."vwVVBReview"
SET
  "E_Mail" = pg_temp.sanitise_email_cached("E_Mail")
WHERE
  "E_Mail" IS NOT NULL;

--
-- Sanitise vwVVBReview.CPF_Visitor_Name
--
UPDATE staging."vwVVBReview"
SET
  "CPF_Visitor_Name" = pg_temp.permute_words_cached("CPF_Visitor_Name", true)
WHERE
  "CPF_Visitor_Name" IS NOT NULL;

--
-- Sanitise vwVVBReview.Client_Company_name
--
UPDATE staging."vwVVBReview"
SET
  "Client_Company_name" = pg_temp.permute_words_cached("Client_Company_name", true)
WHERE
  "Client_Company_name" IS NOT NULL;

--
-- Sanitise vwVVBReview.Team_Leader_Name
--
UPDATE staging."vwVVBReview"
SET
  "Team_Leader_Name" = pg_temp.permute_words_cached("Team_Leader_Name", true)
WHERE
  "Team_Leader_Name" IS NOT NULL;

--
-- Sanitise vwVVBReview.Host_Country_Expert_Name
--
UPDATE staging."vwVVBReview"
SET
  "Host_Country_Expert_Name" = pg_temp.permute_words_cached("Host_Country_Expert_Name", true)
WHERE
  "Host_Country_Expert_Name" IS NOT NULL;

--
-- Sanitise vwVVBReview.Secondary_Email
--
UPDATE staging."vwVVBReview"
SET
  "Secondary_Email" = pg_temp.sanitise_email_cached("Secondary_Email")
WHERE
  "Secondary_Email" IS NOT NULL;

--
-- Sanitise vwVVBReview.Technical_Expert_Name
--
UPDATE staging."vwVVBReview"
SET
  "Technical_Expert_Name" = pg_temp.permute_words_cached("Technical_Expert_Name", true)
WHERE
  "Technical_Expert_Name" IS NOT NULL;

--
-- Sanitise vwVVBReview.Email
--
UPDATE staging."vwVVBReview"
SET
  "Email" = pg_temp.sanitise_email_cached("Email")
WHERE
  "Email" IS NOT NULL;

--
-- Sanitise vwVVBReview.Trainee_Name
--
UPDATE staging."vwVVBReview"
SET
  "Trainee_Name" = pg_temp.permute_words_cached("Trainee_Name", true)
WHERE
  "Trainee_Name" IS NOT NULL;

--
-- Sanitise LUFProjectsDetails.Notes
--
UPDATE staging."LUFProjectsDetails"
SET
  "Notes" = pg_temp.permute_words_cached("Notes", true)
WHERE
  "Notes" IS NOT NULL AND length(trim("Notes")) > 0;

--
-- Sanitise LUFProjectsDetails.Notes
--
UPDATE staging."Projects"
SET
  "StreetAndNumber" = pg_temp.permute_words_cached("StreetAndNumber", true)
WHERE
  "StreetAndNumber" IS NOT NULL AND length(trim("StreetAndNumber")) > 0;

--
-- Sanitise vwCertificationReview.Validated_By
--
UPDATE staging."vwCertificationReview"
SET
  "Validated_By" = pg_temp.sanitise_email_cached("Validated_By")
WHERE
  "Validated_By" IS NOT NULL;

--
-- Sanitise vwCertificationReview.Project_Developer
--
UPDATE staging."vwCertificationReview"
SET
  "Project_Developer" = pg_temp.permute_words_cached("Project_Developer", true)
WHERE
  "Project_Developer" IS NOT NULL;

--
-- Sanitise vwCertificationReview.Notes_VVB_Score
--
UPDATE staging."vwCertificationReview"
SET
  "Notes_VVB_Score" = pg_temp.permute_words_cached("Notes_VVB_Score", true)
WHERE
  "Notes_VVB_Score" IS NOT NULL;
