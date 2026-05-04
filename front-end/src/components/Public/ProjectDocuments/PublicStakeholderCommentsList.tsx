import ExpandLessIcon from "@mui/icons-material/ExpandLess";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { Box, IconButton, Table, TableBody, TableCell, TableHead, TableRow, Typography } from "@mui/material";
import dayjs from "dayjs";
import React, { useState } from "react";

import { PublicStakeholderComment } from "../PublicConsultation/PublicConsultation";
import "./ProjectDocuments.css";

interface PublicStakeholderCommentsListProps {
  comments?: PublicStakeholderComment[];
}

const MAX_COMMENT_LENGTH = 300;

export const PublicStakeholderCommentsList: React.FC<PublicStakeholderCommentsListProps> = ({ comments }) => {
  const [expandedComments, setExpandedComments] = useState<Set<number>>(new Set());

  const toggleComment = (commentId: number) => {
    setExpandedComments((prev) => {
      const newSet = new Set(prev);
      if (newSet.has(commentId)) {
        newSet.delete(commentId);
      } else {
        newSet.add(commentId);
      }
      return newSet;
    });
  };

  const truncateComment = (comment: string, commentId: number) => {
    const isExpanded = expandedComments.has(commentId);
    if (comment.length <= MAX_COMMENT_LENGTH) {
      return comment;
    }
    return isExpanded ? comment : comment.substring(0, MAX_COMMENT_LENGTH) + "...";
  };

  if (!comments?.length) {
    return (
      <Typography variant="body1" sx={{ textAlign: "left", padding: "10px 0 0 5px" }}>
        No Public Comments Found
      </Typography>
    );
  }
  return (
    <Table sx={{ whiteSpace: "normal", wordWrap: "break-word" }}>
      <TableHead>
        <TableRow className="table-headings">
          <TableCell className="row-text">Submitter</TableCell>
          <TableCell className="row-text">Submitted Date</TableCell>
          <TableCell className="row-text">Comment</TableCell>
        </TableRow>
      </TableHead>
      <TableBody>
        {comments.map((comment) => (
          <TableRow key={"comment-" + comment.id} className="table-headings">
            <TableCell className="row-text">{comment.anonymous ? "Anonymous" : comment.uploadedByName}</TableCell>
            <TableCell className="row-text">
              {comment.uploadedDate ? dayjs.utc(comment.uploadedDate).format("DD/MM/YYYY") : ""}
            </TableCell>
            <TableCell className="row-text">
              <Box
                sx={{
                  display: "flex",
                  alignItems: "flex-start",
                  wordBreak: "break-word",
                  overflowWrap: "break-word",
                  gap: 1,
                }}
              >
                {truncateComment(comment.submittedComment, comment.id)}
                {comment.submittedComment.length > MAX_COMMENT_LENGTH && (
                  <IconButton
                    size="small"
                    onClick={() => toggleComment(comment.id)}
                    aria-label={expandedComments.has(comment.id) ? "Collapse comment" : "Expand comment"}
                    data-testid={`expand-comment-${comment.id}`}
                    sx={{ padding: "4px" }}
                  >
                    {expandedComments.has(comment.id) ? <ExpandLessIcon /> : <ExpandMoreIcon />}
                  </IconButton>
                )}
              </Box>
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
};
