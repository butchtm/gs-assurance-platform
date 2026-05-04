package org.goldstandard.assurance.api.service;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.BundleProjectDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BundleService {

  private final BundleFinder bundleFinder;

  public Page<BundleProjectDTO> getBundleProjects(Integer bundleId, Pageable pageable) {
    return bundleFinder.getBundleProjects(bundleId, pageable);
  }
}
