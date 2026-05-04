import { fireEvent, render, screen } from "@testing-library/react";
import { BrowserRouter } from "react-router-dom";

import { UnreadMessagesNotifications } from "./UnreadMessagesNotifications";

const mockUnread = [
  {
    requestId: 1,
    conversationId: 1,
    unreadCount: 3,
  },
  {
    requestId: 2,
    conversationId: 2,
    unreadCount: 5,
  },
];

const mockedNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockedNavigate,
}));

describe("UnreadMessagesNotifications", () => {
  afterEach(() => mockedNavigate.mockReset());

  it("displays the number of unread messages", () => {
    render(
      <BrowserRouter>
        <UnreadMessagesNotifications unreadMessages={mockUnread} />
      </BrowserRouter>,
    );
    expect(screen.getByTestId("unread-messages-badge")).toBeInTheDocument();
    expect(screen.getByText("8")).toBeInTheDocument();
  });
  it("shows no badge if no new messages", () => {
    render(
      <BrowserRouter>
        <UnreadMessagesNotifications unreadMessages={[]} />
      </BrowserRouter>,
    );
    expect(screen.queryByText("8")).not.toBeInTheDocument();
  });
  it("allows for navigating to My Requests page", () => {
    render(
      <BrowserRouter>
        <UnreadMessagesNotifications unreadMessages={mockUnread} />
      </BrowserRouter>,
    );
    const button = screen.getByTestId("unread-messages-button");
    fireEvent.click(button);
    expect(mockedNavigate).toHaveBeenCalledWith("/my-requests");
  });
  it("disables navigation for no new messages", () => {
    render(
      <BrowserRouter>
        <UnreadMessagesNotifications unreadMessages={[]} />
      </BrowserRouter>,
    );
    const button = screen.getByTestId("unread-messages-button");
    fireEvent.click(button);

    expect(button).toBeDisabled();
    expect(mockedNavigate).not.toHaveBeenCalled();
  });
});
