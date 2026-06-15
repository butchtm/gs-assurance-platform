import CssBaseline from "@mui/material/CssBaseline";
import { ThemeProvider } from "@mui/material/styles";
import React from "react";
import { Route, BrowserRouter as Router, Routes } from "react-router-dom";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";

import Footer from "./common/footer/Footer";
import AccessibilityStatement from "./common/footer/policies/AccessibilityStatement";
import PrivacyPolicy from "./common/footer/policies/PrivacyPolicy";
import NoAccess from "./common/full-screen-message/NoAccess";
import Unauthorized from "./common/full-screen-message/Unauthorized";
import { Header } from "./common/header/Header";
import { AdministrationLandingPage } from "./components/Administration/AdministrationLandingPage";
import ForgotPassword from "./components/Auth/Credentials/ForgotPassword";
import { AuthProvider } from "./components/Auth/Login/AuthProvider";
import LoginPage from "./components/Auth/Login/LoginPage";
import CookieConsent from "./components/cookie-consent/CookieConsent";
import { PlatformAccountsList } from "./components/PlatformAccounts/PlatformAccountsList";
import PlatformAccountView from "./components/PlatformAccounts/PlatformAccountView";
import { CreateProjectWizard } from "./components/Project/create-project/CreateProject";
import { ProjectList } from "./components/Project/project-list/ProjectList";
import { ProjectTreeList } from "./components/Project/project-list/ProjectTreeList";
import UpdateProject from "./components/Project/update-project/UpdateProject";
import UpdateProjectStatus from "./components/Project/update-project/UpdateProjectStatus";
import { CreditingPeriodDetail } from "./components/Project/view-project/key-project-information/CreditingPeriodDetail";
import ViewProject from "./components/Project/view-project/ViewProject";
import ProjectDocumentsList from "./components/Public/ProjectDocuments/PublicProjectRequestList";
import PublicConsultationPage from "./components/Public/PublicConsultation/PublicConsultationPage";
import PublicConsultationProjectOnlyPage from "./components/Public/PublicConsultation/PublicConsultationProjectOnlyPage";
import PublicConsultationList from "./components/Public/PublicConsultation/request-list/PublicConsultationList";
import DocumentUploadFileType from "./components/requests/actions/documents/DocumentUploadFileType";
import { AllRequestsTable } from "./components/requests/all-requests/AllRequestsTable";
import CreateRequestWizard from "./components/requests/create-request/Wizard/CreateRequestWizard";
import { CommonDashboardTable } from "./components/requests/dashboard/CommonDashboardTable";
import MyRequests from "./components/requests/my-requests/MyRequests";
import UpdateRequestWizard from "./components/requests/update-request/UpdateRequestWizard";
import { UsersList } from "./components/User/UsersList";
import { PermissionGroup } from "./PermissionGroup";
import { ProtectedRoute } from "./ProtectedRoute";
import { PublicOnlyRoute } from "./PublicOnlyRoute";
import { GlobalStylesComponent, theme } from "./theme";
import { FormLandingPage } from "./components/Forms/FormLandingPage";

const queryClient = new QueryClient();

const App: React.FC = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <GlobalStylesComponent>
          <AuthProvider>
            <Router>
              <div className="App">
                <Header />
                <div className="content">
                  <div className="body">
                    <Routes>
                      <Route path="/public-consultations" element={<PublicConsultationList />} />
                      <Route path="/public-consultations/request/:requestId" element={<PublicConsultationPage />} />
                      <Route
                        path="/public-consultations/project/:gsId"
                        element={<PublicConsultationProjectOnlyPage />}
                      />
                      <Route path="/project-documents/:gsId" element={<ProjectDocumentsList />} />
                      <Route path="/project-document-types" element={<DocumentUploadFileType />} />
                      <Route path="/privacy-policy" element={<PrivacyPolicy />} />
                      <Route path="/accessibility-statement" element={<AccessibilityStatement />} />
                      <Route path="/Unauthorized" element={<Unauthorized />} />
                      <Route path="/NoAccess" element={<NoAccess />} />
                      <Route path="/forgot-password" element={<PublicOnlyRoute element={<ForgotPassword />} />} />
                      <Route path="/" element={<PublicOnlyRoute element={<LoginPage />} />} />
                      <Route path="/login" element={<PublicOnlyRoute element={<LoginPage />} />} />
                      <Route
                        path="/accounts"
                        element={
                          <ProtectedRoute
                            element={<PlatformAccountsList />}
                            roles={[PermissionGroup.GS_ADMIN, PermissionGroup.ARM_TEAM]}
                          />
                        }
                      />
                      <Route
                        path="/account/:accountId"
                        element={
                          <ProtectedRoute
                            element={<PlatformAccountView />}
                            roles={[PermissionGroup.GS_ADMIN, PermissionGroup.ARM_TEAM]}
                          />
                        }
                      />
                      <Route
                        path="/users"
                        element={<ProtectedRoute element={<UsersList />} roles={[PermissionGroup.GS_ADMIN]} />}
                      />
                      <Route path="/my-requests" element={<ProtectedRoute element={<MyRequests />} />} />
                      <Route
                        path="/all-requests"
                        element={
                          <ProtectedRoute
                            element={<AllRequestsTable />}
                            roles={[PermissionGroup.ARM_TEAM, PermissionGroup.GS_ADMIN]}
                          />
                        }
                      />
                      <Route
                        path="/common-dashboard"
                        element={
                          <ProtectedRoute
                            element={<CommonDashboardTable />}
                            roles={[PermissionGroup.ARM_TEAM, PermissionGroup.GS_ADMIN, PermissionGroup.SUPPORT]}
                          />
                        }
                      />
                      <Route
                        path="/projects"
                        element={
                          <ProtectedRoute
                            element={<ProjectList />}
                            roles={[
                              PermissionGroup.GS_ADMIN,
                              PermissionGroup.GS_REVIEWER,
                              PermissionGroup.GS_REGISTRY,
                              PermissionGroup.ARM_TEAM,
                              PermissionGroup.VVB,
                              PermissionGroup.TAC,
                              PermissionGroup.FINANCE,
                              PermissionGroup.SUPPORT,
                            ]}
                          />
                        }
                      />
                      <Route
                        path="/project-trees"
                        element={
                          <ProtectedRoute element={<ProjectTreeList />} roles={[PermissionGroup.PROJECT_DEVELOPER]} />
                        }
                      />
                      <Route
                        path="/projects/:projectId/:activeTab?"
                        element={<ProtectedRoute element={<ViewProject />} />}
                      />
                      <Route
                        path="/projects/creditingPeriods/:periodId"
                        element={<ProtectedRoute element={<CreditingPeriodDetail />} />}
                      />
                      <Route
                        path="/create-new-project"
                        element={
                          <ProtectedRoute
                            element={<CreateProjectWizard />}
                            roles={[
                              PermissionGroup.GS_ADMIN,
                              PermissionGroup.ARM_TEAM,
                              PermissionGroup.PROJECT_DEVELOPER,
                            ]}
                          />
                        }
                      />
                      <Route
                        path="/update-project"
                        element={
                          <ProtectedRoute
                            element={<UpdateProject />}
                            roles={[
                              PermissionGroup.GS_ADMIN,
                              PermissionGroup.ARM_TEAM,
                              PermissionGroup.PROJECT_DEVELOPER,
                            ]}
                          />
                        }
                      />
                      <Route
                        path="/update-project-status"
                        element={
                          <ProtectedRoute
                            element={<UpdateProjectStatus />}
                            roles={[PermissionGroup.GS_ADMIN, PermissionGroup.ARM_TEAM]}
                          />
                        }
                      />
                      <Route
                        path="/create-request"
                        element={
                          <ProtectedRoute
                            element={<CreateRequestWizard />}
                            roles={[
                              PermissionGroup.GS_ADMIN,
                              PermissionGroup.ARM_TEAM,
                              PermissionGroup.PROJECT_DEVELOPER,
                            ]}
                          />
                        }
                      />
                      <Route
                        path="/update-request"
                        element={
                          <ProtectedRoute
                            element={<UpdateRequestWizard />}
                            roles={[
                              PermissionGroup.GS_ADMIN,
                              PermissionGroup.ARM_TEAM,
                              PermissionGroup.PROJECT_DEVELOPER,
                              PermissionGroup.VVB,
                              PermissionGroup.GS_REVIEWER,
                              PermissionGroup.GS_REGISTRY,
                            ]}
                          />
                        }
                      />
                      <Route
                        path="/administration"
                        element={
                          <ProtectedRoute
                            element={<AdministrationLandingPage />}
                            roles={[PermissionGroup.GS_ADMIN, PermissionGroup.ARM_TEAM]}
                          />
                        }
                      />
                      <Route
                        path="/projects/:projectId/requests/:requestId/form/:formId"
                        element={<ProtectedRoute element={<FormLandingPage />} />}
                      />
                    </Routes>
                  </div>
                </div>
                <ToastContainer position={toast.POSITION.BOTTOM_RIGHT} />
                <CookieConsent />
                <Footer />
              </div>
            </Router>
          </AuthProvider>
        </GlobalStylesComponent>
      </ThemeProvider>
    </QueryClientProvider>
  );
};

export default App;
