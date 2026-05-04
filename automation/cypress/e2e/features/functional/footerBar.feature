@group11
Feature: handful of footer features 

    Scenario Outline: <username> selects option and page successfully loads
        Given I login as "<username>"
        When I click "<footerFeature>" from the page footer
        Then "<footerFeature>" from footer has loaded a new page

    Examples:
            | username       | footerFeature      | 
            | ADMIN          | Privacy Policy     |
            | ARM TEAM       | Terms & Conditions |
            | ARM TEAM       | Accessability      |

    Scenario Outline: <username> opens and checks for app version
        Given I login as "<username>"
        When I click "<footerFeature>" from the page footer
        Then Version modal has opened with UI and API version
        And I can close the version modal

    Examples:
            | username       | footerFeature      | 
            | ARM TEAM       | Version            |