import React from "react";
import "./Pagination.css";

export interface PaginationState {
  pageIndex: number;
  pageSize: number;
}

interface PaginationProps {
  total: number;
  current: number;
  onChange: (page: number) => void;
}

const Pagination: React.FC<PaginationProps> = ({ total, current, onChange }) => {
  const getPaginationRange = () => {
    const range = [];
    const numPagesToShow = 5;

    let start = current - 2;
    let end = current + 2;

    if (start < 1) {
      start = 1;
      end = Math.min(numPagesToShow, total);
    }

    if (end > total) {
      end = total;
      start = Math.max(1, total - numPagesToShow + 1);
    }

    for (let i = start; i <= end; i++) {
      range.push(i);
    }

    return range;
  };

  const paginationRange = getPaginationRange();

  return (
    <div className="pagination-container">
      <button onClick={() => onChange(1)} disabled={current === 1}>
        First
      </button>
      {paginationRange.map((page) => (
        <button
          key={page}
          className={page === current ? "active" : ""}
          onClick={() => onChange(page)}
          data-testid={`page-${page}`}
        >
          {page}
        </button>
      ))}
      <button onClick={() => onChange(total)} disabled={current === total}>
        Last
      </button>
    </div>
  );
};

export default Pagination;
