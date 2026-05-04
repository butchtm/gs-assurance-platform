export interface PagedResource<T> {
  content: T;
  totalPages: number;
  pageNumber: number;
  totalElements: number;
}
