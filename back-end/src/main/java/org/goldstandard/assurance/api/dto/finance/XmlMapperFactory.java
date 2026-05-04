package org.goldstandard.assurance.api.dto.finance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XmlMapperFactory {

  public static ObjectMapper build() {
    return new XmlMapper().enable(SerializationFeature.INDENT_OUTPUT);
  }
}
