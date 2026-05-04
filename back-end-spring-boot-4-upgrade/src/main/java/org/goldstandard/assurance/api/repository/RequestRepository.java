package org.goldstandard.assurance.api.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.projection.RequestProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository
    extends PagingAndSortingRepository<Request, Integer>,
        CrudRepository<Request, Integer>,
        JpaSpecificationExecutor<Request> {

  List<Request> findByProjectId(Integer projectId);

  @Query(
      nativeQuery = true,
      value = "SELECT request_id FROM gs.request_document WHERE document_id = ?1")
  Optional<Integer> findRequestIdForDocumentId(UUID documentId);

  <T> Iterable<T> findAllByIdIn(Iterable<Integer> ids, Class<T> type);

  // native query required as class based projection stopped working
  // after spring boot 4 upgrade
  @Query(
      nativeQuery = true,
      value =
          """
                                        select
                                          p.id as projectId,
                                          p.gsid as projectGsid,
                                          p.name as projectName
                                        from
                                          gs.request r
                                        left join
                                          gs.base_project_request bpr on bpr.request_id = r.id
                                        left join
                                          gs.project p on bpr.project_id = p.id
                                        where
                                          r.id = ?
                                        """)
  RequestProject findRequestProjectById(Integer id);

  @Query(nativeQuery = true, value = "SELECT description FROM gs.request_type WHERE id = ?1")
  String findRequestDescriptionForTypeId(Integer typeId);

  Optional<Request> findByInvoiceId(Integer invoiceId);

  @Query(
      nativeQuery = true,
      value =
          """
                    select p.account_id
                    from gs.base_project_request bpr
                    join gs.project p
                    on bpr.project_id = p.id
                    where request_id = :request_id
                    """)
  Collection<Integer> getProjectAccountIdsForRequestId(
      @Param(value = "request_id") Integer requestId);

  @Query(
      nativeQuery = true,
      value =
          """
                    SELECT r.*, bpr.project_id
                    FROM gs.request r
                    LEFT JOIN gs.base_project_request bpr
                    ON bpr.request_id = r.id
                    LEFT JOIN gs.project p
                    ON bpr.project_id = p.id
                    LEFT JOIN gs.stakeholder_consultation sc
                    ON sc.request_id = bpr.request_id
                    WHERE NOW() BETWEEN sc.start_date AND sc.end_date
                    AND sc.consultation_type_id = 0
                    """)
  Page<Request> findAllInConsultation(Pageable pageable);

  // native query required as class based projection stopped working
  // after spring boot 4 upgrade
  @Query(
      nativeQuery = true,
      value =
          """
                              select
                                p.id as projectId,
                                p.gsid as projectGsid,
                                p.name as projectName
                              from
                                gs.request r
                              left join
                                gs.base_project_request bpr on bpr.request_id = r.id
                              left join
                                gs.project p on bpr.project_id = p.id
                              where
                                p.account_id = ?
                              """)
  List<RequestProject> findRequestProjectsByProjectAccountId(Integer accountId);
}
