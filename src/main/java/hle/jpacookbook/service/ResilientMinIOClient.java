package hle.jpacookbook.service;

import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.minio.*;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class ResilientMinIOClient {
    private final MinioClient minioClient;

    public ResilientMinIOClient(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    // TODO: How to trigger retry mechanism
    // TODO: How to check the object existed and version info

    @Retry(name = "minio-get")
    @SneakyThrows
    public GetObjectResponse getObjectWithRetry(String bucket, String filePath) {
        return minioClient.getObject(GetObjectArgs
                .builder()
                .bucket(bucket)
                .object(filePath)
                .build()
        );
    }

    @Retry(name = "minio-upload")
    @SneakyThrows
    public ObjectWriteResponse uploadObjectWithRetry(String bucket, String path ,File file) {
        return minioClient.uploadObject(UploadObjectArgs
                .builder()
                .bucket(bucket)
                .object(path)
                .filename(file.getAbsolutePath())
                .build());
    }

    @SneakyThrows
    public StatObjectResponse statObj(String bucket, String path) {
        return minioClient.statObject(StatObjectArgs.builder()
                .bucket(bucket)
                .object(path)
                .build());
    }
}
