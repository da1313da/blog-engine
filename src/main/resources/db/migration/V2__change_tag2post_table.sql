DROP TABLE IF EXISTS tag2post;
CREATE TABLE tag2post (
  tag_id int NOT NULL,
  post_id int NOT NULL,
  PRIMARY KEY (tag_id, post_id),
  KEY FK4qq5n9ys2a760c3k3offw4tqp (post_id),
  CONSTRAINT link_to_post_fk FOREIGN KEY (post_id) REFERENCES tags (id),
  CONSTRAINT link_to_tag_fk FOREIGN KEY (tag_id) REFERENCES posts (id)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;