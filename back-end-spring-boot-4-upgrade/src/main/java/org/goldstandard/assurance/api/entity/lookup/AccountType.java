package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AccountType extends ExtendedReferenceDataType {
  public static final int REVIEWER_ACCOUNT_TYPE_ID = 0;
  public static final int VVB_ACCOUNT_TYPE_ID = 1;
  public static final int GS_ACCOUNT_TYPE_ID = 2;
  public static final int PROJECT_DEVELOPER_TYPE_ID = 3;
  public static final int GS_ARM_ACCOUNT_TYPE_ID = 4;
  public static final int GS_FINANCE_ACCOUNT_TYPE_ID = 5;
  public static final int TAC_ACCOUNT_TYPE_ID = 6;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "assignment_type_account_type",
      joinColumns = @JoinColumn(name = "account_type_id"),
      inverseJoinColumns = @JoinColumn(name = "assignment_type_id"))
  private Set<AssignmentType> assignmentTypes;

  public AccountType() {}

  public AccountType(Integer id) {
    this.id = id;
  }

  public boolean isTac() {
    return hasId(TAC_ACCOUNT_TYPE_ID);
  }

  public boolean isProjectDeveloper() {
    return hasId(PROJECT_DEVELOPER_TYPE_ID);
  }

  public boolean isVvb() {
    return hasId(VVB_ACCOUNT_TYPE_ID);
  }

  public boolean isReviewer() {
    return hasId(REVIEWER_ACCOUNT_TYPE_ID);
  }

  public boolean isArm() {
    return hasId(GS_ARM_ACCOUNT_TYPE_ID);
  }

  private boolean hasId(Integer otherId) {
    return Objects.equals(this.id, otherId);
  }
}
