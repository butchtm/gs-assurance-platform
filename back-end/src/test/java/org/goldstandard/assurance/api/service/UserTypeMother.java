package org.goldstandard.assurance.api.service;

import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;

public class UserTypeMother {

  public static UserType user1(String subject) {
    return builder(subject).build();
  }

  public static UserType user2(String subject) {
    return UserType.builder()
        .username("user-2")
        .attributes(subjectAttribute(subject))
        .enabled(false)
        .build();
  }

  public static UserType user3(String subject) {
    return UserType.builder()
        .username("user-3")
        .attributes(subjectAttribute(subject))
        .enabled(false)
        .build();
  }

  public static UserType.Builder builder(String subject) {
    return UserType.builder()
        .username("user-1")
        .attributes(
            subjectAttribute(subject),
            attributeType("given_name", "user-1-given-name"),
            attributeType("family_name", "user-1-family-name"),
            attributeType("email", "user-1@email.com"),
            attributeType("email_verified", String.valueOf(true)))
        .enabled(true);
  }

  private static AttributeType subjectAttribute(String value) {
    return attributeType("sub", value);
  }

  private static AttributeType attributeType(String key, String value) {
    return AttributeType.builder().name(key).value(value).build();
  }
}
