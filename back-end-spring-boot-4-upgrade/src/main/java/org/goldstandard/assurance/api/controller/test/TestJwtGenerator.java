package org.goldstandard.assurance.api.controller.test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TestJwtGenerator {
  private final Algorithm rsa256;

  public TestJwtGenerator(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
    this.rsa256 = Algorithm.RSA256(publicKey, privateKey);
  }

  public String createToken(String role, UUID id) {
    return JWT.create()
        .withSubject(id == null ? UUID.randomUUID().toString() : id.toString())
        .withIssuer("local")
        .withIssuedAt(new Date())
        .withExpiresAt(ZonedDateTime.now().plusDays(1).toInstant())
        .withClaim("cognito:groups", role != null ? List.of(role) : List.of())
        .sign(this.rsa256);
  }
}
