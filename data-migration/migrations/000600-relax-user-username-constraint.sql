-- relax the constraint on user_name as userids can be deleted and recreated
-- on Cognito and we associate them via their sub claim
ALTER TABLE gs.user
DROP CONSTRAINT user_user_name_key;
