import CloseIcon from "@mui/icons-material/Close";
import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControlLabel,
  Grid,
  IconButton,
  Switch,
  Typography,
  useMediaQuery,
  useTheme,
} from "@mui/material";
import React, { useEffect, useState } from "react";
import { toast } from "react-toastify";

import {
  adminUpdateMfaSettings,
  AdminUpdateMfaSettingsRequest,
  getMfaSettings,
  MfaSettings,
  setupSoftwareToken,
  SoftwareTokenSetupResponse,
  updateMfaSettings,
  UpdateMfaSettingsRequest,
  UpdateMfaSettingsResponse,
} from "./MultiFactorSettings";
import SoftwareTokenSetup from "./SoftwareTokenSetup";

interface UserMultiFactorSettingsModalProps {
  isOpen: boolean;
  onClose: () => void;
  isAdminMode?: boolean;
  username?: string;
}

const UserMultiFactorSettingsModal: React.FC<UserMultiFactorSettingsModalProps> = ({
  isOpen,
  onClose,
  isAdminMode = false,
  username,
}) => {
  const [loading, setLoading] = useState(false);
  const [softwareTokenMfaEnabled, setSoftwareTokenMfaEnabled] = useState(false);
  const [emailMfaEnabled, setEmailMfaEnabled] = useState(false);
  const [initialSettings, setInitialSettings] = useState<MfaSettings | null>(null);

  const [showQrSetup, setShowQrSetup] = useState(false);
  const [qrCodeSetup, setQrCodeSetup] = useState<SoftwareTokenSetupResponse | null>(null);
  const theme = useTheme();

  const isXsScreen = useMediaQuery(theme.breakpoints.down("sm"));
  const isMediumOrSmaller = useMediaQuery(theme.breakpoints.down("md"));

  let dialogMaxWidth: "xs" | "sm" | "md" | "lg";
  if (showQrSetup) {
    dialogMaxWidth = isMediumOrSmaller ? "md" : "lg";
  } else if (isXsScreen) {
    dialogMaxWidth = "xs";
  } else {
    dialogMaxWidth = "sm";
  }

  useEffect(() => {
    if (isOpen && (!isAdminMode || username)) {
      loadMfaSettings();
    }
  }, [isOpen, isAdminMode, username]);

  const loadMfaSettings = async () => {
    try {
      setLoading(true);
      const settings = await getMfaSettings(isAdminMode ? username : undefined);
      setInitialSettings(settings);
      setSoftwareTokenMfaEnabled(settings.softwareTokenMfaEnabled);
      setEmailMfaEnabled(settings.emailMfaEnabled);
    } catch (error) {
      console.error("Failed to load MFA settings:", error);
      toast.error("Failed to load MFA settings. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const enableUserSoftwareToken = async () => {
    try {
      setLoading(true);
      const setupResponse = await setupSoftwareToken();
      setQrCodeSetup(setupResponse);
      setShowQrSetup(true);
      setSoftwareTokenMfaEnabled(true);
    } catch (error) {
      console.error("Failed to setup software token:", error);
      toast.error("Failed to setup software token. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const disableSoftwareToken = () => {
    setSoftwareTokenMfaEnabled(false);
    setShowQrSetup(false);
    setQrCodeSetup(null);
  };

  const handleSoftwareTokenToggle = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const enabled = event.target.checked;

    if (enabled) {
      if (isAdminMode) {
        return; // Admin cannot enable new tokens, only disable
      } else {
        await enableUserSoftwareToken();
      }
    } else {
      disableSoftwareToken();
    }
  };

  const handleVerificationComplete = async () => {
    setShowQrSetup(false);
    setQrCodeSetup(null);
    await loadMfaSettings();
  };

  const handleCancelQrSetup = () => {
    setShowQrSetup(false);
    setQrCodeSetup(null);
    setSoftwareTokenMfaEnabled(false);
  };

  const saveUserMfaSettings = async () => {
    const updateRequest: UpdateMfaSettingsRequest = {
      softwareTokenMfaEnabled,
      emailMfaEnabled,
    };
    return await updateMfaSettings(updateRequest);
  };

  const saveAdminMfaSettings = async () => {
    if (!username) {
      throw new Error("Username is required for admin operations");
    }
    const adminUpdateRequest: AdminUpdateMfaSettingsRequest = {
      username,
      softwareTokenMfaEnabled,
      emailMfaEnabled,
    };
    return await adminUpdateMfaSettings(adminUpdateRequest);
  };

  const updateCurrentMfaState = (response: UpdateMfaSettingsResponse) => {
    setSoftwareTokenMfaEnabled(response.softwareTokenMfaEnabled);
    setEmailMfaEnabled(response.emailMfaEnabled);
  };

  const handleSave = async () => {
    try {
      setLoading(true);

      const response = isAdminMode ? await saveAdminMfaSettings() : await saveUserMfaSettings();

      if (response.success) {
        updateCurrentMfaState(response);
        toast.success("MFA settings updated successfully!");
        onClose();
      } else {
        toast.error("Failed to update MFA settings. Please try again.");
      }
    } catch (error) {
      console.error("Failed to update MFA settings:", error);
      toast.error("Failed to update MFA settings. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const hasChanges = () => {
    if (!initialSettings) return false;

    return (
      softwareTokenMfaEnabled !== initialSettings.softwareTokenMfaEnabled ||
      emailMfaEnabled !== initialSettings.emailMfaEnabled
    );
  };

  const ADMIN_HEADER = `Admin: Multi Factor Authentication Settings for ${username}`;
  const USER_HEADER = "Multi Factor Authentication Settings";

  return (
    <Dialog
      open={isOpen}
      onClose={onClose}
      maxWidth={dialogMaxWidth}
      fullWidth
      fullScreen={isXsScreen}
      aria-labelledby="mfa-settings-dialog-title"
    >
      <DialogTitle id="mfa-settings-dialog-title" sx={{ m: 0, p: 2, color: "var(--color-text-black)" }}>
        {isAdminMode ? ADMIN_HEADER : USER_HEADER}
        <IconButton
          aria-label="close"
          onClick={onClose}
          sx={{
            position: "absolute",
            right: 8,
            top: 8,
            color: "var(--color-text-white)",
          }}
          disabled={loading}
        >
          <CloseIcon />
        </IconButton>
      </DialogTitle>

      <DialogContent dividers sx={{ pt: 3, px: 3, pb: 2 }}>
        <Grid
          container
          spacing={3}
          direction={showQrSetup ? { xs: "column", sm: "row" } : "column"}
          alignItems={showQrSetup ? { xs: "stretch", sm: "flex-start" } : "stretch"}
        >
          <Grid item xs={12} sm={showQrSetup ? 6 : 12}>
            <Typography variant="body1" sx={{ mb: 3, lineHeight: 1.6 }}>
              {isAdminMode
                ? `As an administrator, you can manage the Multi Factor Authentication settings for this user. You can disable software token MFA and enable or disable email MFA for ${username}.`
                : "Enabling 2 Factor Authentication on your account will add an extra layer of security when logging in, you will be prompted for a code aswell as your password which is unique to you. Never give this code to anyone else."}
            </Typography>

            <Box sx={{ mb: 3 }}>
              <FormControlLabel
                control={
                  <Switch checked={softwareTokenMfaEnabled} onChange={handleSoftwareTokenToggle} disabled={loading} />
                }
                data-testid={isAdminMode ? "admin-software-token-mfa-toggle" : "software-token-mfa-toggle"}
                label={
                  <Box>
                    <Typography variant="body1" sx={{ fontWeight: "bold" }}>
                      {isAdminMode
                        ? "Software Token MFA (Via Authenticator App)"
                        : "Enable Application Based MFA (Via Authenticator App)"}
                    </Typography>
                    {isAdminMode && (
                      <Typography variant="body2" color="textSecondary">
                        Admin can only disable, not set up new tokens
                      </Typography>
                    )}
                  </Box>
                }
              />
            </Box>

            {!softwareTokenMfaEnabled && !emailMfaEnabled && (
              <Box
                sx={{
                  mt: 2,
                  p: 2,
                  backgroundColor: theme.palette.warning.light,
                  borderRadius: 1,
                  border: theme.palette.warning.light,
                }}
              >
                <Typography variant="body2" color="var(--color-text-black)" sx={{ fontWeight: 500 }}>
                  Warning: Disabling all MFA methods will reduce your account security. At least one MFA method is
                  recommended.
                </Typography>
              </Box>
            )}
          </Grid>

          {showQrSetup && qrCodeSetup && !isAdminMode && (
            <Grid item xs={12} sm={6} sx={{ borderLeft: { sm: "1px solid #e0e0e0" }, pl: { sm: 2 } }}>
              <SoftwareTokenSetup
                qrCodeSetup={qrCodeSetup}
                onVerificationComplete={handleVerificationComplete}
                onCancel={handleCancelQrSetup}
                emailMfaEnabled={emailMfaEnabled}
              />
            </Grid>
          )}
        </Grid>
      </DialogContent>

      <DialogActions sx={{ px: 3, pb: 2 }}>
        {!showQrSetup && (
          <>
            <Button onClick={onClose} disabled={loading}>
              Cancel
            </Button>
            <Button variant="contained" onClick={handleSave} disabled={loading || !hasChanges()}>
              {loading ? "Saving..." : "Save"}
            </Button>
          </>
        )}
      </DialogActions>
    </Dialog>
  );
};

export default UserMultiFactorSettingsModal;
