@group13
Feature: tac user specific tests

    Scenario Outline: logged in <username> comments on consultations
        Given I login as "<username>"
        When "<username>" click "my requests" from the navbar successfully
        And I open a combined design request from my request dashboard
        Then An open combined design request with stakeholder consultation can be seen

        When I can click add comment from the request actions area
        Then Add comment modal has appeared for TAC user

        When I select comment from field text area
        And I type "This is my test TAC comment" into comment field text area
        And I can check my comment check for pii issues
        Then I can submit my comment and see success popup

    Examples:
            | username       |
            | TAC            |