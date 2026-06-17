-- wire the sequence into the id column
ALTER TABLE gs.user
ALTER COLUMN id SET DEFAULT nextval('gs.seq_user');
