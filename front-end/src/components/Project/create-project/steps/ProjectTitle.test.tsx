import { render, screen, waitFor } from "@testing-library/react";
import { Formik } from "formik";
import userEvent from "@testing-library/user-event";

import mockProjects from "../../../../common/project/DebugProject";

import { ProjectTitle } from "./ProjectTitle";

describe("ProjectTitle", () => {
  it("renders correctly", async () => {
    render(
      <Formik initialValues={{ name: "" }} onSubmit={jest.fn()}>
        <ProjectTitle />
      </Formik>,
    );

    await waitFor(() => {
      expect(screen.getByLabelText("Project Title*")).toBeInTheDocument();
    });
  });

  it("renders with parent POA prefix when parent POA selected", async () => {
    render(
      <Formik initialValues={{ name: "" }} onSubmit={jest.fn()}>
        <ProjectTitle parentPoa={mockProjects[0]} />
      </Formik>,
    );

    await waitFor(() => {
      const projectTitleField = screen.getByLabelText("Project Title*");
      expect(projectTitleField).toBeInTheDocument();
      expect(projectTitleField).toHaveValue(`${mockProjects[0].gsid}-`);
    });
  });

  it("renders with parent POA and real case VPA prefix when parent POA and real case selected", async () => {
    render(
      <Formik initialValues={{ name: "" }} onSubmit={jest.fn()}>
        <ProjectTitle parentPoa={mockProjects[0]} parentRealCase={mockProjects[1]} />
      </Formik>,
    );

    await waitFor(() => {
      const projectTitleField = screen.getByLabelText("Project Title*");
      expect(projectTitleField).toBeInTheDocument();
      expect(projectTitleField).toHaveValue(`${mockProjects[0].gsid}-${mockProjects[1].gsid}-`);
    });
  });

  it("does not allow parent projects prefix to be removed by user input", async () => {
    render(
      <Formik initialValues={{ name: "" }} onSubmit={jest.fn()}>
        <ProjectTitle parentPoa={mockProjects[0]} parentRealCase={mockProjects[1]} />
      </Formik>,
    );
    const projectTitleField = screen.getByLabelText("Project Title*");

    await userEvent.clear(projectTitleField);

    await waitFor(() => {
      expect(projectTitleField).toBeInTheDocument();
      expect(projectTitleField).toHaveValue(`${mockProjects[0].gsid}-${mockProjects[1].gsid}-`);
    });
  });

  it("should not apply prefix to title when existing project is opened for update", async () => {
    const initialProjectTitle = "Initial Project Title";
    render(
      <Formik initialValues={{ name: initialProjectTitle }} onSubmit={jest.fn()}>
        <ProjectTitle parentPoa={mockProjects[0]} parentRealCase={mockProjects[1]} isUpdate={true} />
      </Formik>,
    );

    await waitFor(() => {
      const projectTitleField = screen.getByLabelText("Project Title*");
      expect(projectTitleField).toBeInTheDocument();
      expect(projectTitleField).toHaveValue(initialProjectTitle);
    });
  });
});
