# Proposal 2 Cross-Linking Demo Recording Script

## Recording Goal

Show Proposal 2 working end to end in the local Assurance app:

- PD users can create the two new cross-link project categories.
- Cross-link Real Case VPA links to a PoA owned by another PD.
- Cross-link Regular Case VPA links to a PoA and a Real Case VPA owned by another PD.
- The created project remains owned by the creating PD.
- Normal client-facing surfaces display `Real Case VPA` / `Regular VPA`, not confusing cross-link terms.
- The linked parent PD does not gain access to the newly created project.

## Source Framing

Use this short intro:

> This demo is for Proposal 2 from the cross-linking requirement. The PDF describes adding new project categories so cross-account PoA selection is possible without changing the existing Regular VPA flow. The follow-up email confirmed the exact labels, the PD self-service scope, the cross-link helper note, all-status PoA selection, all Real Case VPAs under the chosen PoA, inherited request and batching behavior, normal dashboard/report labels, and no visibility for the linked parent PD account.

Key source points to mention:

- Proposal 2 adds two new project classifications:
  - `Real Case VPA (cross linkage)`
  - `Regular Case VPA (cross linkage)`
- These labels should be visible to PD users in Create Project.
- The helper note should explain that cross linkage means the VPA's PD account differs from the PoA's PD account, or the Regular Case VPA's PD account differs from its Real Case VPA's PD account.
- For Real Case VPA cross linkage, the PD selects a target PoA.
- For Regular Case VPA cross linkage, the PD selects both the target PoA and the corresponding Real Case VPA.
- PoA options include all statuses, including New, Listed, and Design Certified.
- Real Case options include all Real Case VPAs under the selected PoA.
- Analytics/reporting should roll these up as normal `Real Case VPA` / `Regular VPA`, not separate cross-link labels.
- Only the creating PD plus Admin/ARM should access the new project; the linked parent PD should not gain visibility or actions.

## Clean Local Starting State

The local database has been cleaned back to seeded data only.

Seeded parent data:

| GSID | Name | Owner | Status/class |
|---|---|---|---|
| `GS910101` | Cross Link Beta PoA - New | Beta PD | PoA, New |
| `GS910102` | Cross Link Beta PoA - Listed | Beta PD | PoA, Listed |
| `GS910103` | Cross Link Beta PoA - Design Certified | Beta PD | PoA, Design Certified |
| `GS910104` | Cross Link Beta Real Case - ACM0001 | Beta PD | Real Case VPA under `GS910102` |
| `GS910105` | Cross Link Beta Real Case - ACM0002 | Beta PD | Real Case VPA under `GS910103` |

No demo-created cross-link children or request rows should exist before recording.

Expected created GSIDs if you follow this script in order:

- First new project: `GS910106`
- Second new project: `GS910107`

## Presenter Script

Use this section while recording. Each step has four parts:

- **Screen:** where you should be.
- **Do:** what to click or enter.
- **Say:** a ready-to-speak line.
- **Point out:** the product detail that matters.

### 0. Before You Hit Record

Screen:

- `http://localhost:3000`
- Logged out, or on a clean login page.

Do:

- Keep this script open on another monitor.
- Keep these account roles in mind:
  - Alpha PD: the PD creating the new cross-linked VPA.
  - Beta PD: the PD who owns the linked PoA / Real Case VPA.
  - Admin/ARM: platform roles that can still support/edit.

Say:

> I am going to show the Proposal 2 cross-linking flow on localhost. The key product idea is simple: Alpha PD can create a VPA that links to Beta PD's PoA or Real Case VPA, but the new project still belongs to Alpha PD.

Point out:

- This is a local demo, not staging or production.
- The seeded Beta projects are the parent data you will link to.

### 1. Explain The Product Problem

Screen:

- Login page or Alpha PD home page.

Do:

- Do not click yet; use this as your spoken setup.

Say:

> The existing problem is that normal VPA creation is account-scoped. A Project Developer usually works with their own PoAs. But Gold Standard has cases where a VPA needs to sit under a PoA owned by a different Project Developer. Proposal 2 solves that by adding two explicit cross-link project categories, instead of changing the existing VPA behavior for everyone.

Point out:

- This is not a new business type of VPA.
- It is the same Real Case / Regular VPA concept, with a cross-account parent relationship.

### 2. Log In As Alpha PD

Screen:

- Login page.

Do:

- Log in as `pats-pd-alpha`.
- After login, land on `My Requests` or the normal authenticated home page.

Say:

> I am now logged in as Alpha PD. Alpha is the creating Project Developer. The linked PoA and Real Case VPA we will use are owned by Beta PD, so this lets us demonstrate the cross-account relationship.

Point out:

- Alpha creates the new project.
- Beta owns the existing parent PoA / Real Case VPA.
- The demo proves linking without transferring ownership.

### 3. Open Create Project And Show The New Options

Screen:

- Authenticated Alpha PD session.

Do:

- Click `Projects`.
- Click `Create Project`.
- Open the `Project Classification` dropdown.

Say:

> In the Create Project flow, we now have two new Proposal 2 options: Real Case VPA cross linkage and Regular Case VPA cross linkage. These are intentionally visible to PD users because the email clarification confirmed PD users should be able to create these cross-linking categories themselves.

Point out:

- Show both dropdown labels:
  - `Real Case VPA (cross linkage)`
  - `Regular Case VPA (cross linkage)`
- Point to the helper note and say:

> The helper text explains what cross linkage means: the VPA's PD account is different from the PoA's PD account, or for Regular Case VPAs, different from the Real Case VPA's PD account.

### 4. Explain Real Case Versus Regular Case Before Creating Anything

Screen:

- Still in Create Project Step 1, classification dropdown visible or recently closed.

Do:

- Pause and explain the model before selecting.

Say:

> Quick distinction before I create the records: a Real Case VPA links directly to a PoA. A Regular Case VPA links to the PoA and also to an existing Real Case VPA under that PoA. So Real Case needs one parent selection; Regular Case needs two selections.

Point out:

```text
PoA
├── Real Case VPA
└── Regular Case VPA
```

Say:

> In this demo, Beta PD owns the PoA and Real Case VPA. Alpha PD is creating the new cross-linked child project.

### 5. Create The Real Case VPA Cross-Link Project

Screen:

- Create Project Step 1.

Do:

- Select `Real Case VPA (cross linkage)`.
- Observe that only `Programme of Activities` appears.
- Open the PoA dropdown.

Say:

> I am starting with Real Case VPA cross linkage. Notice that this only asks for the target PoA. That matches the product rule: a Real Case VPA links directly to a Programme of Activities.

Point out:

- Show the Beta PoAs across statuses:
  - `Cross Link Beta PoA - New (GS910101)`
  - `Cross Link Beta PoA - Listed (GS910102)`
  - `Cross Link Beta PoA - Design Certified (GS910103)`

Say:

> The dropdown includes PoAs across statuses: New, Listed, and Design Certified. That came directly from the email clarification.

Do:

- Select `Cross Link Beta PoA - Listed (GS910102)`.
- Enter title: `Demo Cross Link Real Case VPA`.
- Continue through the wizard.
- Use:
  - Methodology: `ACM0001 Flaring or use of landfill gas`
  - Version: `1`
  - Transitioning Project: `No`
  - Fee Schedule: `Cash per Credit`
- On review, click `Finish`.

Say while filling:

> The remaining wizard fields are normal project creation fields. The important Proposal 2 behavior is the parent selection and ownership rule.

Expected result:

- New project is created.
- If the database was reset, this should be `GS910106`.

Say after submit:

> This new project was created by Alpha PD, but it links to Beta PD's PoA `GS910102`.

### 6. Verify The Real Case Project Detail Page

Screen:

- Project tree or project detail page for the new Real Case cross-link project.

Do:

- Open the created project, expected `GS910106`.
- Find `Project Class`.
- Find `POA GSID`.

Say:

> On the detail page, the project class is displayed as Real Case VPA. We do not keep showing the cross-link tag here because the email clarified that dashboards, reports, and normal client-facing surfaces should not introduce confusing new classification terms.

Point out:

- `Project Class` should say `Real Case VPA`.
- `POA GSID` should be `GS910102`.

Say:

> The cross-link category was useful during creation. After creation, the project behaves like a normal Real Case VPA, with the linked PoA relationship stored underneath.

### 7. Create The Regular Case VPA Cross-Link Project

Screen:

- Authenticated as Alpha PD.

Do:

- Go back to `Projects`.
- Click `Create Project`.
- Select `Regular Case VPA (cross linkage)`.

Say:

> Now I will create the Regular Case VPA cross-link. The difference is that Regular Case needs both the PoA and the Real Case VPA under that PoA.

Point out:

- Two selectors appear:
  - `Programme of Activities`
  - `Real Case VPA`

Say:

> This is the key product difference between Real Case and Regular Case. Real Case links to the PoA. Regular Case links to the PoA and the Real Case VPA under it.

Do:

- Select `Cross Link Beta PoA - Listed (GS910102)`.
- Open `Real Case VPA`.
- Select `Cross Link Beta Real Case - ACM0001 (GS910104)`.

Say:

> After I choose the PoA, the Real Case VPA dropdown is filtered to Real Case VPAs under that selected PoA. Here I am selecting Beta's Real Case VPA `GS910104`.

Do:

- Enter title: `Demo Cross Link Regular Case VPA`.
- Continue through the wizard.
- Use:
  - Methodology: `ACM0001 Flaring or use of landfill gas`
  - Version: `1`
  - Transitioning Project: `No`
  - Fee Schedule: `Cash per Credit`
- On review, click `Finish`.

Expected result:

- New project is created.
- If the database was reset and Real Case was created first, this should be `GS910107`.

Say after submit:

> This project was created by Alpha PD, but it is linked into Beta's PoA hierarchy: PoA `GS910102`, Real Case VPA `GS910104`.

### 8. Verify The Regular Case Project Detail Page

Screen:

- Project tree or detail page for the new Regular Case cross-link project.

Do:

- Open the created project, expected `GS910107`.
- Find `Project Class`.
- Find `POA GSID`.

Say:

> On the detail page, this displays as Regular VPA. Again, the cross-link label is not shown as a separate long-term client-facing category.

Point out:

- `Project Class` should say `Regular VPA`.
- `POA GSID` should be `GS910102`.

Say:

> Product-wise, this confirms the implementation is not creating a confusing new reporting category. It is still a Regular VPA, but with cross-account linkage enabled during creation.

### 9. Optional: Create Requests To Show Existing Request Behavior

Use this section only if you want a longer demo. Skip it if the recording is already long.

Screen:

- Detail page for the new Real Case or Regular Case project.

Do for Real Case:

- Open the Real Case cross-link project.
- Click `Requests`.
- Click `Create New Request`.
- Select `Design Certification | Real Case VPA`.
- Select `No` for batch if prompted.
- Select `Period 1`.
- Enter estimated annual emission reduction/removal: `100`.
- Click `Next`.
- Click `Finish`.

Say:

> This is using the existing Real Case VPA request path. That is intentional: the new cross-link category inherits the existing request behavior instead of adding a new request family.

Do for Regular Case:

- Open the Regular cross-link project.
- Click `Requests`.
- Click `Create New Request`.
- Select `Inclusion | Regular Case VPA`.
- Select `No` for batch if prompted.
- Select `Period 1`.
- Enter estimated annual emission reduction/removal: `100`.
- Click `Next`.
- Click `Finish`.

Say:

> This uses the existing Regular Case VPA request path. So from the request workflow standpoint, the project behaves like the normal VPA type.

Point out:

- Fresh `In Preparation` requests may not appear in the common dashboard table by default because that table has a default `First RoR Start Date` filter.
- Do not spend time debugging that during the video unless dashboard proof is the goal.

### 10. Verify The Linked Parent PD Does Not Get Access

Screen:

- Alpha PD session, after both projects are created.

Do:

- Copy or remember the new Regular cross-link project URL, expected `http://localhost:3000/projects/GS910107`.
- Log out Alpha PD.
- Log in as `pats-pd-beta`.
- Open the Alpha-created project URL directly.

Say before opening:

> Now I am switching to Beta PD. Beta owns the linked PoA and Real Case VPA, but the email clarified that the linked parent PD should not gain visibility or actions on Alpha's newly created project.

Expected result:

- Beta sees `You do not have access to this` or another no-access page.

Say:

> This is the access-control part of the demo. Cross-linking creates a hierarchy relationship, but it does not transfer ownership and it does not grant Beta access to Alpha's new project.

Point out:

- This rule came from the follow-up email, not the original PDF.
- The PDF describes Proposal 2 and cross-account selection.
- The email clarified ownership/visibility.

### 11. Close The Demo

Screen:

- Beta no-access page, or switch back to Alpha project detail if you prefer a more positive ending.

Say:

> To summarize: Proposal 2 adds two PD-selectable cross-link creation options. Real Case cross linkage selects a cross-account PoA. Regular Case cross linkage selects the cross-account PoA plus the Real Case VPA under it. The created project remains owned by the creating PD, normal screens display Real Case VPA or Regular VPA, and the linked parent PD does not gain access.

Optional final sentence:

> So the product value is that Gold Standard can support cross-account PoA/VPA hierarchy relationships without changing the existing normal VPA flows and without creating confusing new reporting categories.

## Fast Talk Track If You Get Stuck

Use this if you freeze:

> The thing to remember is: cross-linking is only special during creation. It lets Alpha PD pick Beta PD's PoA or Real Case VPA. After creation, the project is still just a Real Case VPA or Regular VPA owned by Alpha PD. Beta's project can be linked as the parent, but Beta does not get access to Alpha's new project.

## What Not To Overclaim In The Video

- Do not say this is deployed to staging or production. This is a local demo.
- Do not say old dummy/workaround projects are migrated or cleaned up. That was explicitly out of scope.
- Do not say dashboard spreadsheet export is fully video-proven unless you also capture the async export artifact.
- Do not say the default dashboard table shows fresh requests without noting the First RoR Start Date filter behavior.
- Do not say Beta has view-only access to Alpha's created project. The confirmed rule is no visibility/action access for the linked parent PD.

## Quick Recovery Notes

If you need to reset the demo again, remove projects `GS910106+` and their requests, then rerun:

```bash
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml exec -T db \
  psql -U postgres -d assurance_db < back-end/local/seed-cross-linking-fixtures.sql
```

Then verify only `GS910101` through `GS910105` exist for the cross-link seed baseline.
