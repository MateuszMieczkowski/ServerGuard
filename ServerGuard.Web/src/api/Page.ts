export interface Page<T> {
    content: T[];
    pageable: Pageable;
    totalPages: number;             
    totalElements: number;
    last: boolean;
    size: number;
    number: number;
    sort: Sort;
    first: boolean;
    numberOfElements: number;
    empty: boolean;
  }
  
  interface Pageable {
    sort: Sort;
    offset: number;
    pageNumber: number;
    pageSize: number;
    paged: boolean;
    unpaged: boolean;
  }
  
  interface Sort {
    sorted: boolean;
    unsorted: boolean;
    empty: boolean;
  }