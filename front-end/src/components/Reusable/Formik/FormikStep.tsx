import { FormikConfig, FormikValues } from "formik";

interface FormikStepProps extends Pick<FormikConfig<FormikValues>, "children" | "validationSchema"> {
  label?: string;
}

// Label is used by the FormikStepper
// eslint-disable-next-line @typescript-eslint/no-unused-vars
export function FormikStep({ children, label }: Readonly<FormikStepProps>) {
  return <>{children}</>;
}
