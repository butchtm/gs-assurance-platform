package org.goldstandard.assurance.api.service.user;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.UserFiltersDTO;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFinder {

  private final UserSpecFactory specFactory;
  private final UserRepository userRepository;

  public List<User> getAllUsers(UserFiltersDTO filters) {
    Specification<User> spec = specFactory.toSpec(filters);
    return userRepository.findAll(spec);
  }

  public Page<User> getAllUsers(UserFiltersDTO filters, Pageable pageable) {
    Specification<User> spec = specFactory.toSpec(filters);
    return userRepository.findAll(spec, pageable);
  }

  public User forceGetUserByIdpUserId(UUID idpUserId) {
    return getUserByIdpUserId(idpUserId)
        .orElseThrow(() -> new NotFoundException("User not found for idp user id %s", idpUserId));
  }

  public Optional<User> getUserByIdpUserId(UUID idpUserId) {
    return userRepository.findByIdpUserId(idpUserId);
  }

  public Optional<User> getByUserName(String userName) {
    return userRepository.findByUserName(userName);
  }

  public Collection<User> getUsersByAccountId(Integer accountId) {
    return userRepository.findAllByAccountId(accountId);
  }

  public Map<Integer, List<User>> getUsersByAccountIds(Collection<Integer> accountIds) {
    return userRepository.findAllByAccountIdIsIn(accountIds).stream()
        .collect(
            Collectors.groupingBy(
                user -> user.getAccount().getId(),
                Collectors.mapping(Function.identity(), Collectors.toList())));
  }

  public Collection<User> getUsersByIds(Collection<Integer> ids) {
    Collection<User> users = userRepository.findAllById(ids);
    if (users.size() != ids.size()) {
      throw new NotFoundException(String.format("Not all users with ids %s found", ids));
    }
    return users;
  }

  public User forceGetUserById(Integer id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("User with id %d not found", id));
  }
}
