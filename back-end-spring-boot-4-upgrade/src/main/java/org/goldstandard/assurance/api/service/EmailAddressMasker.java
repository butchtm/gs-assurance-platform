package org.goldstandard.assurance.api.service;

import org.springframework.stereotype.Component;
import uk.co.mruoc.string.mask.IgnoreFirstNAndLastNCharsMasker;

@Component
public class EmailAddressMasker extends IgnoreFirstNAndLastNCharsMasker {

  public EmailAddressMasker() {
    super(4, 6);
  }
}
