import { AxiosResponse } from "axios";
import { createContext, ReactNode, useEffect, useMemo, useState } from "react";

import { ReferenceTypeProvider } from "../../../common/data-types/DataReferenceTypes";
import { PermissionGroup } from "../../../PermissionGroup";
import AuthenticationRepository, { LoginResponse } from "../../APIClient/AuthenticationRepository";
import { User } from "../../User/User";

export interface AuthContextType {
  isAuthenticated: boolean;
  user?: User;
  login(username: string, password: string): Promise<AxiosResponse<LoginResponse>>;
  setNewPassword(username: string, newPassword: string, givenName: string, familyName: string): Promise<void>;
  selectMfaType(username: string, challengeType: string): Promise<void>;
  respondToSoftwareTokenMfa(username: string, code: string): Promise<AxiosResponse<LoginResponse>>;
  respondToEmailOtp(username: string, code: string): Promise<AxiosResponse<LoginResponse>>;
  logout(): Promise<void>;
  isProjectDeveloperUser(): boolean;
  isAdminUser(): boolean;
  isArmUser(): boolean;
  isFinanceUser(): boolean;
  isReviewerUser(): boolean;
  isRegistryUser(): boolean;
  isTacUser(): boolean;
  isVvbUser(): boolean;
  isSupportUser(): boolean;
  isUserOwnerOf(accountId: number): boolean;
}

export const AuthContext = createContext<AuthContextType>({
  isAuthenticated: false,
  user: undefined,
  login: () => {
    throw Error("AuthContext login called too early");
  },
  setNewPassword: () => {
    throw Error("AuthContext setNewPassword called too early");
  },
  selectMfaType: () => {
    throw Error("AuthContext selectMfaType called too early");
  },
  respondToSoftwareTokenMfa: () => {
    throw Error("AuthContext respondToSoftwareTokenMfa called too early");
  },
  respondToEmailOtp: () => {
    throw Error("AuthContext respondToEmailOtp called too early");
  },
  logout: () => {
    throw Error("AuthContext logout called too early");
  },
  isProjectDeveloperUser: () => {
    throw Error("AuthContext isProjectDeveloperUser called too early");
  },
  isAdminUser: () => {
    throw Error("AuthContext isAdminUser called too early");
  },
  isArmUser: () => {
    throw Error("AuthContext isArmUser called too early");
  },
  isFinanceUser: () => {
    throw Error("AuthContext isFinanceUser called too early");
  },
  isReviewerUser: () => {
    throw Error("AuthContext isReviewerUser called too early");
  },
  isRegistryUser: () => {
    throw Error("AuthContext isRegistryUser called too early");
  },
  isTacUser: () => {
    throw Error("AuthContext isTacUser called too early");
  },
  isVvbUser: () => {
    throw Error("AuthContext isVvbUser called too early");
  },
  isSupportUser: () => {
    throw Error("AuthContext isSupportUser called too early");
  },
  isUserOwnerOf: () => {
    throw Error("AuthContext is isUserOwnerOf called too early");
  },
});

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isAuthLoading, setIsAuthLoading] = useState(true);
  const [user, setUser] = useState<User>();

  const fetchAuth = async () => {
    try {
      const response = await AuthenticationRepository.verifyToken();
      setIsAuthenticated(response.data.authenticated);
      setUser(response.data.user);
    } catch (error) {
      console.error("Error validating auth token: ", error);
    } finally {
      setIsAuthLoading(false);
    }
  };

  useEffect(() => {
    setIsAuthLoading(true);
    fetchAuth();
  }, []);

  const login = async (username: string, password: string): Promise<AxiosResponse<LoginResponse>> => {
    const loginResponse = await AuthenticationRepository.login(username, password);
    if (loginResponse.data.newPasswordRequired || loginResponse.data.mfaChallengeRequired) {
      return loginResponse;
    }

    setUser(loginResponse.data.user);
    setIsAuthenticated(true);
    return loginResponse;
  };

  const setNewPassword = async (username: string, newPassword: string, givenName: string, familyName: string) => {
    setIsAuthLoading(true);
    await AuthenticationRepository.setNewPassword(username, newPassword, givenName, familyName);
    await fetchAuth();
  };

  const selectMfaType = async (username: string, challengeType: string) => {
    await AuthenticationRepository.selectMfaType(username, challengeType);
  };

  const respondToSoftwareTokenMfa = async (username: string, code: string): Promise<AxiosResponse<LoginResponse>> => {
    const mfaResponse = await AuthenticationRepository.respondToSoftwareTokenMfa(username, code);
    if (mfaResponse.data.user) {
      setUser(mfaResponse.data.user);
      setIsAuthenticated(true);
    }
    return mfaResponse;
  };

  const respondToEmailOtp = async (username: string, code: string): Promise<AxiosResponse<LoginResponse>> => {
    const mfaResponse = await AuthenticationRepository.respondToEmailOtp(username, code);
    if (mfaResponse.data.user) {
      setUser(mfaResponse.data.user);
      setIsAuthenticated(true);
    }
    return mfaResponse;
  };

  const logout = async () => {
    await AuthenticationRepository.logout();
    setIsAuthenticated(false);
    setUser(undefined);
  };

  const userHasGroup = (permissionGroup: PermissionGroup) => {
    return user?.groups.includes(permissionGroup) || false;
  };

  const isProjectDeveloperUser = () => {
    return userHasGroup(PermissionGroup.PROJECT_DEVELOPER);
  };

  const isAdminUser = () => {
    return userHasGroup(PermissionGroup.GS_ADMIN);
  };

  const isArmUser = () => {
    return userHasGroup(PermissionGroup.ARM_TEAM);
  };

  const isFinanceUser = () => {
    return userHasGroup(PermissionGroup.FINANCE);
  };

  const isReviewerUser = () => {
    return userHasGroup(PermissionGroup.GS_REVIEWER);
  };

  const isRegistryUser = () => {
    return userHasGroup(PermissionGroup.GS_REGISTRY);
  };

  const isTacUser = () => {
    return userHasGroup(PermissionGroup.TAC);
  };

  const isVvbUser = () => {
    return userHasGroup(PermissionGroup.VVB);
  };

  const isSupportUser = () => {
    return userHasGroup(PermissionGroup.SUPPORT);
  };

  const isUserOwnerOf = (accountId: number) => {
    return accountId == user?.account?.id;
  };

  const value = useMemo<AuthContextType>(
    () => ({
      isAuthenticated,
      user,
      setNewPassword,
      login,
      logout,
      selectMfaType,
      respondToSoftwareTokenMfa,
      respondToEmailOtp,
      isProjectDeveloperUser,
      isAdminUser,
      isArmUser,
      isFinanceUser,
      isReviewerUser,
      isRegistryUser,
      isTacUser,
      isVvbUser,
      isSupportUser,
      isUserOwnerOf,
    }),
    [isAuthenticated, user],
  );

  const renderChildren = (): ReactNode => {
    if (isAuthLoading) return <></>;

    if (isAuthenticated) {
      return <ReferenceTypeProvider>{children}</ReferenceTypeProvider>;
    }
    return children;
  };
  return <AuthContext.Provider value={value}>{renderChildren()}</AuthContext.Provider>;
};
