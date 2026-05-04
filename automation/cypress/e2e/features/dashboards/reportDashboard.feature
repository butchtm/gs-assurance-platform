@group14
Feature: /common-dashboard (in progress)

    Scenario Outline: <username> opens dashboard and clears default values
        Given I login as "<username>"
        Then "<username>" click "common-dashboard" from the navbar successfully
        And I clear the common dashboard filters of default value

        Examples:
            | username | 
            | ADMIN    |
            | ARM TEAM |
    
     Scenario Outline: <username> uses name or email filter
        Given I login as "<username>"
        Then "<username>" click "common-dashboard" from the navbar successfully
        And I clear the common dashboard filters of default value

        When I select "Name or Exact GSID" filter from common dashboard page
        And I type "automation test (POA)" into "name or gsid (common dashboard)"
        Then "automation test (POA)" "project" is displayed in the list 

        Examples:
            | username | 
            #| ADMIN    | flakey for some reason
            | ARM TEAM |

     Scenario Outline: <username> uses request status filter
        Given I login as "<username>"
        Then "<username>" click "common-dashboard" from the navbar successfully
        And I clear the common dashboard filters of default value

        When I select "Request Status" filter from common dashboard page
        And I select "In Preparation" from common dashboard filter drop down
        Then "In Preparation" is displayed in column "Request Status" on common dashboard

        Examples:
            | username | 
            | ADMIN    |
            | ARM TEAM |

     Scenario Outline: <username> uses request type filter
        Given I login as "<username>"
        Then "<username>" click "common-dashboard" from the navbar successfully
        And I clear the common dashboard filters of default value

        When I select "Request Type" filter from common dashboard page
        And I select "Annual Report" from common dashboard filter drop down
        Then "Annual Report" is displayed in column "Request Type" on common dashboard

        Examples:
            | username | 
            | ADMIN    |
            | ARM TEAM |

     Scenario Outline: <username> uses ror count filter
        Given I login as "<username>"
        Then "<username>" click "common-dashboard" from the navbar successfully
        And I clear the common dashboard filters of default value

        When I select "RoR Count" filter from common dashboard page
        And I select "1" from common dashboard filter drop down
        Then "1" is displayed in column "RoR Count" on common dashboard

        Examples:
            | username | 
            | ADMIN    |
            | ARM TEAM |

    Scenario Outline: <username> uses project classification filter
        Given I login as "<username>"
        Then "<username>" click "common-dashboard" from the navbar successfully
        And I clear the common dashboard filters of default value

        When I select "Project Classification" filter from common dashboard page
        And I select "Programme of Activities" from common dashboard filter drop down
        Then "Programme of Activities" is displayed in column "Project Classification" on common dashboard

        Examples:
            | username | 
            | ADMIN    |
            | ARM TEAM |

    Scenario Outline: <username> uses project type filter
        Given I login as "<username>"
        Then "<username>" click "common-dashboard" from the navbar successfully
        And I clear the common dashboard filters of default value

        When I select "Project Type" filter from common dashboard page
        And I select "Agriculture" from common dashboard filter drop down
        Then "Agriculture" is displayed in column "Project Type" on common dashboard

        Examples:
            | username | 
            | ADMIN    |
            | ARM TEAM |

    Scenario Outline: <username> uses invoice status filter
        Given I login as "<username>"
        Then "<username>" click "common-dashboard" from the navbar successfully
        And I clear the common dashboard filters of default value

        When I select "Invoice Status" filter from common dashboard page
        And I select "Generated" from common dashboard filter drop down
        Then "Generated" is displayed in column "Invoice Status" on common dashboard

        Examples:
            | username | 
            | ADMIN    |
            | ARM TEAM |

    Scenario Outline: <username> selects a project link
        Given I login as "<username>"
        Then "<username>" click "common-dashboard" from the navbar successfully
        And I clear the common dashboard filters of default value

        When I select "Name or Exact GSID" filter from common dashboard page
        And I type "automation test (POA)" into "name or gsid (common dashboard)"
        Then "automation test (POA)" "project" is displayed in the list 

        When I click the "automation test (POA)" GSID from request list
        Then project information can be seen 
        
        Examples:
            | username | 
            | ADMIN    |
            | ARM TEAM |

    Scenario Outline: <username> selects last page navigation
        Given I login as "<username>"
        Then "<username>" click "common-dashboard" from the navbar successfully
        And I clear the common dashboard filters of default value

        When I select "Name or Exact GSID" filter from common dashboard page
        And I type "automation test (POA)" into "name or gsid (common dashboard)"
        Then "automation test (POA)" "project" is displayed in the list 

        When I select "last page" from list navigation buttons
        Then "last page" is displayed and related button disabled
        
        Examples:
            | username | 
            | ADMIN    |
            | ARM TEAM |


    Scenario Outline: <username> selects request link
        Given I login as "<username>"
        Then "<username>" click "common-dashboard" from the navbar successfully
        And I clear the common dashboard filters of default value

        When I select "Name or Exact GSID" filter from common dashboard page
        And I type "automation test (POA)" into "name or gsid (common dashboard)"
        Then "automation test (POA)" "project" is displayed in the list 

        When I select request link from my "automation test (POA)" from column "3"
        Then request information can be seen    
        
        Examples:
            | username | 
            | ADMIN    |
            | ARM TEAM |
     
    Scenario Outline: <username> changes rows per page
        Given I login as "<username>"
        Then "<username>" click "common-dashboard" from the navbar successfully
        And I clear the common dashboard filters of default value

        When I select "rows per page" from all requests dashboard page
        And I select "20" from rows drop down
        Then 20 rows are on the page
        
        Examples:
            | username | 
            | ADMIN    |
            | ARM TEAM |