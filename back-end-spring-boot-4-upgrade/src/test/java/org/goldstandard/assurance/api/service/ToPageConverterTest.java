package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.List;
import org.goldstandard.assurance.api.entity.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ToPageConverterTest {

  private static final Pageable PAGEABLE_DEFAULT = PageRequest.of(0, 5);
  private static final Pageable PAGEABLE_PAGE_ONE_SIZE_1 = PageRequest.of(0, 1);
  private static final Pageable PAGEABLE_PAGE_TWO_SIZE_1 = PageRequest.of(1, 1);
  private static final List<Request> REQUESTS = List.of(mock(Request.class), mock(Request.class));

  @Test
  void toPageReturnsNoElements() {
    var result = ToPageConverter.toPage(PAGEABLE_DEFAULT, Collections.emptyList());
    assertThat(result).isInstanceOf(Page.class);
    assertThat(result.getContent()).isEmpty();
    assertThat(result.getNumber()).isEqualTo(PAGEABLE_DEFAULT.getPageNumber());
    assertThat(result.getSize()).isEqualTo(PAGEABLE_DEFAULT.getPageSize());
  }

  @Test
  void toPageElementCountLowerThanPageSize() {
    var result = ToPageConverter.toPage(PAGEABLE_DEFAULT, REQUESTS);
    assertThat(result.getContent()).hasSize(REQUESTS.size());
  }

  @Test
  void toPageElementCountHigherThanPageSize() {
    var result = ToPageConverter.toPage(PAGEABLE_PAGE_ONE_SIZE_1, REQUESTS);
    assertThat(result.getContent()).hasSize(PAGEABLE_PAGE_ONE_SIZE_1.getPageSize());
  }

  @Test
  void toPageReturnsDataInCorrectPages() {
    var requestOne = REQUESTS.getFirst();
    var requestTwo = REQUESTS.get(1);
    var firstPageResult = ToPageConverter.toPage(PAGEABLE_PAGE_ONE_SIZE_1, REQUESTS);
    assertThat(firstPageResult.getContent())
        .containsOnly(requestOne)
        .hasSize(PAGEABLE_PAGE_ONE_SIZE_1.getPageSize());
    var secondPageResult = ToPageConverter.toPage(PAGEABLE_PAGE_TWO_SIZE_1, REQUESTS);
    assertThat(secondPageResult.getContent())
        .containsOnly(requestTwo)
        .hasSize(PAGEABLE_PAGE_TWO_SIZE_1.getPageSize());
  }
}
