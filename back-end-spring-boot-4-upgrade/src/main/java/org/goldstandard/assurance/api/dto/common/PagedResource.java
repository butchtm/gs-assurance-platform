package org.goldstandard.assurance.api.dto.common;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;

@NoArgsConstructor
@Getter
@ToString
public class PagedResource<T> {
  List<T> content;
  Integer pageSize;
  Integer pageNumber;
  Integer totalPages;
  long totalElements;

  public PagedResource(Page<T> page) {
    this.content = page.getContent();
    this.pageSize = page.getPageable().getPageSize();
    // Spring's pagination is 0 indexed so add back 1
    this.pageNumber = page.getPageable().getPageNumber() + 1;
    this.totalPages = page.getTotalPages();
    this.totalElements = page.getTotalElements();
  }
}
