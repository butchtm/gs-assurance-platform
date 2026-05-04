interface HelperText {
  [key: string]: () => JSX.Element;
}

export const requestsHelperText: HelperText = {
  emissionsReduction: () => (
    <>
      Please state either the average annual emission reductions of the crediting period (for design certification
      requests) or the total emission reductions of the monitoring period (for performance certification requests).
    </>
  ),
};
