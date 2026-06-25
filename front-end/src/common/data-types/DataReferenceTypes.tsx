import { useQuery } from "@tanstack/react-query";
import React, { createContext, ReactNode, useCallback, useContext, useMemo } from "react";

import { apiClient } from "@/components/APIClient/Client";
import {
  getAllMethodologies,
  MethodologyReferenceType,
} from "@/components/Administration/reference-types/ReferenceTypes";

export interface ReferenceType {
  id: number;
  code: string;
  description: string;
  active?: boolean;
  paa?: boolean;
}

export interface ReferenceTypes {
  [key: string]: ReferenceType[];
}

export type RequestType = {
  id: number;
  code: string;
  description: string;
  isActive: boolean;
  supportsPAA: boolean;
};

export interface Options {
  value: number | string;
  label: string;
  flag?: boolean; // to handle boolean values in select options, since the value needs to be a string for the Select component but we want to preserve the boolean type for form values
}

export const getReferenceValueIdsByCodes = (referenceCodes: string[], referenceType: ReferenceType[]) => {
  if (referenceCodes && referenceType) {
    return referenceType.filter((ref) => referenceCodes.includes(ref.code)).map((ref) => ref.id);
  }
  return [];
};

export const getReferenceValueCodeById = (referenceId: number | null | undefined, referenceTypes: ReferenceType[]) => {
  const type = getReferenceTypeById(referenceId, referenceTypes);
  return type?.code;
};

export const getReferenceValueDescriptionById = (
  referenceId: number | null,
  referenceTypes: ReferenceType[],
): string | undefined => {
  const type = getReferenceTypeById(referenceId, referenceTypes);
  return type?.description;
};

export const getReferenceTypeById = (
  referenceId: number | null | undefined,
  referenceTypes: ReferenceType[],
): ReferenceType | undefined => {
  if (referenceId != null && referenceTypes) {
    return getReferenceType((type) => type.id === referenceId, referenceTypes);
  }
};

export const getReferenceTypeByCode = (
  code: string | null | undefined,
  referenceTypes: ReferenceType[],
): ReferenceType | undefined => {
  if (code != null && referenceTypes) {
    return getReferenceType((type) => type.code === code, referenceTypes);
  }
};

export const getReferenceType = (
  predicate: (type: ReferenceType) => boolean,
  referenceTypes: ReferenceType[],
): ReferenceType | undefined => {
  if (referenceTypes) {
    return referenceTypes.find((type) => predicate(type));
  }
};

export type ReferenceDataIdsOrId = number | number[] | null;

export interface ReferenceTypeContextType {
  activeReferenceTypes: ReferenceTypes;
  allReferenceTypes: ReferenceTypes;
  inactiveReferenceTypes: ReferenceTypes;
  referenceMethodologies: MethodologyReferenceType[];
  activeRequestTypes: RequestType[];
  getCustomReferenceOptions: (reference: string, currentValue?: ReferenceDataIdsOrId) => Options[];
  refreshReferenceData: () => void;
}

export const ReferenceTypeContext = createContext<ReferenceTypeContextType>({
  activeReferenceTypes: {},
  allReferenceTypes: {},
  inactiveReferenceTypes: {},
  referenceMethodologies: [],
  activeRequestTypes: [],
  getCustomReferenceOptions: () => {
    throw Error("ReferenceTypeContext getCustomReferenceOptions called too early");
  },
  refreshReferenceData: () => {
    throw Error("ReferenceTypeContext refreshReferenceData called too early");
  },
});

export const useReferenceTypes = () => useContext(ReferenceTypeContext);

const filterActiveReferenceTypes = (types: ReferenceTypes, active: boolean): ReferenceTypes => {
  return Object.keys(types).reduce((acc, key) => {
    acc[key] = types[key].filter((type: ReferenceType) => type.active === active);

    return acc;
  }, {} as ReferenceTypes);
};

const staleTimeFiveMinutes = 5 * 60 * 1000; // 5 minutes in milliseconds, easier to read and maintain than a large number literal
const selectableInactiveProjectClassificationCodes = new Set([
  "VPA_CPA",
  "REAL_CASE_VPA_CROSS_LINKAGE",
  "REGULAR_VPA_CROSS_LINKAGE",
]);

export const ReferenceTypeProvider: React.FC<{
  children: ReactNode;
}> = ({ children }) => {
  const {
    data: allReferenceTypes,
    isSuccess: allReferenceTypesSuccess,
    refetch: refetchAllReferenceTypes,
  } = useQuery<ReferenceTypes>({
    queryKey: ["reference-data-types"],
    queryFn: () => apiClient.makeRequest("/reference-data-types", "GET"),
    refetchOnWindowFocus: false,
    refetchOnReconnect: false,
    staleTime: staleTimeFiveMinutes,
  });

  const {
    data: referenceMethodologies,
    isSuccess: referenceMethodologiesFetched,
    refetch: refetchReferenceMethodologies,
  } = useQuery<MethodologyReferenceType[]>({
    queryKey: ["reference-methodologies"],
    queryFn: getAllMethodologies,
    refetchOnWindowFocus: false,
    refetchOnReconnect: false,
    staleTime: staleTimeFiveMinutes,
  });

  const { data: activeRequestTypes, isSuccess: activeRequestTypesFetched } = useQuery<RequestType[]>({
    queryKey: ["active-request-types"],
    queryFn: () => apiClient.makeRequest("/request-types", "GET"),
    select: (data) => data.filter((type: RequestType) => type.isActive),
    refetchOnWindowFocus: false,
    refetchOnReconnect: false,
    staleTime: 24 * 60 * 60 * 1000, // 24 hours in milliseconds
  });
  const hasLoaded = allReferenceTypesSuccess && referenceMethodologiesFetched && activeRequestTypesFetched;

  // Ensure we never pass undefined into the context value — use empty
  // defaults until data is available.
  const safeAllReferenceTypes: ReferenceTypes = allReferenceTypes ?? {};
  const safeReferenceMethodologies: MethodologyReferenceType[] = referenceMethodologies ?? [];
  const safeActiveRequestTypes: RequestType[] = activeRequestTypes ?? [];
  const activeReferenceTypes = useMemo(() => {
    return filterActiveReferenceTypes(safeAllReferenceTypes, true);
  }, [safeAllReferenceTypes]);

  const inactiveReferenceTypes = useMemo(() => {
    return filterActiveReferenceTypes(safeAllReferenceTypes, false);
  }, [safeAllReferenceTypes]);

  const getCustomReferenceOptions = useCallback(
    (reference: string, currentValue?: ReferenceDataIdsOrId): Options[] => {
      const isLegacy = isLegacyValue(inactiveReferenceTypes, reference, currentValue);

      const referenceTypeDataSource =
        isLegacy || reference === "ProjectClassification" ? safeAllReferenceTypes : activeReferenceTypes;

      const referenceTypeData =
        reference == "Methodology" ? safeReferenceMethodologies : referenceTypeDataSource[reference];
      if (!referenceTypeData) return [];

      return referenceTypeData
        .filter(
          (item) =>
            reference !== "ProjectClassification" ||
            item.active ||
            selectableInactiveProjectClassificationCodes.has(item.code),
        )
        .map((item) => {
          return {
            value: item.id,
            label: item.description,
          };
        });
    },
    [activeReferenceTypes, safeAllReferenceTypes, inactiveReferenceTypes, safeReferenceMethodologies],
  );

  const refreshReferenceData = () => {
    refetchAllReferenceTypes();
    refetchReferenceMethodologies();
  };

  const value = useMemo<ReferenceTypeContextType>(
    () => ({
      activeReferenceTypes,
      allReferenceTypes: safeAllReferenceTypes,
      inactiveReferenceTypes,
      referenceMethodologies: safeReferenceMethodologies,
      activeRequestTypes: safeActiveRequestTypes,
      getCustomReferenceOptions,
      refreshReferenceData,
    }),
    [
      activeReferenceTypes,
      safeAllReferenceTypes,
      inactiveReferenceTypes,
      safeReferenceMethodologies,
      safeActiveRequestTypes,
      getCustomReferenceOptions,
      refreshReferenceData,
    ],
  );

  return <ReferenceTypeContext.Provider value={value}>{hasLoaded && children}</ReferenceTypeContext.Provider>;
};

const isLegacyValue = (
  inactiveReferenceTypes: ReferenceTypes,
  reference: string,
  value?: ReferenceDataIdsOrId,
): boolean => {
  if (value == null || value == undefined) return false;

  const referenceArray = inactiveReferenceTypes[reference];
  if (!referenceArray) return false;

  const checkSingleValue = (val: number | string) => referenceArray.some((ref) => ref.id === val);

  if (Array.isArray(value)) {
    return value.some((val) => checkSingleValue(val));
  } else {
    return checkSingleValue(value);
  }
};
