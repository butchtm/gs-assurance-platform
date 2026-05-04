@group19
Feature: request conversations

    Scenario Outline: <username> allows conversation to start on request
        Given I login as "<username>"
        And I click "projects" from the navbar
        And I search a "<projectStatus>" "<projectType>" GSID
        And I select "<projectStatus>" "<projectType>" GSID from results as "<username>"
        And I select the "requests" nav button in my project
        And I open my request "<requestType>" from the request view
        Then I can see the correct "<requestType>" header

        When I select the request conversation button "Open Conversation"
        Then I can see the conversation modal header which reads "<requestType>"
        And For later tests I assign an qaqc and vvb users

    Examples:
            | username | requestType                     | projectType           | invoiceStatus | projectStatus | 
            | ARM TEAM | Design Change \| With VVB no. 1 | automation test (POA) | Not Triggered | New           |
            | ADMIN    | Design Change \| With VVB no. 2 | automation test (POA) | Not Triggered | New           |

    Scenario Outline: <username> closes all conversation on a request
        Given I login as "<username>"
        And I click "projects" from the navbar
        And I search a "<projectStatus>" "<projectType>" GSID
        And I select "<projectStatus>" "<projectType>" GSID from results as "<username>"
        And I select the "requests" nav button in my project 
        And I open my request "<requestType>" from the request view
        Then I can see the correct "<requestType>" header

        When I select the request conversation button "View Active Conversation"
        Then I can see the conversation modal header which reads "<requestType>"

        When I select close conversation to prevent anyone sending messages
        And I can see the confirmation modal to close conversations
        And I confirm my intent to close conversation to prevent anyone sending messages
        Then I can see my conversation button says "View Closed Conversation"

    Examples:
            | username | requestType                     | projectType           | invoiceStatus | projectStatus | 
            | ARM TEAM | Design Change \| With VVB no. 1 | automation test (POA) | Not Triggered | New           |
            | ADMIN    | Design Change \| With VVB no. 2 | automation test (POA) | Not Triggered | New           |

     Scenario Outline: <username> re-opens a conversation on a request
        Given I login as "<username>"
        And I click "projects" from the navbar
        And I search a "<projectStatus>" "<projectType>" GSID
        And I select "<projectStatus>" "<projectType>" GSID from results as "<username>"
        And I select the "requests" nav button in my project 
        And I open my request "<requestType>" from the request view
        Then I can see the correct "<requestType>" header

        When I select the request conversation button "View Closed Conversation"
        Then I can see the conversation modal header which reads "<requestType>"

        When I select re-open conversation to again allow sending messages
        And I can see the confirmation modal to re-open conversation
        And I confirm my intent to re-open conversation to prevent anyone sending messages
        Then I can see my conversation button says "View Active Conversation"

    Examples:
            | username | requestType                     | projectType           | invoiceStatus | projectStatus | 
            | ARM TEAM | Design Change \| With VVB no. 1 | automation test (POA) | Not Triggered | New           |
            | ADMIN    | Design Change \| With VVB no. 2 | automation test (POA) | Not Triggered | New           |

    Scenario Outline: <username> sends a standard message to pd/vvb
        Given I login as "<username>"
        And I click "projects" from the navbar
        And I search a "<projectStatus>" "<projectType>" GSID
        And I select "<projectStatus>" "<projectType>" GSID from results as "<username>"
        And I select the "requests" nav button in my project 
        And I open my request "<requestType>" from the request view
        Then I can see the correct "<requestType>" header

        When I select the request conversation button "View Active Conversation"
        Then I can see the conversation modal header which reads "<requestType>"

        When I select the rich text area and type "I am <displayName>, hi vvb/pd"
        And I select the send message button
        Then I can see my "<displayName>" in the message header
        And I can see "I am <displayName>, hi vvb/pd" in the messages

    Examples:
            | username | displayName    | requestType                     | projectType           | invoiceStatus | projectStatus | 
            | ARM TEAM | autotest-arm   | Design Change \| With VVB no. 2 | automation test (POA) | Not Triggered | New           |
            | ADMIN    | autotest-admin | Design Change \| With VVB no. 2 | automation test (POA) | Not Triggered | New           |

    Scenario Outline: <username> sends a rich text message to pd/vvb with 4 styles applied 
        Given I login as "<username>"
        And I click "projects" from the navbar
        And I search a "<projectStatus>" "<projectType>" GSID
        And I select "<projectStatus>" "<projectType>" GSID from results as "<username>"
        And I select the "requests" nav button in my project 
        And I open my request "<requestType>" from the request view
        Then I can see the correct "<requestType>" header

        When I select the request conversation button "View Active Conversation"
        Then I can see the conversation modal header which reads "<requestType>"

        When I select the "bold" button to apply style to my text
        And I select the "italic" button to apply style to my text
        And I select the "underline" button to apply style to my text
        And I select the "strike" button to apply style to my text
        And I select the rich text area and type "fancy message text"
        And I select the send message button
        Then I can see my "<displayName>" in the message header
        And I can see my "fancy message text" with 4 styles applied to my message

    Examples:
            | username | displayName    | requestType                     | projectType           | invoiceStatus | projectStatus | 
            | ARM TEAM | autotest-arm   | Design Change \| With VVB no. 2 | automation test (POA) | Not Triggered | New           |
            | ADMIN    | autotest-admin | Design Change \| With VVB no. 2 | automation test (POA) | Not Triggered | New           |


    Scenario Outline: <username> has new message icon and count on my request dashboard 
        Given I login as "<username>"
        And "<username>" click "my requests" from the navbar successfully
        And I clear the my requests dashboard request status filter as "<username>"
        And I select "Name or Exact GSID" from my requests dashboard page
        And I type "automation test (POA)" into "name or gsid (my requests)"
        Then "automation test (POA)" "project" is displayed in the list 
        And I locate "<messageCount>" new message label for my "<requestType>" row

    Examples:
            | username | displayName  | requestType                     | projectType           | messageCount | projectStatus |
            | ARM TEAM | autotest-arm | Design Change \| With VVB no. 2 | automation test (POA) | 1            | New           |
            | PD       | autotest-pd  | Design Change \| With VVB no. 2 | automation test (POA) | 4            | New           |
            | VVB      | autotest-vvb | Design Change \| With VVB no. 2 | automation test (POA) | 4            | New           |

    Scenario Outline: <username> has new message icon and count in the request header
        Given I login as "<username>"
        And "<username>" click "my requests" from the navbar successfully
        And I clear the my requests dashboard request status filter as "<username>"
        And I select "Name or Exact GSID" from my requests dashboard page
        And I type "automation test (POA)" into "name or gsid (my requests)"
        Then "automation test (POA)" "project" is displayed in the list

        When I select my "<requestType>" link to my request
        And I locate "<messageCount>" new messages in my request header

    Examples:
            | username | displayName  | requestType                     | projectType           | messageCount | projectStatus |
            | ARM TEAM | autotest-arm | Design Change \| With VVB no. 2 | automation test (POA) | 1            | New           |
            | PD       | autotest-pd  | Design Change \| With VVB no. 2 | automation test (POA) | 4            | New           |
            | VVB      | autotest-vvb | Design Change \| With VVB no. 2 | automation test (POA) | 4            | New           |

    Scenario Outline: <username> has new message count on request button
        Given I login as "<username>"
        And "<username>" click "my requests" from the navbar successfully
        And I clear the my requests dashboard request status filter as "<username>"
        And I select "Name or Exact GSID" from my requests dashboard page
        And I type "automation test (POA)" into "name or gsid (my requests)"
        Then "automation test (POA)" "project" is displayed in the list 

        When I select my "<requestType>" link to my request
        And I locate "<messageCount>" in top corner of view conversation button

    Examples:
            | username | displayName  | requestType                     | projectType           | messageCount | projectStatus |
            | ARM TEAM | autotest-arm | Design Change \| With VVB no. 2 | automation test (POA) | 1            | New           |
            | PD       | autotest-pd  | Design Change \| With VVB no. 2 | automation test (POA) | 4            | New           |
            | VVB      | autotest-vvb | Design Change \| With VVB no. 2 | automation test (POA) | 4            | New           |

    Scenario Outline: <username> can view new messages from arm team and admin
        Given I login as "<username>"
        And "<username>" click "my requests" from the navbar successfully
        And I clear the my requests dashboard request status filter as "<username>"
        And I select "Name or Exact GSID" from my requests dashboard page
        And I type "automation test (POA)" into "name or gsid (my requests)"
        Then "automation test (POA)" "project" is displayed in the list 

        When I select my "<requestType>" link to my request
        And I select the request conversation button "View Active Conversation"
        Then I can see the conversation modal header which reads "<requestType>"
        And I can see "Gold Standard ARM Team" as the message sender
        And I can see "Gold Standard Admin" as the message sender
        And I can see my "fancy message text" with 4 styles applied to my message

    Examples:
            | username | displayName  | requestType                     | projectType           | messageCount | projectStatus |
            #| ARM TEAM | autotest-arm | Design Change \| With VVB no. 2 | automation test (POA) | 1            | New           |
            | PD       | autotest-pd  | Design Change \| With VVB no. 2 | automation test (POA) | 4            | New           |
            | VVB      | autotest-vvb | Design Change \| With VVB no. 2 | automation test (POA) | 4            | New           |

    Scenario Outline: <username> can see read receipts on recent messages
       Given I login as "<username>"
        And I click "projects" from the navbar
        And I search a "<projectStatus>" "<projectType>" GSID
        And I select "<projectStatus>" "<projectType>" GSID from results as "<username>"
        And I select the "requests" nav button in my project 
        And I open my request "<requestType>" from the request view
        Then I can see the correct "<requestType>" header

        When I select the request conversation button "View Active Conversation"
        Then I can see the conversation modal header which reads "<requestType>"
        And I can see the pd and vvb user has read my message
        
    Examples:
            | username | displayName    | requestType                     | projectType           | messageCount  | projectStatus |
            | ADMIN    | autotest-admin | Design Change \| With VVB no. 2 | automation test (POA) | Not Triggered | New           |
            | ARM TEAM | autotest-arm   | Design Change \| With VVB no. 2 | automation test (POA) | Not Triggered | New           |

    Scenario Outline: <username> can send new messages to arm team and admin
        Given I login as "<username>"
        And "<username>" click "my requests" from the navbar successfully
        And I clear the my requests dashboard request status filter as "<username>"
        And I select "Name or Exact GSID" from my requests dashboard page
        And I type "automation test (POA)" into "name or gsid (my requests)"
        Then "automation test (POA)" "project" is displayed in the list 

        When I select my "<requestType>" link to my request
        And I select the request conversation button "View Active Conversation"
        Then I can see the conversation modal header which reads "<requestType>"
        
        When I select the "bold" button to apply style to my text
        And I select the "italic" button to apply style to my text
        And I select the "underline" button to apply style to my text
        And I select the "strike" button to apply style to my text
        And I select the rich text area and type "fancy message text"
        And I select the send message button
        Then I can see my "<displayName>" in the message header
        And I can see my "fancy message text" with 4 styles applied to my message
        
    Examples:
            | username | displayName  | requestType                     | projectType           | messageCount | projectStatus |
            #| ARM TEAM | autotest-arm | Design Change \| With VVB no. 2 | automation test (POA) | 1            | New           |
            | PD       | autotest-pd  | Design Change \| With VVB no. 2 | automation test (POA) | 4            | New           |
            | VVB      | autotest-vvb | Design Change \| With VVB no. 2 | automation test (POA) | 4            | New           |