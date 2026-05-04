import React from "react";

import FullScreenMessage from "./FullScreenMessage";

const NoAccess: React.FC = () => {
  return (
    <FullScreenMessage
      headline={"You do not have access to this"}
      body={"Please contact support if this is an error"}
    />
  );
};

export default NoAccess;
