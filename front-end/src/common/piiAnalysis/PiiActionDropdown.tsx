import { Autocomplete, FormControl, TextField } from "@mui/material";

import { PermissionGroup } from "../../PermissionGroup";
import ProtectedComponent from "../../ProtectedComponent";

export interface PiiActionOption {
  value: number;
  label: string;
  code?: string;
}

interface PiiActionDropdownProps {
  supportedActions: PiiActionOption[];
  selectedAction: PiiActionOption | null;
  onActionChange: (selectedAction: PiiActionOption | null) => void;
}

export const PiiActionDropdown: React.FC<PiiActionDropdownProps> = ({
  supportedActions,
  selectedAction,
  onActionChange,
}: PiiActionDropdownProps) => {
  return (
    <ProtectedComponent roles={[PermissionGroup.ARM_TEAM, PermissionGroup.GS_ADMIN]}>
      <FormControl fullWidth margin="normal">
        <Autocomplete
          id={"pii-action-autocomplete"}
          options={supportedActions}
          getOptionLabel={(option) => (option ? option.label : "")}
          isOptionEqualToValue={(option, value) => option == value}
          value={selectedAction}
          renderInput={(params) => (
            <TextField
              {...params}
              label={"Action"}
              variant="outlined"
              required={true}
              data-testid={"pii-action-select"}
            />
          )}
          onChange={(_, newAction) => {
            onActionChange(newAction);
          }}
        />
      </FormControl>
    </ProtectedComponent>
  );
};
