package org.goldstandard.assurance.api.service.conversation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.goldstandard.assurance.api.entity.view.UnreadMessagesView;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UnreadMessagesSpecFactory {

  public Specification<UnreadMessagesView> build(Integer userId, Collection<Integer> requestIds) {
    return hasUserId(userId).and(requestIdIn(new HashSet<>(requestIds)));
  }

  public Specification<UnreadMessagesView> build(Collection<Integer> userIds, Integer requestId) {
    return hasRequestId(requestId).and(userIdIn(new HashSet<>(userIds)));
  }

  public Specification<UnreadMessagesView> build(
      Collection<Integer> assignmentTypesToExclude, Collection<Integer> accountTypesToExclude) {
    return Specification.not(hasAnyOfAssignmentTypeIds(assignmentTypesToExclude))
        .or(assignmentTypeIdIsNull())
        .and(Specification.not(hasAccountTypeId(accountTypesToExclude)));
  }

  public static Specification<UnreadMessagesView> hasAccountTypeId(
      Collection<Integer> accountTypeIds) {
    return (unreadMessages, cq, cb) ->
        cb.in(unreadMessages.get("accountTypeId")).value(accountTypeIds);
  }

  private static Specification<UnreadMessagesView> hasAnyOfAssignmentTypeIds(
      Collection<Integer> accountTypeIds) {
    return (unreadMessages, cq, cb) ->
        cb.in(unreadMessages.get("assignmentTypeId")).value(accountTypeIds);
  }

  private static Specification<UnreadMessagesView> assignmentTypeIdIsNull() {
    return (unreadMessages, cq, cb) -> cb.isNull(unreadMessages.get("assignmentTypeId"));
  }

  private static Specification<UnreadMessagesView> hasUserId(Integer userId) {
    return (unreadMessages, cq, cb) -> cb.equal(unreadMessages.get("userId"), userId);
  }

  private static Specification<UnreadMessagesView> hasRequestId(Integer requestId) {
    return (unreadMessages, cq, cb) -> cb.equal(unreadMessages.get("requestId"), requestId);
  }

  private static Specification<UnreadMessagesView> requestIdIn(Set<Integer> requestIds) {
    return (unreadMessages, cq, cb) -> unreadMessages.get("requestId").in(requestIds);
  }

  private static Specification<UnreadMessagesView> userIdIn(Set<Integer> userIds) {
    return (unreadMessages, cq, cb) -> unreadMessages.get("userId").in(userIds);
  }
}
