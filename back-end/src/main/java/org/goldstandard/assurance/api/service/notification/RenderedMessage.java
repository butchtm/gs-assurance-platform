package org.goldstandard.assurance.api.service.notification;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.goldstandard.assurance.api.entity.notification.NotificationTemplate;
import org.goldstandard.assurance.api.exception.RendererException;

public record RenderedMessage(String subject, String htmlBody, String textBody) {
  public static RenderedMessage renderMessage(
      Configuration configuration, NotificationTemplate template, Object model) {

    final String subject =
        renderMessagePart(
            configuration,
            templateName(template.baseName(), MessagePart.SUBJECT),
            template.getSubject(),
            model);
    final String htmlBody =
        renderMessagePart(
            configuration,
            templateName(template.baseName(), MessagePart.HTML_BODY),
            template.getHtmlBody(),
            model);
    final String textBody =
        renderMessagePart(
            configuration,
            templateName(template.baseName(), MessagePart.TEXT_BODY),
            template.getTextBody(),
            model);

    return new RenderedMessage(subject, htmlBody, textBody);
  }

  private static String templateName(String name, MessagePart messagePart) {
    return String.format("%s-%s", name, messagePart.name());
  }

  private static String renderMessagePart(
      Configuration configuration, String templateName, String templateSource, Object model) {

    if (templateSource == null) {
      return null;
    }
    try {
      final Template template = new Template(templateName, templateSource, configuration);
      final Writer writer = new StringWriter();
      template.process(model, writer);
      writer.flush();
      return writer.toString();
    } catch (IOException | TemplateException e) {
      throw new RendererException("Error rendering %s. %s", templateName, e.getMessage());
    }
  }

  enum MessagePart {
    SUBJECT,
    HTML_BODY,
    TEXT_BODY
  }
}
