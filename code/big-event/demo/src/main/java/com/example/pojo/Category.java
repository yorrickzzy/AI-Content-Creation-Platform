package com.example.pojo;

import lombok.Data;

import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Update;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;

@Data
public class Category {
    @NotNull(groups=Update.class)
    private Integer id;
    @NotEmpty
    private String categoryName;
    @NotEmpty
    private String categoryAlias;
    private Integer createUser;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    private Integer version;

    public interface Add extends Default{

    }

    public interface Update extends Default{

    }
}
