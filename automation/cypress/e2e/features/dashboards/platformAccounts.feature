@group6
Feature: /accounts page and /accounts/id

    Scenario Outline: <username> uses name filter
        Given I login as "<username>"
        Then "<username>" click "platform accounts" from the navbar successfully

        When I select "name filter" from accounts page
        And I type "GS Finance Team" into "filter by name - accounts"
        Then account name "GS Finance Team" is displayed in the list

        Examples:
            | username       |
            | ADMIN          |
            
    Scenario Outline: <username> uses type filter <type>
        Given I login as "<username>"
        Then "<username>" click "platform accounts" from the navbar successfully

        When I select "type filter" from accounts page
        And I select "<type>" from account type filter 
        Then account name "<accountName>" is displayed in the list

        Examples:
            | username | type     | accountName     |
            | ADMIN    | vvb      | VVB             | 
            | ADMIN    | tac      | TAC             |
            | ADMIN    | reviewer | Reviewer        |
            | ADMIN    | finance  | GS Finance Team | 

    Scenario Outline: <username> changes rows per page
        Given I login as "<username>"
        Then "<username>" click "platform accounts" from the navbar successfully

        When I select "rows per page" from accounts page
        And I select "20" from rows drop down
        Then 20 rows are on the page

        Examples:
            | username |
            | ADMIN    |

    Scenario Outline: <username> selects platform account
        Given I login as "<username>"
        Then "<username>" click "platform accounts" from the navbar successfully

        When I select "name filter" from accounts page
        And I type "<accountName>" into "filter by name - accounts"
        Then account name "<accountName>" is displayed in the list

        When I select "<accountName>" account from displayed list
        Then platform account title "<accountName>" can be seen

        Examples:
            | username | accountName      | 
            | ADMIN    | GS Finance Team  | 
            | ADMIN    | GS ARM Team      |
            | ADMIN    | GS Registry Team |
            | ADMIN    | Reviewer         | 

    Scenario Outline: <username> edits platform account
        Given I login as "<username>"
        Then "<username>" click "platform accounts" from the navbar successfully

        When I select "name filter" from accounts page
        And I type "<accountName>" into "filter by name - accounts"
        Then account name "<accountName>" is displayed in the list

        When I select "<accountName>" account from displayed list
        Then platform account title "<accountName>" can be seen

        When I select edit icon from account details area
        And edit account text is at header of modal
        And I type "<accountRename>" into account name field
        And I select update button to apply new account details 
        Then platform account title "<accountRename>" can be seen

        When I select edit icon from account details area
        And edit account text is at header of modal
        And I type "<accountName>" into account name field
        And I select update button to apply new account details 
        Then platform account title "<accountName>" can be seen

        Examples:
            | username | accountName      | accountRename     |
            | ADMIN    | GS Finance Team  | accountNameEdited |
            | ADMIN    | GS ARM Team      | accountNameEdited |
            | ADMIN    | GS Registry Team | accountNameEdited |
            | ADMIN    | Reviewer         | accountNameEdited |

    Scenario Outline: <username> creates platform account
        Given I login as "<username>"
        Then "<username>" click "platform accounts" from the navbar successfully

        When I select "create account" from accounts page
        And I input my details for "<accountType>" account
        Then I select create account button
        
        When I select "name filter" from accounts page
        And I type "<accountName>" into "filter by name - accounts"
        Then account name "<accountName>" is displayed in the list
        Examples:
            | username | accountType | accountName       |
            | ADMIN    | Reviewer    | autotest-Reviewer |
            | ADMIN    | VVB         | autotest-VVB      |
            | ADMIN    | TAC         | autotest-TAC      |