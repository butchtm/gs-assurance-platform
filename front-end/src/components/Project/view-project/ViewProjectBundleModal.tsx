import CloseIcon from "@mui/icons-material/Close";
import { Box, IconButton, Modal, Table, TableBody, TableCell, TableHead, TableRow, Typography } from "@mui/material";
import React, { useEffect, useState } from "react";

import { ReferenceType } from "../../../common/data-types/DataReferenceTypes";
import { ProjectBundleInfo } from "../../../common/project/Project";
import Pagination from "../../Reusable/Pagination";
import { getProjectBundles } from "../Project";

interface AcceptDeclineModalProps {
  projectBundleId: number;
  bundleName: string;
  isOpen: boolean;
  onClose: (status?: ReferenceType) => void;
}

const ViewProjectBundleModal: React.FC<AcceptDeclineModalProps> = ({
  projectBundleId,
  bundleName,
  isOpen,
  onClose,
}) => {
  const [bundles, setBundles] = useState<ProjectBundleInfo[]>();
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    const fetchBundles = async () => {
      const bundleResponse = await getProjectBundles(projectBundleId, currentPage);
      setBundles(bundleResponse.content);
      setTotalPages(bundleResponse.totalPages);
    };
    fetchBundles();
  }, [currentPage]);

  const handleClose = () => {
    onClose();
  };

  return (
    <Modal open={isOpen} onClose={() => onClose()}>
      <Box sx={{ backgroundColor: "white", padding: 10, borderRadius: 8 }}>
        <IconButton
          onClick={handleClose}
          data-testid="close-btn"
          style={{
            float: "right",
          }}
        >
          <CloseIcon />
        </IconButton>
        <Typography variant="h6" component="h2" sx={{ display: "flex", justifyContent: "space-around", mb: 2 }}>
          {bundleName}
        </Typography>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>GSID</TableCell>
              <TableCell>Project Name</TableCell>
              <TableCell>Current Request</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {bundles &&
              bundles?.length > 0 &&
              bundles.map((bundleProject) => (
                <TableRow key={bundleProject.id}>
                  <TableCell>
                    <a href={`/projects/${bundleProject.gsid}`} target="_blank" rel="noopener noreferrer">
                      {bundleProject.gsid}
                    </a>
                  </TableCell>
                  <TableCell>{bundleProject.name}</TableCell>
                  <TableCell>
                    <a
                      href={`/projects/${bundleProject.gsid}/requests?expand=${bundleProject.currentRequest.id}`}
                      target="_blank"
                      rel="noopener noreferrer"
                    >
                      {bundleProject.currentRequest.name}
                    </a>
                  </TableCell>
                </TableRow>
              ))}
          </TableBody>
        </Table>
        <Pagination
          total={totalPages}
          current={currentPage}
          onChange={(newPage) => {
            setCurrentPage(newPage);
            window.scrollTo(0, 0);
          }}
        />
      </Box>
    </Modal>
  );
};

export default ViewProjectBundleModal;
