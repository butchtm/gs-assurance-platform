import React from "react";

import FullScreenMessage from "./FullScreenMessage";

const Unauthorized: React.FC = () => {
  return <FullScreenMessage headline={"Not Authorised"} body={"Please contact support if this is an error"} />;
};

export default Unauthorized;
