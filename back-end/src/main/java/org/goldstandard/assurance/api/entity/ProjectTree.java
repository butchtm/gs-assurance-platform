package org.goldstandard.assurance.api.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProjectTree {

  private final ProjectTreeNode root;
}
