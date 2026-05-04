package org.goldstandard.assurance.api.controller.test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Profile({"!production"})
@Service
@Slf4j
public class TestClearAutomatedTestDataService {
  @PersistenceContext private final EntityManager entityManager;
  private final ResourceLoader resourceLoader;

  @Transactional
  public void deleteAllAutomatedTestData() {
    executeQuery(readSql());
  }

  private void executeQuery(String sql) {
    int deleted = entityManager.createNativeQuery(sql).executeUpdate();
    log.info("deleted {} automated test data projects", deleted);
  }

  private String readSql() {
    try {
      Resource resource = resourceLoader.getResource("classpath:delete-automated-test-data.sql");
      return new String(Files.readAllBytes(Paths.get(resource.getURI())));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
