package hle.jpacookbook.service;

import io.github.resilience4j.retry.annotation.Retry;
import io.minio.*;
import io.minio.errors.*;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.function.BiPredicate;

@Service
public class ResilientMinIOClient {
    private final MinioClient minioClient;

    public ResilientMinIOClient(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

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
    public ObjectWriteResponse uploadObjectWithRetry_Good(String bucket, String path , File file) {
        return uploadObjectWithRetry(this::isObjExist_Good, bucket, path, file);
    }

    @Retry(name = "minio-upload")
    public ObjectWriteResponse uploadObjectWithRetry_Bad(String bucket, String path , File file) {
        return uploadObjectWithRetry(this::isObjExist_Bad, bucket, path, file);
    }

    @SneakyThrows
    private ObjectWriteResponse uploadObjectWithRetry(BiPredicate<String, String> objExistedChecker, String bucket, String path , File file) {
        if (!objExistedChecker.test(bucket, path)) {
            return minioClient.uploadObject(UploadObjectArgs
                    .builder()
                    .bucket(bucket)
                    .object(path)
                    .filename(file.getAbsolutePath())
                    .build());
        }

        return null;
    }

    @SneakyThrows
    private Boolean isObjExist_Good(String bucket, String path)  {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucket)
                    .object(path)
                    .build());

            return true;
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                return false;
            }
            throw e;
        }
    }

    private Boolean isObjExist_Bad(String bucket, String path) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucket)
                    .object(path)
                    .build());

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
