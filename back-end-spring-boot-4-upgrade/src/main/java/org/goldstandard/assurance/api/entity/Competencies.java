package org.goldstandard.assurance.api.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.entity.lookup.ProjectAsset;
import org.goldstandard.assurance.api.entity.lookup.ProjectLocation;
import org.goldstandard.assurance.api.entity.lookup.ProjectSize;
import org.goldstandard.assurance.api.entity.lookup.ProjectType;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.goldstandard.assurance.api.entity.lookup.SustainableDevelopmentGoal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Competencies {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private Integer estimatedAnnualEmissionReductionAmount;

  @ElementCollection(fetch = FetchType.EAGER)
  private List<String> projectGsids;

  @OneToMany(fetch = FetchType.EAGER)
  private List<ProjectAsset> certifiedProducts;

  @OneToMany(fetch = FetchType.EAGER)
  private List<Account> vvbsAssigned;

  @OneToMany(fetch = FetchType.EAGER)
  private List<Account> projectDevelopers;

  @OneToMany(fetch = FetchType.EAGER)
  private List<SustainableDevelopmentGoal> sustainableDevelopmentGoals;

  @OneToMany(fetch = FetchType.EAGER)
  private List<ProjectType> projectTypes;

  @OneToMany(fetch = FetchType.EAGER)
  private List<ProjectLocation> projectLocations;

  @OneToMany(fetch = FetchType.EAGER)
  private List<ProjectSize> projectScales;

  @OneToMany(fetch = FetchType.EAGER)
  private List<Methodology> methodologies;

  @OneToMany(fetch = FetchType.EAGER)
  private List<RequestType> requestTypes;
}
