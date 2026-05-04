package org.goldstandard.assurance.api.dto.finance;

import java.math.BigDecimal;
import java.math.RoundingMode;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class BigDecimalSerializer extends ValueSerializer<BigDecimal> {

  @Override
  public void serialize(BigDecimal value, JsonGenerator generator, SerializationContext context)
      throws JacksonException {
    var scaled = value.setScale(5, RoundingMode.UNNECESSARY);
    generator.writeNumber(scaled);
  }
}
