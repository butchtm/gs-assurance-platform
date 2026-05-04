import { Box, Button, TextField, Typography } from "@mui/material";
import React, { useState } from "react";
import QRCode from "react-qr-code";
import { toast } from "react-toastify";

import { SoftwareTokenSetupResponse, verifySoftwareToken } from "./MultiFactorSettings";

interface SoftwareTokenSetupProps {
  qrCodeSetup: SoftwareTokenSetupResponse;
  onVerificationComplete: () => void;
  onCancel: () => void;
  emailMfaEnabled: boolean;
}

const SoftwareTokenSetup: React.FC<SoftwareTokenSetupProps> = ({
  qrCodeSetup,
  onVerificationComplete,
  onCancel,
  emailMfaEnabled,
}) => {
  const [verificationCode, setVerificationCode] = useState("");
  const [isVerifying, setIsVerifying] = useState(false);

  const handleVerifyToken = async () => {
    if (!verificationCode) {
      toast.error("Please enter the verification code from your authenticator app.");
      return;
    }

    try {
      setIsVerifying(true);
      const verifyResponse = await verifySoftwareToken({
        code: verificationCode,
        emailMfaEnabled,
      });

      if (verifyResponse.verified) {
        toast.success("Software token setup completed successfully!");
        onVerificationComplete();
      } else {
        toast.error("Invalid verification code. Please try again.");
      }
    } catch (error) {
      console.error("Failed to verify software token:", error);
      toast.error("Failed to verify software token. Please try again.");
    } finally {
      setIsVerifying(false);
    }
  };

  return (
    <Box sx={{ ml: 4, mt: 2, p: 2, borderRadius: 1 }}>
      <Typography variant="h6" sx={{ mb: 2, color: "rgb(0, 186, 190)" }}>
        Setup Authenticator App
      </Typography>

      <Typography variant="body2" sx={{ mb: 2 }}>
        Scan this QR code with your authenticator app (Google Authenticator, Microsoft Authenticator, etc.):
      </Typography>

      <Box sx={{ display: "flex", justifyContent: "center", mb: 2 }}>
        <QRCode value={qrCodeSetup.otpAuthUrl} size={200} />
      </Box>

      <Typography variant="body2" sx={{ mb: 2, color: "text.secondary" }}>
        Or manually enter this code. Do not share this code with anyone: <strong>{qrCodeSetup.secretCode}</strong>
      </Typography>

      <TextField
        fullWidth
        label="Enter verification code from your app"
        value={verificationCode}
        onChange={(e) => setVerificationCode(e.target.value)}
        disabled={isVerifying}
        sx={{ mb: 2 }}
        data-testid="verification-code-input"
      />

      <Box sx={{ display: "flex", gap: 1 }}>
        <Button
          variant="contained"
          onClick={handleVerifyToken}
          disabled={isVerifying || !verificationCode}
          sx={{
            backgroundColor: "rgb(0, 186, 190)",
            "&:hover": {
              backgroundColor: "rgb(0, 166, 170)",
            },
          }}
        >
          {isVerifying ? "Verifying..." : "Verify & Complete"}
        </Button>
        <Button variant="outlined" onClick={onCancel} disabled={isVerifying}>
          Cancel
        </Button>
      </Box>
    </Box>
  );
};

export default SoftwareTokenSetup;
