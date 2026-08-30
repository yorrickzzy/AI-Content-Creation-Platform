package com.example.utils;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.model.PutObjectRequest;

@Component
public class AliOssUtil {
    private final String endpoint;
    private final String bucketName;
    private final String accessKeyId;
    private final String accessKeySecret;

    public AliOssUtil(
            @Value("${oss.endpoint}") String endpoint,
            @Value("${oss.bucket-name}") String bucketName,
            @Value("${oss.access-key-id}") String accessKeyId,
            @Value("${oss.access-key-secret}") String accessKeySecret) {
        this.endpoint = endpoint;
        this.bucketName = bucketName;
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
    }

    public String uploadFile(String objectName, InputStream inputStream) {
        if (bucketName.isBlank() || accessKeyId.isBlank() || accessKeySecret.isBlank()) {
            throw new IllegalStateException("OSS 未配置，请在 demo/.env 中填写 OSS_BUCKET_NAME、OSS_ACCESS_KEY_ID 和 OSS_ACCESS_KEY_SECRET");
        }

        ClientBuilderConfiguration configuration = new ClientBuilderConfiguration();
        configuration.setConnectionTimeout(10_000);
        configuration.setSocketTimeout(30_000);
        configuration.setMaxErrorRetry(2);
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret, configuration);
        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, objectName, inputStream);
            ossClient.putObject(request);
            return endpoint.replace("https://", "https://" + bucketName + ".") + "/" + objectName;
        } catch (OSSException oe) {
            throw new IllegalStateException("OSS 拒绝上传：" + oe.getErrorCode());
        } catch (ClientException ce) {
            throw new IllegalStateException("连接 OSS 失败，请检查网络或 OSS Endpoint");
        } finally {
            ossClient.shutdown();
        }
    }
}
