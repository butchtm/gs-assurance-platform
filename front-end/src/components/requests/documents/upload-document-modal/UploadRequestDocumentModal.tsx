import CloseIcon from "@mui/icons-material/Close";
import { Autocomplete, FormControlLabel, IconButton, Switch } from "@mui/material";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import Modal from "@mui/material/Modal";
import TextField from "@mui/material/TextField";
import Typography from "@mui/material/Typography";
import dayjs from "dayjs";
import React, { useContext, useEffect, useState } from "react";
import { toast } from "react-toastify";

import { ReferenceType, useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { GSDocument } from "../../../../common/document/GSDocument";
import { canPiiActionBeMadePublic } from "../../../../common/piiAnalysis/PiiAction";
import { piiHelperText, publicSwitchDisabledPiiHelperText } from "../../../../common/piiAnalysis/PiiAnalysis";
import { GSRequest } from "../../../../common/request/GSRequest";
import { PermissionGroup } from "../../../../PermissionGroup";
import ProtectedComponent from "../../../../ProtectedComponent";
import { AuthContext } from "../../../Auth/Login/AuthProvider";
import CustomTooltipText, { CustomTooltip } from "../../../Reusable/FormFields/CustomTooltipText";
import { helperTexts } from "../../../Project/create-project/HelperText";
import { getAllRequestForms, RequestForm } from "../../../Forms/Forms";
import { RequiredDocumentsResponse } from "../../Requests";

import { checkFileSupportedForPiiScan, DocumentType, modifyDocument, uploadDocument } from "./UploadRequestDocument";

interface UploadDocumentModalProps {
  isOpen: boolean;
  projectId: number | null;
  request: GSRequest;
  edit: boolean;
  document: GSDocument | null;
  onClose: () => void;
  requiredDocuments?: RequiredDocumentsResponse;
}

const UploadDocumentModal: React.FC<UploadDocumentModalProps> = ({
  isOpen,
  projectId,
  request,
  edit,
  document,
  onClose,
  requiredDocuments,
}) => {
  const [documentTypes, setDocumentTypes] = useState<DocumentType[] | undefined>([]);
  const [selectedDocumentTypeId, setSelectedDocumentTypeId] = useState<number | null>(null);
  const [file, setFile] = useState<File | null>(null);
  const [access, setAccess] = useState<boolean>(false);
  const [publicPrivateToggleDisabled, setPublicPrivateToggleDisabled] = useState<boolean>(true);
  const [fileName, setFileName] = useState("");
  const { activeReferenceTypes, allReferenceTypes } = useReferenceTypes();
  const [isFilePiiScannable, setIsFilePiiScannable] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string>("");
  const authContext = useContext(AuthContext);

  useEffect(() => {
    const getDocumentTypes = (): ReferenceType[] => {
      if (request.migrated) {
        return allReferenceTypes?.DocumentType;
      }
      return activeReferenceTypes?.DocumentType;
    };

    const filterDocumentTypesForUser = (documentTypes: ReferenceType[]): ReferenceType[] => {
      if (authContext.isReviewerUser()) {
        return documentTypes.filter((type) => type.code == "REVIEW_FEEDBACK_FORM");
      }
      return documentTypes;
    };

    const filterDocumentTypesByForms = async (documentTypes: ReferenceType[]) => {
      const response = await getAllRequestForms(request.id);
      if (response && requiredDocuments?.REQUIRED) {
        const formTypeIds = response.map((form: RequestForm) => form.type.id);
        const equivalentDocTypeIds = requiredDocuments.REQUIRED.filter(
          (docType) => docType.formType?.id !== undefined && formTypeIds.includes(docType.formType?.id),
        ).map((x) => x.documentTypeId);
        return documentTypes.filter((docType) => !equivalentDocTypeIds.includes(docType.id));
      }
      return documentTypes;
    };

    const updateDocumentTypes = async () => {
      const types = await filterDocumentTypesByForms(filterDocumentTypesForUser(getDocumentTypes()));
      setDocumentTypes(types);
      if (types.length === 1) {
        setSelectedDocumentTypeId(types[0].id);
      }
    };

    if (isOpen) {
      updateDocumentTypes();
    }
  }, [isOpen, activeReferenceTypes, allReferenceTypes]);

  useEffect(() => {
    if (document) {
      setFileName(document.fileName);

      if (documentTypes?.some((type) => type.id === document.documentTypeId)) {
        setSelectedDocumentTypeId(document.documentTypeId);
      }

      setAccess(document.access);
    }
  }, [document?.fileName, document, documentTypes]);

  useEffect(() => {
    if (isUploadedByReviewer()) {
      setPublicPrivateToggleDisabled(true);
    } else if (authContext.isAdminUser()) {
      setPublicPrivateToggleDisabled(false);
    } else {
      setPublicPrivateToggleDisabled(!canPiiActionBeMadePublic(document?.piiAction));
    }
  });

  const isUploadedByReviewer = (): boolean => {
    const accountType = document?.uploadedByAccountType;
    if (!accountType) {
      return false;
    }
    return accountType.code === "REVIEWER";
  };

  const handleFileChange = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = event.target.files ? event.target.files[0] : null;
    if (selectedFile) {
      setIsFilePiiScannable(await checkFileSupportedForPiiScan(selectedFile.type, selectedFile.size));
    }
    setFile(selectedFile);
    setFileName(selectedFile ? selectedFile.name : "");
    setErrorMessage(""); // Clear error when user selects a new file
  };

  const submitModifyDocument = async () => {
    if (selectedDocumentTypeId == null) {
      const errMsg = "No document type chosen";
      setErrorMessage(errMsg);
      toast.error(errMsg);
      return;
    }

    if (document) {
      try {
        await modifyDocument(document.id, selectedDocumentTypeId, access, document.category, document.latest);
        toast.success("Modified document successfully");
        setErrorMessage(""); // Clear error on success
        onClose();
      } catch {
        const errMsg = "Error modifying document";
        setErrorMessage(errMsg);
        toast.error(errMsg);
      }
    }
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setErrorMessage(""); // Clear error on new submission attempt

    if (document && edit) {
      submitModifyDocument();
      return;
    }

    if (selectedDocumentTypeId == undefined || !file) {
      const errMsg = "No file or document type chosen";
      setErrorMessage(errMsg);
      toast.error(errMsg);
      return;
    }

    const uploadToast = toast.info("Uploading document...", {
      autoClose: false,
      closeButton: false,
      draggable: false,
      closeOnClick: false,
    });

    const response = await uploadDocument(projectId, request, selectedDocumentTypeId, file);

    if (!response.success) {
      const serverInfo = response.serverHeader ? ` (${response.serverHeader})` : "";
      const errMsg = "Failed to upload document: " + response.detail + serverInfo;
      setErrorMessage(errMsg);
      toast.update(uploadToast, {
        render: errMsg,
        type: toast.TYPE.ERROR,
        autoClose: 5000,
        closeButton: true,
      });
      return;
    }

    toast.update(uploadToast, {
      render: "Document has uploaded successfully",
      type: toast.TYPE.SUCCESS,
      autoClose: 3000,
      closeButton: true,
    });
    setErrorMessage(""); // Clear error on success
    onClose();
  };

  const getDocumentType = () => {
    if (!documentTypes) {
      return null;
    }

    const matchedDocType = documentTypes.find((documentType) => documentType.id == selectedDocumentTypeId);
    if (matchedDocType) {
      return matchedDocType;
    }
    return null;
  };

  const getPublicAccessDisabledHelperText = () => {
    if (isUploadedByReviewer()) {
      return <>This Document cannot be made public due to it being uploaded by a reviewer.</>;
    }
    return publicSwitchDisabledPiiHelperText;
  };

  return (
    <Modal open={isOpen} onClose={onClose}>
      <Box
        sx={{
          position: "absolute",
          top: "50%",
          left: "50%",
          width: "50%",
          transform: "translate(-50%, -50%)",
          bgcolor: "background.paper",
          boxShadow: 24,
          p: 4,
          borderRadius: 2,
          display: "flex",
          flexDirection: "column",
        }}
      >
        <IconButton
          onClick={onClose}
          style={{
            marginLeft: "auto",
          }}
        >
          <CloseIcon />
        </IconButton>
        <form onSubmit={handleSubmit}>
          <TextField
            label="Review Name"
            value={request.reviewName}
            InputProps={{
              readOnly: true,
            }}
            variant="outlined"
            margin="normal"
            fullWidth
            data-testid="review-name-input"
          />

          {(selectedDocumentTypeId === null || documentTypes?.some((type) => type.id === selectedDocumentTypeId)) && (
            <FormControl margin="normal" fullWidth>
              <Autocomplete
                options={documentTypes ?? []}
                getOptionLabel={(option) =>
                  request.migrated && !option.active ? `${option.description} (Legacy)` : option.description
                }
                noOptionsText={"No document types available"}
                value={getDocumentType()}
                onChange={(_, documentType) => {
                  setSelectedDocumentTypeId(documentType ? Number(documentType.id) : null);
                  setErrorMessage(""); // Clear error when user selects a document type
                }}
                renderInput={(params) => (
                  <TextField
                    {...params}
                    label={"Document Type"}
                    variant="outlined"
                    required={true}
                    data-testid="document-type-select"
                  />
                )}
              />
            </FormControl>
          )}

          {edit && (
            <>
              <ProtectedComponent
                roles={[PermissionGroup.GS_ADMIN, PermissionGroup.ARM_TEAM, PermissionGroup.PROJECT_DEVELOPER]}
              >
                <Typography component="span" variant="body1" sx={{ fontWeight: "bold" }}>
                  Access:
                </Typography>
                <FormControlLabel
                  control={
                    <>
                      <Switch
                        checked={access}
                        disabled={publicPrivateToggleDisabled}
                        onChange={(e) => setAccess(e.target.checked)}
                        name="publicPrivateToggle"
                        color="primary"
                        data-testid="public-private-toggle"
                      />
                      {publicPrivateToggleDisabled && (
                        <CustomTooltipText
                          styleOverrides={{ paddingBottom: "8px" }}
                          text={getPublicAccessDisabledHelperText()}
                          type={CustomTooltip.HELP}
                        />
                      )}
                    </>
                  }
                  label={access ? "Public Document" : "Private Document"}
                  labelPlacement="start"
                  sx={{ marginY: 2 }}
                  data-testid="access-toggle-label"
                />
              </ProtectedComponent>
              <Box sx={{ display: "flex", flexDirection: "column", gap: 2, marginBottom: 2 }}>
                {document?.uploadedTimestamp && (
                  <Box>
                    <Typography component="span" variant="body1" sx={{ fontWeight: "bold", mr: 2 }}>
                      Uploaded Date:
                    </Typography>
                    <Typography component="span" variant="body1">
                      {dayjs.utc(document?.uploadedTimestamp).format("DD/MM/YYYY HH:mm")}
                    </Typography>
                  </Box>
                )}
                {document?.modifiedTimestamp && (
                  <Box>
                    <Typography component="span" variant="body1" sx={{ fontWeight: "bold", mr: 2 }}>
                      Modified Date:
                    </Typography>
                    <Typography component="span" variant="body1">
                      {dayjs.utc(document?.modifiedTimestamp).format("DD/MM/YYYY HH:mm")}
                    </Typography>
                  </Box>
                )}
              </Box>
            </>
          )}

          <Box sx={{ display: "flex", alignItems: "center", gap: 2, marginBottom: 2 }}>
            {!edit && (
              <>
                <Button variant="contained" component="label">
                  Choose File <input type="file" hidden onChange={handleFileChange} data-testid="file-input" />
                </Button>
                <CustomTooltipText text={helperTexts.uploadDocument()} type={CustomTooltip.HELP} />
              </>
            )}
            {edit && (
              <Typography component="span" variant="body1" sx={{ fontWeight: "bold" }}>
                File Name:
              </Typography>
            )}
            {fileName && <Typography variant="body1">{fileName}</Typography>}
          </Box>
          {!edit && isFilePiiScannable && (
            <Typography component="span" variant="body1" sx={{ fontWeight: "bold" }}>
              Your document will be scanned for PII. You will receive an email notification if any PII is detected.
              <CustomTooltipText
                styleOverrides={{ paddingBottom: "8px" }}
                text={piiHelperText}
                type={CustomTooltip.HELP}
              />
            </Typography>
          )}
          {errorMessage && (
            <Typography
              variant="body1"
              sx={{
                color: "error.main",
                padding: 2,
                borderRadius: 1,
                marginBottom: 2,
                fontWeight: "bold",
              }}
              data-testid="error-message"
            >
              {errorMessage}
            </Typography>
          )}
          <Button type="submit" variant="contained" color="primary" fullWidth>
            {edit ? "Update" : "Upload"}
          </Button>
        </form>
      </Box>
    </Modal>
  );
};

export default UploadDocumentModal;
