import { Button, Typography } from "@mui/material";
import EmailIcon from "@mui/icons-material/Email";
import PhonelinkIcon from "@mui/icons-material/Phonelink";

import { MfaType } from "./MfaCodeForm";

interface MfaTypeSelectionFormProps {
  onMfaTypeSelection: (selectedType: string) => void;
}

const MfaTypeSelectionForm = ({ onMfaTypeSelection }: MfaTypeSelectionFormProps) => {
  return (
    <form>
      <Typography variant="h5" gutterBottom>
        Select Verification Method
      </Typography>
      <Typography variant="body2" color="textSecondary" sx={{ mb: 2 }}>
        Your account has multi-factor authentication enabled. Please choose from one of the verification methods below
      </Typography>
      <Button
        onClick={() => onMfaTypeSelection(MfaType.SOFTWARE_TOKEN_MFA)}
        variant="outlined"
        color="primary"
        fullWidth
        sx={{ mt: 2 }}
      >
        <PhonelinkIcon /> &nbsp; Authenticator App
      </Button>
      <Button
        onClick={() => onMfaTypeSelection(MfaType.EMAIL_OTP)}
        variant="outlined"
        color="primary"
        fullWidth
        sx={{ mt: 2 }}
      >
        <EmailIcon /> &nbsp; Email One-Time-Passcode
      </Button>
    </form>
  );
};

export default MfaTypeSelectionForm;
