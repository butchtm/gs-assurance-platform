package org.goldstandard.assurance.api.configuration;

import jakarta.annotation.Nonnull;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class AsyncConfiguration implements AsyncConfigurer {

  @Override
  @Bean(name = "mvcAsyncTaskExecutor")
  @Nonnull
  public AsyncTaskExecutor getAsyncExecutor() {
    log.info("creating mvc async task executor");
    return new ThreadPoolTaskExecutorBuilder()
        .corePoolSize(4)
        .maxPoolSize(8)
        .queueCapacity(10)
        .taskDecorator(new ContextCopyingTaskDecorator())
        .build();
  }

  @Bean(name = "piiScanExecutor")
  public Executor piiScanExecutor() {
    return new ThreadPoolTaskExecutorBuilder().threadNamePrefix("PiiScan-").build();
  }

  @Override
  @Nonnull
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return new SimpleAsyncUncaughtExceptionHandler();
  }

  @Bean
  public WebMvcConfigurer webMvcConfigurer(
      @Qualifier("mvcAsyncTaskExecutor") AsyncTaskExecutor taskExecutor,
      CallableProcessingInterceptor callableProcessingInterceptor) {
    return new WebMvcConfigurer() {
      @Override
      public void configureAsyncSupport(@Nonnull AsyncSupportConfigurer configurer) {
        configurer.setDefaultTimeout(7200000).setTaskExecutor(taskExecutor);
        configurer.registerCallableInterceptors(callableProcessingInterceptor);
        WebMvcConfigurer.super.configureAsyncSupport(configurer);
      }
    };
  }

  @Bean
  public CallableProcessingInterceptor callableProcessingInterceptor() {
    return new TimeoutCallableProcessingInterceptor() {
      @Override
      @Nonnull
      public <T> Object handleTimeout(
          @Nonnull NativeWebRequest request, @Nonnull Callable<T> task) {
        log.error("mvc async task timed out");
        return "timeout";
      }
    };
  }
}
