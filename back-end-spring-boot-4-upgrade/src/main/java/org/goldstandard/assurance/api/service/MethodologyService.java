package org.goldstandard.assurance.api.service;

import static org.apache.commons.lang3.StringUtils.join;
import static org.springframework.data.domain.Sort.Direction.ASC;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.MethodologyManagementDTO;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriodMethodology;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.exception.BadRequestException;
import org.goldstandard.assurance.api.repository.MethodologyRepository;
import org.goldstandard.assurance.api.repository.ProjectCreditingPeriodMethodologyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MethodologyService {
  private final MethodologyRepository repository;
  private final MethodologyFactory factory;
  private final MethodologyValidator validator;
  private final ProjectCreditingPeriodMethodologyRepository periodMethodologyRepository;
  private final ProjectFinder projectFinder;

  public Page<Methodology> getAll(Pageable pageable, String description) {
    return repository.findAll(descriptionContains(description), pageable);
  }

  public Collection<Methodology> getAll() {
    return repository.findAll(sortByIdAsc());
  }

  public Methodology create(MethodologyManagementDTO methodologyDTO) {
    validator.validate(methodologyDTO);
    var created = factory.create(methodologyDTO);
    return repository.save(created);
  }

  public Methodology update(Integer id, MethodologyManagementDTO methodologyDTO) {
    var existing = repository.forceFindById(id);
    var updated = factory.update(existing, methodologyDTO);
    return repository.save(updated);
  }

  public void delete(Integer id) {
    checkIfMethodologyIsCurrentlyInUse(id);
    var existing = repository.forceFindById(id);
    repository.delete(existing);
  }

  private void checkIfMethodologyIsCurrentlyInUse(Integer id) {
    var usedBy = periodMethodologyRepository.findAllByMethodology_Id(id);
    if (!usedBy.isEmpty()) {
      var usedByGsids = getProjectGsids(usedBy);
      throw new BadRequestException(
          "Cannot delete methodology with id: %s, as it is currently in use by [%s].",
          id, join(usedByGsids, ", "));
    }
  }

  private List<String> getProjectGsids(Collection<ProjectCreditingPeriodMethodology> usedBy) {
    var projectIds =
        usedBy.stream()
            .map(ProjectCreditingPeriodMethodology::getProjectId)
            .collect(Collectors.toSet());
    return projectFinder.getByIds(projectIds).stream().map(Project::getGsid).toList();
  }

  private static Specification<Methodology> descriptionContains(String description) {
    return (root, query, criteria) -> {
      String likeDescription = "%" + description + "%";
      return criteria.like(
          criteria.lower(root.get("description")),
          criteria.lower(criteria.literal(likeDescription)));
    };
  }

  private static Sort sortByIdAsc() {
    return Sort.by(ASC, "id");
  }
}
