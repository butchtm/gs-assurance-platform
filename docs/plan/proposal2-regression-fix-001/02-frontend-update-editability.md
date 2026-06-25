# Task 02: Frontend Update Form Editability And Payload

Owner role: `developer`
Review role: `tech-lead`
Source of truth: `docs/plan/2026-06-18_proposal2_regression_fix_001.md`

## Goal

Make the Update Project UI match main parity and stop stripping valid edits from the submit payload.

Main parity to preserve:

| Role | Status | Project Developer Account | Project Classification | Project Title |
|---|---|---|---|---|
| Admin | Any status | Editable | Editable | Editable |
| ARM | Any status | Preserve current behavior | Preserve current behavior | Preserve current behavior |
| Owning PD | `New` | Read-only | Editable | Editable |
| Owning PD | Listed or later | Not editable | Not editable | Not editable |

For owning PD users, `New` does not mean the whole update wizard is editable. It means only Project Classification and Project Title are editable. Later wizard steps and non-title/classification fields stay protected unless the client expands the requirement.

## Current Problem

`Step1.tsx` disables account, classification, PoA, and Real Case VPA fields whenever `isUpdate` is true.

`UpdateProject.tsx` then overwrites submitted values with the original project values:

- `accountId`
- `projectClassificationId`
- `masterProjectId`
- `parentProjectId`

So even if the fields are enabled, valid edits may not reach the backend.

## Files To Change

- `front-end/src/components/Project/update-project/UpdateProject.tsx`
- `front-end/src/components/Project/create-project/steps/Step1.tsx`
- `front-end/src/components/Project/create-project/steps/Step2.tsx`
- `front-end/src/components/Project/create-project/steps/Step3.tsx`
- `front-end/src/components/Project/create-project/steps/Step4.tsx`
- `front-end/src/components/Project/create-project/steps/Step5.tsx`
- Crediting-period subcomponents used by `Step3`
- `front-end/src/components/Project/create-project/steps/ProjectTitle.tsx`
- `front-end/src/components/Reusable/FormFields/CustomAutoComplete.tsx` only if it lacks the disabled-state support needed by `Step1`
- `front-end/src/components/Reusable/FormFields/CustomAccountSearch.tsx` only if it lacks the disabled-state support needed by `Step1`
- `front-end/src/components/Project/update-project/UpdateProject.test.tsx`
- `front-end/src/components/Project/create-project/steps/Step1.test.tsx`

## Implementation Plan

1. Add field-level capabilities in `UpdateProject.tsx`.
   - `canEditDeveloperAccount`
   - `canEditProjectClassification`
   - `canEditProjectTitle`
   - `canEditParentHierarchy`

2. Compute capabilities from role and persisted project status.
   - Admin: all true in every status.
   - ARM: preserve current behavior. Until browser parity says otherwise, treat ARM like Admin because backend currently allows it.
   - Owning PD + `New`: classification/title true; account false; parent hierarchy false.
   - Owning PD + non-New: update unavailable.
   - Non-owning PD: update unavailable.

3. Pass capabilities into `Step1`.
   - Keep default create behavior unchanged: create mode should behave as all fields editable according to existing role logic.
   - Replace blanket `disabled={isUpdate}` with capability checks.

4. Protect later wizard steps for PD New updates.
   - Admin and verified ARM parity may keep current access to later update steps.
   - Owning PD + `New` cannot edit Step2, Step3, Step4, Step5, crediting periods, transition details, dates, fees, notes, locations, SDGs, size, type, assets, or parent hierarchy.
   - If the existing UI still renders those steps for PD New, render them read-only or preserve original values in the submit payload.

5. Add disabled support to small reusable controls only if missing.
   - `ProjectTitle` gets `disabled?: boolean`.
   - `CustomAutoComplete` gets `disabled?: boolean`.
   - `CustomAccountSearchAutoComplete` gets `disabled?: boolean`.

6. Fix submit payload.
   - Authorized edits must submit changed `accountId`, `projectClassificationId`, `name`, `masterProjectId`, and `parentProjectId`.
   - Unauthorized fields should intentionally preserve original values, not accidentally preserve everything.
   - PD New submits only changed title/classification plus required unchanged values for protected fields.
   - Keep transition-project behavior unchanged.

## Acceptance Criteria

- Admin can open Update Project for New, Listed, and De-certified projects.
- Admin sees account/classification/title/parent fields enabled where the field exists.
- PD owner can update own New project classification/title.
- PD owner cannot edit account.
- PD owner cannot edit or submit changes to Step2-Step5 values while status is New.
- PD owner cannot update Listed or later projects.
- ARM can log in and the UI preserves current main/backend behavior.
- Submit payload contains changed authorized fields.
- Submit payload preserves protected fields for unauthorized roles.

## Tests

Frontend focused:

```bash
cd front-end
$HOME/.nvm/versions/node/v20.19.0/bin/node ./node_modules/jest/bin/jest.js --runTestsByPath \
  src/components/Project/update-project/UpdateProject.test.tsx \
  src/components/Project/create-project/steps/Step1.test.tsx \
  --coverage=false \
  --maxWorkers=50%
```

Add or update:

- `UpdateProject.test.tsx`
  - Admin New project: fields enabled and payload includes changed account/classification/title.
  - Admin Listed project: fields enabled and payload includes changed account/classification/title.
  - Admin De-certified project: fields enabled and payload includes changed account/classification/title.
  - PD owner New: title/classification enabled, account disabled or read-only.
  - PD owner New: Step2-Step5 values are read-only or original values are preserved in the submit payload.
  - PD owner New: direct UI submit does not include mutations for description, location, project type, dates, fee schedule, notes, crediting periods, transition details, parent hierarchy, SDGs, size, type, or assets.
  - PD owner Listed: update unavailable.
  - Non-owning PD: update unavailable.
  - ARM: expected fields enabled after local ARM parity is confirmed.

- `Step1.test.tsx`
  - Capability props drive disabled states.
  - Create mode remains unchanged.
  - Parent PoA and Real Case selectors disable when `canEditParentHierarchy` is false.
  - Title disables when `canEditProjectTitle` is false.

## Do Not Do

- Do not infer editability from `isUpdate` alone.
- Do not add new state-management libraries.
- Do not change create wizard behavior except through safe defaults.
- Do not globally expose cross-link labels outside intended selectors.
