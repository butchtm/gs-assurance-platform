CREATE TABLE pg_temp.title_updates (
    name varchar NOT NULL,
    updated_subject varchar NOT NULL,
    PRIMARY KEY (name)
);

INSERT INTO pg_temp.title_updates (name, updated_subject)
VALUES

   ('REQUEST_RECEIVED',
    '${request.name} received for ${project.gsid}')

  ,('COMPLETENESS_CHECK_FINDINGS',
    '${request.name} for ${project.gsid} has not passed the completeness check stage')

  ,('YOUR_TURN',
    '${project.gsid} ${request.name} ready for you to work on')

  ,('REQUEST_CLOSED',
    '${project.gsid} ${request.name} approved')
;

UPDATE    gs.notification_template nt
SET       subject = tu.updated_subject
FROM      pg_temp.title_updates tu
WHERE     nt.name = tu.name AND
          nt.version = 'v0.0.1'
;