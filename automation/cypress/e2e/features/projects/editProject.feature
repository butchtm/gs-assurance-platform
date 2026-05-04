@group1A
Feature: /update-project

    Scenario Outline: <username> edits <field> on a <projectStatus> project
        Given I login as "<username>"
        When "<username>" click "projects" from the navbar successfully
        And I click "projects search bar" from projects page
        And I type "automation test (standalone project)" into "projects search bar"
        And I click "Edit" for my "automation test (standalone project)" with "<projectStatus>" status
        Then "Update Project" can be seen

        When I change "<field>" to include new information
        And I select update
        Then Success message appears
        And I can observe new "<field>" on the project information page

        Examples:
            | username       | field                 | projectStatus  |
            | ADMIN          | project description   | New            |
            | ADMIN          | project location      | New            |
            | ADMIN          | project scale         | New            |
            | ADMIN          | project types         | New            |
            | ADMIN          | certified products    | New            |
            | ADMIN          | registration type     | New            |
            | ADMIN          | carbon stream         | New            |
            | ADMIN          | fee schedule          | New            |
            | ADMIN          | s d goals             | New            |


            | ADMIN          | project description   | Listed         |
            | ADMIN          | project location      | Listed         |
            | ADMIN          | project scale         | Listed         |
            | ADMIN          | project types         | Listed         |
            | ADMIN          | certified products    | Listed         |
            | ADMIN          | registration type     | Listed         |
            | ADMIN          | carbon stream         | Listed         |
            | ADMIN          | fee schedule          | Listed         |
            | ADMIN          | s d goals             | Listed         |

            | ARM TEAM       | project description   | New            |
            | ARM TEAM       | project location      | New            |
            | ARM TEAM       | project scale         | New            |
            | ARM TEAM       | project types         | New            |
            | ARM TEAM       | certified products    | New            |
            | ARM TEAM       | registration type     | New            |
            | ARM TEAM       | carbon stream         | New            |
            | ARM TEAM       | fee schedule          | New            |
            | ARM TEAM       | s d goals             | New            |


            | ARM TEAM       | project description   | Listed         |
            | ARM TEAM       | project location      | Listed         |
            | ARM TEAM       | project scale         | Listed         |
            | ARM TEAM       | project types         | Listed         |
            | ARM TEAM       | certified products    | Listed         |
            | ARM TEAM       | registration type     | Listed         |
            | ARM TEAM       | carbon stream         | Listed         |
            | ARM TEAM       | fee schedule          | Listed         |
            | ARM TEAM       | s d goals             | Listed         |

            | PD             | project description   | New            |
            | PD             | project location      | New            |
            | PD             | project scale         | New            |
            | PD             | project types         | New            |
            | PD             | certified products    | New            |
            | PD             | registration type     | New            |
            | PD             | carbon stream         | New            |
            | PD             | fee schedule          | New            |
            | PD             | s d goals             | New            |