import { cloneDeep } from "lodash";
import isEqual from "lodash/isEqual";
import { MRT_ColumnFiltersState } from "material-react-table";
import { DependencyList, useEffect, useRef } from "react";

export function useDeepCompareEffect(effect: React.EffectCallback, deps?: React.DependencyList) {
  const previousDependencies = useRef<React.DependencyList | undefined>();
  const hasMounted = useRef(false);

  useEffect(() => {
    if (hasMounted.current) {
      if (!isEqual(previousDependencies.current, deps) && !isDateRangeException(deps, previousDependencies.current)) {
        effect();
      }
    } else {
      hasMounted.current = true;
    }
    previousDependencies.current = cloneDeep(deps);
  }, [effect, deps]);

  const isDateRangeException = (current?: DependencyList, previous?: DependencyList) => {
    if (previous && current) {
      const typedPrevious = previous as MRT_ColumnFiltersState[];
      const typedCurrent = current as MRT_ColumnFiltersState[];
      const previousDateRangeFilter = typedPrevious[0].find((filter) => filter.id == "firstRoundStartDate");
      const currentDateRangeFilter = typedCurrent[0].find((filter) => filter.id == "firstRoundStartDate");
      return (
        !previousDateRangeFilter &&
        Array.isArray(currentDateRangeFilter?.value) &&
        currentDateRangeFilter?.value.filter((filter) => filter !== undefined).length == 0
      );
    }
    return false;
  };
}
