package org.goldstandard.assurance.api.service.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.goldstandard.assurance.api.entity.UpsertUserRequest;
import org.goldstandard.assurance.api.entity.User;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;

@Component
public class CognitoUserConverter {

  private static final String SUB = "sub";
  private static final String GIVEN_NAME = "given_name";
  private static final String FAMILY_NAME = "family_name";
  private static final String EMAIL = "email";
  private static final String EMAIL_VERIFIED = "email_verified";

  public static String toUsernameFilter(String username) {
    return String.format("username = \"%s\"", username);
  }

  public User toUser(UserType userType, Collection<String> groups) {
    Map<String, String> attributes = toMap(userType.attributes());
    var user = new User();
    user.setUserName(userType.username());
    user.setGivenName(attributes.get(GIVEN_NAME));
    user.setFamilyName(attributes.get(FAMILY_NAME));
    user.setEmailAddress(attributes.get(EMAIL));
    user.setEmailVerified(Boolean.parseBoolean(attributes.get(EMAIL_VERIFIED)));
    user.setGroups(new ArrayList<>(groups));
    user.setIdpUserId(UUID.fromString(attributes.get(SUB)));
    return user;
  }

  public Collection<AttributeType> toAttributes(UpsertUserRequest request) {
    return List.of(
        toGivenNameAttribute(request.getFirstName()),
        toFamilyNameAttribute(request.getLastName()),
        toEmailAttribute(request.getEmail()),
        toEmailVerifiedAttribute(true));
  }

  public Collection<AttributeType> toAttributes(User user) {
    return List.of(
        toGivenNameAttribute(user.getGivenName()),
        toFamilyNameAttribute(user.getFamilyName()),
        toEmailAttribute(user.getEmailAddress()),
        toEmailVerifiedAttribute(user.getEmailVerified()));
  }

  private static AttributeType toGivenNameAttribute(String value) {
    return AttributeType.builder().name(GIVEN_NAME).value(value).build();
  }

  private static AttributeType toFamilyNameAttribute(String value) {
    return AttributeType.builder().name(FAMILY_NAME).value(value).build();
  }

  private static AttributeType toEmailAttribute(String value) {
    return AttributeType.builder().name(EMAIL).value(value).build();
  }

  private static AttributeType toEmailVerifiedAttribute(boolean verified) {
    return AttributeType.builder().name(EMAIL_VERIFIED).value(Boolean.toString(verified)).build();
  }

  private static Map<String, String> toMap(List<AttributeType> attributes) {
    return attributes.stream().collect(Collectors.toMap(AttributeType::name, AttributeType::value));
  }
}
