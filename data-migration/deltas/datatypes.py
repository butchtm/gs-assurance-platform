from typing import List, TypedDict

class RequestIdsToUpsert(TypedDict):
    closedInScOpenInAssurance: List[int]
    existsInScNotInAssurance: List[int]
    requestIsInFlght: List[int]
