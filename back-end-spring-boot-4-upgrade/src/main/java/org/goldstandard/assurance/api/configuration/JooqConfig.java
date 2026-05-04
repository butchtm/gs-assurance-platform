package org.goldstandard.assurance.api.configuration;

import javax.sql.DataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JooqConfig {

  @Bean
  public DSLContext dsl(DataSource dataSource) {
    DefaultConfiguration config = new DefaultConfiguration();
    config.set(dataSource);
    config.set(SQLDialect.POSTGRES);
    return new DefaultDSLContext(config);
  }
}
