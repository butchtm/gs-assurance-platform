import { render, screen } from "@testing-library/react";

import { AuthContext, AuthContextType } from "../../Auth/Login/AuthProvider";

import { ConversationParticipant, Message } from "./Conversation";
import { MessageHandler } from "./MessageHandler";

const mockMessage: Message = {
  id: 1,
  content: "This is the message content",
  senderUsername: "janedoe",
  sentTimestamp: "2023-03-03T12:10:00.000Z",
  senderAccountType: ConversationParticipant.ProjectDeveloper,
  readByAccountTypes: [],
  readByUsers: [],
};

describe("MessageHandler", () => {
  it("displays message contents and sender information", () => {
    render(
      <AuthContext.Provider
        value={
          {
            isArmUser: () => true,
            isAdminUser: () => false,
          } as AuthContextType
        }
      >
        <MessageHandler message={mockMessage} />
      </AuthContext.Provider>,
    );

    expect(screen.getByText("This is the message content")).toBeInTheDocument();
    expect(screen.getByText("janedoe - Project Developer (03/03/2023 12:10)")).toBeInTheDocument();
  });

  it("doesn't display ARM usernames for non-ARM accounts", () => {
    render(
      <AuthContext.Provider
        value={
          {
            isArmUser: () => false,
            isAdminUser: () => false,
          } as AuthContextType
        }
      >
        <MessageHandler
          message={{
            ...mockMessage,
            senderAccountType: ConversationParticipant.GSARMTeam,
          }}
        />
      </AuthContext.Provider>,
    );

    expect(screen.getByText("This is the message content")).toBeInTheDocument();
    expect(screen.getByText("Gold Standard ARM Team (03/03/2023 12:10)")).toBeInTheDocument();
    expect(screen.queryByText("janedoe")).not.toBeInTheDocument();
  });

  it("displays ARM usernames for ARM accounts", () => {
    render(
      <AuthContext.Provider
        value={
          {
            isArmUser: () => true,
            isAdminUser: () => false,
          } as AuthContextType
        }
      >
        <MessageHandler
          message={{
            ...mockMessage,
            senderAccountType: ConversationParticipant.GSARMTeam,
          }}
        />
      </AuthContext.Provider>,
    );

    expect(screen.getByText("This is the message content")).toBeInTheDocument();
    expect(screen.getByText("janedoe - Gold Standard ARM Team (03/03/2023 12:10)")).toBeInTheDocument();
  });

  it("doesn't display admin usernames for non-admin accounts", () => {
    render(
      <AuthContext.Provider
        value={
          {
            isArmUser: () => false,
            isAdminUser: () => false,
          } as AuthContextType
        }
      >
        <MessageHandler
          message={{
            ...mockMessage,
            senderAccountType: null,
          }}
        />
      </AuthContext.Provider>,
    );

    expect(screen.getByText("This is the message content")).toBeInTheDocument();
    expect(screen.getByText("Gold Standard Admin (03/03/2023 12:10)")).toBeInTheDocument();
    expect(screen.queryByText("janedoe")).not.toBeInTheDocument();
  });

  it("displays admin usernames for admin accounts", () => {
    render(
      <AuthContext.Provider
        value={
          {
            isArmUser: () => true,
            isAdminUser: () => false,
          } as AuthContextType
        }
      >
        <MessageHandler
          message={{
            ...mockMessage,
            senderAccountType: null,
          }}
        />
      </AuthContext.Provider>,
    );

    expect(screen.getByText("This is the message content")).toBeInTheDocument();
    expect(screen.getByText("janedoe - Gold Standard Admin (03/03/2023 12:10)")).toBeInTheDocument();
  });
});
