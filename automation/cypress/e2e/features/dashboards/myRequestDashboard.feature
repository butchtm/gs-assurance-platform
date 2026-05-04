@group15
Feature: My requests dashboard
 

    Scenario Outline: <username> uses name or email filter
        Given I login as "<username>"
        Then "<username>" click "my requests" from the navbar successfully
        And I clear the my requests dashboard request status filter as "<username>"

        When I select "Name or Exact GSID" from my requests dashboard page
        And I type "automation test (POA)" into "name or gsid (my requests)"
        Then "automation test (POA)" "project" is displayed in the list 

        Examples:
            | username | 
            | PD       |
            | ARM TEAM |
            | VVB      |
            | REVIEWER |

    Scenario Outline: <username> selects last page nav button
        Given I login as "<username>"
        Then "<username>" click "my requests" from the navbar successfully

        When I select "last page" from list navigation buttons
        Then "last page" is displayed and related button disabled

        Examples:
            | username |
            | PD       |
            | ARM TEAM |
            # VVB      |
            #| REVIEWER |

    Scenario Outline: <username> selects project gsid link
        Given I login as "<username>"
        Then "<username>" click "my requests" from the navbar successfully
        And I clear the my requests dashboard request status filter as "<username>"


        When I select "Name or Exact GSID" from my requests dashboard page
        And I type "automation test (POA)" into "name or gsid (my requests)"
        Then "automation test (POA)" "project" is displayed in the list 

        When I click the "automation test (POA)" GSID from request list
        Then project information can be seen 

        Examples:
            | username | 
            | PD       |
            | ARM TEAM |
            | VVB      |
            | REVIEWER |
    
    Scenario Outline: <username> selects request link  
        Given I login as "<username>"
        Then "<username>" click "my requests" from the navbar successfully
        And I clear the my requests dashboard request status filter as "<username>"

        When I select "Name or Exact GSID" from my requests dashboard page
        And I type "automation test (POA)" into "name or gsid (my requests)"
        Then "automation test (POA)" "project" is displayed in the list 

        When I select request link from my "automation test (POA)" from column "<index>"
        Then request information can be seen    

        Examples:
            | username | index | 
            | PD       | 2     |
            | ARM TEAM | 3     |
            | VVB      | 2     |
            | REVIEWER | 2     |