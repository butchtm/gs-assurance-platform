package org.goldstandard.assurance.api.fixtures;

import java.time.ZonedDateTime;
import java.util.List;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.creditingperiod.DbMethodologyUpdate;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectMethodologyUpdate;
import org.goldstandard.assurance.api.enumeration.MethodologyOperation;
import org.goldstandard.assurance.api.enumeration.ProjectUpdateVerdict;

public class ProjectMethodologyUpdateMother {

  public static ProjectMethodologyUpdate build() {
    return ProjectMethodologyUpdate.builder()
        .id(1)
        .projectId(2)
        .periodId(3)
        .request(request())
        .createdTimestamp(ZonedDateTime.now())
        .methodologyUpdates(List.of(methodology(1), methodology(2)))
        .createdBy(account())
        .verdict(ProjectUpdateVerdict.PROJECT_FORCED)
        .build();
  }

  private static Request request() {
    return Request.builder().id(1).requestName("requestName").build();
  }

  private static DbMethodologyUpdate methodology(Integer index) {
    return DbMethodologyUpdate.builder()
        .methodologyId(1000 + index)
        .newMethodologyVersion(index.doubleValue())
        .initialMethodologyVersion(index.doubleValue())
        .operation(MethodologyOperation.ADD)
        .build();
  }

  private static Account account() {
    return Account.builder().id(1).name("testAccountName").build();
  }
}
