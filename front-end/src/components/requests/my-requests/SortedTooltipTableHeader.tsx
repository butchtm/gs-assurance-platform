import React from "react";

import CustomHelperText, { CustomTooltip } from "../../Reusable/FormFields/CustomTooltipText";

interface SortedTooltipTableHeaderProps {
  title: string;
  helperText: string;
  onSort?: () => void;
}

export const SortedTooltipTableHeader: React.FC<SortedTooltipTableHeaderProps> = ({ title, helperText, onSort }) => {
  return (
    <>
      {onSort ? (
        <div onClick={onSort} className="clickable" style={{ display: "inline-block" }}>
          {title}
        </div>
      ) : (
        <span style={{ display: "inline-block" }}>{title}</span>
      )}
      <div style={{ display: "inline-block" }}>
        <CustomHelperText text={helperText} type={CustomTooltip.HELP} />
      </div>
    </>
  );
};
