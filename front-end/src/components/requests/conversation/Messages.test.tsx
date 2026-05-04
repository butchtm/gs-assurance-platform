import { fireEvent, render, screen, waitFor } from "@testing-library/react";

import { AuthContext, AuthContextType } from "../../Auth/Login/AuthProvider";

import { Messages } from "./Messages";
import { ConversationParticipant, getMessages, markRead, Message } from "./Conversation";

jest.mock("./Conversation", () => ({
  ...jest.requireActual("./Conversation"),
  getMessages: jest.fn(),
  markRead: jest.fn(),
}));

const mockMessage: Message = {
  id: 1,
  content: "This is the message content",
  senderUsername: "janedoe",
  sentTimestamp: "2023-03-03T12:10:00.000Z",
  senderAccountType: ConversationParticipant.ProjectDeveloper,
  readByAccountTypes: [],
  readByUsers: [],
};

const mockUpdateRequest = jest.fn();

const renderWithAuth = (open?: boolean) => {
  return render(
    <AuthContext.Provider
      value={
        {
          isArmUser: () => false,
          isAdminUser: () => false,
        } as AuthContextType
      }
    >
      <Messages
        projectId={1}
        requestId={2}
        conversationId={3}
        openConversation={open ?? true}
        updateRequest={mockUpdateRequest}
      />
    </AuthContext.Provider>,
  );
};

describe("Messages", () => {
  beforeEach(() => {
    (getMessages as jest.Mock).mockResolvedValue({
      content: [
        {
          ...mockMessage,
          readByAccountTypes: ["Project Developer Account", "VVB Account", "ARM Team Account"],
          id: 2,
        },
        mockMessage,
        {
          ...mockMessage,
          id: 3,
        },
      ],
      pageSize: 10,
      pageNumber: 1,
      totalPages: 5,
      totalElements: 50,
    });
  });
  afterEach(() => {
    (getMessages as jest.Mock).mockReset();
    (markRead as jest.Mock).mockReset();
  });

  it("marks the conversation as read and updates the request on load", async () => {
    renderWithAuth();
    await waitFor(() => {
      expect(markRead as jest.Mock).toHaveBeenCalledWith(1, 2, 3);
      expect(markRead as jest.Mock).toHaveBeenCalledTimes(1);
      expect(mockUpdateRequest).toHaveBeenCalledTimes(1);
    });
  });

  it("fetches the messages on load", async () => {
    renderWithAuth();
    await waitFor(() => {
      expect(getMessages as jest.Mock).toHaveBeenCalledWith(1, 2, 3, 1);
      expect(getMessages as jest.Mock).toHaveBeenCalledTimes(1);
    });
  });
  it("shows who has seen the message", async () => {
    renderWithAuth();
    await waitFor(() => {
      expect(screen.getByText("Seen by: Project Developer Account, VVB Account, ARM Team Account")).toBeInTheDocument();
    });
  });
  it("allows for loading of more messages", async () => {
    renderWithAuth();

    await waitFor(() => {
      expect(getMessages as jest.Mock).toHaveBeenCalledWith(1, 2, 3, 1);
      expect(getMessages as jest.Mock).toHaveBeenCalledTimes(1);
    });

    const loadMoreButton = screen.getByText("Load More");
    fireEvent.click(loadMoreButton);

    await waitFor(() => {
      expect(getMessages as jest.Mock).toHaveBeenCalledWith(1, 2, 3, 2);
      expect(getMessages as jest.Mock).toHaveBeenCalledTimes(2);
    });
  });
  it("disables message box and send when conversation closed", async () => {
    const { container } = renderWithAuth(false);
    const input = container.getElementsByClassName("ql-editor")[0];
    const sendButton = screen.getByText("Send");
    await waitFor(() => {
      expect(input.getAttribute("contenteditable")).toBe("false");
      expect(sendButton).toBeDisabled();
    });
  });
});
