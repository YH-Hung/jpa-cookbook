package hle.jpacookbook.service;

import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.minio.*;
import io.minio.errors.*;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
    @SneakyThrows
    public ObjectWriteResponse uploadObjectWithRetry(String bucket, String path ,File file) {
        if (!isObjExist_Bad(bucket, path)) {
            return minioClient.uploadObject(UploadObjectArgs
                    .builder()
                    .bucket(bucket)
                    .object(path)
                    .filename(file.getAbsolutePath())
                    .build());
        }

        return null;
    }

    private Boolean isObjExist(String bucket, String path) throws ServerException, InsufficientDataException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, ErrorResponseException {
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
