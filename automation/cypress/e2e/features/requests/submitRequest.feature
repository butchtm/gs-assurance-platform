@groupRS
Feature: wizard to progress requests on /project/GSid#

    Scenario Outline: <username> submits <request> request to <targetStatus> on a <project>
        Given I am "<username>" who wants to submit a "<request>" on a "<project>"
            #Login as username 
            #depending on username, navigate to request action 
        When I select submit request as "<username>" for "<request>" going to "<targetStatus>"
            #Plainly click the button
        And "<username>" selects my answers for "<request>" going to "<targetStatus>"
            #request&username combo should determine which questions to look for and which answers to give
        And I submit my answers to go to "<targetStatus>"
            #Plainly click the button
        Then I can observe "<targetStatus>" as a tag on "<request>"
        
        Examples:
        #this is very targetStatus order reliant
            | username | request                         | targetStatus         | project            |

            | PD       | Preliminary Review no. 1        | Completeness Check   | POA                |
            | PD       | Preliminary Review no. 1        | Completeness Check   | REALCASE           |
            | PD       | Preliminary Review no. 1        | Completeness Check   | REGULARCASE        |
            | PD       | Preliminary Review no. 1        | Completeness Check   | Standalone         |

            | ARM TEAM | Preliminary Review no. 1        | In Assignment        | POA                |
            | ARM TEAM | Preliminary Review no. 1        | In Assignment        | REALCASE           |
            | ARM TEAM | Preliminary Review no. 1        | In Assignment        | REGULARCASE        |
            | ARM TEAM | Preliminary Review no. 1        | In Assignment        | Standalone         |

            | ARM TEAM | Preliminary Review no. 1        | In Round Completion  | POA                |
            | ARM TEAM | Preliminary Review no. 1        | In Round Completion  | REALCASE           |
            | ARM TEAM | Preliminary Review no. 1        | In Round Completion  | REGULARCASE        |
            | ARM TEAM | Preliminary Review no. 1        | In Round Completion  | Standalone         |

            | ARM TEAM | Preliminary Review no. 1        | Completed - Approved  | POA                |
            | ARM TEAM | Preliminary Review no. 1        | Completed - Approved  | REALCASE           |
            | ARM TEAM | Preliminary Review no. 1        | Completed - Approved  | REGULARCASE        |
            | ARM TEAM | Preliminary Review no. 1        | Completed - Approved  | Standalone         |
            

            | PD       | Design Change \| With VVB no. 1 | In Audit             | POA                |
            | PD       | Design Change \| With VVB no. 1 | In Audit             | REALCASE           |
            | PD       | Design Change \| With VVB no. 1 | In Audit             | REGULARCASE        |
            | PD       | Design Change \| With VVB no. 1 | In Audit             | Standalone         |

            | VVB      | Design Change \| With VVB no. 1 | Completeness Check   | POA                |
            | VVB      | Design Change \| With VVB no. 1 | Completeness Check   | REALCASE           |
            | VVB      | Design Change \| With VVB no. 1 | Completeness Check   | REGULARCASE        |
            | VVB      | Design Change \| With VVB no. 1 | Completeness Check   | Standalone         |

            | ARM TEAM | Design Change \| With VVB no. 1 | In Assignment        | POA                |
            | ARM TEAM | Design Change \| With VVB no. 1 | In Assignment        | REALCASE           |
            | ARM TEAM | Design Change \| With VVB no. 1 | In Assignment        | REGULARCASE        |
            | ARM TEAM | Design Change \| With VVB no. 1 | In Assignment        | Standalone         |  

            | ARM TEAM | Design Change \| With VVB no. 1 | In Progress          | POA                |
            | ARM TEAM | Design Change \| With VVB no. 1 | In Progress          | REALCASE           |
            | ARM TEAM | Design Change \| With VVB no. 1 | In Progress          | REGULARCASE        |
            | ARM TEAM | Design Change \| With VVB no. 1 | In Progress          | Standalone         |

            | REVIEWER | Design Change \| With VVB no. 1 | In Round Completion  | POA                |
            | REVIEWER | Design Change \| With VVB no. 1 | In Round Completion  | REALCASE           |
            | REVIEWER | Design Change \| With VVB no. 1 | In Round Completion  | REGULARCASE        |
            | REVIEWER | Design Change \| With VVB no. 1 | In Round Completion  | Standalone         |

            | ARM TEAM | Design Change \| With VVB no. 1 | Completed - Approved | POA                |
            | ARM TEAM | Design Change \| With VVB no. 1 | Completed - Approved | REALCASE           |
            | ARM TEAM | Design Change \| With VVB no. 1 | Completed - Approved | REGULARCASE        |
            | ARM TEAM | Design Change \| With VVB no. 1 | Completed - Approved | Standalone         |



            | PD       | Design Certification \| Project no. 1 | In Audit             | POA                |
            | PD       | Design Certification \| Project no. 1 | In Audit             | REALCASE           |
            | PD       | Design Certification \| Project no. 1 | In Audit             | REGULARCASE        |
            | PD       | Design Certification \| Project no. 1 | In Audit             | Standalone         |

            | VVB      | Design Certification \| Project no. 1 | Completeness Check   | POA                |
            | VVB      | Design Certification \| Project no. 1 | Completeness Check   | REALCASE           |
            | VVB      | Design Certification \| Project no. 1 | Completeness Check   | REGULARCASE        |
            | VVB      | Design Certification \| Project no. 1 | Completeness Check   | Standalone         |

            | ARM TEAM | Design Certification \| Project no. 1 | In Assignment        | POA                |
            | ARM TEAM | Design Certification \| Project no. 1 | In Assignment        | REALCASE           |
            | ARM TEAM | Design Certification \| Project no. 1 | In Assignment        | REGULARCASE        |
            | ARM TEAM | Design Certification \| Project no. 1 | In Assignment        | Standalone         |  

            | ARM TEAM | Design Certification \| Project no. 1 | In Progress          | POA                |
            | ARM TEAM | Design Certification \| Project no. 1 | In Progress          | REALCASE           |
            | ARM TEAM | Design Certification \| Project no. 1 | In Progress          | REGULARCASE        |
            | ARM TEAM | Design Certification \| Project no. 1 | In Progress          | Standalone         |

            | REVIEWER | Design Certification \| Project no. 1 | In Round Completion  | POA                |
            | REVIEWER | Design Certification \| Project no. 1 | In Round Completion  | REALCASE           |
            | REVIEWER | Design Certification \| Project no. 1 | In Round Completion  | REGULARCASE        |
            | REVIEWER | Design Certification \| Project no. 1 | In Round Completion  | Standalone         |



            | PD       | Combined Design and Performance Certification \| Project no. 1 | In Audit             | POA                |
            | PD       | Combined Design and Performance Certification \| Project no. 1 | In Audit             | REALCASE           |
            | PD       | Combined Design and Performance Certification \| Project no. 1 | In Audit             | REGULARCASE        |
            | PD       | Combined Design and Performance Certification \| Project no. 1 | In Audit             | Standalone         |

            | VVB      | Combined Design and Performance Certification \| Project no. 1 | Completeness Check   | POA                |
            | VVB      | Combined Design and Performance Certification \| Project no. 1 | Completeness Check   | REALCASE           |
            | VVB      | Combined Design and Performance Certification \| Project no. 1 | Completeness Check   | REGULARCASE        |
            | VVB      | Combined Design and Performance Certification \| Project no. 1 | Completeness Check   | Standalone         |

            | ARM TEAM | Combined Design and Performance Certification \| Project no. 1 | In Assignment        | POA                |
            | ARM TEAM | Combined Design and Performance Certification \| Project no. 1 | In Assignment        | REALCASE           |
            | ARM TEAM | Combined Design and Performance Certification \| Project no. 1 | In Assignment        | REGULARCASE        |
            | ARM TEAM | Combined Design and Performance Certification \| Project no. 1 | In Assignment        | Standalone         |  

            | ARM TEAM | Combined Design and Performance Certification \| Project no. 1 | In Progress          | POA                |
            | ARM TEAM | Combined Design and Performance Certification \| Project no. 1 | In Progress          | REALCASE           |
            | ARM TEAM | Combined Design and Performance Certification \| Project no. 1 | In Progress          | REGULARCASE        |
            | ARM TEAM | Combined Design and Performance Certification \| Project no. 1 | In Progress          | Standalone         |

            | REVIEWER | Combined Design and Performance Certification \| Project no. 1 | In Round Completion  | POA                |
            | REVIEWER | Combined Design and Performance Certification \| Project no. 1 | In Round Completion  | REALCASE           |
            | REVIEWER | Combined Design and Performance Certification \| Project no. 1 | In Round Completion  | REGULARCASE        |
            | REVIEWER | Combined Design and Performance Certification \| Project no. 1 | In Round Completion  | Standalone         |



            | PD       | Inclusion \| Regular Case VPA no. 1 | Completeness Check   | POA                |
            | PD       | Inclusion \| Regular Case VPA no. 1 | Completeness Check   | REALCASE           |
            | PD       | Inclusion \| Regular Case VPA no. 1 | Completeness Check   | REGULARCASE        |
            | PD       | Inclusion \| Regular Case VPA no. 1 | Completeness Check   | Standalone         |

            | ARM TEAM | Inclusion \| Regular Case VPA no. 1 | In Assignment        | POA                |
            | ARM TEAM | Inclusion \| Regular Case VPA no. 1 | In Assignment        | REALCASE           |
            | ARM TEAM | Inclusion \| Regular Case VPA no. 1 | In Assignment        | REGULARCASE        |
            | ARM TEAM | Inclusion \| Regular Case VPA no. 1 | In Assignment        | Standalone         |  

            | ARM TEAM | Inclusion \| Regular Case VPA no. 1 | In Progress          | POA                |
            | ARM TEAM | Inclusion \| Regular Case VPA no. 1 | In Progress          | REALCASE           |
            | ARM TEAM | Inclusion \| Regular Case VPA no. 1 | In Progress          | REGULARCASE        |
            | ARM TEAM | Inclusion \| Regular Case VPA no. 1 | In Progress          | Standalone         |

            | REVIEWER | Inclusion \| Regular Case VPA no. 1 | In Round Completion  | POA                |
            | REVIEWER | Inclusion \| Regular Case VPA no. 1 | In Round Completion  | REALCASE           |
            | REVIEWER | Inclusion \| Regular Case VPA no. 1 | In Round Completion  | REGULARCASE        |
            | REVIEWER | Inclusion \| Regular Case VPA no. 1 | In Round Completion  | Standalone         |
            


            | PD       | Decertification \| Project no. 1 | Completeness Check   | POA                |
            | PD       | Decertification \| Project no. 1 | Completeness Check   | REALCASE           |
            | PD       | Decertification \| Project no. 1 | Completeness Check   | REGULARCASE        |
            | PD       | Decertification \| Project no. 1 | Completeness Check   | Standalone         |

            | ARM TEAM | Decertification \| Project no. 1 | In Assignment        | POA                |
            | ARM TEAM | Decertification \| Project no. 1 | In Assignment        | REALCASE           |
            | ARM TEAM | Decertification \| Project no. 1 | In Assignment        | REGULARCASE        |
            | ARM TEAM | Decertification \| Project no. 1 | In Assignment        | Standalone         |  

            | ARM TEAM | Decertification \| Project no. 1 | In Progress          | POA                |
            | ARM TEAM | Decertification \| Project no. 1 | In Progress          | REALCASE           |
            | ARM TEAM | Decertification \| Project no. 1 | In Progress          | REGULARCASE        |
            | ARM TEAM | Decertification \| Project no. 1 | In Progress          | Standalone         |

            | REVIEWER | Decertification \| Project no. 1 | In Round Completion  | POA                |
            | REVIEWER | Decertification \| Project no. 1 | In Round Completion  | REALCASE           |
            | REVIEWER | Decertification \| Project no. 1 | In Round Completion  | REGULARCASE        |
            | REVIEWER | Decertification \| Project no. 1 | In Round Completion  | Standalone         |