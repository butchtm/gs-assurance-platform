import { Box, Grid, Typography } from "@mui/material";
import React, { useContext, useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import { AccountTypeIds } from "../../AccountType";
import { ReferenceType, ReferenceTypeContext } from "../../common/data-types/DataReferenceTypes";

import { EditAccountCompetenciesModal } from "./Modal/EditAccountCompetenciesModal";
import EditAccountModal from "./Modal/EditAccountModal";
import { getAccount, getAccountCompetencies, getAccounts } from "./PlatformAccount";
import {
  CompetenciesDisplay,
  getRefDataNameForCompetencyKey,
  PlatformAccount,
  UpdateCreateCompetenciesPayload,
} from "./PlatformAccountTypes";
import { AccountCompetenciesCard } from "./ViewPlatformAccount/AccountCompetenciesCard";
import AccountDetailsCard from "./ViewPlatformAccount/AccountDetailsCard";
import { AccountUsersCard } from "./ViewPlatformAccount/AccountUsersCard";

const PlatformAccountView: React.FC = (): JSX.Element => {
  const [account, setAccount] = useState<PlatformAccount | undefined>(undefined);
  const [accounts, setAccounts] = useState<PlatformAccount[]>([]);
  const [isEditAccountModalOpen, setIsEditAccountModalOpen] = useState<boolean>(false);
  const [isEditCompetenciesModalOpen, setIsEditCompetenciesModalOpen] = useState<boolean>(false);
  const [competencies, setCompetencies] = useState<CompetenciesDisplay | undefined>(undefined);
  const [shouldDisplayCompetencies, setShouldDisplayCompetencies] = useState<boolean>(false);
  const { accountId } = useParams();
  const { activeReferenceTypes } = useContext(ReferenceTypeContext);

  const refreshAccount = () => {
    const fetchAccountById = async () => {
      const response = await getAccount(Number(accountId));
      setAccount(response);
    };
    fetchAccountById();
  };

  useEffect(() => {
    refreshAccount();
  }, []);

  useEffect(() => {
    const fetchAccounts = async () => {
      try {
        const filters = { accountTypeIds: [AccountTypeIds.PROJECT_DEVELOPER, AccountTypeIds.VVB] };
        const fetchedAccounts = await getAccounts(filters);
        setAccounts(fetchedAccounts.content);
      } catch (error) {
        console.error("Failed to fetch accounts:", error);
      }
    };

    const fetchCompetencies = async () => {
      await fetchAccounts();
      const fetchedCompetencies = await getAccountCompetencies(Number(accountId));
      if (fetchedCompetencies) {
        const transformedCompetencies = transformCompetencies(fetchedCompetencies);
        setCompetencies(transformedCompetencies);
      }
    };

    const shouldShowCompetencies =
      account?.accountType?.id != null &&
      [AccountTypeIds.VVB, AccountTypeIds.GS_REVIEWER, AccountTypeIds.TAC].includes(account?.accountType.id);
    setShouldDisplayCompetencies(shouldShowCompetencies);
    if (shouldShowCompetencies) {
      fetchCompetencies();
    }
  }, [account]);

  const handleOpenEditAccount = () => {
    setIsEditAccountModalOpen(true);
  };

  const handleOpenEditCompetencies = () => {
    setIsEditCompetenciesModalOpen(true);
  };

  const handleCloseEditCompetencies = () => {
    refreshAccount();
    setIsEditCompetenciesModalOpen(false);
  };

  const handleCloseEditModal = () => {
    refreshAccount();
    setIsEditAccountModalOpen(false);
  };

  const transformCompetencies = (competenciesToTransform: UpdateCreateCompetenciesPayload): CompetenciesDisplay => {
    const displayValues: CompetenciesDisplay = {
      estimatedAnnualEmissionReductionAmount: null,
      projectTypeIds: [],
      projectLocationIds: [],
      projectScaleIds: [],
      projectDeveloperIds: [],
      methodologyIds: [],
      requestTypeIds: [],
      certifiedProductIds: [],
      sustainableDevelopmentGoalIds: [],
    };

    (Object.keys(competenciesToTransform) as Array<keyof UpdateCreateCompetenciesPayload>).forEach((key) => {
      const values = competenciesToTransform[key];
      const refDataName = getRefDataNameForCompetencyKey(key);

      let refDataFiltered: ReferenceType[] | null = null;
      let refDataDescriptions: string[] = [];

      if (Array.isArray(values) && typeof values[0] === "number") {
        if (key === "projectDeveloperIds" || key === "vvbAssignedIds") {
          displayValues[key] = accounts.filter((acc) => (values as number[]).includes(acc.id)).map((acc) => acc.name);
        } else {
          refDataFiltered = activeReferenceTypes[refDataName].filter((ref: ReferenceType) =>
            (values as number[]).includes(ref.id),
          );
          refDataDescriptions = refDataFiltered.map((ref) => ref.description);
          (displayValues[key] as string[]) = refDataDescriptions;
        }
      } else if (typeof values === "number" && key !== "accountId") {
        (displayValues[key] as string[]) = [values.toLocaleString()];
      }
    });
    return displayValues;
  };

  return (
    <>
      <Box className="view-container" sx={{ display: "flex", flexDirection: "column", gap: 2, height: "100%" }}>
        <Box className="title" mb={2}>
          {account?.name}
        </Box>
        <Grid container spacing={2} sx={{ flexGrow: 1 }}>
          <Grid item xs={12} md={6} sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
            <Box sx={{ flexGrow: 1, display: "flex", flexDirection: "column" }}>
              {account ? (
                <AccountDetailsCard account={account} onEditClick={handleOpenEditAccount} />
              ) : (
                <Typography variant="body1">Loading account details...</Typography>
              )}
            </Box>

            {shouldDisplayCompetencies && (
              <Box sx={{ flexGrow: 1 }}>
                <AccountCompetenciesCard handleOpenEditModal={handleOpenEditCompetencies} competencies={competencies} />
              </Box>
            )}
          </Grid>

          <Grid item xs={12} md={6} sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
            <Box sx={{ flexGrow: 1 }}>
              <AccountUsersCard handleOpenEditModal={handleOpenEditAccount} account={account} />
            </Box>
          </Grid>
        </Grid>
      </Box>
      {account && <EditAccountModal isOpen={isEditAccountModalOpen} account={account} onClose={handleCloseEditModal} />}

      {account && (
        <EditAccountCompetenciesModal
          isOpen={isEditCompetenciesModalOpen}
          account={account}
          accounts={accounts}
          onClose={handleCloseEditCompetencies}
        />
      )}
    </>
  );
};

export default PlatformAccountView;
