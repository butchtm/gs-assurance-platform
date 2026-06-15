@emailTestSuite
Feature: emails contain the correct content

    #inprep to cc: x2 emails, template 1 for PD template 4 for ARM Team
    Scenario Outline: template 1 & template 4 (from in prep to cc with no vvb assigned)
        Given I login as "<username>"
        When I click "projects" from the navbar
        And I search a "<projectStatus>" "automation test (POA)" GSID
        And I select "<projectStatus>" "automation test (POA)" GSID from results as "<username>"
        And I select the "requests" nav button in my project 
        And I select "create new request" from the request view
        Then The "/create-request" page is loaded

        When I click "request type" from request creation page
        And I click "<requestTypeCreation>" from dropdown during request creation
        Then Field contains "<requestTypeCreation>" in "value field"

        When I click "create batch request?" from request creation page
        And I click "<batch>" from dropdown during request creation
        Then Field contains "<batch>" in "div element" 
        
        When I click "credit period id" from request creation page
        And I click "period" from dropdown during request creation
        Then Field contains "Period 1" in "div element"

        When I click "estimated emission reduction/removal" from request creation page
        And I type "<emissionCheck>" into "emission reduction or removal"
        Then Field contains "<emissionCheck>" in "value field"

        When I click "next" from request creation page 
        Then I am taken to request creation page "2"

        When I click "finish" from request creation page 
        Then Success message appears

        When I select the "requests" nav button in my project
        And I open my request "<requestTypeSubmission>" from the request view
        And I select the submit button
        And I select submit from submission modal
        Then I can capture my last "<noOfNotif>" email notifications
        Then I check my email notifications "<templates>" for "<requestTypeSubmission>" to "<targetStatus>"

    Examples:
            | username    | requestTypeCreation | requestTypeSubmission    | emissionCheck | batch | projectStatus | targetStatus       | noOfNotif | templates              |
            | ADMIN       | Preliminary Review  | Preliminary Review no. 1 | 1,000         | No    | Listed        | Completeness Check | 2         | template 1, template 4 |

    #cc to in audit: x1 emails, template 2 for PD
    Scenario Outline: template 2 (no vvb assigned)
        Given I login as "<username>"
        When I click "projects" from the navbar
        And I search a "<projectStatus>" "automation test (POA)" GSID
        And I select "<projectStatus>" "automation test (POA)" GSID from results as "<username>"
        And I select the "requests" nav button in my project 
        And I open my request "<requestTypeSubmission>" from the request view
        Then I can see the correct "<requestTypeSubmission>" header

        When I select the submit button
        And I set my answers so that completeness check has not passed
        Then I select submit from submission modal
        And I can capture my last "<noOfNotif>" email notifications
        And I check my email notifications "<templates>" for "<requestTypeSubmission>" to "<targetStatus>"

    Examples:
            | username    | requestTypeCreation | requestTypeSubmission    | emissionCheck | batch | projectStatus | targetStatus       | noOfNotif | templates              |
            | ADMIN       | Preliminary Review  | Preliminary Review no. 1 | 1,000         | No    | Listed        | In Preparation     | 1         | template 2             |


###################################################### proper valuable tests to notification below here the above 
    #vvb assigned to a request email template 8
    Scenario Outline: template 8 (vvb)
        Given I login as "<username>"
        When I click "projects" from the navbar
        And I search a "<projectStatus>" "automation test (POA)" GSID
        And I select "<projectStatus>" "automation test (POA)" GSID from results as "<username>"
        And I select the "requests" nav button in my project 
        And I select "create new request" from the request view
        Then The "/create-request" page is loaded

        When I click "request type" from request creation page
        And I click "<requestTypeCreation>" from dropdown during request creation
        Then Field contains "<requestTypeCreation>" in "value field"

        When I click "create batch request?" from request creation page
        And I click "<batch>" from dropdown during request creation
        Then Field contains "<batch>" in "div element" 
        
        When I click "credit period id" from request creation page
        And I click "period" from dropdown during request creation
        Then Field contains "Period 1" in "div element"

        When I click "estimated emission reduction/removal" from request creation page
        And I type "<emissionCheck>" into "emission reduction or removal"
        Then Field contains "<emissionCheck>" in "value field"

        When I click "next" from request creation page 
        Then I am taken to request creation page "Methodologies"

        When I click "next" from request creation page 
        Then I am taken to request creation page "3"

        When I click "finish" from request creation page 
        Then Success message appears

        When I select the "requests" nav button in my project
        And I open my request "<requestTypeSubmission>" from the request view
        And I select the submit button
        And I select a VVB account to allow me to submit
        And I select submit from submission modal

        Then I can capture my last "<noOfNotif>" email notifications
        Then I check my email notifications "<templates>" for "<requestTypeSubmission>" to "<targetStatus>"

    Examples:
            | username    | requestTypeCreation         | requestTypeSubmission           | emissionCheck | batch | projectStatus | targetStatus | noOfNotif | templates   |
            | ADMIN       | Design Change \| With VVB   | Design Change \| With VVB no. 1 | 1,000         | No    | Listed        | In Audit     | 1         | template 8  |

    #audit to cc: x3 emails, template 1 for PD and VVB, template 4 for ARM Team
    Scenario Outline: template 1 (pd) & template 4 & template 1 (vvb) (from audit to cc with vvb assigned)
        Given I login as "<username>"
        When I click "projects" from the navbar
        And I search a "<projectStatus>" "automation test (POA)" GSID
        And I select "<projectStatus>" "automation test (POA)" GSID from results as "<username>"
        And I select the "requests" nav button in my project 
        And I open my request "<requestTypeSubmission>" from the request view
        Then I can see the correct "<requestTypeSubmission>" header

        When I select the submit button
        Then I select submit from submission modal
        And I can capture my last "<noOfNotif>" email notifications
        And I check my email notifications "<templates>" for "<requestTypeSubmission>" to "<targetStatus>"

    Examples:
            | username    | requestTypeCreation         | requestTypeSubmission           | emissionCheck | batch | projectStatus | targetStatus        | noOfNotif | templates                                 |
            | ADMIN       | Design Change \| With VVB   | Design Change \| With VVB no. 1 | 1,000         | No    | Listed        | Completeness Check  | 3         | template 1, template 1:vvb, template 4    |

    #cc to in audit: x2 emails, template 2 for PD and VVB
    Scenario Outline: template 2 (from cc to in audit with vvb assigned)
        Given I login as "<username>"
        When I click "projects" from the navbar
        And I search a "<projectStatus>" "automation test (POA)" GSID
        And I select "<projectStatus>" "automation test (POA)" GSID from results as "<username>"
        And I select the "requests" nav button in my project 
        And I open my request "<requestTypeSubmission>" from the request view
        Then I can see the correct "<requestTypeSubmission>" header

        When I select the submit button
        And I set my answers so that completeness check has not passed
        Then I select submit from submission modal
        And I can capture my last "<noOfNotif>" email notifications
        And I check my email notifications "<templates>" for "<requestTypeSubmission>" to "<targetStatus>" 

    Examples:
            | username    | requestTypeCreation          | requestTypeSubmission           | emissionCheck | batch | projectStatus | targetStatus | noOfNotif | templates                  |
            | ADMIN       | Design Change \| With VVB    | Design Change \| With VVB no. 1 | 1,000         | No    | Listed        | In Audit     | 2         | template 2, template 2:vvb |

###

    # (from in audit to cc to in assignment) 
    Scenario Outline: template 3 (pd + vvb + arm team)
        Given I login as "<username>"
        When I click "projects" from the navbar
        And I search a "<projectStatus>" "automation test (POA)" GSID
        And I select "<projectStatus>" "automation test (POA)" GSID from results as "<username>"
        And I select the "requests" nav button in my project 
        And I open my request "<requestTypeSubmission>" from the request view
        Then I can see the correct "<requestTypeSubmission>" header

        When I select the submit button
        Then I select submit from submission modal

        When I select the submit button
        And I set my answers so that completeness check has passed with consultation
        Then I select submit from submission modal

        And I can capture my last "<noOfNotif>" email notifications
        And I check my email notifications "<templates>" for "<requestTypeSubmission>" to "<targetStatus>"

    Examples:
            | username    | requestTypeCreation          | requestTypeSubmission           | emissionCheck | batch | projectStatus | targetStatus   | noOfNotif | templates                                  |
            | ADMIN       | Design Change \| With VVB    | Design Change \| With VVB no. 1 | 1,000         | No    | Listed        | In Assignment  | 3         | template 3, template 3:vvb, template 3:arm |

###

    # (no movement just finance issuance notif via making invoice generated) 
    Scenario Outline: template 6 (pd + vvb)
        Given I login as "<username>"
        When I click "projects" from the navbar
        And I search a "<projectStatus>" "automation test (POA)" GSID
        And I select "<projectStatus>" "automation test (POA)" GSID from results as "<username>"
        And I select the "requests" nav button in my project 
        And I open my request "<requestTypeSubmission>" from the request view
        Then I can see the correct "<requestTypeSubmission>" header

        When I select create invoice button from request actions
        And I can see invoice created toast popup
        And I can see invoice status in request status area
        
        When I select update invoice status from request actions
        And I enter "<targetStatus>" as my invoice status
        And I can save my invoice update
        And I can see invoice updated toast popup
        Then Invoice status reads as "<targetStatus>"

        And I can capture my last "<noOfNotif>" email notifications
        And I check my email notifications "<templates>" for "<requestTypeSubmission>" to "<targetStatus>"

    Examples:
            | username    | requestTypeCreation          | requestTypeSubmission           | emissionCheck | batch | projectStatus | targetStatus | noOfNotif | templates                  |
            | ADMIN       | Design Change \| With VVB    | Design Change \| With VVB no. 1 | 1,000         | No    | Listed        | Generated    | 2         | template 6, template 6:vvb |


    #template 9 - reviweer assigned 
    Scenario Outline: template 9 (reviewer assigned)
        Given I login as "<username>"
        When I click "projects" from the navbar
        And I search a "<projectStatus>" "automation test (POA)" GSID
        And I select "<projectStatus>" "automation test (POA)" GSID from results as "<username>"
        And I select the "requests" nav button in my project 
        And I open my request "<requestTypeSubmission>" from the request view
        Then I can see the correct "<requestTypeSubmission>" header

        When I select view assignments button from request actions
        And I can see "automation test (POA)" header in assignment modal
        Then I assign the "Reviewer" to perform the review stage
        And I can see assignments updated toast popup

        And I can capture my last "<noOfNotif>" email notifications
        And I check my email notifications "<templates>" for "<requestTypeSubmission>" to "<targetStatus>"

    Examples:
            | username    | requestTypeCreation          | requestTypeSubmission           | emissionCheck | batch | projectStatus | targetStatus       | noOfNotif | templates  |
            | ADMIN       | Design Change \| With VVB    | Design Change \| With VVB no. 1 | 1,000         | No    | Listed        | Reviewer Assigned  | 1         | template 9 |

    
  #modify so that design change request is stakeholder consultation enabled
#add template 10 ---------- does not seem to be working perhaps due to newer tac/ngo notifications 
#add template 11 (make comments) --------------- some unknowns here around the GS ARM Team users getting emails twice and the need for that
#add template 5 initial round 2 emails to vvb/pd
#add template 12 submit to round 2 review 
#add template 7 submit to completed approved (x4 emails)
