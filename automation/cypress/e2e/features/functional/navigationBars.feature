@group5
Feature: navigation bars page API tests

    Scenario Outline: <username> selects admin from primary navbar
        Given I login as "<username>"
        When I click "<button>" from the navbar
        Then admins landing page text is visible
        Then This page has an accessability check performed

    Examples:
            | username       | button                | 
            
            | ADMIN          | admin                 |
            | ARM TEAM       | admin                 |


    Scenario Outline: <username> selects <button> from primary navbar
        Given I login as "<username>"
        Then "<username>" click "<button>" from the navbar successfully
        Then This page has an accessability check performed

    Examples:
            | username       | button                | 

            #| ADMIN          | platform accounts     |
            | ADMIN          | users                 |
            | ADMIN          | consultations         |  
            | ADMIN          | projects              |
            | ADMIN          | all requests          |

            | PD             | my requests           |
            | PD             | projects              |
            | PD             | consultations         |

            | VVB            | my requests           |
            | VVB            | projects              |
            | VVB            | consultations         |

            | ARM TEAM       | my requests           |
            | ARM TEAM       | projects              |
            | ARM TEAM       | consultations         |
            | ARM TEAM       | all requests          |

            | TAC            | my requests           |
            | TAC            | projects              |
            | TAC            | consultations         |

            | FINANCE        | my requests           |
            | FINANCE        | projects              |
            | FINANCE        | consultations         |

            | REVIEWER       | my requests           |
            | REVIEWER       | projects              |
            | REVIEWER       | consultations         |

    Scenario Outline: <username> selects common dashboard from primary navbar
        Given I login as "<username>"
        When I click "<button>" from the navbar
        Then common dashboard has loaded and available to the user
        Then This page has an accessability check performed

    Examples:
            | username       | button                | 
            
            | ADMIN          | common-dashboard      |
            | ARM TEAM       | common-dashboard      |

