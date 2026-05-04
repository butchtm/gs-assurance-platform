import { Button, TextField, Typography } from "@mui/material";
import { FormEvent, useState } from "react";
import PasswordChecklist from "react-password-checklist";

const FinishAccountSetupForm = ({
  submitAccountSetup,
}: {
  submitAccountSetup: (newPassword: string, givenName: string, familyName: string) => void;
}) => {
  const [newPassword, setNewPassword] = useState("");
  const [confirmNewPassword, setConfirmNewPassword] = useState("");
  const [givenName, setGivenName] = useState("");
  const [familyName, setFamilyName] = useState("");
  const [isNewPasswordValid, setIsNewPasswordValid] = useState(false);

  const submitPasswordChallenge = (event: FormEvent) => {
    event.preventDefault();
    submitAccountSetup(newPassword, givenName, familyName);
  };

  const isFormInvalid = (): boolean => {
    return !givenName || !familyName || !isNewPasswordValid;
  };

  return (
    <form onSubmit={submitPasswordChallenge}>
      <Typography variant="h6" gutterBottom>
        Finish setting up your account
      </Typography>
      <TextField
        label="First Name"
        type="text"
        value={givenName}
        onChange={(event) => setGivenName(event.target.value)}
        margin="normal"
        fullWidth
        required
        data-testid="given-name-input"
      />
      <TextField
        label="Last Name"
        type="text"
        value={familyName}
        onChange={(event) => setFamilyName(event.target.value)}
        margin="normal"
        fullWidth
        required
        data-testid="family-name-input"
      />
      <TextField
        label="New Password"
        type="password"
        value={newPassword}
        onChange={(event) => setNewPassword(event.target.value)}
        margin="normal"
        autoComplete="new-password"
        fullWidth
        data-testid="new-password-input"
      />
      <TextField
        label="Confirm New Password"
        type="password"
        value={confirmNewPassword}
        onChange={(event) => setConfirmNewPassword(event.target.value)}
        margin="normal"
        autoComplete="new-password"
        fullWidth
        data-testid="confirm-new-password-input"
      />
      <PasswordChecklist
        rules={["minLength", "specialChar", "number", "capital", "lowercase", "match"]}
        minLength={8}
        value={newPassword}
        valueAgain={confirmNewPassword}
        onChange={setIsNewPasswordValid}
      />
      <br />
      <Button disabled={isFormInvalid()} type="submit" variant="contained">
        Submit
      </Button>
    </form>
  );
};

export default FinishAccountSetupForm;
