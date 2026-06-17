INSERT INTO gs.request_status_transition
  (request_status_id,
   permitted_request_status_id,
   minimum_round_of_review_count,
   permitted_during_consultation)
VALUES(
    (SELECT id FROM gs.request_status r where r.code='IN_ROUND_COMPLETION'),
    (SELECT id FROM gs.request_status r where r.code='IN_PROGRESS'),
    null,
    false);