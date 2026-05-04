@group1B
Feature: /login page 

    Scenario: Login page loads
        Given I visit assurance platform
        Then The "/login" page is loaded

    Scenario Outline: Login as user '<username>' (Happy path)
        Given I can see the username & password fields
        When I click the username field
        And I input user "<username>"
        When I click the password field
        And I input pass "<password>"
        When I click login button
        Then I am taken to "<url>"
        #And I can see login success message

    Examples:
        | username                | password              | url              |
        | ADMIN_USERNAME          | ADMIN_PASSWORD        | /all-requests    |
        | PD_USERNAME             | PD_PASSWORD           | /my-requests     |
        | VVB_USERNAME            | VVB_PASSWORD          | /my-requests     |
        | REVIEWER_USERNAME       | REVIEWER_PASSWORD     | /my-requests     |
        | FINANCE_USERNAME        | FINANCE_PASSWORD      | /my-requests     |
        | ARM_USERNAME            | ARM_PASSWORD          | /my-requests     |
        | TAC_USERNAME            | TAC_PASSWORD          | /my-requests     |
