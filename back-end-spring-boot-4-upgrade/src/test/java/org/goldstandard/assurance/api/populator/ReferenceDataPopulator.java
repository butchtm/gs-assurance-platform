package org.goldstandard.assurance.api.populator;

import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import javax.sql.DataSource;
import org.goldstandard.assurance.api.repository.CurrentStandardVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

@Component
public class ReferenceDataPopulator {

  @Autowired DataSource dataSource;

  @Autowired CurrentStandardVersionRepository referenceRepo;

  public void populate() {
    try (Connection conn = dataSource.getConnection()) {
      ScriptUtils.executeSqlScript(conn, new ClassPathResource("database/h2_compatibility.sql"));
      ScriptUtils.executeSqlScript(conn, new ClassPathResource("database/reference-data.sql"));
    } catch (Exception e) {
      fail("Unexpected exception during database population", e);
    }
  }

  public boolean isPopulated() {
    return referenceRepo.count() > 0;
  }
}
