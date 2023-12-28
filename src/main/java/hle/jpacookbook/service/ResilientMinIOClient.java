package hle.jpacookbook.service;

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

    @SneakyThrows
    @TimeLimiter(name = "minioGet")
    public GetObjectResponse getObjectWithRetry(String bucket, String filePath) {
        return minioClient.getObject(GetObjectArgs
                .builder()
                .bucket(bucket)
                .object(filePath)
                .build()
        );
    }

    @SneakyThrows
    @TimeLimiter(name = "minioUpload")
    public ObjectWriteResponse uploadObjectWithRetry(String bucket, String path ,File file) {
        return minioClient.uploadObject(UploadObjectArgs
                .builder()
                .bucket(bucket)
                .object(path)
                .filename(file.getAbsolutePath())
                .build());
    }
}
