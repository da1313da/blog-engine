-- `blog-engine`.captcha_codes definition

CREATE TABLE `captcha_codes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` tinytext NOT NULL,
  `secret_code` tinytext NOT NULL,
  `time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- `blog-engine`.global_settings definition

CREATE TABLE `global_settings` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `value` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- `blog-engine`.tags definition

CREATE TABLE `tags` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- `blog-engine`.users definition

CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `is_moderator` tinyint NOT NULL,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `photo` text,
  `reg_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- `blog-engine`.posts definition

CREATE TABLE `posts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `is_active` tinyint NOT NULL,
  `moderation_status` varchar(255) NOT NULL,
  `text` text NOT NULL,
  `time` datetime NOT NULL,
  `title` varchar(255) NOT NULL,
  `view_count` int NOT NULL,
  `moderator_id` int DEFAULT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `post_moderator_fk` (`moderator_id`),
  KEY `post_user_fk` (`user_id`),
  CONSTRAINT `post_moderator_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `post_user_fk` FOREIGN KEY (`moderator_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- `blog-engine`.tag2post definition

CREATE TABLE `tag2post` (
  `tag_id` int NOT NULL,
  `post_id` int NOT NULL,
  PRIMARY KEY (`tag_id`,`post_id`),
  KEY `link_to_post_fk` (`post_id`),
  CONSTRAINT `link_to_post_fk` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`),
  CONSTRAINT `link_to_tag_fk` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- `blog-engine`.post_comments definition

CREATE TABLE `post_comments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `text` text NOT NULL,
  `time` datetime NOT NULL,
  `parent_id` int DEFAULT NULL,
  `post_id` int NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `self_comment_fk` (`parent_id`),
  KEY `comments_post_fk` (`post_id`),
  KEY `comments_user_fk` (`user_id`),
  CONSTRAINT `comments_post_fk` FOREIGN KEY (`parent_id`) REFERENCES `post_comments` (`id`),
  CONSTRAINT `comments_user_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `self_comment_fk` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- `blog-engine`.post_votes definition

CREATE TABLE `post_votes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `time` datetime NOT NULL,
  `value` tinyint NOT NULL,
  `post_id` int NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `votes_post_fk` (`post_id`),
  KEY `votes_user_fk` (`user_id`),
  CONSTRAINT `votes_post_fk` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`),
  CONSTRAINT `votes_user_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;