package com.example.controller;

import com.example.pojo.Result;
import com.example.utils.AliOssUtil;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
public class FileUploadController {
    private final AliOssUtil aliOssUtil;

    public FileUploadController(AliOssUtil aliOssUtil) {
        this.aliOssUtil = aliOssUtil;
    }

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return Result.error("请选择要上传的图片");
        }
        if (file.getSize() > 5L * 1024 * 1024) {
            return Result.error("封面图片不能超过 5MB");
        }
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            return Result.error("请上传 JPG、PNG、WebP 等图片文件");
        }
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String filename = "covers/" + UUID.randomUUID() + suffix.toLowerCase();
        String url = aliOssUtil.uploadFile(filename, file.getInputStream());
        return Result.success(url);
    }
}