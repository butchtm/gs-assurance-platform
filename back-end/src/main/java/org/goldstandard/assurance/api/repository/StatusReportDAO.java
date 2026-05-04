package org.goldstandard.assurance.api.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.view.StatusReportProjectsGrouping;
import org.goldstandard.assurance.api.entity.view.StatusReportRequestsGrouping;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class StatusReportDAO {

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public Integer getSuccessfulCompletenessCheckRequestsCount(
      LocalDate filterStartDate, LocalDate filterEndDate) {
    var sql =
        """
          SELECT
            count(*) as count
          FROM
            gs.request_status_history rsh
          WHERE rsh.previous_status_id = (SELECT id FROM gs.request_status WHERE code='COMPLETENESS_CHECK')
            AND rsh.next_status_id = (SELECT id FROM gs.request_status WHERE code='IN_ASSIGNMENT')
        """
            + buildDateFilter("rsh.changed_at", filterStartDate, filterEndDate);

    var params = buildDateParams(filterStartDate, filterEndDate);

    return jdbcTemplate.queryForObject(sql, params, Integer.class);
  }

  public List<StatusReportProjectsGrouping> fetchProjectsGroupedByType(
      LocalDate filterStartDate, LocalDate filterEndDate) {
    var sql =
        """
        SELECT
          type, type_description, count(*) as count
        FROM
          gs.v_status_report_projects
        WHERE true
        """
            + buildDateFilter("start_date", filterStartDate, filterEndDate)
            + """

        GROUP BY type, type_description""";

    var params = buildDateParams(filterStartDate, filterEndDate);

    return jdbcTemplate.query(
        sql,
        params,
        (rs, rowNum) ->
            StatusReportProjectsGrouping.builder()
                .type(rs.getString("type"))
                .description(rs.getString("type_description"))
                .count(rs.getInt("count"))
                .build());
  }

  public List<StatusReportRequestsGrouping> fetchCompletedRequestGroupedByType(
      LocalDate filterStartDate, LocalDate filterEndDate) {
    var sql =
        """
        SELECT
          type, type_description, count(*) as count
        FROM
          gs.v_status_report_requests
        WHERE status_code IN ('COMPLETED_APPROVED', 'COMPLETED_REJECTED')
        """
            + buildDateFilter("end_date", filterStartDate, filterEndDate)
            + """

        GROUP BY type, type_description""";

    var params = buildDateParams(filterStartDate, filterEndDate);

    return jdbcTemplate.query(
        sql,
        params,
        (rs, rowNum) ->
            StatusReportRequestsGrouping.builder()
                .type(rs.getString("type"))
                .description(rs.getString("type_description"))
                .count(rs.getInt("count"))
                .build());
  }

  private String buildDateFilter(
      String dateColumn, LocalDate filterStartDate, LocalDate filterEndDate) {
    if (filterStartDate == null && filterEndDate == null) {
      return "";
    }

    var filter = new StringBuilder();

    if (filterStartDate != null) {
      filter.append(" AND ").append(dateColumn).append(" >= :startDate");
    }
    if (filterEndDate != null) {
      filter.append(" AND ").append(dateColumn).append(" < :endDate");
    }

    return filter.toString();
  }

  private Map<String, Object> buildDateParams(LocalDate filterStartDate, LocalDate filterEndDate) {
    var params = new HashMap<String, Object>();
    if (filterStartDate != null) {
      params.put("startDate", Date.valueOf(filterStartDate));
    }
    if (filterEndDate != null) {
      params.put("endDate", Date.valueOf(filterEndDate.plusDays(1)));
    }
    return params;
  }
}
