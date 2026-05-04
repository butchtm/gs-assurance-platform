import { MRT_Cell } from "material-react-table";
import { Link } from "react-router-dom";

import { AllRequestsResponse } from "../../../components/requests/Requests";
import { HighRiskPill } from "../../../components/requests/reusable/HighRiskPill";

export const GsidWithRiskCell = ({ cell }: { cell: MRT_Cell<AllRequestsResponse, unknown> }) => {
  const renderedCellValue = cell.getValue<string>();
  const row = cell.row.original;
  const isHighRisk = row.projectRisk || false;

  return (
    <div>
      <Link target="_blank" to={`/projects/${renderedCellValue}`}>
        {renderedCellValue}
      </Link>
      {isHighRisk && (
        <div className="mt-1">
          <HighRiskPill />
        </div>
      )}
    </div>
  );
};
