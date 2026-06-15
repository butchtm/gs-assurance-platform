UPDATE gs.request_status_notification SET
  min_round_index = 1,
  max_round_index = 1
WHERE
    request_owner_template_name = 'REVIEW_INITIATED'