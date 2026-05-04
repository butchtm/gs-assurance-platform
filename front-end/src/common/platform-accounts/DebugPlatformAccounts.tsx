import { PlatformAccount } from "../../components/PlatformAccounts/PlatformAccountTypes";
import { getMockedUsersByAccountId } from "../user/DebugUser";

export const mockPlatformAccounts: PlatformAccount[] = [
  {
    id: 1,
    accountType: { id: 1, code: "PROJECT-DEVELOPER", description: "Desc1" },
    name: "Account One",
    emailAddress: "accountone@example.com",
    address: "123 First Street",
    users: getMockedUsersByAccountId(1),
    active: true,
  },
  {
    id: 2,
    accountType: { id: 1, code: "VVB", description: "VVB" },
    name: "Account Two",
    emailAddress: "accounttwo@example.com",
    address: "456 Second Avenue",
    users: getMockedUsersByAccountId(2),
    active: false,
  },
  {
    id: 3,
    accountType: { id: 3, code: "Type3", description: "Desc3" },
    name: "Account Three",
    emailAddress: "accountthree@example.com",
    address: "789 Third Boulevard",
    users: getMockedUsersByAccountId(3),
    active: true,
  },
  {
    id: 4,
    accountType: { id: 4, code: "Type4", description: "Desc4" },
    name: "Account Four",
    emailAddress: "accountthree@example.com",
    address: "789 Third Boulevard",
    users: getMockedUsersByAccountId(4),
    active: true,
  },
];
