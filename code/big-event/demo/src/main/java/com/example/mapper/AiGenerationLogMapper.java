package com.example.mapper;

import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AiGenerationLogMapper {
    @Insert("insert into ai_generation_log(user_id, article_id, generation_type, model, input_length, output_content, status, duration_ms, error_message, create_time) "
            + "values(#{userId}, #{articleId}, #{generationType}, #{model}, #{inputLength}, #{outputContent}, #{status}, #{durationMs}, #{errorMessage}, #{createTime})")
    void insert(
            @Param("userId") Integer userId,
            @Param("articleId") Integer articleId,
            @Param("generationType") String generationType,
            @Param("model") String model,
            @Param("inputLength") int inputLength,
            @Param("outputContent") String outputContent,
            @Param("status") String status,
            @Param("durationMs") long durationMs,
            @Param("errorMessage") String errorMessage,
            @Param("createTime") LocalDateTime createTime);
}