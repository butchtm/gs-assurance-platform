package org.goldstandard.assurance.api.populator;

import static org.goldstandard.assurance.api.populator.ProjectPopulator.NUMBER_OF_PROJECTS;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.lookup.Bundle;
import org.goldstandard.assurance.api.repository.BundleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BundlePopulator {

  public static final int NUMBER_OF_BUNDLES = NUMBER_OF_PROJECTS;

  public static List<Bundle> TEST_BUNDLES;

  @Autowired BundleRepository bundleRepository;

  public boolean isPopulated() {
    return bundleRepository.count() > 0;
  }

  public void populate() {
    var bundles = new ArrayList<Bundle>();
    for (int i = 1; i <= NUMBER_OF_BUNDLES; i++) {
      var bundle = Bundle.builder().id(i).name("Test Bundle " + i).build();
      bundles.add(bundle);
      log.info("populated bundle with id {}", bundle.getId());
    }
    bundleRepository.saveAll(bundles);

    TEST_BUNDLES =
        StreamSupport.stream(
                bundleRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).spliterator(), false)
            .toList();
  }
}
