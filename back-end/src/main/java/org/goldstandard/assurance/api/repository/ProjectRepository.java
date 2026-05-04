package org.goldstandard.assurance.api.repository;

import java.util.Collection;
import java.util.Optional;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.projection.ProjectMinimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProjectRepository
    extends PagingAndSortingRepository<Project, Integer>,
        CrudRepository<Project, Integer>,
        JpaSpecificationExecutor<Project> {

  Optional<Project> findByGsid(String gsid);

  Collection<Project> findAllByAccountId(Integer accountId);

  <T> Collection<T> findAllByIdIn(Iterable<Integer> ids, Class<T> type);

  Page<Project> findByBundleId(Integer bundleId, Pageable pageable);

  default Collection<ProjectMinimal> findAllPOAsByAccountId(Integer accountId) {
    return findAllByAccountIdAndProjectClassificationIdIn(
        accountId, ProjectClassification.getPOAIds());
  }

  default Collection<ProjectMinimal> findAllPOAs() {
    return findAllByProjectClassificationIdIn(ProjectClassification.getPOAIds());
  }

  Collection<ProjectMinimal> findAllByAccountIdAndProjectClassificationIdIn(
      Integer accountId, Collection<Integer> classificationIds);

  Collection<ProjectMinimal> findAllByProjectClassificationIdIn(
      Collection<Integer> classificationIds);

  default Collection<Project> findAllRealCaseVPAsByAccountIdAndMasterProjectId(
      Integer accountId, Integer masterProjectId) {
    return findAllByAccountIdAndMasterProjectIdAndProjectClassificationIdIn(
        accountId, masterProjectId, ProjectClassification.getRealCaseVpaIds());
  }

  Collection<Project> findAllByAccountIdAndMasterProjectIdAndProjectClassificationIdIn(
      Integer accountId, Integer masterProjectId, Collection<Integer> classificationIds);

  boolean existsByMasterProjectIdAndProjectClassificationId(
      Integer masterProjectId, Integer classificationId);

  Collection<Project> findAllByParentProjectId(Integer parentProjectId);

  @Transactional
  @Modifying
  @Query(
      nativeQuery = true,
      value = "update gs.project set registry_id = :registryId where id = :projectId")
  void updateRegistryId(Integer projectId, Integer registryId);
}
