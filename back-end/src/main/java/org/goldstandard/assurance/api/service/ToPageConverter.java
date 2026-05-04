package org.goldstandard.assurance.api.service;

import java.util.Collection;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ToPageConverter {

  public static <T> Page<T> toPage(Pageable pageable, Collection<T> allItems) {
    final var pageItems =
        allItems.stream().skip(pageable.getOffset()).limit(pageable.getPageSize()).toList();
    return new PageImpl<>(pageItems, pageable, allItems.size());
  }
}
