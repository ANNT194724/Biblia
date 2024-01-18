-- biblia.author definition

CREATE TABLE `author` (
  `author_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  `alias` varchar(256) DEFAULT NULL,
  `photo` varchar(512) DEFAULT NULL,
  `born` varchar(64) DEFAULT NULL,
  `died` varchar(64) DEFAULT NULL,
  `website` varchar(512) DEFAULT NULL,
  `description` text,
  `status` int DEFAULT NULL,
  `created_time` timestamp NULL DEFAULT NULL,
  `updated_time` timestamp NULL DEFAULT NULL,
  `updated_user` int DEFAULT NULL,
  `delete_flag` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`author_id`)
) ENGINE=InnoDB AUTO_INCREMENT=80 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- biblia.book definition

CREATE TABLE `book` (
  `book_id` bigint NOT NULL AUTO_INCREMENT,
  `ISBN` varchar(16) NOT NULL,
  `title` varchar(128) NOT NULL,
  `alias` varchar(128) DEFAULT NULL,
  `image_url` varchar(512) DEFAULT NULL,
  `series_id` bigint DEFAULT NULL,
  `series` varchar(128) DEFAULT NULL,
  `publisher_id` int DEFAULT NULL,
  `publisher` varchar(128) DEFAULT NULL,
  `issuing_house_id` int DEFAULT NULL,
  `issuing_house` varchar(128) DEFAULT NULL,
  `published_year` int DEFAULT NULL,
  `language` varchar(16) DEFAULT NULL,
  `pages_no` int DEFAULT NULL,
  `description` text,
  `rating` float DEFAULT '0',
  `fahasa_link` varchar(256) DEFAULT NULL,
  `status` int NOT NULL,
  `created_time` timestamp NULL DEFAULT NULL,
  `updated_time` timestamp NULL DEFAULT NULL,
  `updated_user` int DEFAULT NULL,
  `delete_flag` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`book_id`),
  FULLTEXT KEY `title` (`title`,`alias`),
  FULLTEXT KEY `title_2` (`title`),
  FULLTEXT KEY `ISBN` (`ISBN`),
  FULLTEXT KEY `alias` (`alias`)
) ENGINE=InnoDB AUTO_INCREMENT=132 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- biblia.genre definition

CREATE TABLE `genre` (
  `genre_id` int NOT NULL AUTO_INCREMENT,
  `genre` varchar(64) DEFAULT NULL,
  `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_time` timestamp NULL DEFAULT NULL,
  `updated_user` int DEFAULT NULL,
  `delete_flag` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`genre_id`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- biblia.hibernate_sequence definition

CREATE TABLE `hibernate_sequence` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- biblia.issuing_house definition

CREATE TABLE `issuing_house` (
  `issuing_house_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `phone_number` varchar(32) DEFAULT NULL,
  `email` varchar(128) DEFAULT NULL,
  `website` varchar(128) DEFAULT NULL,
  `facebook` varchar(256) DEFAULT NULL,
  `address` varchar(256) DEFAULT NULL,
  `logo_url` varchar(512) DEFAULT NULL,
  `description` varchar(512) DEFAULT NULL,
  `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_time` timestamp NULL DEFAULT NULL,
  `updated_user` int DEFAULT NULL,
  `delete_flag` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`issuing_house_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- biblia.`language` definition

CREATE TABLE `language` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8_general_ci NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `local` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=143 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- biblia.publisher definition

CREATE TABLE `publisher` (
  `publisher_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `phone_number` varchar(32) DEFAULT NULL,
  `email` varchar(128) DEFAULT NULL,
  `website` varchar(256) DEFAULT NULL,
  `facebook` varchar(256) DEFAULT NULL,
  `address` varchar(256) DEFAULT NULL,
  `logo_url` varchar(512) DEFAULT NULL,
  `description` varchar(512) DEFAULT NULL,
  `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_time` timestamp NULL DEFAULT NULL,
  `updated_user` int DEFAULT NULL,
  `delete_flag` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`publisher_id`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- biblia.`role` definition

CREATE TABLE `role` (
  `role_id` int NOT NULL AUTO_INCREMENT,
  `role_code` varchar(16) NOT NULL,
  `role_name` varchar(32) DEFAULT NULL,
  `description` varchar(128) DEFAULT NULL,
  `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_time` timestamp NULL DEFAULT NULL,
  `updated_user` varchar(128) DEFAULT NULL,
  `delete_flag` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- biblia.series definition

CREATE TABLE `series` (
  `series_id` bigint NOT NULL,
  `title` varchar(128) NOT NULL,
  `alias` varchar(128) DEFAULT NULL,
  `publisher_id` int NOT NULL,
  `publisher` varchar(128) DEFAULT NULL,
  `issuing_house_id` int DEFAULT NULL,
  `issuing_house` varchar(128) DEFAULT NULL,
  `language` varchar(16) DEFAULT NULL,
  `description` varchar(512) DEFAULT NULL,
  `created_time` timestamp NULL DEFAULT NULL,
  `updated_time` timestamp NULL DEFAULT NULL,
  `updated_user` int DEFAULT NULL,
  `delete_flag` int NOT NULL,
  PRIMARY KEY (`series_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- biblia.shelf definition

CREATE TABLE `shelf` (
  `shelf_id` bigint NOT NULL,
  `book_id` bigint DEFAULT NULL,
  `user_id` char(10) DEFAULT NULL,
  `status` int DEFAULT NULL,
  `created_time` timestamp NULL DEFAULT NULL,
  `updated_time` timestamp NULL DEFAULT NULL,
  `updated_user` int DEFAULT NULL,
  `delete_flag` int NOT NULL,
  PRIMARY KEY (`shelf_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- biblia.`user` definition

CREATE TABLE `user` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `login_id` varchar(128) NOT NULL,
  `password` varchar(128) NOT NULL,
  `phone_number` varchar(16) DEFAULT NULL,
  `role_code` varchar(16) DEFAULT NULL,
  `username` varchar(128) DEFAULT NULL,
  `birthday` date DEFAULT NULL,
  `avatar_url` varchar(256) DEFAULT NULL,
  `status` int NOT NULL,
  `created_time` timestamp NULL DEFAULT NULL,
  `updated_time` timestamp NULL DEFAULT NULL,
  `updated_user` int DEFAULT NULL,
  `delete_flag` int NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- biblia.book_author definition

CREATE TABLE `book_author` (
  `book_author_id` bigint NOT NULL AUTO_INCREMENT,
  `book_id` bigint NOT NULL,
  `author_id` int NOT NULL,
  `role` varchar(32) DEFAULT NULL,
  `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_time` timestamp NULL DEFAULT NULL,
  `updated_user` int DEFAULT NULL,
  `delete_flag` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`book_author_id`),
  KEY `FK_book_author_1_idx` (`author_id`),
  KEY `FK_book_author_0_idx` (`book_id`),
  CONSTRAINT `FK_book_author_0` FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`),
  CONSTRAINT `FK_book_author_1` FOREIGN KEY (`author_id`) REFERENCES `author` (`author_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=137 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- biblia.book_genre definition

CREATE TABLE `book_genre` (
  `book_genre_id` bigint NOT NULL AUTO_INCREMENT,
  `genre_id` int NOT NULL,
  `book_id` bigint NOT NULL,
  `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_user` int DEFAULT NULL,
  `delete_flag` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`book_genre_id`),
  KEY `FK_book_genre_0_idx` (`genre_id`),
  KEY `FK_book_genre_1_idx` (`book_id`),
  CONSTRAINT `FK_book_genre_0` FOREIGN KEY (`genre_id`) REFERENCES `genre` (`genre_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_book_genre_1` FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=229 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- biblia.refresh_token definition

CREATE TABLE `refresh_token` (
  `token_id` bigint NOT NULL AUTO_INCREMENT,
  `expiry_time` timestamp NOT NULL,
  `token` varchar(255) NOT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`token_id`),
  UNIQUE KEY `UK_or156wbneyk8noo4jstv55ii3` (`token`),
  KEY `FK_refres_token_0_idx` (`user_id`),
  CONSTRAINT `FK_refres_token_0` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- biblia.review definition

CREATE TABLE `review` (
  `review_id` bigint NOT NULL AUTO_INCREMENT,
  `book_id` bigint NOT NULL,
  `user_id` int NOT NULL,
  `username` varchar(128) DEFAULT NULL,
  `rating` float NOT NULL,
  `content` text,
  `status` decimal(1,0) NOT NULL DEFAULT '1',
  `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_time` timestamp NULL DEFAULT NULL,
  `updated_user` bigint DEFAULT NULL,
  `delete_flag` decimal(1,0) NOT NULL DEFAULT '1',
  PRIMARY KEY (`review_id`),
  KEY `FK_rating_0_idx` (`book_id`),
  KEY `FK_rating_1_idx` (`user_id`),
  CONSTRAINT `FK_rating_0` FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_rating_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- biblia.series_genre definition

CREATE TABLE `series_genre` (
  `series_genre_id` int NOT NULL AUTO_INCREMENT,
  `series_id` bigint DEFAULT NULL,
  `genre_id` int DEFAULT NULL,
  `created_time` timestamp NULL DEFAULT NULL,
  `updated_time` timestamp NULL DEFAULT NULL,
  `updated_user` int DEFAULT NULL,
  `delete_flag` int NOT NULL,
  PRIMARY KEY (`series_genre_id`),
  KEY `FK_series_genre_0` (`series_id`),
  KEY `FK_series_genre_1_idx` (`genre_id`),
  CONSTRAINT `FK_series_genre_0` FOREIGN KEY (`series_id`) REFERENCES `series` (`series_id`),
  CONSTRAINT `FK_series_genre_1` FOREIGN KEY (`genre_id`) REFERENCES `genre` (`genre_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- biblia.user_role definition

CREATE TABLE `user_role` (
  `user_role_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `role_id` int NOT NULL,
  `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_time` timestamp NULL DEFAULT NULL,
  `delete_flag` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`user_role_id`),
  KEY `user_role_role_null_fk` (`role_id`),
  KEY `user_role_user_null_fk` (`user_id`),
  CONSTRAINT `user_role_role_null_fk` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`),
  CONSTRAINT `user_role_user_null_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- biblia.verification_token definition

CREATE TABLE `verification_token` (
  `token_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `token` varchar(256) DEFAULT NULL,
  `created_time` timestamp NULL DEFAULT NULL,
  `expired_time` timestamp NULL DEFAULT NULL,
  `verified_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`token_id`),
  KEY `verification_token_user_null_fk` (`user_id`),
  CONSTRAINT `verification_token_user_null_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;