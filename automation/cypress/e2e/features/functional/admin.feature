@group10
Feature: /administration page

    Scenario Outline: Clear down test methodologies
        Given I login to the API as an admin
        And I use the API to clear all test methodologies

        When I visit assurance platform
        And I click "admin" from the navbar
        And I select methodologies from admin side nav area
        And I type "automated test" into methodologies search bar

        Then The methodology list is empty

    Examples:
            | username       | button                | 

            | ADMIN          | admin                 |

    Scenario Outline: <username> uses side bar navigation
        Given I login as "<username>"
        When I click "admin" from the navbar

        When I collapse the admin side nav area
        Then admin side nav bar is collapsed

        When I expand the admin side nav area
        Then admin side nav bar is expanded

    Examples:
            | username       | button                | 

            | ADMIN          | admin                 |
            | ARM TEAM       | admin                 |

    Scenario Outline: <username> uses methodologies page from admin reference types
        Given I login as "<username>"
        When I click "admin" from the navbar

        When I select methodologies from admin side nav area
        Then I can see a list of active methodologies

    Examples:
            | username       | button                | 
            | ADMIN          | admin                 |


    Scenario Outline: <username> searches for existing methodology and verifies default non-PAA status
        Given I login as "<username>"
        When I click "admin" from the navbar

        When I select methodologies from admin side nav area
        And I type "Not provided" into methodologies search bar
        Then I can see "Not provided" methodology from the list

        When I select edit icon from row for "Not provided"

        Then I can see edit methodologies title in modal
        And The PAA checkbox is currently "unchecked"

    Examples:
            | username       | button                | 
            | ADMIN          | admin                 |


    Scenario Outline: <username> creates new inactive PAA methodology on methodologies admin page 
        Given I login as "<username>"
        When I click "admin" from the navbar

        When I select methodologies from admin side nav area
        And I select create to add a new methodology
        Then I can see create methodologies title in modal
        And The PAA checkbox is currently "checked"
        And The Active checkbox is currently "unchecked"

        When I select text area and type "automated test new inactive methodology PAA"
        And I select create button which has become active
        And I type "automated test new inactive methodology PAA" into methodologies search bar
        Then I can see "automated test new inactive methodology PAA" methodology from the list

    Examples:
            | username       | button                | 
            | ADMIN          | admin                 |

    Scenario Outline: <username> creates new active PAA methodology on methodologies admin page 
        Given I login as "<username>"
        When I click "admin" from the navbar

        When I select methodologies from admin side nav area
        And I select create to add a new methodology
        Then I can see create methodologies title in modal
        And The PAA checkbox is currently "checked"
        And The Active checkbox is currently "unchecked"

        When I select text area and type "automated test new active methodology PAA"
        And I click the Active checkbox
        And I select create button which has become active
        And I type "automated test new active methodology PAA" into methodologies search bar
        Then I can see "automated test new active methodology PAA" methodology from the list

    Examples:
            | username       | button                | 
            | ADMIN          | admin                 |

    Scenario Outline: <username> creates new inactive non-PAA methodology on methodologies admin page 
        Given I login as "<username>"
        When I click "admin" from the navbar

        When I select methodologies from admin side nav area
        And I select create to add a new methodology
        Then I can see create methodologies title in modal
        And The PAA checkbox is currently "checked"
        And The Active checkbox is currently "unchecked"

        When I select text area and type "automated test new inactive methodology non PAA"
        And I click the PAA checkbox
        And I select create button which has become active
        And I type "automated test new inactive methodology non PAA" into methodologies search bar
        Then I can see "automated test new inactive methodology non PAA" methodology from the list

    Examples:
            | username       | button                | 
            | ADMIN          | admin                 |


    Scenario Outline: <username> creates new active non-PAA methodology on methodologies admin page 
        Given I login as "<username>"
        When I click "admin" from the navbar

        When I select methodologies from admin side nav area
        And I select create to add a new methodology
        Then I can see create methodologies title in modal
        And The PAA checkbox is currently "checked"
        And The Active checkbox is currently "unchecked"

        When I select text area and type "automated test new active methodology non PAA"
        And I click the PAA checkbox
        And I click the Active checkbox
        And I select create button which has become active
        And I type "automated test new active methodology non PAA" into methodologies search bar
        Then I can see "automated test new active methodology non PAA" methodology from the list

    Examples:
            | username       | button                | 
            | ADMIN          | admin                 |


    Scenario Outline: <username> views <methodology> on methodologies admin page and verifies PAA box is <checkboxStatus>
        Given I login as "<username>"
        When I click "admin" from the navbar

        When I select methodologies from admin side nav area
        And I type "<methodology>" into methodologies search bar
        Then I can see "<methodology>" methodology from the list
        And The PAA label on "<methodology>" should "<paaLabel>"
        And I select edit icon from row for "<methodology>"

        Then I can see edit methodologies title in modal
        And The PAA checkbox is currently "<paaCheckboxStatus>"
        And The Active checkbox is currently "<activeCheckboxStatus>"

    Examples:
            | username       | button                | methodology                                        | paaCheckboxStatus  | activeCheckboxStatus | paaLabel  |
            | ADMIN          | admin                 | automated test new inactive methodology PAA        | checked            | unchecked            | exist     |
            | ADMIN          | admin                 | automated test new inactive methodology non PAA    | unchecked          | unchecked            | not exist |
            | ADMIN          | admin                 | automated test new active methodology PAA          | checked            | checked              | exist     |
            | ADMIN          | admin                 | automated test new active methodology non PAA      | unchecked          | checked              | not exist |

    
    Scenario Outline: <username> filters the methodology list to show PAA methodologies
        Given I login as "<username>"
        When I click "admin" from the navbar

        When I select methodologies from admin side nav area
        And I type "automated test" into methodologies search bar
        And I click the PAA filter button
        And The PAA filter button is set to "unset"
        And I select filter by PAA

        Then The PAA filter button is set to "paa"
        And The PAA filter button shows a filter is applied
        And I cannot see "<methodology>" methodology on the list
        And Only PAA methodologies show on the list
        And I can see "<paamethodology>" methodology from the list

    Examples:
            | username       | button                | methodology                                      | paamethodology                               |
            | ADMIN          | admin                 | automated test new inactive methodology non PAA  | automated test new inactive methodology PAA  |
            | ADMIN          | admin                 | automated test new active methodology non PAA    | automated test new active methodology PAA    |


    Scenario Outline: <username> filters the methodology list to show non-PAA methodologies
        Given I login as "<username>"
        When I click "admin" from the navbar

        When I select methodologies from admin side nav area
        And I type "automated test" into methodologies search bar
        And I click the PAA filter button
        And The PAA filter button is set to "unset"
        And I select filter by PAA
        And I select filter by PAA

        Then The PAA filter button is set to "no paa"
        And The PAA filter button shows a filter is applied
        And I cannot see "<methodology>" methodology on the list
        And No PAA methodologies show on the list
        And I can see "<nonpaamethodology>" methodology from the list

    Examples:
            | username       | button                | methodology                                  | nonpaamethodology                                |
            | ADMIN          | admin                 | automated test new inactive methodology PAA  | automated test new inactive methodology non PAA  |
            | ADMIN          | admin                 | automated test new active methodology PAA    | automated test new active methodology non PAA    |


    Scenario Outline: <username> cycles through the PAA filter on the methodology list
        Given I login as "<username>"
        When I click "admin" from the navbar

        When I select methodologies from admin side nav area
        And I click the PAA filter button
        And The PAA filter button is set to "unset"
        And I select filter by PAA

        Then The PAA filter button is set to "paa"
        And The PAA filter button shows a filter is applied
        And Only PAA methodologies show on the list

        And I select filter by PAA
        Then The PAA filter button is set to "no paa"
        And No PAA methodologies show on the list

    Examples:
            | username       | button                | 
            | ADMIN          | admin                 |


    Scenario Outline: <username> edits PAA status of <methodology> on methodologies admin page
        Given I login as "<username>"
        When I click "admin" from the navbar

        When I select methodologies from admin side nav area
        And I type "<methodology>" into methodologies search bar
        Then I can see "<methodology>" methodology from the list
        And I select edit icon from row for "<methodology>"

        Then I can see edit methodologies title in modal
        And The PAA checkbox is currently "<currentPaaCheckboxStatus>"
        And I click the PAA checkbox
        And The PAA checkbox is currently "<newPaaCheckboxStatus>"

        When I click the Edit button
        Then I can see "<methodology>" methodology from the list
        And The PAA label on "<methodology>" should "<paaLabel>"
        And I select edit icon from row for "<methodology>"
        And The PAA checkbox is currently "<newPaaCheckboxStatus>"

    Examples:
            | username       | button                | methodology                                        | currentPaaCheckboxStatus  | newPaaCheckboxStatus | paaLabel  |
            | ADMIN          | admin                 | automated test new inactive methodology PAA        | checked                   | unchecked            | not exist |
            | ADMIN          | admin                 | automated test new inactive methodology non PAA    | unchecked                 | checked              | exist     |
            | ADMIN          | admin                 | automated test new active methodology PAA          | checked                   | unchecked            | not exist |
            | ADMIN          | admin                 | automated test new active methodology non PAA      | unchecked                 | checked              | exist     |
    


    Scenario Outline: <username> deletes new methodology on methodologies admin page 
        Given I login as "<username>"
        When I click "admin" from the navbar

        When I select methodologies from admin side nav area
        And I type "<methodology>" into methodologies search bar
        Then I can see "<methodology>" methodology from the list

        When I select edit icon from row for "<methodology>"
        Then I can see edit methodologies title in modal

        When I select delete button from edit methodology modal
        And A delete confirmation modal is displayed
        Then I confirm I want to delete methodology 
        And I cannot see "<methodology>" methodology on the list

    Examples:
            | username       | button                | methodology                                      |
            | ADMIN          | admin                 | automated test new inactive methodology PAA      |
            | ADMIN          | admin                 | automated test new inactive methodology non PAA  |
            | ADMIN          | admin                 | automated test new active methodology PAA        |
            | ADMIN          | admin                 | automated test new active methodology non PAA    |
    

    Scenario Outline: <username> uses status reporting page from admin reporting
        Given I login as "<username>"
        When I click "admin" from the navbar

        When I select status reporting from admin side nav area
        Then I can see the total completed requests 

    Examples:
            | username       | button                | 
            | ADMIN          | admin                 |
            | ARM TEAM       | admin                 |