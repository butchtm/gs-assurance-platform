@viewEditRequests

Feature: View and edit existing requests

    Scenario Outline: <username> is able to view '<requestType>' with '<paaStatus>' on a '<projectStatus>' '<projectClass>' project
        Given I view a "<requestStatus>" "<requestType>" request with PAA status "<paaStatus>" on a "<projectStatus>" "automation test (<projectClass>)" project as a "<username>"
        And The request type "<requestType>" is displayed

        And I click Update Request
        Then PAA aligned is currently "<paaStatus>"

        Examples:
            | username       | projectStatus    | projectClass        | requestStatus  | requestType                                       | paaStatus |
            | PD             | New              | Standalone          | In Preparation | Preliminary Review                                | true      |
            | PD             | New              | Standalone          | In Preparation | Design Certification \| Project                   | true      |
            | ARM TEAM       | New              | Standalone          | In Preparation | Inclusion \| Regular Case VPA                     | true      |



    Scenario Outline: '<username>' is able to edit the PAA status of '<requestType>'
        Given I view a "<requestStatus>" "<requestType>" request with PAA status "<paaStatus>" on a "<projectStatus>" "automation test (<projectClass>)" project as a "<username>"
        And The request type "<requestType>" is displayed
        And I click Update Request
        And I click Back
        And I click Back
        And I click Back
        And PAA aligned is currently "<paaStatus>"

        And I select PAA aligned to be "<newPaaStatus>"

        Then PAA aligned is currently "<newPaaStatus>"
        And I click Next
        And I click Next
        And I click Next
        And I click Finish
        And I click Update Request
        And I click Back
        And I click Back
        And I click Back
        And PAA aligned is currently "<newPaaStatus>"

        Examples:
            | username       | projectStatus    | projectClass        | requestStatus  | requestType                                       | paaStatus | newPaaStatus |
            | ARM TEAM       | New              | POA                 | In Preparation | Preliminary Review                                | true      | false        |
            


    Scenario Outline: '<username>' is able to edit the PAA status of '<requestType>'
        Given I view a "<requestStatus>" "<requestType>" request with PAA status "<paaStatus>" on a "<projectStatus>" "automation test (<projectClass>)" project as a "<username>"
        And The request type "<requestType>" is displayed
        And I click Update Request
        And PAA aligned is currently "<paaStatus>"

        And I select PAA aligned to be "<newPaaStatus>"

        Then PAA aligned is currently "<newPaaStatus>"
        And I click Next
        And I click Finish
        And I click Update Request
        And PAA aligned is currently "<newPaaStatus>"

        Examples:
            | username       | projectStatus    | projectClass        | requestStatus  | requestType                                       | paaStatus | newPaaStatus |
            | ARM TEAM       | New              | Standalone          | In Preparation | Design Change \| With VVB                         | true      | false        |
            | PD             | New              | Standalone          | In Preparation | Design Change \| With VVB                         | false     | true         |


    Scenario Outline:: '<username>' is able to view and edit the PAA status of batched '<requestType>'
        Given I view a "<requestStatus>" "<requestType>" request with PAA status "<paaStatus>" on a "<projectStatus>" "automation test (<projectClass>)" project as a "<username>"
        And The request type "<requestType>" is displayed
        And I click Update Request
        And I click Back
        And I click Back
        And I click Back
        And I click Back
        And I click Back
        And PAA aligned is currently "<paaStatus>"

        And I select PAA aligned to be "<newPaaStatus>"

        Then PAA aligned is currently "<newPaaStatus>"

        And I click Next
        And I click Next
        And I click Next
        And I click Next
        And I click Next
        And I click Finish
        And I click Update Request
        And I click Back
        And I click Back
        And I click Back
        And I click Back
        And I click Back
        And PAA aligned is currently "<newPaaStatus>"

        Examples:
            | username       | projectStatus    | projectClass        | requestStatus  | requestType                                       | paaStatus | newPaaStatus |
            | ARM TEAM       | New              | REALCASE            | In Preparation | Design Change \| With VVB                         | false     | true         |
            | PD             | New              | REALCASE            | In Preparation | Design Change \| With VVB                         | true      | false        |
