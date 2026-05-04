import { useLocation, type Location } from "react-router-dom";

import { ReferenceType } from "../../common/data-types/DataReferenceTypes";
import { isFormEnabled } from "../../common/env/env";
import FullScreenMessage from "../../common/full-screen-message/FullScreenMessage";

import { RequestForm } from "./Forms";
import { FormWrapper } from "./FormWrapper";

interface LocationState {
  formType: ReferenceType;
  requestId: number;
  form: RequestForm;
  readonly?: boolean;
}

export const FormLandingPage = () => {
  const location: Location<LocationState> = useLocation();
  const data = location.state;

  if (isFormEnabled() && location.state.formType) {
    return <FormWrapper requestId={data.requestId} form={data.form} readonly={data.readonly} />;
  }

  return <FullScreenMessage headline={"Page content not found"} body={"The content of this page is not accessible"} />;
};
