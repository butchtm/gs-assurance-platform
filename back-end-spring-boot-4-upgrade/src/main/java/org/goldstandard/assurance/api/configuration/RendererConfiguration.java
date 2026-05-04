package org.goldstandard.assurance.api.configuration;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class RendererConfiguration {

  @Bean
  public Configuration getRendererConfiguration() {
    Configuration rendererConfiguration = new Configuration(Configuration.VERSION_2_3_32);
    rendererConfiguration.setDefaultEncoding("UTF-8");
    rendererConfiguration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    rendererConfiguration.setLogTemplateExceptions(false);
    rendererConfiguration.setWrapUncheckedExceptions(true);
    rendererConfiguration.setFallbackOnNullLoopVariable(false);
    return rendererConfiguration;
  }
}
