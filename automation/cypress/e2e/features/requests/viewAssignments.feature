@group16
Feature: wizard to perform assignments

    Scenario Outline: <username> adds finance assignment 
        Given I login as "<username>"
        When "<username>" click "all requests" from the navbar successfully
        And I type "New" "<projectType>" GSID into search filter on "all" request dashboard
        Then "<projectType>" "project" is displayed in the list 

        When I select request link "<requestType>" on all request dashboard
        Then request information can be seen

        When I select view assignments button from request actions
        And I can see "<projectType>" header in assignment modal
        Then I assign the "<assignName>" to collect fee
        And I can see assignments updated toast popup
        
        Examples:
            | username    | projectType                          | requestType              | assignName        |
            | ARM TEAM    | automation test (standalone project) | Preliminary Review no. 1 | GS Finance Team   |