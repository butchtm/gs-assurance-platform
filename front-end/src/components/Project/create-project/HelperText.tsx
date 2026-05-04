import Link from "@mui/material/Link";

interface HelperText {
  [key: string]: (batch?: boolean) => JSX.Element;
}

export const helperTexts: HelperText = {
  projectClassification: () => (
    <>
      Standalone Project if this is a not part of a programme Programme of Activities if this is a new programme Regular
      VPA if this is a VPA of an old programme&nbsp;
      <Link
        href="https://globalgoals.goldstandard.org/standards/107_V1.2_PAR_Programme-of-Activity-Requirements.pdf"
        target="_blank"
      >
        (version 1 of the PoA requirements).
      </Link>
      <br />
      <br />
      For the difference between Regular and Real Case VPA,&nbsp;
      <Link href="https://globalgoals.goldstandard.org/107-par-programme-of-activity-requirements/" target="_blank">
        please see version 2 of the PoA requirements.
      </Link>
    </>
  ),
  projectTitle: () => (
    <>
      Please define a title for your project. If this is a VPA, please see the requirements for naming VPAs in the&nbsp;
      <Link href="https://globalgoals.goldstandard.org/107-par-programme-of-activity-requirements/" target="_blank">
        PoA requirements
      </Link>
    </>
  ),
  projectDescription: () => (
    <p>
      Please be as specific as possible without adding information (e.g. numbers) that may change in the future.
      <br />
      <br />
      This description will remain fixed throughout the project.
      <br />
      <br />
      Please describe the technology or measure, the location, and the expected impact of the project.
    </p>
  ),
  projectCountry: () => (
    <p>
      If the programme of activities spans multiple countries, please select all that apply. Standalone projects and
      VPAs are limited to one country.
    </p>
  ),
  projectScale: () => (
    <p>
      General scale definitions are available under: the&nbsp;
      <Link href="https://globalgoals.goldstandard.org/501-pr-ghg-emissions-reductions-sequestration/" target="_blank">
        GHG Emissions Reductions & Sequestration Product Requirements&nbsp;
      </Link>
      <br />
      section &ldquo;Project Scale&rdquo;.
      <br />
      <br />
      You can find more information on project scales under the respective&nbsp;
      <Link href="https://globalgoals.goldstandard.org/200-activity-requirements/" target="_blank">
        Activity Requirements.
      </Link>
    </p>
  ),
  poaTooltip: () => (
    <>
      <p>Only one scale can be selected for the PoA.</p>
      <p>
        The PoA scale determines which scales its child Real Case VPAs can select:
        <br />- <strong>Large</strong> allows <strong>Large</strong>, <strong>Small</strong>, and <strong>Micro</strong>
        .
        <br />- <strong>Small</strong> allows <strong>Small</strong> and <strong>Micro</strong>.
        <br />- <strong>Micro</strong> allows only <strong>Micro</strong>.
      </p>
      <p>
        General scale definitions are available under: the&nbsp;
        <Link
          href="https://globalgoals.goldstandard.org/501-pr-ghg-emissions-reductions-sequestration/"
          target="_blank"
        >
          GHG Emissions Reductions & Sequestration Product Requirements&nbsp;
        </Link>
        <br />
        section &ldquo;Project Scale&rdquo;.
        <br />
        <br />
        You can find more information on project scales under the respective&nbsp;
        <Link href="https://globalgoals.goldstandard.org/200-activity-requirements/" target="_blank">
          Activity Requirements.
        </Link>
      </p>
    </>
  ),
  realCaseVpaTooltip: () => (
    <>
      <p>Only one scale can be selected for the Real Case VPA.</p>
      <p>
        The available scales depend on the PoA&apos;s scale:
        <br />- If the PoA selects <strong>Large</strong>, the Real Case VPA can select <strong>Large</strong>,{" "}
        <strong>Small</strong>, or <strong>Micro</strong>.
        <br />- If the PoA selects <strong>Small</strong>, the Real Case VPA can select <strong>Small</strong> or{" "}
        <strong>Micro</strong>.
        <br />- If the PoA selects <strong>Micro</strong>, the Real Case VPA can only select <strong>Micro</strong>.
      </p>
      <p>
        General scale definitions are available under: the&nbsp;
        <Link
          href="https://globalgoals.goldstandard.org/501-pr-ghg-emissions-reductions-sequestration/"
          target="_blank"
        >
          GHG Emissions Reductions & Sequestration Product Requirements&nbsp;
        </Link>
        <br />
        section &ldquo;Project Scale&rdquo;.
        <br />
        <br />
        You can find more information on project scales under the respective&nbsp;
        <Link href="https://globalgoals.goldstandard.org/200-activity-requirements/" target="_blank">
          Activity Requirements.
        </Link>
      </p>
    </>
  ),
  regularCaseVpaTooltip: () => (
    <>
      <p>Only one scale can be selected for the Regular Case VPA.</p>
      <p>
        The available scales depend on the Real Case VPA&apos;s scale:
        <br />- If the Real Case VPA selects <strong>Large</strong>, the Regular Case VPA can select{" "}
        <strong>Large</strong>, <strong>Small</strong>, or <strong>Micro</strong>.
        <br />- If the Real Case VPA selects <strong>Small</strong>, the Regular Case VPA can select{" "}
        <strong>Small</strong> or <strong>Micro</strong>.
        <br />- If the Real Case VPA selects <strong>Micro</strong>, the Regular Case VPA can only select{" "}
        <strong>Micro</strong>.
      </p>
      <p>
        General scale definitions are available under: the&nbsp;
        <Link
          href="https://globalgoals.goldstandard.org/501-pr-ghg-emissions-reductions-sequestration/"
          target="_blank"
        >
          GHG Emissions Reductions & Sequestration Product Requirements&nbsp;
        </Link>
        <br />
        section &ldquo;Project Scale&rdquo;.
        <br />
        <br />
        You can find more information on project scales under the respective&nbsp;
        <Link href="https://globalgoals.goldstandard.org/200-activity-requirements/" target="_blank">
          Activity Requirements.
        </Link>
      </p>
    </>
  ),
  projectType: () => (
    <p>
      Please select the project type that is most appropriate to your project, at least one projec type must be selected
    </p>
  ),
  projectMethodologies: () => (
    <p>
      Please select all methodologies you are planning to use in the project.
      <br />
      <br />
      You can type a keyword or methodology number to search.
    </p>
  ),
  methodologyVersionNumber: () => <p>Input a version number of 0 for any unapproved methodologies.</p>,
  projectStartDate: () => (
    <p>
      Select the start date of the project. For the definition of start date, please see the&nbsp;
      <Link href="https://globalgoals.goldstandard.org/101-par-principles-requirements/" target="_blank">
        GS4GG Principles and Requirements
      </Link>
    </p>
  ),
  registrationType: () => (
    <p>
      If the stakeholder consultation was/will be conducted before the project start date, please select “Regular”.
      <br />
      <br />
      If the stakeholder consultation was/will be conducted after the project start date, please select “Retroactive”.
      <br />
      <br />
      For more information, see “Project start date” in&nbsp;
      <Link href="https://globalgoals.goldstandard.org/101-par-principles-requirements/" target="_blank">
        GS4GG Principles and Requirements
      </Link>
    </p>
  ),
  certifiedProduct: () => (
    <p>
      If you wish to issue carbon credits, please select “Carbon”.
      <br />
      <br />
      You may select additional certified product types from the list.
    </p>
  ),
  transitioningProject: () => (
    <p>
      If the project is new, please select No.
      <br />
      <br />
      If the project is already registered under another standard, please select Yes and answer a few additional
      questions.
    </p>
  ),
  transitioningOtherStandard: () => (
    <p>
      Please select the standard from which you are transitioning the project. Please see transition requirements in
      the&nbsp;
      <Link href="https://globalgoals.goldstandard.org/501-pr-ghg-emissions-reductions-sequestration/" target="_blank">
        GHG Product Requirements document.
      </Link>
    </p>
  ),
  transitioningCreditingPeriodStartEnd: () => (
    <p>
      Please indicate the current crediting period start and end dates in the standard from which you are transitioning.
    </p>
  ),
  transitioningPathway: () => (
    <p>
      Please select whether you are transitioning to GS VERs (not maintaining the original standard) or to GS CERs
      (maintaining CDM as the original standard).
      <br />
      <br />
      For more information, please see the&nbsp;
      <Link href="https://globalgoals.goldstandard.org/501-pr-ghg-emissions-reductions-sequestration/" target="_blank">
        GHG Product Requirements.
      </Link>
    </p>
  ),
  transitioningApprovalProcedure: () => (
    <p>
      Please select the transition approval procedure.
      <br />
      <br />
      For more details, see the&nbsp;
      <Link
        href="https://globalgoals.goldstandard.org/t-transition-cdm-and-other-standard-to-gs4gg-form/"
        target="_blank"
      >
        Transition Request Form
      </Link>
    </p>
  ),
  sustainableDevelopmentGoals: () => (
    <p>
      Please select minimum 3 Sustainable Development Goals the project will seek to positively impact.
      <br />
      <br />
      You always need to select SDG 13.
    </p>
  ),
  feeSchedule: (batch?: boolean) => (
    <p>
      Please indicate if payments will be made in cash per credit or as share of the credits issued (Share of proceeds).
      <br />
      {batch && <p>This will apply to all monitoring periods within the current batch.</p>}
      <br />
      For more information, see the&nbsp;
      <Link href="https://globalgoals.goldstandard.org/fees/" target="_blank">
        Gold Standard fee schedule.
      </Link>
    </p>
  ),
  creditingPeriods: () => (
    <p>
      Please enter the start and end date of the crediting period under Gold Standard.
      <br />
      <br />
      For more information, please see the&nbsp;
      <Link href="https://globalgoals.goldstandard.org/501-pr-ghg-emissions-reductions-sequestration/" target="_blank">
        GHG Product Requirements.
      </Link>
    </p>
  ),
  programmeOfActivities: () => <p>Please select the PoA under which you would like to add this Regular VPA.</p>,
  realCaseVPA: () => (
    <p>
      Please select the Real Case VPA under which you would like to include this Regular VPA.
      <br />
      <br />
      If this is a version 1 PoA (that does not have any Real Case VPAs), please leave blank.
    </p>
  ),
  requestType: () => <p>To update request type, you can cancel this request and create a new one.</p>,
  documentPiiRequiresReview: () => (
    <>
      <p>This document has been flagged for containing potential Personally Identifiable Information (PII).</p>
      <p>
        The ARM Team will conduct a review of the document. If they determine that the document contains inappropriate
        or excessive PII, they may remove it from the platform and contact you to upload a revised version. If the
        document is intended for public access, the ARM Team will also assess and approve it for publication, where
        appropriate.
      </p>
    </>
  ),
  documentPiiNotAcceptable: () => (
    <p>
      This document contains Personally Identifiable Information (PII) that has been marked as not acceptable for the
      public by the ARM team. Therefore this document cannot be made public.
    </p>
  ),
  commentPiiRequiresReview: () => (
    <>
      <p>This comment has been flagged for containing potential Personally Identifiable Information (PII).</p>
      <p>
        The ARM Team will conduct a review of the comment. If they determine that the comment contains inappropriate or
        excessive PII, they may remove it from the platform and contact you to upload a revised version.
      </p>
    </>
  ),
  commentPiiNotAcceptable: () => (
    <p>
      This comment contains Personally Identifiable Information (PII) that has been marked as not acceptable for the
      public by the ARM team. Therefore this comment cannot be made public.
    </p>
  ),
  uploadDocument: () => (
    <>
      Supported File Types: 30+ file types allowed (PDF, DOCX, XLSX etc.)&nbsp;
      <Link href="/project-document-types" target="_blank">
        Click here to see full list
      </Link>
    </>
  ),
};
