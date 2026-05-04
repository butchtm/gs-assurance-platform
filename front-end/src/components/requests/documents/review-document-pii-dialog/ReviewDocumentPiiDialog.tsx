import { Box, Dialog, DialogActions, DialogContent, DialogTitle, Typography } from "@mui/material";
import Button from "@mui/material/Button";
import React, { useEffect, useState } from "react";

import { GSDocument } from "../../../../common/document/GSDocument";
import { GSRequest } from "../../../../common/request/GSRequest";
import { FilterableSortableTable } from "../../../../common/filterable-sortable-table/FilterableSortableTable";
import { PaginationState } from "../../../Reusable/Pagination";
import { piiHelperText } from "../../../../common/piiAnalysis/PiiAnalysis";
import CustomTooltipText, { CustomTooltip } from "../../../Reusable/FormFields/CustomTooltipText";
import { PiiActionOption, PiiActionDropdown } from "../../../../common/piiAnalysis/PiiActionDropdown";
import { useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { PiiActionSubmit } from "../../../../common/piiAnalysis/PiiActionSubmit";
import { getActionOptionByCode, getSupportedActionOptions } from "../../../../common/piiAnalysis/PiiAction";

import { getPiiResults, PageGroupedPii } from "./ReviewDocumentPii";

interface ReviewDocumentPiiDialogProps {
  isOpen: boolean;
  request: GSRequest;
  document: GSDocument;
  onClose: () => void;
  onDocumentModify: () => void;
}

const DOCUMENT_PII_ACTIONS = [
  "UNDER_INVESTIGATION",
  "ACCEPTED",
  "PII_NOT_SUITABLE",
  "DOCUMENT_REMOVED",
  "NO_PII_FOUND",
];

const basePiiTableColumns = [
  {
    accessorKey: "entity",
    header: "Entity",
    enableColumnFilter: false,
    enableSorting: false,
  },
  {
    accessorKey: "probability",
    header: "Probability",
    enableColumnFilter: false,
    enableSorting: false,
  },
  {
    accessorKey: "occurrences",
    header: "Occurrences",
    enableColumnFilter: false,
    enableSorting: false,
  },
];

const pageNumberColumn = {
  accessorKey: "pageNumber",
  header: "Page Number",
  enableColumnFilter: false,
  enableSorting: false,
};

const locationColumn = {
  accessorKey: "location",
  header: "Location",
  enableColumnFilter: false,
  enableSorting: false,
};

export const ReviewDocumentPiiDialog: React.FC<ReviewDocumentPiiDialogProps> = ({
  isOpen,
  request,
  document,
  onClose,
  onDocumentModify,
}) => {
  const [paginationState, setPaginationState] = useState<PaginationState>({
    pageIndex: 0,
    pageSize: 5,
  });
  const [rowCount, setRowCount] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [piiResults, setPiiResults] = useState<PageGroupedPii[]>([]);
  const { activeReferenceTypes } = useReferenceTypes();
  const [supportedActionOptions, setSupportedActionOptions] = useState<PiiActionOption[]>([]);
  const [selectedAction, setSelectedAction] = useState<PiiActionOption | null>(null);
  const [piiTableColumns, setPiiTableColumns] = useState(basePiiTableColumns);

  const addPageNumberColumn = () => {
    const columnsWithPageNumber = [...basePiiTableColumns.slice(0, 2), pageNumberColumn, basePiiTableColumns[2]];
    setPiiTableColumns(columnsWithPageNumber);
  };

  const addLocationColumn = () => {
    const columnsWithLocation = [...basePiiTableColumns.slice(0, 2), locationColumn, basePiiTableColumns[2]];
    setPiiTableColumns(columnsWithLocation);
  };

  const fetchPiiResults = async () => {
    setIsLoading(true);
    const piiResultsResponse = await getPiiResults(
      document.id,
      paginationState.pageIndex + 1,
      paginationState.pageSize,
    );
    if (piiResultsResponse) {
      setPiiResults(piiResultsResponse.content);
      setRowCount(piiResultsResponse.totalElements);
      if (piiResultsResponse.content[0].pageNumber) {
        addPageNumberColumn();
      }
      if (piiResultsResponse.content[0].location) {
        addLocationColumn();
      }
    }
    setIsLoading(false);
  };

  useEffect(() => {
    fetchPiiResults();
  }, [paginationState.pageIndex, paginationState.pageSize]);

  useEffect(() => {
    setSupportedActionOptions(getSupportedActionOptions(DOCUMENT_PII_ACTIONS, activeReferenceTypes));
  }, [activeReferenceTypes]);

  const handlePiiActionSubmit = () => {
    onDocumentModify();
    onClose();
  };

  const preselectedAction = getActionOptionByCode(supportedActionOptions, document.piiAction);

  return (
    <Dialog open={isOpen} aria-labelledby="view-document-pii-dialog-title">
      <DialogTitle id="view-document-pii-dialog-title">Document PII Results</DialogTitle>
      <DialogContent>
        <Box sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
          <Box>
            <Typography component="span" variant="body1" sx={{ fontWeight: "bold", mr: 2 }}>
              File Name:
            </Typography>
            <Typography component="span" variant="body1">
              {document.fileName}
            </Typography>
          </Box>
          <Box>
            <Typography component="span" variant="body1" sx={{ fontWeight: "bold", mr: 2 }}>
              Request Name:
            </Typography>
            <Typography component="span" variant="body1">
              {request.reviewName}
            </Typography>
          </Box>
          <Box>
            <Typography component="span" variant="body1" sx={{ fontWeight: "bold", mr: 2 }}>
              Current Action:
            </Typography>
            <Typography component="span" variant="body1">
              {preselectedAction?.label ?? "No Action Taken"}
            </Typography>
          </Box>
          <PiiActionDropdown
            supportedActions={supportedActionOptions}
            selectedAction={preselectedAction}
            onActionChange={setSelectedAction}
          />
          <Box>
            <Typography component="span" variant="body1" sx={{ fontWeight: "bold" }}>
              Identified PII
              <CustomTooltipText
                styleOverrides={{ paddingBottom: "8px" }}
                text={piiHelperText}
                type={CustomTooltip.HELP}
              />
            </Typography>
            <Box sx={{ textAlign: "center" }}>
              <FilterableSortableTable
                columns={piiTableColumns}
                data={piiResults}
                onPaginationChange={setPaginationState}
                state={{
                  pagination: paginationState,
                  isLoading: isLoading,
                }}
                rowCount={rowCount}
                enableFilters={false}
                enableGlobalFilter={false}
                enablePagination={true}
                enableBottomToolbar={false}
              />
            </Box>
          </Box>
        </Box>
      </DialogContent>
      <DialogActions>
        <Button variant="outlined" onClick={onClose}>
          Close
        </Button>
        <PiiActionSubmit
          projectId={request.project.id}
          requestId={request.id}
          documentId={document.id}
          selectedAction={selectedAction}
          onSubmit={handlePiiActionSubmit}
        />
      </DialogActions>
    </Dialog>
  );
};
