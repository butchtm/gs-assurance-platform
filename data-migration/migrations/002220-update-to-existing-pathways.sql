--update for pathway 1.2
UPDATE gs.activity SET
  total_days = 8
WHERE
   id = 4;

UPDATE gs.pathway_group SET
  total_days = 10
WHERE
   id = 2;

--update for pathway 1.3
UPDATE gs.activity SET
  total_days = 8
WHERE
   id = 8;

UPDATE gs.pathway_group SET
  total_days = 20
WHERE
   id = 4;

--update for pathway 1.4
UPDATE gs.activity SET
  total_days = 7
WHERE
   id = 13;

UPDATE gs.pathway_group SET
  total_days = 20
WHERE
   id = 6;