@group18
Feature: tests relating to the project risk feature

    Scenario Outline: <username> can open risk update modal
        Given I login as "<username>"
        When I click "projects" from the navbar
        And I select "<projectStatus>" "automation test (POA)" GSID from results as "<username>"
        Then project information can be seen 

        #
        When I select the "risk" nav button in my project
        And I select the update risk button
        Then I can see my update project risk modal

    Examples:
            | username    | projectStatus   | 
            | ADMIN       | Listed          |
            | ARM TEAM    | New             |

    Scenario Outline: <username> updates the risk summary
        Given I login as "<username>"
        When I click "projects" from the navbar
        And I select "<projectStatus>" "automation test (POA)" GSID from results as "<username>"
        Then project information can be seen 

        #
        When I select the "risk" nav button in my project
        And I select the update risk button
        And I type my risk information into the summary field
        Then I save my risk update by clicking the update button
        And I can see my description in the risk history

    Examples:
            | username    | projectStatus   | 
            | ADMIN       | Listed          |
            | ARM TEAM    | New             |

    Scenario Outline: <username> checks the history table for recent update
        Given I login as "<username>"
        When I click "projects" from the navbar
        And I select "<projectStatus>" "automation test (POA)" GSID from results as "<username>"
        Then project information can be seen 

        When I select the "risk" nav button in my project
        Then I can see my details "<risk>" "<user>" "<description>" in the risk table

    Examples:
            | username    | risk      | user      | description               | projectStatus   | 
            | ADMIN       | No Risk   | auto test | this project is high risk | Listed          |
            | ARM TEAM    | No Risk   | auto test | this project is high risk | New             |

    Scenario Outline: <username> turns on the risk flag
        Given I login as "<username>"
        When I click "projects" from the navbar
        And I select "<projectStatus>" "automation test (POA)" GSID from results as "<username>"
        Then project information can be seen 

        #
        When I select the "risk" nav button in my project
        And I select the update risk button
        And I select the risk toggle button
        Then I save my risk update by clicking the update button
        And I can see my details "<risk>" "<user>" "<description>" in the risk table
        And I can see high risk flag at top of page

    Examples:
            | username    | risk      | user      | description               | projectStatus   | 
            | ADMIN       | High Risk | auto test | this project is high risk | Listed          |
            | ARM TEAM    | High Risk | auto test | this project is high risk | New             |

    Scenario Outline: <username> selects risk warning from top of page to read content
        Given I login as "<username>"
        When I click "projects" from the navbar
        And I select "<projectStatus>" "automation test (POA)" GSID from results as "<username>"
        Then project information can be seen 

        #
        Given I can see high risk flag at top of page
        When I select the high risk warning from top of my project
        Then I can see my risk summary "<description>" in the risk banner


    Examples:
            | username    | risk      | user      | description               | projectStatus   | 
            | ADMIN       | High Risk | auto test | this project is high risk | Listed          |
            | ARM TEAM    | High Risk | auto test | this project is high risk | New             |

    Scenario Outline: <username> selects risk warning from top of page to update risk content
        Given I login as "<username>"
        When I click "projects" from the navbar
        And I select "<projectStatus>" "automation test (POA)" GSID from results as "<username>"
        Then project information can be seen 

        #
        Given I can see high risk flag at top of page
        When I select the high risk warning from top of my project
        Then I can see my risk summary "<description>" in the risk banner

        When I select the update risk icon from high risk banner    
        And I can see my update project risk modal 
        And I update the risk summary to include "<updatedDescription>"
        Then I save my risk update by clicking the update button
        And I can see my risk summary "<updatedDescription>" in the risk banner


    Examples:
            | username    | risk      | user      | description               | projectStatus   | updatedDescription         |
            | ADMIN       | High Risk | auto test | this project is high risk | Listed          | project is still high risk |
            | ARM TEAM    | High Risk | auto test | this project is high risk | New             | project is still high risk |
    
    Scenario Outline: <username> selects risk warning from top of request creation
        Given I login as "<username>"
        When I click "projects" from the navbar
        And I select "<projectStatus>" "automation test (POA)" GSID from results as "<username>"
        Then project information can be seen 

        #
        When I select the "requests" nav button in my project
        And I select "create new request" from the request view
        And I select the high risk warning from top of request creation wizard
        Then I can see my risk summary "<updatedDescription>" in the risk banner

    Examples:
            | username    | risk      | user      | description               | projectStatus   | updatedDescription         |
            | ADMIN       | High Risk | auto test | this project is high risk | Listed          | project is still high risk |
            | ARM TEAM    | High Risk | auto test | this project is high risk | New             | project is still high risk |

    Scenario Outline: <username> turns off the risk flag
        Given I login as "<username>"
        When I click "projects" from the navbar
        And I select "<projectStatus>" "automation test (POA)" GSID from results as "<username>"
        Then project information can be seen 

        #
        When I select the "risk" nav button in my project
        And I select the update risk button
        And I select the risk toggle button
        Then I save my risk update by clicking the update button
        And I can see my details "<risk>" "<user>" "<description>" in the risk table

    Examples:
            | username    | risk    | user      | description               | projectStatus   | 
            | ADMIN       | No Risk | auto test | this project is high risk | Listed          |
            | ARM TEAM    | No Risk | auto test | this project is high risk | New             |
