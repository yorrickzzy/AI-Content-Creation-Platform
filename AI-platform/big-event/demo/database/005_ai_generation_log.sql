CREATE TABLE `ai_generation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` INT NOT NULL,
    `article_id` INT NULL,
    `generation_type` VARCHAR(50) NOT NULL,
    `model` VARCHAR(100) NOT NULL,
    `input_length` INT NOT NULL,
    `output_content` TEXT NULL,
    `status` VARCHAR(20) NOT NULL,
    `duration_ms` BIGINT NOT NULL,
    `error_message` VARCHAR(1000) NULL,
    `create_time` DATETIME NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_ai_generation_user_time` (`user_id`, `create_time`),
    KEY `idx_ai_generation_article` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI内容生成调用日志';