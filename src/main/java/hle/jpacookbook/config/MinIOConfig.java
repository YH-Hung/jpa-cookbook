package hle.jpacookbook.config;

import hle.jpacookbook.dto.MinIOProperties;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class MinIOConfig {

    final
    MinIOProperties minIOProperties;

    public MinIOConfig(MinIOProperties minIOProperties) {
        this.minIOProperties = minIOProperties;
    }

    @Bean
    public MinioClient minioClient() {
        MinioClient client = MinioClient.builder()
                .endpoint(minIOProperties.getEndpoint())
                .credentials(minIOProperties.getAccessKey(), minIOProperties.getSecretKey())
                .build();

        client.setTimeout(
                TimeUnit.SECONDS.toMillis(minIOProperties.getConnectTimeoutSec()),
                TimeUnit.SECONDS.toMillis(minIOProperties.getWriteTimeoutSec()),
                TimeUnit.SECONDS.toMillis(minIOProperties.getReadTimeoutSec())
        );

        return client;
    }
}
