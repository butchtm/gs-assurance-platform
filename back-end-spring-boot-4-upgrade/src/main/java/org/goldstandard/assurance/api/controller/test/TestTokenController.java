package org.goldstandard.assurance.api.controller.test;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Profile("test")
@RestController
public class TestTokenController {

  TestJwtGenerator generator;

  public TestTokenController(
      @Value("classpath:local/public.pem") final RSAPublicKey publicKey,
      @Value("classpath:local/private.pem") final RSAPrivateKey privateKey) {
    this.generator = new TestJwtGenerator(publicKey, privateKey);
  }

  @GetMapping("token")
  public String getToken(
      @RequestParam(value = "role", required = false) String role,
      @RequestParam(value = "id", required = false) UUID id) {
    return this.generator.createToken(role, id);
  }
}
