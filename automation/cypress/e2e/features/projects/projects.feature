Feature: /projects page 

    #Test cases:
        #Background: Projects page loads 
        #Scenario: Projects page loads
        #Scenario: Search a project
        #Open selected project
        #Open edit page for selected project 

    @group2A
    Scenario Outline: Projects page loads for '<username>'

        Given I login as "<username>"
        When "<username>" click "projects" from the navbar successfully
        Then The "/projects" page is loaded
        Examples:
            | username    | 
            | REVIEWER    |
            | VVB         |
            | PD          | 
            | TAC         |
            | ADMIN       |
            | ARM TEAM    |
            | FINANCE     |

    @group2A
    Scenario Outline: Search a project as '<username>'
        Given I login as "<username>"
        When "<username>" click "projects" from the navbar successfully
        Then The "/projects" page is loaded

        When I click "projects search bar" from projects page
        And I type "automation test (<projectClass>)" into "projects search bar"
        Then My project "automation test (<projectClass>)" with "<projectStatus>" status can be seen
        Examples:
            | username    | projectClass         | projectStatus   | 
            | REVIEWER    | standalone project   | Listed          |
            | VVB         | standalone project   | Listed          |
            | PD          | standalone project   | Listed          |
            | TAC         | standalone project   | Listed          |
            | ADMIN       | standalone project   | Listed          |
            | ARM TEAM    | standalone project   | Listed          |
            | FINANCE     | standalone project   | Listed          |

    @group2B
    Scenario Outline: '<username>' selects '<action>' on a '<projectClass>' which is '<projectStatus>' 
        Given I login as "<username>"
        When "<username>" click "projects" from the navbar successfully
        Then The "/projects" page is loaded
        
        When I click "projects search bar" from projects page
        And I type "automation test (<projectClass>)" into "projects search bar"
        Then My project "automation test (<projectClass>)" with "<projectStatus>" status can be seen
        
        When I click "<action>" for my "automation test (<projectClass>)" with "<projectStatus>" status
        Then "<uiConfirmation>" can be seen
        #And The "<apiConfirmation>" page is loaded 
        
        Examples:
            | username   | projectClass         | projectStatus   | action | uiConfirmation          |
            | PD         | standalone project   | New             | View   | Project Characteristics |
            | TAC        | standalone project   | New             | View   | Project Characteristics |
            | ADMIN      | standalone project   | New             | View   | Project Characteristics |
            | ARM TEAM   | standalone project   | New             | View   | Project Characteristics |
            | FINANCE    | standalone project   | New             | View   | Project Characteristics |

            | REVIEWER   | standalone project   | Listed          | View   | Project Characteristics |
            | VVB        | standalone project   | Listed          | View   | Project Characteristics |
            | PD         | standalone project   | Listed          | View   | Project Characteristics |
            | TAC        | standalone project   | Listed          | View   | Project Characteristics |
            | ADMIN      | standalone project   | Listed          | View   | Project Characteristics |
            | ARM TEAM   | standalone project   | Listed          | View   | Project Characteristics |
            | FINANCE    | standalone project   | Listed          | View   | Project Characteristics |

            | PD         | standalone project   | New             | Edit   | Update Project          |
            | ADMIN      | standalone project   | New             | Edit   | Update Project          |
            | ARM TEAM   | standalone project   | New             | Edit   | Update Project          |
            | ADMIN      | standalone project   | Listed          | Edit   | Update Project          |

            | PD         | POA                  | New             | View   | Project Characteristics |
            | TAC        | POA                  | New             | View   | Project Characteristics |
            | ADMIN      | POA                  | New             | View   | Project Characteristics |
            | ARM TEAM   | POA                  | New             | View   | Project Characteristics |
            | FINANCE    | POA                  | New             | View   | Project Characteristics |

            | REVIEWER   | POA                  | Listed          | View   | Project Characteristics |
            | VVB        | POA                  | Listed          | View   | Project Characteristics |
            | PD         | POA                  | Listed          | View   | Project Characteristics |
            | TAC        | POA                  | Listed          | View   | Project Characteristics |
            | ADMIN      | POA                  | Listed          | View   | Project Characteristics |
            | ARM TEAM   | POA                  | Listed          | View   | Project Characteristics |
            | FINANCE    | POA                  | Listed          | View   | Project Characteristics |

            | PD         | POA                  | New             | Edit   | Update Project          |
            | ADMIN      | POA                  | New             | Edit   | Update Project          |
            | ARM TEAM   | POA                  | New             | Edit   | Update Project          |
            | ADMIN      | POA                  | Listed          | Edit   | Update Project          |
            
            | PD         | REGULARCASE          | New             | View   | Project Characteristics |
            | TAC        | REGULARCASE          | New             | View   | Project Characteristics |
            | ADMIN      | REGULARCASE          | New             | View   | Project Characteristics |
            | ARM TEAM   | REGULARCASE          | New             | View   | Project Characteristics |
            | FINANCE    | REGULARCASE          | New             | View   | Project Characteristics |

            | REVIEWER   | REGULARCASE          | Listed          | View   | Project Characteristics |
            | VVB        | REGULARCASE          | Listed          | View   | Project Characteristics |
            | PD         | REGULARCASE          | Listed          | View   | Project Characteristics |
            | TAC        | REGULARCASE          | Listed          | View   | Project Characteristics |
            | ADMIN      | REGULARCASE          | Listed          | View   | Project Characteristics |
            | ARM TEAM   | REGULARCASE          | Listed          | View   | Project Characteristics |
            | FINANCE    | REGULARCASE          | Listed          | View   | Project Characteristics |

            | PD         | REGULARCASE          | New             | Edit   | Update Project          |
            | ADMIN      | REGULARCASE          | New             | Edit   | Update Project          |
            | ARM TEAM   | REGULARCASE          | New             | Edit   | Update Project          |
            | ADMIN      | REGULARCASE          | Listed          | Edit   | Update Project          |
            
            | PD         | REALCASE             | New             | View   | Project Characteristics |
            | TAC        | REALCASE             | New             | View   | Project Characteristics |
            | ADMIN      | REALCASE             | New             | View   | Project Characteristics |
            | ARM TEAM   | REALCASE             | New             | View   | Project Characteristics |
            | FINANCE    | REALCASE             | New             | View   | Project Characteristics |

            | REVIEWER   | REALCASE             | Listed          | View   | Project Characteristics |
            | VVB        | REALCASE             | Listed          | View   | Project Characteristics |
            | PD         | REALCASE             | Listed          | View   | Project Characteristics |
            | TAC        | REALCASE             | Listed          | View   | Project Characteristics |
            | ADMIN      | REALCASE             | Listed          | View   | Project Characteristics |
            | ARM TEAM   | REALCASE             | Listed          | View   | Project Characteristics |
            | FINANCE    | REALCASE             | Listed          | View   | Project Characteristics |

            | PD         | REALCASE             | New             | Edit   | Update Project          |
            | ADMIN      | REALCASE             | New             | Edit   | Update Project          |
            | ARM TEAM   | REALCASE             | New             | Edit   | Update Project          |
            | ADMIN      | REALCASE             | Listed          | Edit   | Update Project          |