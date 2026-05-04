package org.goldstandard.assurance.api.database.h2;

import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

@Component
public class Compatibility {

  @Autowired DataSource dataSource;

  public void patch() {
    try (Connection conn = dataSource.getConnection()) {
      ScriptUtils.executeSqlScript(conn, new ClassPathResource("database/h2_compatibility.sql"));
    } catch (Exception e) {
      fail("Unexpected exception during database patching", e);
    }
  }
}
