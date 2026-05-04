@group3
Feature: /create-request (monitoring periods)
#This file is an extension on requestCreation.feature however specifically for request types which do monitoring periods
#The requestCreation.feature is rather full and became difficult to maintain hence the split 

    #standalone project specific
    Scenario Outline: Create '<requestType>' on Standalone Project which is '<projectStatus>' by '<username>'
        Given I login as "<username>"
        When "<username>" click "projects" from the navbar successfully
        And I search a "<projectStatus>" "automation test (<projectClass>)" GSID
        And I select "<projectStatus>" "automation test (<projectClass>)" GSID from results as "<username>"
        And I select the "requests" nav button in my project 
        And I select "create new request" from the request view 
        Then The "/create-request" page is loaded

        When I click "request type" from request creation page
        And I click "<requestType>" from dropdown during request creation
        Then Field contains "<requestType>" in "value field"
        
        When I click "credit period id" from request creation page
        And I click "period" from dropdown during request creation
        Then Field contains "Period 1" in "div element"

        When I click "add new monitoring period" from request creation page
        Then monitoring period start and end date appear

        When I supply fee schedule as required by request type "<requestType>"
        Then Field contains "Cash per Credit" for request type "<requestType>"

        When I click "estimated emission reduction/removal" from request creation page
        And I type "<emissionCheck>" into "emission reduction or removal"
        Then Field contains "<emissionCheck>" in "value field"

        When I click "next" from project creation page 
        Then I am taken to page "2"

        When I click "finish" from project creation page 
        Then Success message appears
        Examples:
            | username | projectClass       | requestType                                                               | emissionCheck | projectStatus   | 
            | ADMIN    | standalone project | Generic                                                                   | 152           | New             |
            
            | ADMIN    | standalone project | Combined Design and Performance Certification \| Project                  | 152           | New             |
            | ADMIN    | standalone project | Combined Design and Performance Certification \| PoA with Real Case VPA   | 152           | New             |
            | ADMIN    | standalone project | Combined Design and Performance Certification \| Real Case or Regular VPA | 152           | New             |

            | PD       | standalone project | Performance Certification \| Project                                      | 152           | New             |
            | PD       | standalone project | Performance Certification \| Real or Regular VPAs                         | 152           | New             |
            #| PD       | standalone project | Issuance                                                                  | 152           | New             |

            | ADMIN    | standalone project | Generic                                                                   | 152           | Listed          |

    #real/regular specific (note that monitoring periods are not available on POAs) - can ignore batching question
    Scenario Outline: Create '<requestType>' on '<projectClass>' which is '<projectStatus>' by '<username>'
        Given I login as "<username>"
        When I click "projects" from the navbar
        And I search a "<projectStatus>" "automation test (<projectClass>)" GSID
        And I select "<projectStatus>" "automation test (<projectClass>)" GSID from results as "<username>"
        And I select the "requests" nav button in my project 
        And I select "create new request" from the request view
        Then The "/create-request" page is loaded

        When I click "request type" from request creation page
        And I click "<requestType>" from dropdown during request creation
        Then Field contains "<requestType>" in "value field"
        
        When I click "credit period id" from request creation page
        And I click "period" from dropdown during request creation
        Then Field contains "Period 1" in "div element"

        When I click "add new monitoring period" from request creation page
        Then monitoring period start and end date appear

        When I supply fee schedule as required by request type "<requestType>"
        Then Field contains "Cash per Credit" for request type "<requestType>"


        When I click "estimated emission reduction/removal" from request creation page
        And I type "<emissionCheck>" into "emission reduction or removal"
        Then Field contains "<emissionCheck>" in "value field"

        When I click "next" from project creation page 
        Then I am taken to page "2"

        When I click "finish" from project creation page 
        Then Success message appears
        
        Examples:
            | username | projectClass  | requestType                                                               | emissionCheck | projectStatus   | 
            | ADMIN    | REGULARCASE   | Combined Design and Performance Certification \| Project                  | 1,000         | New             |
            | ADMIN    | REGULARCASE   | Performance Certification \| Project                                      | 1,000         | New             |

            | ADMIN    | REALCASE      | Combined Design and Performance Certification \| Project                  | 152           | New             |
            | ADMIN    | REALCASE      | Performance Certification \| Project                                      | 152           | New             |

    #real/regular specific (note specific for requests which can't ignore batching question)
    Scenario Outline: Create '<requestType>' on '<projectClass>' which is '<projectStatus>' by '<username>'
        Given I login as "<username>"
        When "<username>" click "projects" from the navbar successfully
        And I search a "<projectStatus>" "automation test (<projectClass>)" GSID
        And I select "<projectStatus>" "automation test (<projectClass>)" GSID from results as "<username>"
        And I select the "requests" nav button in my project 
        And I select "create new request" from the request view
        Then The "/create-request" page is loaded

        When I click "request type" from request creation page
        And I click "<requestType>" from dropdown during request creation
        Then Field contains "<requestType>" in "value field"

        When I click "create batch request?" from request creation page
        And I click "<batch>" from dropdown during request creation
        Then Field contains "<batch>" in "div element" 

        When I click "credit period id" from request creation page
        And I click "period" from dropdown during request creation
        Then Field contains "Period 1" in "div element"

        When I click "add new monitoring period" from request creation page
        Then monitoring period start and end date appear

        When I supply fee schedule as required by request type "<requestType>"
        Then Field contains "Cash per Credit" for request type "<requestType>"


        When I click "estimated emission reduction/removal" from request creation page
        And I type "<emissionCheck>" into "emission reduction or removal"
        Then Field contains "<emissionCheck>" in "value field"

        When I click "next" from project creation page 
        Then I am taken to page "2"

        When I click "finish" from project creation page 
        Then Success message appears
        
        Examples:
            | username | projectClass  | requestType                                                               | emissionCheck | projectStatus   | batch |
            | ADMIN    | REGULARCASE   | Generic                                                                   | 1,000         | New             | No    |
            | PD       | REGULARCASE   | Performance Certification \| Real or Regular VPAs                         | 152           | New             | No    |
            #| PD       | REGULARCASE   | Issuance                                                                  | 152           | New             | No    |
            #| ADMIN    | REGULARCASE   | Combined Design and Performance Certification \| Real Case or Regular VPA | 152           | New             | No    |

            | ADMIN    | REALCASE      | Generic                                                                   | 152           | New             | No    |
            | PD       | REALCASE      | Performance Certification \| Real or Regular VPAs                         | 152           | New             | No    |
           #| PD       | REALCASE      | Issuance                                                                  | 152           | New             | No    |
           #| ADMIN    | REALCASE      | Combined Design and Performance Certification \| Real Case or Regular VPA | 152           | New             | No    |