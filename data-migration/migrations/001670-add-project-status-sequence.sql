ALTER TABLE gs.project_status ADD COLUMN sequence integer NULL;
UPDATE gs.project_status SET sequence = 1 where id = 1;
UPDATE gs.project_status SET sequence = 2 where id = 2;
UPDATE gs.project_status SET sequence = 3 where id = 3;
UPDATE gs.project_status SET sequence = 4 where id = 4;
UPDATE gs.project_status SET sequence = 5 where id = 5;