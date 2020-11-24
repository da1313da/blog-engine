ALTER TABLE posts
DROP FOREIGN KEY post_user_fk;
ALTER TABLE posts
CHANGE COLUMN moderator_id moderator_id INT NULL ;
ALTER TABLE posts
ADD CONSTRAINT post_user_fk FOREIGN KEY (moderator_id) REFERENCES users (id);