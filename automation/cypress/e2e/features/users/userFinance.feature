@group17
Feature: finance user specific tests

    Scenario Outline: <username> creates an invoice on a request
        Given I login as "<username>"
        And I type "New" "<projectType>" GSID into search filter on "my" request dashboard
        Then "<projectType>" "project" is displayed in the list 

        When I select request link "<requestType>" on finance my request dashboard
        Then request information can be seen

        When I select create invoice button from request actions
        And I can see invoice created toast popup
        And I can see invoice status in request status area
        Then Invoice status reads as "<invoiceStatus>"

    Examples:
            | username | requestType              | projectType                          | invoiceStatus |
            | FINANCE  | Preliminary Review no. 1 | automation test (standalone project) | Not Triggered |


    Scenario Outline: <username> updates an invoice amount
        Given I login as "<username>"
        And I type "New" "<projectType>" GSID into search filter on "my" request dashboard
        Then "<projectType>" "project" is displayed in the list 

        When I select request link "<requestType>" on finance my request dashboard
        Then request information can be seen

        When I select update invoice status from request actions
        And I enter "5,555" as my invoice amount 
        And I can save my invoice update
        And I can see invoice updated toast popup
        Then Invoice amount shows as "5,555" in request status area

    Examples:
            | username | requestType              | projectType                          | invoiceStatus |
            | FINANCE  | Preliminary Review no. 1 | automation test (standalone project) | Not Triggered |

    Scenario Outline: <username> updates an invoice status
        Given I login as "<username>"
        And I type "New" "<projectType>" GSID into search filter on "my" request dashboard
        Then "<projectType>" "project" is displayed in the list 

        When I select request link "<requestType>" on finance my request dashboard
        Then request information can be seen

        When I select update invoice status from request actions
        And I enter "<invoiceStatus>" as my invoice status
        And I can save my invoice update
        And I can see invoice updated toast popup
        Then Invoice status reads as "<invoiceStatus>"

    Examples:
            | username | requestType              | projectType                          | invoiceStatus |
            | FINANCE  | Preliminary Review no. 1 | automation test (standalone project) | Paid          |