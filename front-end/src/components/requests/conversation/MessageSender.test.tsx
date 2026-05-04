import { render, screen } from "@testing-library/react";

import { MessageSender } from "./MessageSender";

jest.mock("./Conversation", () => ({
  ...jest.requireActual("./Conversation"),
  sendMessage: jest.fn(),
}));

describe("MessageSender", () => {
  it("enables the text box and send button", () => {
    const { container } = render(
      <MessageSender projectId={1} requestId={2} conversationId={3} onMessageSent={() => ({})} disabled={false} />,
    );
    const input = container.getElementsByClassName("ql-editor")[0];
    const sendButton = screen.getByText("Send");

    expect(input.getAttribute("contenteditable")).toBe("true");
    expect(sendButton).not.toBeDisabled();
  });
  it("can disable the text box and send button", () => {
    const { container } = render(
      <MessageSender projectId={1} requestId={2} conversationId={3} onMessageSent={() => ({})} disabled={true} />,
    );
    const input = container.getElementsByClassName("ql-editor")[0];
    const sendButton = screen.getByText("Send");

    expect(input.getAttribute("contenteditable")).toBe("false");
    expect(sendButton).toBeDisabled();
  });
});
