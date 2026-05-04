package org.goldstandard.assurance.api.service.notification;

import java.util.List;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Request;

public record UnreadNotificationData(Account account, List<Request> requests) {}
