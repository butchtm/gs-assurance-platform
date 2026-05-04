package org.goldstandard.assurance.api.service.notification;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.configuration.NotificationConfiguration;
import org.goldstandard.assurance.api.repository.notification.NotificationContactMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ContactMappingService {

  @Autowired private NotificationConfiguration notificationConfiguration;

  @Autowired private NotificationContactMappingRepository mappingRepository;

  public String mapContact(String contact) {
    if (Objects.isNull(contact) || !notificationConfiguration.isContactMappingEnabled()) {
      return contact;
    }
    var mapped = toMappedAddress(contact);
    log.warn("contact mapping is enabled, mapped {} to {}", contact, mapped);
    return mapped;
  }

  private String toMappedAddress(String contact) {
    var mappings = mappingRepository.findMappingForAddress(contact);
    if (mappings.isEmpty()) {
      return null;
    }
    return mappings.getFirst();
  }
}
