package org.goldstandard.assurance.api.entity;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommentAccessRights {

  private final boolean includeTac;
  private final boolean includeSuppressed;
  private final boolean includeDeleted;
  private final boolean includePrivate;
  private final boolean anonymise;
}
