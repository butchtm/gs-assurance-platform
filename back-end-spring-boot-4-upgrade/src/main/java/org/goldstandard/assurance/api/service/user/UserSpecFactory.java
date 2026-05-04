package org.goldstandard.assurance.api.service.user;

import static org.springframework.data.jpa.domain.Specification.unrestricted;

import java.util.Objects;
import org.goldstandard.assurance.api.dto.UserFiltersDTO;
import org.goldstandard.assurance.api.entity.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UserSpecFactory {

  public Specification<User> toSpec(UserFiltersDTO filters) {
    Specification<User> spec = unrestricted();
    spec = filterIsActiveIfRequired(spec, filters);
    spec = filterSearchTermIfRequired(spec, filters);
    return spec;
  }

  private static Specification<User> filterIsActiveIfRequired(
      Specification<User> spec, UserFiltersDTO filters) {
    Boolean active = filters.getActive();
    if (Objects.isNull(active)) {
      return spec;
    }
    return spec.and(isActive(active));
  }

  private static Specification<User> isActive(boolean active) {
    return (user, cq, cb) -> cb.equal(user.get("isActive"), active);
  }

  private static Specification<User> filterSearchTermIfRequired(
      Specification<User> spec, UserFiltersDTO filters) {
    String searchTerm = filters.getSearchTerm();

    if (Objects.isNull(searchTerm)) {
      return spec;
    }
    return spec.and(hasEmailAddress(searchTerm).or(hasUsername(searchTerm)));
  }

  private static Specification<User> hasEmailAddress(String emailAddress) {
    return (user, cq, cb) -> cb.like(user.get("emailAddress"), toLikeTerm(emailAddress));
  }

  private static Specification<User> hasUsername(String username) {
    return (user, cq, cb) -> cb.like(user.get("userName"), toLikeTerm(username));
  }

  private static String toLikeTerm(String term) {
    return String.format("%%%s%%", term);
  }
}
