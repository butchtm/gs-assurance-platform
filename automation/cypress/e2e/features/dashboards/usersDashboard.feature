 @group7
 Feature: users dashboard available to Admin

    Scenario Outline: <username> uses active user filter as <type>
        Given I login as "<username>"
        Then "<username>" click "users" from the navbar successfully

        When I select "active filter" from users dashboard page
        And I set filter to see "<type>" users
        Then "<type>" "active" is displayed in the list 

        Examples:
            | username | type     |
            | ADMIN    | Inactive |
            | ADMIN    | Active   | 

    Scenario Outline: <username> uses name or email filter
        Given I login as "<username>"
        Then "<username>" click "users" from the navbar successfully

        When I select "name or email" from users dashboard page
        And I type "autotest-pd" into "name or email"
        Then "autotest-pd" "name" is displayed in the list 

        Examples:
            | username | 
            | ADMIN    |

    Scenario Outline: <username> selects platform account link from list
        Given I login as "<username>"
        Then "<username>" click "users" from the navbar successfully

        When I select "name or email" from users dashboard page
        And I type "autotest-pd" into "name or email"
        Then "PD" "account" is displayed in the list 
        And I click the "PD" link to accounts
        Then platform account title "PD" can be seen

        Examples:
            | username | 
            | ADMIN    |

    Scenario Outline: <username> changes rows per page
        Given I login as "<username>"
        Then "<username>" click "users" from the navbar successfully

        When I select "rows per page" from accounts page
        And I select "20" from rows drop down
        Then 20 rows are on the page

        Examples:
            | username |
            | ADMIN    |
    
    Scenario Outline: <username> selects last page nav button
        Given I login as "<username>"
        Then "<username>" click "users" from the navbar successfully

        When I select "last page" from list navigation buttons
        Then "last page" is displayed and related button disabled

        Examples:
            | username |
            | ADMIN    |