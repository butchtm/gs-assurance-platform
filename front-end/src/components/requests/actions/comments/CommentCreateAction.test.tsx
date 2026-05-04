import { fireEvent, render, screen } from "@testing-library/react";

import { debugRequests } from "../../../../common/request/DebugRequests";
import { getMockedUsersByAccountId } from "../../../../common/user/DebugUser";
import { AuthContext, AuthContextType } from "../../../Auth/Login/AuthProvider";

import { CommentCreateAction } from "./CommentCreateAction";

describe("CommentCreateAction", () => {
  it("renders the add comments button", async () => {
    render(<CommentCreateAction request={debugRequests[0]} updateRequest={jest.fn()} disabled={false} />);

    const buttonIcon = await screen.findByTestId("add-comment-button");
    expect(buttonIcon).toBeInTheDocument();
  });

  it("renders the disabled add comments button", async () => {
    render(<CommentCreateAction request={debugRequests[0]} updateRequest={jest.fn()} disabled={true} />);

    const button = await screen.findByTestId("add-comment-button");
    expect(button.hasAttribute("disabled")).toEqual(true);
  });

  it("opens the modal when the add comment button is clicked", async () => {
    render(
      <AuthContext.Provider
        value={
          {
            user: getMockedUsersByAccountId(1)[0],
          } as AuthContextType
        }
      >
        <CommentCreateAction request={debugRequests[0]} updateRequest={jest.fn()} disabled={false} />
      </AuthContext.Provider>,
    );

    fireEvent.click(await screen.findByTestId("add-comment-button"));
    expect(await screen.findByTestId("comment-anonymously")).toBeInTheDocument();
  });
});
