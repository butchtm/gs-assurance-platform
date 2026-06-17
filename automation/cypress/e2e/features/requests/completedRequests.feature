@completedRequests
Feature: PAA status remains and cannot be changed on a Completed - Approved request

    Scenario Outline: <username> is able to view '<requestType>' with '<paaStatus>' on a '<projectStatus>' '<projectClass>' project and cannot edit the request
        Given I view a "<requestStatus>" "<requestType>" request with PAA status "<paaStatus>" on a "<projectStatus>" "automation test (<projectClass>)" project as a "<username>"
        And The request type "<requestType>" is displayed

        Then There are no actions available

        Examples:
            | username       | projectStatus    | projectClass        | requestStatus        | requestType                                                  | paaStatus |
            | ADMIN          | New              | POA                 | Completed - Approved | Design Change \| With VVB                                    | true      |
            | PD             | New              | Standalone          | Completed - Approved | Preliminary Review                                           | true      |
            | PD             | New              | REGULARCASE         | Completed - Approved | Preliminary Review                                           | false     |
            | PD             | New              | Standalone          | In Round Completion  | Combined Design and Performance Certification \| Project     | true      |


    Scenario Outline: <username> is able to validate '<requestType>' with '<paaStatus>' on a '<projectStatus>' '<projectClass>' project and can edit the request
        Given I view a "<requestStatus>" "<requestType>" request with PAA status "<paaStatus>" on a "<projectStatus>" "automation test (<projectClass>)" project as a "<username>"
        And The request type "<requestType>" is displayed

        And I click Update Request
        Then PAA aligned is currently "<paaStatus>"

        Examples:
            | username       | projectStatus    | projectClass        | requestStatus        | requestType                                                  | paaStatus |
            | ADMIN          | New              | REALCASE            | In Round Completion  | Inclusion \| Regular Case VPA                                | true      |


