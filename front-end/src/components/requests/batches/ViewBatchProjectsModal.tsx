import CloseIcon from "@mui/icons-material/Close";
import { Box, IconButton, Modal, Table, TableBody, TableCell, TableHead, TableRow, Typography } from "@mui/material";
import React, { useEffect, useState } from "react";

import { Project } from "../../../common/project/Project";
import { GSRequest } from "../../../common/request/GSRequest";
import { getBatchProjects } from "../../Project/Project";
import Pagination from "../../Reusable/Pagination";

interface ViewBatchProjectsModalProps {
  isOpen: boolean;
  onClose: () => void;
  request: GSRequest;
}
const ViewBatchProjectsModal: React.FC<ViewBatchProjectsModalProps> = ({ isOpen, onClose, request }) => {
  const [batchProjectIds] = useState<number[]>(request.legacyBatchProjectIds || []);
  const [batchProjects, setBatchProjects] = useState<Project[]>();
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    const fetchBatchProjects = async () => {
      const batchProjectsResponse = await getBatchProjects(batchProjectIds, currentPage);
      setTotalPages(batchProjectsResponse.totalPages);
      setBatchProjects(batchProjectsResponse.content);
    };

    fetchBatchProjects();
  }, [currentPage]);

  const handleClose = () => {
    onClose();
  };

  return (
    <Modal open={isOpen} onClose={() => onClose()}>
      <Box
        sx={{
          backgroundColor: "white",
          height: "fit-content",
          minHeight: 350,
          padding: 6,
          borderRadius: 8,
        }}
      >
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
          Projects in this batch request
        </Typography>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell className="row-text">GSID</TableCell>
              <TableCell className="row-text">Project Name</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {batchProjects &&
              batchProjects?.length > 0 &&
              batchProjects.map((batchProject) => (
                <TableRow key={batchProject.id}>
                  <TableCell className="row-text">
                    <a href={`/projects/${batchProject.gsid}`} target="_blank" rel="noopener noreferrer">
                      {batchProject.gsid}
                    </a>
                  </TableCell>
                  <TableCell>{batchProject.name}</TableCell>
                </TableRow>
              ))}
          </TableBody>
        </Table>
        <Pagination
          total={totalPages}
          current={currentPage}
          onChange={(newPage) => {
            setCurrentPage(newPage);
          }}
        />
      </Box>
    </Modal>
  );
};

export default ViewBatchProjectsModal;
