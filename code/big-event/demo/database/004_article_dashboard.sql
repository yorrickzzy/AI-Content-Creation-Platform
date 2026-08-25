ALTER TABLE `article`
    ADD COLUMN `view_count` BIGINT NOT NULL DEFAULT 0 COMMENT '阅读量' AFTER `version`,
    ADD INDEX `idx_article_public` (`state`, `category_id`, `create_time`),
    ADD INDEX `idx_article_views` (`state`, `view_count`);