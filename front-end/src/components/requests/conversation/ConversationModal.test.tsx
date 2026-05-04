import { fireEvent, render, screen } from "@testing-library/react";

import { debugNotClosedRequest, debugClosedRequests } from "../../../common/request/DebugRequests";
import { ReferenceType } from "../../../common/data-types/DataReferenceTypes";
import { AuthContext, AuthContextType } from "../../Auth/Login/AuthProvider";
import { mockActiveReferenceTypes } from "../../../common/data-types/DebugReferenceTypes";

import { ConversationModal } from "./ConversationModal";
import { updateConversationStatus } from "./Conversation";

jest.mock("./Conversation", () => ({
  ...jest.requireActual("./Conversation"),
  updateConversationStatus: jest.fn(),
  getMessages: jest.fn(),
  markRead: jest.fn(),
}));

jest.mock("../../../common/data-types/DataReferenceTypes", () => ({
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
  }),
}));

const openStatus: ReferenceType = {
  id: 1,
  code: "OPEN",
  description: "Open",
};

const closedStatus: ReferenceType = {
  id: 2,
  code: "CLOSED",
  description: "Closed",
};

const openConversationRequest = {
  ...debugNotClosedRequest,
  conversation: {
    id: 1,
    status: openStatus,
  },
};

const closedConversationRequest = {
  ...debugNotClosedRequest,
  conversation: {
    id: 1,
    status: closedStatus,
  },
};

describe("ConversationModal", () => {
  it("should close a conversation", () => {
    render(
      <AuthContext.Provider
        value={
          {
            isAdminUser: () => true,
          } as AuthContextType
        }
      >
        <ConversationModal
          isOpen={true}
          request={openConversationRequest}
          onClose={() => ({})}
          updateRequest={() => ({})}
        />
      </AuthContext.Provider>,
    );
    const closeButton = screen.getByText("Close Conversation");
    fireEvent.click(closeButton);
    expect(screen.getByText("Confirm Closure of Conversation")).toBeInTheDocument();
    const confirmCloseButton = screen.getByTestId("confirm-close-conversation-button");
    fireEvent.click(confirmCloseButton);
    expect(updateConversationStatus).toHaveBeenCalledWith(
      openConversationRequest.project.id,
      openConversationRequest.id,
      openConversationRequest.conversation.id,
      closedStatus,
    );
  });

  it("cannot close conversation if not ARM or Admin", () => {
    render(
      <AuthContext.Provider
        value={
          {
            isAdminUser: () => false,
            isArmUser: () => false,
          } as AuthContextType
        }
      >
        <ConversationModal
          isOpen={true}
          request={openConversationRequest}
          onClose={() => ({})}
          updateRequest={() => ({})}
        />
      </AuthContext.Provider>,
    );
    expect(screen.queryByText("Close Conversation")).not.toBeInTheDocument();
  });

  it("should re-open a conversation", () => {
    render(
      <AuthContext.Provider
        value={
          {
            isAdminUser: () => true,
          } as AuthContextType
        }
      >
        <ConversationModal
          isOpen={true}
          request={closedConversationRequest}
          onClose={() => ({})}
          updateRequest={() => ({})}
        />
      </AuthContext.Provider>,
    );
    const reOpenButton = screen.getByText("Re-Open Conversation");
    fireEvent.click(reOpenButton);
    expect(screen.getByText("Confirm Re-Open of Conversation")).toBeInTheDocument();
    const confirmReOpenButton = screen.getByTestId("confirm-re-open-conversation-button");
    fireEvent.click(confirmReOpenButton);
    expect(updateConversationStatus).toHaveBeenCalledWith(
      closedConversationRequest.project.id,
      closedConversationRequest.id,
      closedConversationRequest.conversation.id,
      openStatus,
    );
  });

  it("cannot re-open conversation if not arm or admin", () => {
    render(
      <AuthContext.Provider
        value={
          {
            isAdminUser: () => false,
            isArmUser: () => false,
          } as AuthContextType
        }
      >
        <ConversationModal
          isOpen={true}
          request={closedConversationRequest}
          onClose={() => ({})}
          updateRequest={() => ({})}
        />
      </AuthContext.Provider>,
    );
    expect(screen.queryByText("Re-Open Conversation")).not.toBeInTheDocument();
  });

  it("cannot re-open a conversation for a closed request", () => {
    render(
      <AuthContext.Provider
        value={
          {
            isAdminUser: () => false,
            isArmUser: () => false,
          } as AuthContextType
        }
      >
        <ConversationModal
          isOpen={true}
          request={{
            ...debugClosedRequests[0],
            conversation: {
              id: 1,
              status: closedStatus,
            },
          }}
          onClose={() => ({})}
          updateRequest={() => ({})}
        />
      </AuthContext.Provider>,
    );
    expect(screen.queryByText("Re-Open Conversation")).not.toBeInTheDocument();
  });
});
