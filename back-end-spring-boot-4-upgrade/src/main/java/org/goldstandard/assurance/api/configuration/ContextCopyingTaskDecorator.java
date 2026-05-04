package org.goldstandard.assurance.api.configuration;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.web.context.request.RequestContextHolder;

@Slf4j
public class ContextCopyingTaskDecorator implements TaskDecorator {

  @Override
  @Nonnull
  public Runnable decorate(@Nonnull Runnable runnable) {
    var requestAttributes = RequestContextHolder.currentRequestAttributes();
    var mdcContext = MDC.getCopyOfContextMap();
    return () -> {
      try {
        RequestContextHolder.setRequestAttributes(requestAttributes);
        MDC.setContextMap(mdcContext);
        runnable.run();
      } catch (Exception e) {
        log.error("task error", e);
      } finally {
        MDC.clear();
        RequestContextHolder.resetRequestAttributes();
      }
    };
  }
}
