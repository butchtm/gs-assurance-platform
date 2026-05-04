import { fireEvent, render, screen } from "@testing-library/react";

import { mockActiveReferenceTypes } from "../../../common/data-types/DebugReferenceTypes";
import * as UserModule from "../../User/User";
import { User } from "../../User/User";

import { PlatformAccountsForm } from "./PlatformAccountsForm";

jest.mock("../../APIClient/Client", () => ({
  apiClient: {
    makeRequest: jest.fn(),
  },
}));

jest.mock("../../User/User", () => ({
  getAllActiveUsers: jest.fn(),
}));

jest.mock("../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../common/data-types/DataReferenceTypes"),
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
  }),
}));

describe("PlatformAccountsForm", () => {
  const mockUsers: User[] = [
    {
      id: 1,
      userName: "User1",
      emailAddress: "user1@example.com",
      isActive: true,
      givenName: "",
      familyName: "",
      groups: [],
      account: {
        id: 1,
        name: "GS ARM Team",
        active: true,
        emailAddress: "",
        address: "",
        accountType: {
          id: 0,
          code: "GS-ARM-TEAM",
          description: "Gold Standard ARM Team",
        },
      },
    },
    {
      id: 2,
      userName: "User2",
      emailAddress: "user2@example.com",
      isActive: false,
      givenName: "",
      familyName: "",
      groups: [],
      account: {
        id: 1,
        name: "GS ARM Team",
        active: true,
        emailAddress: "",
        address: "",
        accountType: {
          id: 0,
          code: "GS-ARM-TEAM",
          description: "Gold Standard ARM Team",
        },
      },
    },
  ];

  beforeEach(() => {
    (UserModule.getAllActiveUsers as jest.Mock).mockResolvedValue(mockUsers);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it("renders with default state for new account", async () => {
    render(
      <PlatformAccountsForm onChange={jest.fn()} onChangeSelectedUserIds={jest.fn()} onValidationChange={jest.fn()} />,
    );
    expect(((await screen.findByLabelText("Account Name")) as HTMLInputElement).value).toBe("");
    expect(((await screen.findByLabelText("Main Contact Email")) as HTMLInputElement).value).toBe("");
    expect(((await screen.findByLabelText("Main Contact Address")) as HTMLInputElement).value).toBe("");
  });

  it("correctly calls handler when account details change", async () => {
    const onChangeFunc = jest.fn();
    render(
      <PlatformAccountsForm
        onChange={onChangeFunc}
        onChangeSelectedUserIds={jest.fn()}
        onValidationChange={jest.fn()}
      />,
    );

    // Fill out the form
    const accountNameInput = await screen.findByLabelText("Account Name");
    fireEvent.change(accountNameInput, { target: { value: "New Account" } });

    const mainContactEmail = await screen.findByLabelText("Main Contact Email");
    fireEvent.change(mainContactEmail, { target: { value: "contact@example.com" } });

    const mainContactAddress = await screen.findByLabelText("Main Contact Address");
    fireEvent.change(mainContactAddress, { target: { value: "123 Main St" } });

    expect(onChangeFunc).toHaveBeenLastCalledWith({
      name: "New Account",
      emailAddress: "contact@example.com",
      active: true,
      address: "123 Main St",
      accountType: {
        id: undefined,
        code: "",
        description: "",
      },
      users: [],
      id: 0,
    });
  });

  it("correctly loads existing account details into fields", async () => {
    render(
      <PlatformAccountsForm
        defaultAccountValues={{
          id: 1,
          name: "VVB Team A",
          active: true,
          emailAddress: "team.a@vvb-gs.com",
          address: "123 Main St",
          accountType: { id: 1, code: "TYPE1", description: "Type 1" },
          users: [mockUsers[0]],
        }}
        onChange={jest.fn()}
        onChangeSelectedUserIds={jest.fn()}
        onValidationChange={jest.fn()}
      />,
    );

    expect(await screen.findByLabelText("Account Name")).toHaveValue("VVB Team A");
  });
});
