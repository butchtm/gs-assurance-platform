DROP VIEW IF EXISTS gs.v_unread_messages;

CREATE VIEW
  gs.v_unread_messages AS
SELECT
  u.id as user_id,
  r.id as request_id,
  c.id as conversation_id,
  count(distinct cm.id) filter (
    WHERE
      cm.sent_by_user_id != u.id
      and not exists (
        SELECT
          1
        FROM
          gs.conversation_message_receipt r
        WHERE
          r.message_id = cm.id
          and r.read_by_user_id = u.id
      )
  ) as unread
FROM
  gs.user u
  LEFT JOIN gs.account a on a.id = u.account_id
  LEFT JOIN gs.project p on a.id = p.account_id
  LEFT JOIN gs.base_project_request bpr on p.id = bpr.project_id
  LEFT JOIN gs.request r on bpr.request_id = r.id
  LEFT JOIN gs.conversation c on c.request_id = r.id
  LEFT JOIN gs.conversation_message cm on cm.conversation_id = c.id
WHERE
  c.id is not null
GROUP BY
  u.id,
  r.id,
  c.id
UNION
SELECT
  u.id as user_id,
  r.id as request_id,
  c.id as conversation_id,
  count(distinct cm.id) filter (
    WHERE
      cm.sent_by_user_id != u.id
      and not exists (
        SELECT
          1
        FROM
          gs.conversation_message_receipt r
        WHERE
          r.message_id = cm.id
          and r.read_by_user_id = u.id
      )
  ) as unread
FROM
  gs.user u
  LEFT JOIN gs.account a on a.id = u.account_id
  LEFT JOIN gs.request_account_assignment raa on raa.account_id = a.id
  LEFT JOIN gs.request r on raa.request_id = r.id
  LEFT JOIN gs.conversation c on c.request_id = r.id
  LEFT JOIN gs.conversation_message cm on cm.conversation_id = c.id
WHERE
  c.id is not null
GROUP BY
  u.id,
  r.id,
  c.id;

GRANT
SELECT
  ON gs.v_unread_messages TO gs_assurance_db_writer;