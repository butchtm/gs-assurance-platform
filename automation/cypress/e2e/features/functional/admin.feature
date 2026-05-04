@group10
Feature: /administration page

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

    Scenario Outline: <username> uses search bar on methodologies admin page 
        Given I login as "<username>"
        When I click "admin" from the navbar

        When I select methodologies from admin side nav area
        And I type "Not provided" into methodologies search bar
        Then I can see "Not provided" methodology from the list

    Examples:
            | username       | button                | 
            | ADMIN          | admin                 |

    Scenario Outline: <username> creates new methodology on methodologies admin page 
        Given I login as "<username>"
        When I click "admin" from the navbar

        When I select methodologies from admin side nav area
        And I select create to add a new methodology
        Then I can see create methodologies title in modal

        When I select text area and type "automated test new methodology"
        And I select create button which has become active
        And I type "automated test new methodology" into methodologies search bar
        Then I can see "automated test new methodology" methodology from the list

    Examples:
            | username       | button                | 
            | ADMIN          | admin                 |


    
    
    Scenario Outline: <username> deletes new methodology on methodologies admin page 
        Given I login as "<username>"
        When I click "admin" from the navbar

        When I select methodologies from admin side nav area
        And I type "automated test new methodology" into methodologies search bar
        Then I can see "automated test new methodology" methodology from the list

        When I select edit icon from row for "automated test new methodology"
        Then I can see edit methodologies title in modal

        When I select delete button from edit methodology modal
        And A delete confirmation modal is displayed
        Then I confirm I want to delete methodology 

    Examples:
            | username       | button                | 
            | ADMIN          | admin                 |
    
    Scenario Outline: <username> uses status reporting page from admin reporting
        Given I login as "<username>"
        When I click "admin" from the navbar

        When I select status reporting from admin side nav area
        Then I can see the total completed requests 

    Examples:
            | username       | button                | 
            | ADMIN          | admin                 |
            | ARM TEAM       | admin                 |