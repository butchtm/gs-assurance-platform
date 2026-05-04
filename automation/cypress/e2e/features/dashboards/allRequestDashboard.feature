@group8
Feature: All requests dashboard
    
    Scenario Outline: <username> uses name or email filter
        Given I login as "<username>"
        Then "<username>" click "all requests" from the navbar successfully

        When I select "Name or Exact GSID" from all requests dashboard page
        And I type "automation test (POA)" into "name or gsid"
        Then "automation test (POA)" "project" is displayed in the list 

        Examples:
            | username | 
            | ADMIN    |
            | ARM TEAM |
    
    Scenario Outline: <username> selects project gsid link
        Given I login as "<username>"
        Then "<username>" click "all requests" from the navbar successfully

        When I select "Name or Exact GSID" from all requests dashboard page
        And I type "automation test (POA)" into "name or gsid"
        Then "automation test (POA)" "project" is displayed in the list 

        When I click the "automation test (POA)" GSID from request list
        Then project information can be seen 

        Examples:
            | username | 
            | ADMIN    |
            | ARM TEAM |
    
    Scenario Outline: <username> selects request link  
        Given I login as "<username>"
        Then "<username>" click "all requests" from the navbar successfully

        When I select "Name or Exact GSID" from all requests dashboard page
        And I type "automation test (POA)" into "name or gsid"
        Then "automation test (POA)" "project" is displayed in the list 

        When I select request link from my "automation test (POA)" request list row
        Then request information can be seen 

        Examples:
            | username | 
            | ADMIN    |
            | ARM TEAM |

    Scenario Outline: <username> changes rows per page
        Given I login as "<username>"
        Then "<username>" click "all requests" from the navbar successfully

        When I select "rows per page" from all requests dashboard page
        And I select "20" from rows drop down
        Then 20 rows are on the page

        Examples:
            | username |
            | ADMIN    |
            | ARM TEAM |

    Scenario Outline: <username> selects last page nav button
        Given I login as "<username>"
        Then "<username>" click "all requests" from the navbar successfully

        When I select "last page" from list navigation buttons
        Then "last page" is displayed and related button disabled

        Examples:
            | username |
            | ADMIN    |
            | ARM TEAM |

        Scenario Outline: <username> uses <filterType> filter
        Given I login as "<username>"
        Then "<username>" click "all requests" from the navbar successfully

        When I select "<filterType>" from all requests dashboard page
        And I type "<filterInput>" into "<filterType>"
        And I select "<filterInput>" from all request dashboard filter dropdown
        Then "<filterInput>" "<filterType>" is displayed in "<indexPos>" index of table

        Examples:
            | username | filterInput        | filterType                      | indexPos | 
            | ADMIN    | PD                 | developer filter (all requests) | 2        |
            | ARM TEAM | PD                 | developer filter (all requests) | 2        |
            
            | ADMIN    | Preliminary Review | request type (all requests)     | 3        |
            | ARM TEAM | Preliminary Review | request type (all requests)     | 3        |

            | ADMIN    | In Preparation	    | request status (all requests)   | 5        |
            | ARM TEAM | In Preparation	    | request status (all requests)   | 5        |
            
            | ADMIN    | Reviewer           | reviewer filter (all requests)  | 8        |
            | ARM TEAM | Reviewer           | reviewer filter (all requests)  | 8        |
            
            | ADMIN    | VVB                | vvb filter (all requests)       | 9        |
            | ARM TEAM | VVB                | vvb filter (all requests)       | 9        |

            | ARM TEAM | GS ARM Team        | qaqc filter (all requests)      | 10       |
            | ADMIN    | GS ARM Team        | qaqc filter (all requests)      | 10       |
