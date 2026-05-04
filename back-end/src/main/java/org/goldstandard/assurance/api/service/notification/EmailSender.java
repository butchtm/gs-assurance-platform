package org.goldstandard.assurance.api.service.notification;

import java.util.concurrent.CompletableFuture;

public interface EmailSender {

  CompletableFuture<String> sendEmailAsync(String toEmailAddress, RenderedMessage message);
}
