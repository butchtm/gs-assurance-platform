package org.goldstandard.assurance.api.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomCollectionUtils {

  public static <T> Collection<T> nullSafeCopy(Collection<T> input) {
    return Optional.ofNullable(input).map(ArrayList::new).orElse(new ArrayList<>());
  }

  public static Collection<Integer> toCollection(Integer id) {
    if (Objects.isNull(id)) {
      return Collections.emptySet();
    }
    return Set.of(id);
  }
}
