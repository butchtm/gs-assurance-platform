package org.goldstandard.assurance.api.service.document;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Document;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UniqueFilenameCalculator {

  public Map<String, Document> toSortedUniqueFilenames(Collection<Document> documents) {
    var filenames = new TreeMap<String, Document>();
    for (Document document : documents) {
      var filename = document.getFileName();
      if (filenames.containsKey(filename)) {
        var duplicate = filenames.remove(filename);
        var duplicateKey = toKey(duplicate);
        filenames.put(duplicateKey, duplicate);
        var key = toKey(document);
        filenames.put(toKey(document), document);
        log.warn(
            "duplicate file {} found for zip used unique names {} and {}",
            filename,
            duplicateKey,
            key);
      } else {
        filenames.put(filename, document);
      }
    }
    return filenames;
  }

  private static String toKey(Document document) {
    return String.format(
        "%s-%s", document.getContainerName().toUpperCase(), document.getFileName());
  }
}
