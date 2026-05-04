@group9
Feature: consultations page 

    Scenario Outline: logged in <username> collapses consultations
        Given I login as "<username>"
        Then "<username>" click "consultations" from the navbar successfully
        Then I collapse or expand all the rows

    Examples:
            | username       |
            | ADMIN          | 
            | PD             | 
            | VVB            | 
            | ARM TEAM       |
            | TAC            |
            | FINANCE        |
            | REVIEWER       |

    Scenario Outline: <username> selects a consultation
        Given I login as "<username>"
        Then "<username>" click "consultations" from the navbar successfully

        When I click a project in consultation
        Then Important information headers can be seen 

    Examples:
            | username       |
            | ADMIN          | 
            | PD             | 
            | VVB            | 
            | ARM TEAM       |
            | TAC            |
            | FINANCE        |
            | REVIEWER       |

    Scenario Outline: <username> adds comment to consultation
        Given I login as "<username>"
        Then "<username>" click "consultations" from the navbar successfully

        When I click a project in consultation
        And I select add comment 
        Then I can see comment modal with fields

        When I fill out comment modal details
        And I can check my comment check for pii issues
        And I click submit comment from modal
        Then I can see a success popup for comment

    Examples:
            | username       |
            | ADMIN          | 
            | PD             | 
            | VVB            | 
            | ARM TEAM       |
            | TAC            |
            | FINANCE        |
            | REVIEWER       |

    