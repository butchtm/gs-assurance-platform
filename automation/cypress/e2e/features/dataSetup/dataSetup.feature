@groupDS
Feature: Data setup tasks & project creation tests


    Scenario: Cleardown all existing test data
        Given I delete all existing projects
        When I visit assurance platform
        And "ADMIN" click "projects" from the navbar successfully
        Then There are no projects shown on the projects page


    Scenario: Create standalone project as PD
        Given I login as "PD"
        And "PD" click "projects" from the navbar successfully
        And I click "create" from projects page
        And Run number increased

        When I click "project classification" from project creation page
        And I click "standalone project" from dropdown 
        Then Field contains "Standalone Project" in "value field"

        When I click "project title" from project creation page
        And I type "automation test (standalone project)" into "project title"
        Then Field contains "automation test (standalone project)" in "value field"

        When I click "next" from project creation page 
        Then I am taken to page "2"

        #####

        When I click "project description" from project creation page
        And I type "my test standalone project description" into "project description"
        Then Field contains "my test standalone project description" in "text area"
        
        When I click "project location" from project creation page
        And I type "United kingdom" into "project location"
        Then Field contains "United" in "div element"

        #not sure why this field stopped working for automation        
        When I click "project scale" from project creation page
        And I click "micro scale" from dropdown
        Then Field contains "Micro Scale" in "div element"

        When I click "project type(s)" from project creation page
        And I click "agriculture" from dropdown
        Then Field contains "Agriculture" in "div element"

        When I click "registration type" from project creation page
        And I click "regular" from dropdown
        Then Field contains "Regular" in "div element"

        When I click "certified products" from project creation page
        And I click "carbon" from dropdown
        Then Field contains "Carbon" in "div element"

        When I click "carbon stream" from project creation page
        And I click "gs ver" from dropdown
        Then Field contains "GS VER" in "div element"

        #####

        When I click "next" from project creation page 
        Then I am taken to page "3"
        
        When I click "project methodologies" from project creation page
        And I click "acm0001 flaring" from dropdown
        Then Field contains "ACM0001 Flaring" in "value field"
        
        When I click "version" from project creation page
        Then Field contains "1" in "value field"

        #####

        When I click "next" from project creation page 
        Then I am taken to page "4"

        When I click "transition project" from project creation page
        And I click "no" from dropdown
        Then Field contains "No" in "div element"

        When I click "next" from project creation page 
        Then I am taken to page "5"

        When I click "emission reduction removal" from project creation page
        And I type "12345" into "emission reduction removal"
        Then Field contains "12,345" in "value field"

        When I click "sustainable development goals" from project creation page
        And I click "climate action" from dropdown
        Then Field contains "13 - Climate Action" in "div element"

        When I click "fee schedule" from project creation page
        And I click "cash per credit" from dropdown
        Then Field contains "Cash per Credit" in "div element"

        When I click "notes" from project creation page
        And I type "A test note for my project" into "notes"
        Then Field contains "A test note for my project" in "text area"

        When I click "next" from project creation page 
        Then I am taken to page "6"

        #Omitting final moments of test to avoid excessive creation of projects
        #When I click "finish" from project creation page 
        Then We finish and see a success message and store "New Standalone" details


    Scenario: Create POA project as PD
        Given I login as "PD"
        And "PD" click "projects" from the navbar successfully
        And I click "create" from projects page

        When I click "project classification" from project creation page
        And I click "poa project" from dropdown 
        Then Field contains "Programme of Activities" in "value field"

        When I click "project title" from project creation page
        And I type "automation test (POA)" into "project title"
        Then Field contains "automation test (POA)" in "value field"

        When I click "next" from project creation page 
        Then I am taken to page "2"

        #####

        When I click "project description" from project creation page
        And I type "my test poa project description" into "project description"
        Then Field contains "my test poa project description" in "text area"
        
        When I click "project location" from project creation page
        And I type "United kingdom" into "project location"
        Then Field contains "United" in "div element"

        When I click "project scale" from project creation page
        And I click "micro scale" from dropdown
        Then Field contains "Micro Scale" in "div element"

        When I click "project type(s)" from project creation page
        And I click "agriculture" from dropdown
        Then Field contains "Agriculture" in "div element"

        When I click "registration type" from project creation page
        And I click "regular" from dropdown
        Then Field contains "Regular" in "div element"

        When I click "certified products" from project creation page
        And I click "carbon" from dropdown
        Then Field contains "Carbon" in "div element"

        When I click "carbon stream" from project creation page
        And I click "gs ver" from dropdown
        Then Field contains "GS VER" in "div element"

        #####

        When I click "next" from project creation page 
        Then I am taken to page "3"
        
        When I click "project methodologies" from project creation page
        And I click "acm0001 flaring" from dropdown
        Then Field contains "ACM0001 Flaring" in "value field"

        When I click "version" from project creation page
        Then Field contains "1" in "value field"

        #####

        When I click "next" from project creation page 
        Then I am taken to page "4"

        When I click "transition project" from project creation page
        And I click "no" from dropdown
        Then Field contains "No" in "div element"

        When I click "next" from project creation page 
        Then I am taken to page "5"

        When I click "emission reduction removal" from project creation page
        And I type "12345" into "emission reduction removal"
        Then Field contains "12,345" in "value field"

        When I click "sustainable development goals" from project creation page
        And I click "climate action" from dropdown
        Then Field contains "13 - Climate Action" in "div element"

        When I click "fee schedule" from project creation page
        And I click "cash per credit" from dropdown
        Then Field contains "Cash per Credit" in "div element"

        When I click "notes" from project creation page
        And I type "A test note for my project" into "notes"
        Then Field contains "A test note for my project" in "text area"

        When I click "next" from project creation page 
        Then I am taken to page "6"

        #Omitting final moments of test to avoid excessive creation of projects
        #When I click "finish" from project creation page 
        Then We finish and see a success message and store "New POA" details

    Scenario: Create Real Case VPA under POA project as PD
        Given I login as "PD"
        And "PD" click "projects" from the navbar successfully
        And I click "create" from projects page

        When I click "project classification" from project creation page
        And I click "real project" from dropdown 
        Then Field contains "Real Case VPA" in "value field"
        And Field contains "Programme of Activities" in "div element"

        When I click "programme of activities" from project creation page 
        And I type "automation test (POA)" into "programme of activities"
        And I click "automation test (POA)" from dropdown
        Then Field contains "automation test (POA)" in "value field"

        When I click "project title" from project creation page
        And I type "automation test (REALCASE)" into "project title"
        Then Field contains "automation test (REALCASE)" in "value field"

        When I click "next" from project creation page 
        Then I am taken to page "2"

        #####

        When I click "project description" from project creation page
        And I type "my test real case vpa project description" into "project description"
        Then Field contains "my test real case vpa project description" in "text area"

        #When I click "project location" from project creation page
        #And I type "United kingdom" into "project location"
        #Then Field contains "United" in "div element"

        When I click "project scale" from project creation page
        And I click "micro scale" from dropdown
        Then Field contains "Micro Scale" in "div element"

        #When I click "project type(s)" from project creation page
        #And I click "agriculture" from dropdown
        #Then Field contains "Agriculture" in "div element"

        When I click "registration type" from project creation page
        And I click "regular" from dropdown
        Then Field contains "Regular" in "div element"

        #When I click "certified products" from project creation page
        #And I click "carbon" from dropdown
        #Then Field contains "Carbon" in "div element"

        When I click "carbon stream" from project creation page
        And I click "gs ver" from dropdown
        Then Field contains "GS VER" in "div element"

        #####
         
        When I click "next" from project creation page 
        Then I am taken to page "3"
        
        When I click "project methodologies" from project creation page
        And I click "acm0001 flaring" from dropdown
        Then Field contains "ACM0001 Flaring" in "value field"

        When I click "version" from project creation page
        Then Field contains "1" in "value field"
        
        #####

        When I click "next" from project creation page 
        Then I am taken to page "4"

        When I click "transition project" from project creation page
        And I click "no" from dropdown
        Then Field contains "No" in "div element"

        When I click "next" from project creation page 
        Then I am taken to page "5"

        When I click "emission reduction removal" from project creation page
        And I type "12345" into "emission reduction removal"
        Then Field contains "12,345" in "value field"

        When I click "fee schedule" from project creation page
        And I click "cash per credit" from dropdown
        Then Field contains "Cash per Credit" in "div element"

        When I click "notes" from project creation page
        And I type "A test note for my project" into "notes"
        Then Field contains "A test note for my project" in "text area"

        When I click "next" from project creation page 
        Then I am taken to page "6"

        #Omitting final moments of test to avoid excessive creation of projects
        #When I click "finish" from project creation page 
        Then We finish and see a success message and store "New Real" details


    Scenario: Create Regular Case VPA under Real Case project as PD
        Given I login as "PD"
        And "PD" click "projects" from the navbar successfully
        And I click "create" from projects page

        When I click "project classification" from project creation page
        And I click "regular project" from dropdown 
        Then Field contains "Regular VPA" in "value field"
        And Field contains "Programme of Activities" in "div element"
        And Field contains "Real Case VPA" in "div element"

        When I click "programme of activities" from project creation page 
        And I type "automation test (POA)" into "programme of activities"
        And I click "automation test (POA)" from dropdown
        Then Field contains "automation test (POA)" in "value field"

        When I click "real case vpa" from project creation page 
        And I type "automation test (REALCASE)" into "real case vpa"
        And I click "automation test (REALCASE)" from dropdown
        Then Field contains "automation test (REALCASE)" in "value field"

        When I click "project title" from project creation page
        And I type "automation test (REGULARCASE)" into "project title"
        Then Field contains "automation test (REGULARCASE)" in "value field"
        
        When I click "next" from project creation page 
        Then I am taken to page "2"

        #####

        When I click "project description" from project creation page
        And I type "my test regular case vpa project description" into "project description"
        Then Field contains "my test regular case vpa project description" in "text area"

        #When I click "project location" from project creation page
        #And I type "United kingdom" into "project location"
        #Then Field contains "United" in "div element"

        When I click "project scale" from project creation page
        And I click "micro scale" from dropdown
        Then Field contains "Micro Scale" in "div element"

        #When I click "project type(s)" from project creation page
        #And I click "agriculture" from dropdown
        #Then Field contains "Agriculture" in "div element"

        When I click "registration type" from project creation page
        And I click "regular" from dropdown
        Then Field contains "Regular" in "div element"

        #When I click "certified products" from project creation page
        #And I click "carbon" from dropdown
        #Then Field contains "Carbon" in "div element"

        When I click "carbon stream" from project creation page
        And I click "gs ver" from dropdown
        Then Field contains "GS VER" in "div element"

        ####

        When I click "next" from project creation page 
        Then I am taken to page "3"
        
        When I click "project methodologies" from project creation page
        And I click "acm0001 flaring" from dropdown
        Then Field contains "ACM0001 Flaring" in "value field"

        When I click "version" from project creation page
        Then Field contains "1" in "value field"

        ####

        When I click "next" from project creation page 
        Then I am taken to page "4"

        When I click "transition project" from project creation page
        And I click "no" from dropdown
        Then Field contains "No" in "div element"

        When I click "next" from project creation page 
        Then I am taken to page "5"

        When I click "emission reduction removal" from project creation page
        And I type "12345" into "emission reduction removal"
        Then Field contains "12,345" in "value field"

        When I click "fee schedule" from project creation page
        And I click "cash per credit" from dropdown
        Then Field contains "Cash per Credit" in "div element"

        When I click "notes" from project creation page
        And I type "A test note for my project" into "notes"
        Then Field contains "A test note for my project" in "text area"

        When I click "next" from project creation page 
        Then I am taken to page "6"

        #Omitting final moments of test to avoid excessive creation of projects
        #When I click "finish" from project creation page 
        Then We finish and see a success message and store "New Regular" details

    
    Scenario Outline: User adds more <type> projects to 1st project structure
        Given I login as "PD"
        And "PD" click "projects" from the navbar successfully
        And I click "create" from projects page
        Then I repeat adding "<number>" "<type>" projects

        Examples:
            | type        | number | 
            | REALCASE    | 2      |
            | REGULARCASE | 2      |
    
    Scenario Outline: User creates <type> and edits project status to listed for 2nd structure
        Given I login as "ADMIN"
        And I click "projects" from the navbar
        And I click "create" from projects page
        Then I add "<number>" "<type>" projects

        Examples:
            | type               | number |
            | standalone project | 1      |
            | POA                | 1      |
            | REALCASE           | 3      |
            | REGULARCASE        | 3      |