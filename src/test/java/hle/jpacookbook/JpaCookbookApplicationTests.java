package hle.jpacookbook;

import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.ToxiproxyClient;
import hle.jpacookbook.model.report.InspectionReport;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.*;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.ToxiproxyContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class JpaCookbookApplicationTests {

    private static final Network network = Network.newNetwork();

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15.4-alpine"
    ).withInitScript("postgres_init.sql")
            .withNetwork(network)
            .withNetworkAliases("postgres");

    // TODO: add MinIO container, proxy
    @Container
    static MinIOContainer minio = new MinIOContainer("minio/minio");


    static Proxy postgresqlProxy;

    @Container
    static final ToxiproxyContainer toxiproxy = new ToxiproxyContainer("ghcr.io/shopify/toxiproxy:2.5.0")
            .withNetwork(network);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) throws IOException {
        var toxiproxyClient = new ToxiproxyClient(toxiproxy.getHost(), toxiproxy.getControlPort());
        postgresqlProxy = toxiproxyClient.createProxy("postgresql", "0.0.0.0:8666", "postgres:5432");

        registry.add("spring.datasource.url", () -> "jdbc:postgresql://%s:%d/%s"
                .formatted(toxiproxy.getHost(), toxiproxy.getMappedPort(8666), postgres.getDatabaseName()));
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("minio.endpoint", minio::getS3URL);
        registry.add("minio.accessKey", minio::getUserName);
        registry.add("minio.secretKey", minio::getPassword);
    }

    @Autowired
    MinioClient minioClient;

    @Test
    void contextLoads() {
    }

    @SneakyThrows
    @Test
    void testParseFromMinIO() {
        ClassLoader loader = getClass().getClassLoader();
        File file = new File(loader.getResource("InspReport.txt").getFile());

        minioClient.makeBucket(
                MakeBucketArgs.builder()
                        .bucket("product")
                        .build());

        minioClient.uploadObject(UploadObjectArgs
                .builder()
                .bucket("product")
                .object("judge/InspReport.txt")
                .filename(file.getAbsolutePath())
                .build());

        // https://www.baeldung.com/java-io-streams-closing
        InputStream is = minioClient.getObject(GetObjectArgs
                .builder()
                .bucket("product")
                .object("judge/InspReport.txt")
                .build());

        try (is) {
            List<String> data = IOUtils.readLines(is, StandardCharsets.UTF_8);
            var result = InspectionReport.parseTmp(data);
            assertThat(result).isEqualTo("12.986343,78.38263");
        }
    }
}
