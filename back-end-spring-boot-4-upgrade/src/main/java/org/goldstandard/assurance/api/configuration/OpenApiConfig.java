package org.goldstandard.assurance.api.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.servlet.ServletContext;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile({"!production"})
@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI openAPI(ServletContext servletContext) {
    Server server = new Server().url(servletContext.getContextPath());
    return new OpenAPI().servers(List.of(server));
  }
}
