package org.goldstandard.assurance.api.dto.finance;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.dataformat.xml.JacksonXmlAnnotationIntrospector;
import tools.jackson.dataformat.xml.XmlMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XmlMapperFactory {

  public static ObjectMapper build() {
    return XmlMapper.builder()
        .annotationIntrospector(new JacksonXmlAnnotationIntrospector())
        .enable(SerializationFeature.INDENT_OUTPUT)
        .build();
  }
}
