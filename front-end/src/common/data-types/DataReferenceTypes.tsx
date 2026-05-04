import React, { createContext, ReactNode, useContext, useEffect, useMemo, useState } from "react";

import { apiClient } from "../../components/APIClient/Client";
import {
  getAllMethodologies,
  MethodologyReferenceType,
} from "../../components/Administration/reference-types/ReferenceTypes";

export interface ReferenceType {
  id: number;
  code: string;
  description: string;
  active?: boolean;
}

export interface ReferenceTypes {
  [key: string]: ReferenceType[];
}

export interface Options {
  value: number | string;
  label: string;
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
  getCustomReferenceOptions: (reference: string, currentValue?: ReferenceDataIdsOrId) => Options[];
  refreshReferenceData: () => Promise<void>;
}

export const ReferenceTypeContext = createContext<ReferenceTypeContextType>({
  activeReferenceTypes: {},
  allReferenceTypes: {},
  inactiveReferenceTypes: {},
  referenceMethodologies: [],
  getCustomReferenceOptions: () => {
    throw Error("ReferenceTypeContext getCustomReferenceOptions called too early");
  },
  refreshReferenceData: () => {
    throw Error("ReferenceTypeContext refreshReferenceData called too early");
  },
});
export const useReferenceTypes = () => useContext(ReferenceTypeContext);

const filterActiveRefrenceTypes = (types: ReferenceTypes, active: boolean): ReferenceTypes => {
  return Object.keys(types).reduce((acc, key) => {
    acc[key] = types[key].filter((type: ReferenceType) => type.active === active);
    return acc;
  }, {} as ReferenceTypes);
};

export const ReferenceTypeProvider: React.FC<{
  children: ReactNode;
}> = ({ children }) => {
  const [allReferenceTypes, setAllReferenceTypes] = useState<ReferenceTypes>({});
  const [inactiveReferenceTypes, setInactiveReferenceTypes] = useState<ReferenceTypes>({});
  const [activeReferenceTypes, setActiveReferenceTypes] = useState<ReferenceTypes>({});
  const [referenceMethodologies, setReferenceMethodologies] = useState<MethodologyReferenceType[]>([]);
  const [hasLoaded, setHasLoaded] = useState(false);

  const fetchReferenceTypes = async () => {
    const types = await apiClient.makeRequest("/reference-data-types", "GET");
    setAllReferenceTypes(types);

    const activeTypes = filterActiveRefrenceTypes(types, true);
    setActiveReferenceTypes(activeTypes);

    const inactiveTypes = filterActiveRefrenceTypes(types, false);
    setInactiveReferenceTypes(inactiveTypes);

    setHasLoaded(true);
  };

  const fetchMethodologies = async () => {
    const response = await getAllMethodologies();
    setReferenceMethodologies(response);
  };

  const fetchData = async () => {
    await Promise.all([fetchReferenceTypes(), fetchMethodologies()]);
  };

  useEffect(() => {
    fetchData();
    const FIVE_MINS_IN_MS = 300_000;
    setInterval(fetchMethodologies, FIVE_MINS_IN_MS);
  }, []);

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

  const getCustomReferenceOptions = (reference: string, currentValue?: ReferenceDataIdsOrId): Options[] => {
    const isLegacy = isLegacyValue(inactiveReferenceTypes, reference, currentValue);

    const referenceTypeDataSource =
      isLegacy || reference === "ProjectClassification" ? allReferenceTypes : activeReferenceTypes;

    const referenceTypeData = reference == "Methodology" ? referenceMethodologies : referenceTypeDataSource[reference];
    if (!referenceTypeData) return [];

    return referenceTypeData
      .filter((item) => reference !== "ProjectClassification" || item.active || item.code === "VPA_CPA")
      .map((item) => {
        return {
          value: item.id,
          label: item.description,
        };
      });
  };

  const refreshReferenceData = async () => {
    await fetchData();
  };

  const value = useMemo<ReferenceTypeContextType>(
    () => ({
      activeReferenceTypes,
      allReferenceTypes,
      inactiveReferenceTypes,
      referenceMethodologies,
      getCustomReferenceOptions,
      refreshReferenceData,
    }),
    [activeReferenceTypes, allReferenceTypes, inactiveReferenceTypes, referenceMethodologies],
  );

  return <ReferenceTypeContext.Provider value={value}>{hasLoaded && children}</ReferenceTypeContext.Provider>;
};
