@group12
Feature: /projects/creditingPeriods/1

    Scenario Outline: <username> opens crediting period 
        Given I login as "<username>"
        When "<username>" click "projects" from the navbar successfully
        And I search a "New" "automation test (POA)" GSID
        And I select "New" "automation test (POA)" GSID from results as "<username>"
        Then project information can be seen 

        #
        When I click "<creditingPeriodId>" from project information page
        Then "<creditingPeriodId>" from project information page has loaded
        And I can see Methodologies for "<creditingPeriodId>" displayed

    Examples:
            | username    | creditingPeriodId  | 
            | ADMIN       | Crediting Period 1 |
            | ARM TEAM    | Crediting Period 1 |
            | PD          | Crediting Period 1 |
            | TAC         | Crediting Period 1 |
            | FINANCE     | Crediting Period 1 |

    Scenario Outline: <username> opens request via link from history table
        Given I login as "<username>"
        When "<username>" click "projects" from the navbar successfully
        And I search a "New" "automation test (POA)" GSID
        And I select "New" "automation test (POA)" GSID from results as "<username>"
        Then project information can be seen 

        #
        When I click "<creditingPeriodId>" from project information page
        Then "<creditingPeriodId>" from project information page has loaded
        And I can see Methodologies for "<creditingPeriodId>" displayed
        
        #
        When I click "<requestLinkId>" from open "<creditingPeriodId>" page
        Then Request "<requestLinkId>" has opened and request status is displayed
        
    Examples:
            | username    | creditingPeriodId  | requestLinkId                    | 
            | ADMIN       | Crediting Period 1 | Design Change \| With VVB no. 1  |
            | ARM TEAM    | Crediting Period 1 | Design Change \| With VVB no. 1  |
            | PD          | Crediting Period 1 | Design Change \| With VVB no. 1  |

    Scenario Outline: <username> opens platform account via link from methodology history
        Given I login as "<username>"
        When I click "projects" from the navbar
        And I search a "New" "automation test (POA)" GSID
        And I select "New" "automation test (POA)" GSID from results as "<username>"
        Then project information can be seen 

        #
        When I click "<creditingPeriodId>" from project information page
        Then "<creditingPeriodId>" from project information page has loaded
        And I can see Methodologies for "<creditingPeriodId>" displayed
        
        #
        When I click "<accountLinkId>" from open "<creditingPeriodId>" page
        Then Account "<accountLinkId>" has opened and account details are displayed
        
    Examples:
            | username    | creditingPeriodId  | accountLinkId | 
            | ADMIN       | Crediting Period 1 | GS ARM Team   |
