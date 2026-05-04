package org.goldstandard.assurance.api.repository.notification;

import java.util.List;
import org.goldstandard.assurance.api.entity.notification.NotificationContactMapping;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface NotificationContactMappingRepository
    extends CrudRepository<NotificationContactMapping, Integer> {

  @Query(
      nativeQuery = true,
      value =
          """
                    SELECT
                      mapped_address
                    FROM
                      gs.notification_contact_mapping
                    WHERE
                       address = ?1 OR
                       address IS NULL
                    ORDER BY address NULLS LAST
                    LIMIT 1
                    """)
  List<String> findMappingForAddress(String address);
}
