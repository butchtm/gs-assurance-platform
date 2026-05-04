Feature: /projects/GS#### page 

    #Todo test cases:
        #Require projects with relationships and transition = yes
            #View transition information
            #View Project VPAs - NEED TO SELECT A PROJECT STILL
        #Request action tests:
            #Upload doc to request
            #Update request
            #Cancel request etc

    @group4A     
    Scenario Outline: '<projectClass>' loads for '<username>' 
        Given I login as "<username>"
        When "<username>" click "projects" from the navbar successfully
        And I search a "<projectStatus>" "automation test (<projectClass>)" GSID
        And I select "<projectStatus>" "automation test (<projectClass>)" GSID from results as "<username>"

        Then Project Characteristics can be seen 
        Examples:
            | username    | projectClass          | projectStatus   | 
            | PD          | standalone project    | Listed          |
            | VVB         | standalone project    | Listed          |
            | TAC         | standalone project    | Listed          |
            | ADMIN       | standalone project    | Listed          |
            | REVIEWER    | standalone project    | Listed          |
            | ARM TEAM    | standalone project    | Listed          |
            | FINANCE     | standalone project    | Listed          |

            | PD          | POA                   | New             |
            | VVB         | POA                   | Listed          |
            | TAC         | POA                   | New             |
            | ADMIN       | POA                   | New             |
            | REVIEWER    | POA                   | Listed          |
            | ARM TEAM    | POA                   | New             |
            | FINANCE     | POA                   | New             |

            | PD          | REGULARCASE           | New             |
            | VVB         | REGULARCASE           | Listed          |
            | TAC         | REGULARCASE           | New             |
            | ADMIN       | REGULARCASE           | New             |
            | REVIEWER    | REGULARCASE           | Listed          |
            | ARM TEAM    | REGULARCASE           | New             |
            | FINANCE     | REGULARCASE           | New             |

            | PD          | REALCASE              | New             |
            | VVB         | REALCASE              | Listed          |
            | TAC         | REALCASE              | New             |
            | ADMIN       | REALCASE              | New             |
            | REVIEWER    | REALCASE              | Listed          |
            | ARM TEAM    | REALCASE              | New             |
            | FINANCE     | REALCASE              | New             |

    @group4B
    Scenario Outline: View requests list for '<username>' on '<projectClass>'
        Given I login as "<username>"
        When "<username>" click "projects" from the navbar successfully

        And I search a "<projectStatus>" "automation test (<projectClass>)" GSID
        And I select "<projectStatus>" "automation test (<projectClass>)" GSID from results as "<username>"

        Then Project Characteristics can be seen

        When I select the "requests" nav button in my project 
        Then I can see list of requests or area for requests
        Examples:
            | username  | projectClass        | projectStatus   |
            | PD        | standalone project  | Listed          |
            | VVB       | standalone project  | Listed          |
            | TAC       | standalone project  | Listed          |
            | ADMIN     | standalone project  | Listed          |
            | REVIEWER  | standalone project  | Listed          |
            | ARM TEAM  | standalone project  | Listed          |
            | FINANCE   | standalone project  | Listed          |

            | PD        | POA                 | Listed          |
            | VVB       | POA                 | Listed          |
            | TAC       | POA                 | Listed          |
            | ADMIN     | POA                 | Listed          |
            | REVIEWER  | POA                 | Listed          |
            | ARM TEAM  | POA                 | Listed          |
            | FINANCE   | POA                 | Listed          |

            | PD        | REGULARCASE         | Listed          |
            | VVB       | REGULARCASE         | Listed          |
            | TAC       | REGULARCASE         | Listed          |
            | ADMIN     | REGULARCASE         | Listed          |
            | REVIEWER  | REGULARCASE         | Listed          |
            | ARM TEAM  | REGULARCASE         | Listed          |
            | FINANCE   | REGULARCASE         | Listed          |

            | PD        | REALCASE            | Listed          |
            | VVB       | REALCASE            | Listed          |
            | TAC       | REALCASE            | Listed          |
            | ADMIN     | REALCASE            | Listed          |
            | REVIEWER  | REALCASE            | Listed          |
            | ARM TEAM  | REALCASE            | Listed          |
            | FINANCE   | REALCASE            | Listed          |

    @group4C
    Scenario Outline: Select request for '<username>' on '<projectClass>'
        Given I login as "<username>"
        When "<username>" click "projects" from the navbar successfully

        And I search a "<projectStatus>" "automation test (<projectClass>)" GSID
        And I select "<projectStatus>" "automation test (<projectClass>)" GSID from results as "<username>"
        Then Project Characteristics can be seen
        
        Given I select the "requests" nav button in my project 
        When I can see list of requests or area for requests
        And I select "<requestType>" from the request view
        Then The request has expanded to reveal actions
        Examples:
            | username    | projectClass           | requestType           | projectStatus   | 
            | PD          | standalone project     | preliminary review    | Listed          |
            | VVB         | standalone project     | preliminary review    | Listed          |
            | TAC         | standalone project     | preliminary review    | Listed          |
            | ADMIN       | standalone project     | preliminary review    | Listed          |
            | REVIEWER    | standalone project     | preliminary review    | Listed          |
            | ARM TEAM    | standalone project     | preliminary review    | Listed          |
            | FINANCE     | standalone project     | preliminary review    | Listed          |

            | PD          | POA                    | preliminary review    | Listed          |
            | VVB         | POA                    | preliminary review    | Listed          |
            | TAC         | POA                    | preliminary review    | Listed          |
            | ADMIN       | POA                    | preliminary review    | Listed          |
            | REVIEWER    | POA                    | preliminary review    | Listed          |
            | ARM TEAM    | POA                    | preliminary review    | Listed          |
            | FINANCE     | POA                    | preliminary review    | Listed          |

            | PD          | REGULARCASE            | preliminary review    | Listed          |
            | VVB         | REGULARCASE            | preliminary review    | Listed          |
            | TAC         | REGULARCASE            | preliminary review    | Listed          |
            | ADMIN       | REGULARCASE            | preliminary review    | Listed          |
            | REVIEWER    | REGULARCASE            | preliminary review    | Listed          |
            | ARM TEAM    | REGULARCASE            | preliminary review    | Listed          |
            | FINANCE     | REGULARCASE            | preliminary review    | Listed          |

            | PD          | REALCASE               | preliminary review    | Listed          |
            | VVB         | REALCASE               | preliminary review    | Listed          |
            | TAC         | REALCASE               | preliminary review    | Listed          |
            | ADMIN       | REALCASE               | preliminary review    | Listed          |
            | REVIEWER    | REALCASE               | preliminary review    | Listed          |
            | ARM TEAM    | REALCASE               | preliminary review    | Listed          |
            | FINANCE     | REALCASE               | preliminary review    | Listed          |

    @group4D
    Scenario Outline: '<username>' uses project VPAs page to search for all projects in a structure
        Given I login as "<username>"
        When "<username>" click "projects" from the navbar successfully

        And I search a "<projectStatus>" "automation test (<projectClass>)" GSID
        And I select "<projectStatus>" "automation test (<projectClass>)" GSID from results as "<username>"
        Then Project Characteristics can be seen
        
        Given I select the "project vpas" nav button in my project
        And I select and expand the whole list of available projects
        Then I can see my "POA" id in my vpas list
        And I can see my "REALCASE" id in my vpas list
        And I can see my "REGULARCASE" id in my vpas list
    Examples:
            | username    | projectClass  | projectStatus   | 
            | PD          | POA           | Listed          |
            | VVB         | POA           | Listed          |
            | TAC         | POA           | Listed          |
            | ADMIN       | POA           | Listed          |
            | REVIEWER    | POA           | Listed          |
            | ARM TEAM    | POA           | Listed          |
            | FINANCE     | POA           | Listed          |
    
    @group4D
    Scenario Outline: '<username>' uses project VPAs page to search for a specific project in a structure
        Given I login as "<username>"
        When "<username>" click "projects" from the navbar successfully

        And I search a "<projectStatus>" "automation test (<projectClass>)" GSID
        And I select "<projectStatus>" "automation test (<projectClass>)" GSID from results as "<username>"
        Then Project Characteristics can be seen
        
        Given I select the "project vpas" nav button in my project
        And I select the search bar on project vpas page
        And I type my regular case GSID into vpas search bar
        Then I can see my "REGULARCASE" id in my vpas list

    Examples:
            | username    | projectClass  | projectStatus   | 
            | PD          | POA           | Listed          |
            | VVB         | POA           | Listed          |
            | TAC         | POA           | Listed          |
            | ADMIN       | POA           | Listed          |
            | REVIEWER    | POA           | Listed          |
            | ARM TEAM    | POA           | Listed          |
            | FINANCE     | POA           | Listed          |