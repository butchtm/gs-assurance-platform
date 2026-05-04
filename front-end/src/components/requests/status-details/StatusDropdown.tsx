import { Grid } from "@mui/material";
import { useEffect, useState } from "react";

import { Assignment } from "../../../components/requests/assignment/Assignment";
import { GSDocument } from "../../../common/document/GSDocument";
import { GSDocumentScrap } from "../../../common/document/GSDocumentScrap";
import { GSRequest, hasCancelledStatus, RequestConsultation, RequestRound } from "../../../common/request/GSRequest";
import { PermissionGroup } from "../../../PermissionGroup";
import ProtectedComponent from "../../../ProtectedComponent";
import { RequiredDocumentsResponse } from "../Requests";
import "../RequestsView.scss";
import { RequestSectionHeader } from "../reusable/RequestSectionHeader";

import { StatusDropdownConsultationPanel } from "./StatusDropdownConsultationPanel";
import { StatusDropdownInvoicePanel } from "./StatusDropdownInvoicePanel";
import { StatusDropdownRequestPanel } from "./StatusDropdownRequestPanel";
import { StatusDropdownReviewPanel } from "./StatusDropdownReviewPanel";
import { CancellationReason } from "./CancellationReason";

const countMatchingDocumentTypes = (requiredDocuments: GSDocumentScrap[], requestDocuments: GSDocument[]): number => {
  const matchingDocuments = requestDocuments.filter((requestDoc) =>
    requiredDocuments.some((requiredDoc) => requiredDoc.documentTypeId === requestDoc.documentTypeId),
  );

  return matchingDocuments.length;
};

export const StatusDropdown = (props: {
  request: GSRequest;
  dashboardFormat: boolean;
  refreshRequest: boolean;
  requestDocuments: GSDocument[];
  consultationDetails?: RequestConsultation[];
  requestTypeRequiredDocs: RequiredDocumentsResponse;
  consultations?: RequestConsultation[];
  assignments: Assignment[];
}) => {
  const [currentRoundOfReview, setCurrentRoundOfReview] = useState<RequestRound>();
  const [documentStatus, setDocumentStatus] = useState<string>("");

  useEffect(() => {
    //Load current round of review info
    const currentROfR = props.request.requestRounds?.find(
      (round) => round.roundIndex === props.request.currentRoundIndex,
    );
    if (currentROfR) {
      setCurrentRoundOfReview(currentROfR);
    }

    const getRequiredDocumentsCount = () => {
      const { request } = props;

      if (!request?.project?.id || !request.requestType?.id) {
        return;
      }

      try {
        const requiredDocumentsResponse = props.requestTypeRequiredDocs;
        const requiredDocuments = requiredDocumentsResponse.REQUIRED || [];
        const requiredDocumentsCount = requiredDocuments.length;

        const requestDocuments = props.requestDocuments;
        const matchingDocumentsCount = countMatchingDocumentTypes(requiredDocuments, requestDocuments);

        const matchingDocumentString = `${matchingDocumentsCount} / ${requiredDocumentsCount}`;

        setDocumentStatus(matchingDocumentString);
      } catch (error) {
        console.error("Error fetching required document count:", error);
      }
    };
    getRequiredDocumentsCount();
  }, [props.refreshRequest, props.requestDocuments]);

  return (
    <>
      <RequestSectionHeader name="Status" />
      <Grid container spacing={props.dashboardFormat ? 1 : 2}>
        <Grid container item direction="column" xs={12} md={12} lg={props.dashboardFormat ? 12 : 6}>
          <StatusDropdownRequestPanel
            request={props.request}
            dashboardFormat={props.dashboardFormat}
            documentStatus={documentStatus}
          ></StatusDropdownRequestPanel>
        </Grid>
        <Grid container item direction="column" xs={12} md={12} lg={props.dashboardFormat ? 12 : 6}>
          <StatusDropdownReviewPanel
            request={props.request}
            currentRoundOfReview={currentRoundOfReview}
            assignments={props.assignments}
          ></StatusDropdownReviewPanel>
          {props.request.inStakeholderConsultation && props.consultations && (
            <StatusDropdownConsultationPanel consultations={props.consultations}></StatusDropdownConsultationPanel>
          )}
          {hasCancelledStatus(props.request) && props.request.cancellationReason && (
            <CancellationReason request={props.request} />
          )}
          {props.request.invoiceId !== undefined && props.request.invoiceId !== null && (
            <ProtectedComponent
              roles={[
                PermissionGroup.FINANCE,
                PermissionGroup.GS_ADMIN,
                PermissionGroup.ARM_TEAM,
                PermissionGroup.SUPPORT,
              ]}
            >
              <StatusDropdownInvoicePanel invoiceId={props.request.invoiceId} refreshRequest={props.refreshRequest} />
            </ProtectedComponent>
          )}
        </Grid>
      </Grid>
    </>
  );
};
