import { RJSFSchema, UiSchema } from "@rjsf/utils";
import { fireEvent, render, screen } from "@testing-library/react";

import { mockActiveReferenceTypes } from "../../common/data-types/DebugReferenceTypes";

import formData from "./mockFormSchema.json";
import { FormWrapper } from "./FormWrapper";
import { saveForm, submitForm } from "./Forms";

jest.mock("react-router-dom", () => ({
  useNavigate: () => jest.fn(),
}));

const formattedFormData = formData.steps.map((step) => ({
  title: step.title,
  schema: step.schema as unknown as RJSFSchema,
  uiSchema: step.uiSchema as UiSchema,
}));

const mockForm = {
  id: 1,
  version: 1.0,
  schema: { steps: formattedFormData },
  type: mockActiveReferenceTypes.DigitalFormType[0],
  status: mockActiveReferenceTypes.RequestFormStatus[1],
  createdAt: "2025-04-24T12:43:41.05365Z",
  updatedAt: "2025-04-27T09:51:41.05365Z",
};

jest.mock("./Forms", () => ({
  ...jest.requireActual("react-router-dom"),
  saveForm: jest.fn(),
  submitForm: jest.fn(),
}));

describe("FormWrapper", () => {
  it("renders the form questions", () => {
    render(<FormWrapper requestId={1} form={mockForm} />);
    expect(screen.getByText("Annual Report")).toBeInTheDocument();
    expect(screen.getByText("Mock Step")).toBeInTheDocument();
    expect(screen.getAllByText("1. This is a mock question 1").length).toBeGreaterThanOrEqual(1);
  });
  it("allows saving of a form", () => {
    render(<FormWrapper requestId={1} form={mockForm} />);
    const saveButton = screen.getByText("Save");
    expect(saveButton).toBeInTheDocument();
    fireEvent.click(saveButton);
    expect(saveForm).toHaveBeenCalledWith(1, [], 1);
  });
  it("prevents saving in read only mode", () => {
    render(<FormWrapper requestId={1} form={mockForm} readonly={true} />);
    const saveButton = screen.queryByText("Save");
    expect(saveButton).not.toBeInTheDocument();
  });
  it("allows submission of a form", () => {
    render(<FormWrapper requestId={1} form={mockForm} />);
    const submitButton = screen.getByText("Submit");
    expect(submitButton).toBeInTheDocument();
    fireEvent.click(submitButton);
    expect(submitForm).toHaveBeenCalledWith(1, [], 1);
  });
  it("prevents submit in readonly mode", () => {
    jest.resetAllMocks();
    render(<FormWrapper requestId={1} form={mockForm} readonly={true} />);
    const submitButton = screen.queryByText("Submit");
    expect(submitButton).not.toBeInTheDocument();
    const returnButton = screen.getByText("Return");
    expect(returnButton).toBeInTheDocument();
    fireEvent.click(returnButton);
    expect(submitForm).not.toHaveBeenCalled();
  });
  it("prevents changing of values in readonly mode", () => {
    render(<FormWrapper requestId={1} form={mockForm} readonly={true} />);
    const input = screen.getAllByLabelText("1. This is a mock question 1")[0];
    expect(input).toBeDisabled();
  });
});
