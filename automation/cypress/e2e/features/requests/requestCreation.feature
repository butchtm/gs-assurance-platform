@group3
Feature: /create-request


    Scenario Outline: Create request page loads for '<username>' on '<projectClass>'    
        Given I login as "<username>"
        When "<username>" click "projects" from the navbar successfully
        And I click "projects search bar" from projects page
        And I type "automation test (<projectClass>)" into "projects search bar"
        And I click "View" for my "automation test (<projectClass>)" with "<projectStatus>" status
        And I select the "requests" nav button in my project 
        And I select "create new request" from the request view
        Then The "/create-request" page is loaded
        Examples:
            | username    | projectClass       | projectStatus   | 
            | PD          | standalone project | New             |
            | ADMIN       | standalone project | New             |
            | ARM TEAM    | standalone project | New             |

            | PD          | POA                | New             |
            | ADMIN       | POA                | New             |
            | ARM TEAM    | POA                | New             |

            | PD          | REGULARCASE        | New             |
            | ADMIN       | REGULARCASE        | New             |
            | ARM TEAM    | REGULARCASE        | New             |

            | PD          | REALCASE           | New             |
            | ADMIN       | REALCASE           | New             |
            | ARM TEAM    | REALCASE           | New             |

    #Standalone project
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

        When I click "estimated emission reduction/removal" from request creation page
        And I type "<emissionCheck>" into "emission reduction or removal"
        Then Field contains "<emissionCheck>" in "value field"

        When I click "next" from project creation page 
        Then I am taken to page "2"

        When I click "finish" from project creation page 
        Then Success message appears
        Examples:
            | username | projectClass       | requestType                                              | emissionCheck | projectStatus   | 
            | PD       | standalone project | Preliminary Review                                       | 152           | New             |
            | PD       | standalone project | Design Certification \| Project                          | 152           | New             |
            | ARM TEAM | standalone project | Inclusion \| Regular Case VPA                            | 152           | New             |
            | PD       | standalone project | Manage Information \| Project or PoA or VPA              | 152           | New             |
            | PD       | standalone project | Decertification \| Project                               | 152           | New             |
            | PD       | standalone project | Annual Report                                            | 152           | New             |

            | PD       | standalone project | Preliminary Review                                       | 152           | Listed          |
    
    #requests which have batching option, and CANT ignore batching question
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

        When I click "estimated emission reduction/removal" from request creation page
        And I type "<emissionCheck>" into "emission reduction or removal"
        Then Field contains "<emissionCheck>" in "value field"

        When I click "next" from project creation page 
        Then I am taken to page "2"

        When I click "finish" from project creation page 
        Then Success message appears
        
        Examples:
            | username | projectClass  | requestType                                              | emissionCheck | batch | projectStatus   | 
            | PD       | POA           | Preliminary Review                                       | 1,000         | No    | New             |
            | ADMIN    | POA           | Generic                                                  | 1,000         | No    | New             |

            | PD       | REGULARCASE   | Preliminary Review                                       | 1,000         | No    | New             |
            | ADMIN    | REGULARCASE   | Generic                                                  | 1,000         | No    | New             |
            | ARM TEAM | REGULARCASE   | Annual Report                                            | 1,000         | No    | New             |
            | ARM TEAM | REGULARCASE   | Inclusion \| Regular Case VPA                            | 1,000         | No    | New             |

            | PD       | REALCASE      | Preliminary Review                                       | 152           | No    | New             |
            | ADMIN    | REALCASE      | Generic                                                  | 152           | No    | New             |
            | ARM TEAM | REALCASE      | Annual Report                                            | 152           | No    | New             |
            | ARM TEAM | REALCASE      | Inclusion \| Regular Case VPA                            | 152           | No    | New             |

    
            #Listed projects / kind of not needed due to request submissions
            | PD       | POA           | Preliminary Review                                       | 1,000         | No    | Listed          |
            | ADMIN    | POA           | Generic                                                  | 1,000         | No    | Listed          |

            | PD       | REGULARCASE   | Preliminary Review                                       | 1,000         | No    | Listed          |
            | ADMIN    | REGULARCASE   | Generic                                                  | 1,000         | No    | Listed          |

            | PD       | REALCASE      | Preliminary Review                                       | 152           | No    | Listed          |
            | ADMIN    | REALCASE      | Generic                                                  | 152           | No    | Listed          |

    #requests which don't require batching at all, and can ignore batching question
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
        
        When I click "credit period id" from request creation page
        And I click "period" from dropdown during request creation
        Then Field contains "Period 1" in "div element"

        When I click "estimated emission reduction/removal" from request creation page
        And I type "<emissionCheck>" into "emission reduction or removal"
        Then Field contains "<emissionCheck>" in "value field"

        When I click "next" from project creation page 
        Then I am taken to page "2"

        When I click "finish" from project creation page 
        Then Success message appears
        
        Examples:
            | username | projectClass  | requestType                                              | emissionCheck | projectStatus   | 
            | PD       | POA           | Design Certification \| Project                          | 1,000         | New             |
            | ADMIN    | POA           | Combined Design and Performance Certification \| Project | 1,000         | New             |
            | PD       | POA           | Manage Information \| Project or PoA or VPA              | 1,000         | New             |
            | ADMIN    | POA           | Performance Certification \| Project                     | 1,000         | New             |
            | ARM TEAM | POA           | Decertification \| Project                               | 1,000         | New             |
            | ARM TEAM | POA           | Annual Report                                            | 1,000         | New             |
            | ARM TEAM | POA           | Inclusion \| Regular Case VPA                            | 1,000         | New             |

            | PD       | REGULARCASE   | Design Certification \| Project                          | 1,000         | New             |
            | PD       | REGULARCASE   | Manage Information \| Project or PoA or VPA              | 1,000         | New             |
            | ARM TEAM | REGULARCASE   | Decertification \| Project                               | 1,000         | New             |

            | PD       | REALCASE      | Design Certification \| Project                          | 152           | New             |
            | PD       | REALCASE      | Manage Information \| Project or PoA or VPA              | 152           | New             |
            | ARM TEAM | REALCASE      | Decertification \| Project                               | 152           | New             |

    #other or same requests that we want to batch
    Scenario Outline: '<username>' creates batched '<requestType>' request using '<batchType>' on a '<projectClass>' project
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

        When I click "next" from project creation page
        And I click "create batch request?" from request creation page
        And I click "<batch>" from dropdown during request creation
        Then Field contains "<batch>" in "div element" 

        When I click "batch request type" from request creation page
        And I click "<batchType>" from dropdown during request creation
        Then Field contains "<batchType>" in "div element" 

        When I click "choose project to batch" from request creation page
        And I add "<numberInBatch>" "automation test (<addProject>)" to field
        Then Field contains "automation test (<projectClass>)" in "div element" 
        And Field contains "automation test (<addProject>)" in "div element" 

        When I click "next" from project creation page 
        Then I am taken to page "2"

        When I click "next" from project creation page 
        Then I am taken to page "3"
        
        When I click "credit period id (batch)" from request creation page
        And I click "period" from dropdown during request creation
        Then Field contains "Period 1" in "div element"

        When I click "estimated emission reduction/removal" from request creation page
        And I type "<emission>" into "emission reduction or removal"
        Then Field contains "<emission>" in "value field"

        When I click "next" from project creation page 
        Then I am taken to page "4"         
        
        Given I repeat steps for "<numberInBatch>" additional project in batch
    
        When I click "finish" from project creation page 
        Then Success message appears

        Examples:
            | username | projectClass | addProject   | requestType               | emission  | batch | projectStatus | batchType                          | numberInBatch |  
            | PD       | POA          | REALCASE     | Preliminary Review        | 1,000     | Yes   | New           | PoA + 1 Real Case                  | 1             |
            | ADMIN    | POA          | REALCASE     | Generic                   | 1,000     | Yes   | New           | PoA + 1 Real Case                  | 1             |

            | PD       | POA          | REALCASE     | Preliminary Review        | 1,000     | Yes   | New           | PoA + 1 or more Real Case          | 1             |
            | ADMIN    | POA          | REALCASE     | Generic                   | 1,000     | Yes   | New           | PoA + 1 or more Real Case          | 2             |

            | PD       | REALCASE     | REGULARCASE  | Preliminary Review        | 1,000     | Yes   | New           | Real Case + 1 or more Regular Case | 1             |
            | ADMIN    | REALCASE     | REGULARCASE  | Generic                   | 1,000     | Yes   | New           | Real Case + 1 or more Regular Case | 2             |

            | PD       | REGULARCASE  | REGULARCASE  | Preliminary Review        | 1,000     | Yes   | New           | 2 or more Regular Case             | 2             |
            | ADMIN    | REGULARCASE  | REGULARCASE  | Generic                   | 1,000     | Yes   | New           | 2 or more Regular Case             | 2             |


    #unique to design change request type standalone version
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

        When I click "estimated emission reduction/removal" from request creation page
        And I type "<emissionCheck>" into "emission reduction or removal"
        Then Field contains "<emissionCheck>" in "value field"

        When I click "next" from project creation page 
        Then I am taken to page "2"

        When I click "add methodology button" from request creation page
        And I click "2nd project methodology field" from request creation page
        And I click "ACM0002" from dropdown during request creation
        Then Field contains "ACM0002" in "value field"

        When I click "2nd methodology version" from request creation page
        And I type "1" into "2nd methodology version"
        Then Field contains "01" in "value field"

        When I click "next" from project creation page 
        Then I am taken to page "3"        

        When I click "finish" from project creation page 
        Then Success message appears
        Examples:
            | username | projectClass       | requestType               | emissionCheck | projectStatus   | 
            | PD       | standalone project | Design Change \| With VVB | 152           | New             |

    #unique to design change request type regular version
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
        And I click "No" from dropdown during request creation
        Then Field contains "No" in "div element" 

        When I click "credit period id" from request creation page
        And I click "period" from dropdown during request creation
        Then Field contains "Period 1" in "div element"

        When I click "estimated emission reduction/removal" from request creation page
        And I type "<emissionCheck>" into "emission reduction or removal"
        Then Field contains "<emissionCheck>" in "value field"

        When I click "next" from project creation page 
        Then I am taken to page "2"

        When I click "add methodology button" from request creation page
        And I click "2nd project methodology field" from request creation page
        And I click "ACM0002" from dropdown during request creation
        Then Field contains "ACM0002" in "value field"

        When I click "2nd methodology version" from request creation page
        And I type "1" into "2nd methodology version"
        Then Field contains "01" in "value field"

        When I click "next" from project creation page 
        Then I am taken to page "3"

        When I click "finish" from project creation page 
        Then Success message appears
        
        Examples:
            | username | projectClass       | requestType                | emissionCheck | projectStatus   | 
            | PD       | POA                | Design Change \| With VVB  | 1,000         | New             |
            | PD       | REGULARCASE        | Design Change \| With VVB  | 1,000         | New             |
            | PD       | REALCASE           | Design Change \| With VVB  | 12,432        | New             |
    
    #unique to design change request type batched version
    Scenario Outline: '<username>' creates batched Design Change requests on '<projectClass>' project
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

        When I click "batch request type" from request creation page
        And I click "<batchType>" from dropdown during request creation
        Then Field contains "<batchType>" in "div element" 

        When I click "choose project to batch" from request creation page
        And I add "<numberInBatch>" "automation test (<addProject>)" to field
        Then Field contains "automation test (<projectClass>)" in "div element" 
        And Field contains "automation test (<addProject>)" in "div element" 

        When I click "next" from project creation page 
        Then I am taken to page "2"

        When I click "next" from project creation page 
        Then I am taken to page "3"
        
        When I click "credit period id (batch)" from request creation page
        And I click "period" from dropdown during request creation
        Then Field contains "Period 1" in "div element"

        When I click "add methodology button" from request creation page
        And I click "2nd project methodology field" from request creation page
        And I click "ACM0002" from dropdown during request creation
        Then Field contains "ACM0002" in "value field"

        When I click "2nd methodology version" from request creation page
        And I type "1" into "2nd methodology version"
        Then Field contains "01" in "value field"

        When I click "estimated emission reduction/removal" from request creation page
        And I type "<emission>" into "emission reduction or removal"
        Then Field contains "<emission>" in "value field"

        When I click "next" from project creation page 
        Then I am taken to page "4"         
        
        Given I repeat on design change "<numberInBatch>" additional times for each project in "<batchType>"
        And I click "finish" from project creation page 
        Then Success message appears

        Examples:
            | username | projectClass | addProject   | requestType               | emission  | batch | projectStatus | batchType                          | numberInBatch |  
            | PD       | POA          | REALCASE     | Design Change \| With VVB | 1,000     | Yes   | New           | PoA + 1 Real Case                  | 1             |
            | PD       | POA          | REALCASE     | Design Change \| With VVB | 1,000     | Yes   | New           | PoA + 1 or more Real Case          | 3             |
            | PD       | REALCASE     | REGULARCASE  | Design Change \| With VVB | 1,000     | Yes   | New           | Real Case + 1 or more Regular Case | 3             |
            | PD       | REGULARCASE  | REGULARCASE  | Design Change \| With VVB | 1,000     | Yes   | New           | 2 or more Regular Case             | 2             |
