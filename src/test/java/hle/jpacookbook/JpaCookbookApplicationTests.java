package hle.jpacookbook;

import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.ToxiproxyClient;
import eu.rekawek.toxiproxy.model.ToxicDirection;
import hle.jpacookbook.model.report.InspectionReport;
import hle.jpacookbook.service.ResilientMinIOClient;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.VersioningConfiguration;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
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
import remover.ToxicRemover;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Timer;

import static org.assertj.core.api.Assertions.*;

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

    @Container
    static MinIOContainer minio = new MinIOContainer("minio/minio")
            .withNetwork(network)
            .withNetworkAliases("minio");


    static Proxy postgresqlProxy;
    static Proxy minioProxy;

    @Container
    static final ToxiproxyContainer toxiproxy = new ToxiproxyContainer("ghcr.io/shopify/toxiproxy:2.5.0")
            .withNetwork(network);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) throws IOException {
        var toxiproxyClient = new ToxiproxyClient(toxiproxy.getHost(), toxiproxy.getControlPort());
        postgresqlProxy = toxiproxyClient.createProxy("postgresql", "0.0.0.0:8666", "postgres:5432");
        minioProxy = toxiproxyClient.createProxy("minio", "0.0.0.0:8667", "minio:9000");

        registry.add("spring.datasource.url", () -> "jdbc:postgresql://%s:%d/%s"
                .formatted(toxiproxy.getHost(), toxiproxy.getMappedPort(8666), postgres.getDatabaseName()));
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("minio.endpoint", () -> "http://%s:%d"
                .formatted(toxiproxy.getHost(), toxiproxy.getMappedPort(8667)));
        registry.add("minio.accessKey", minio::getUserName);
        registry.add("minio.secretKey", minio::getPassword);
    }

    @Autowired
    ResilientMinIOClient resilientMinIOClient;

    @Autowired
    MinioClient minioClient;

    static final String BUCKET_NAME = "product";

    @SneakyThrows
    @BeforeAll
    static void beforeAll() {
        MinioClient tempMinIOClient = MinioClient.builder()
                .endpoint(minio.getS3URL())
                .credentials(minio.getUserName(), minio.getPassword())
                .build();

        boolean isBucketExist = tempMinIOClient.bucketExists(BucketExistsArgs.builder()
                .bucket(BUCKET_NAME)
                .build());

        if (!isBucketExist) {
            tempMinIOClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(BUCKET_NAME)
                            .build());
        }

        tempMinIOClient.setBucketVersioning(SetBucketVersioningArgs.builder()
                        .bucket(BUCKET_NAME)
                        .config(new VersioningConfiguration(VersioningConfiguration.Status.ENABLED, false))
                .build());

        ClassLoader loader = JpaCookbookApplicationTests.class.getClassLoader();
        File file = new File(loader.getResource("InspReport.txt").getFile());

        tempMinIOClient.uploadObject(UploadObjectArgs
                .builder()
                .bucket(BUCKET_NAME)
                .object("judge/InspReport.txt")
                .filename(file.getAbsolutePath())
                .build());
    }

    @Test
    void contextLoads() {
    }

    @SneakyThrows
    @Test
    void parseFromMinIO() {
        // https://www.baeldung.com/java-io-streams-closing
        InputStream is = minioClient.getObject(GetObjectArgs
                .builder()
                .bucket(BUCKET_NAME)
                .object("judge/InspReport.txt")
                .build());

        try (is) {
            List<String> data = IOUtils.readLines(is, StandardCharsets.UTF_8);
            var result = InspectionReport.parseTmp(data);
            assertThat(result).isEqualTo("12.986343,78.38263");
        }
    }

    @SneakyThrows
    @Test
    void get_TimeoutByResponseLatency() {
        minioProxy.toxics().latency("latency", ToxicDirection.DOWNSTREAM, 20_000);

        assertThatExceptionOfType(SocketTimeoutException.class)
                .isThrownBy(() -> uploadFile("judge/InspReport_1.txt"));

        minioProxy.toxics().get("latency").remove();
    }

    @SneakyThrows
    @Test
    void get_TimeoutByResponseLatency_RecoverByRetry() {
        minioProxy.toxics().latency("latency", ToxicDirection.DOWNSTREAM, 20_000);
        Timer timer = new Timer();
        timer.schedule(new ToxicRemover(minioProxy, "latency"), 21_000);

        assertThatCode(() -> {
            InputStream is = resilientMinIOClient.getObjectWithRetry(BUCKET_NAME, "judge/InspReport.txt");

            try (is) {
                List<String> data = IOUtils.readLines(is, StandardCharsets.UTF_8);
                var result = InspectionReport.parseTmp(data);
                assertThat(result).isEqualTo("12.986343,78.38263");
            }
        }).doesNotThrowAnyException();
    }

    @SneakyThrows
    @Test
    void upload_TimeoutByLowBandwidth() {
        minioProxy.toxics().bandwidth("cut_downstream", ToxicDirection.DOWNSTREAM, 0);
        minioProxy.toxics().bandwidth("cut_upstream", ToxicDirection.UPSTREAM, 0);

        assertThatExceptionOfType(SocketTimeoutException.class)
                .isThrownBy(() -> uploadFile("judge/InspReport_2.txt"));

        minioProxy.toxics().get("cut_downstream").remove();
        minioProxy.toxics().get("cut_upstream").remove();
    }

    @SneakyThrows
    @Test
    void upload_VersionChange_Normal() {
        String objPath = "judge/InspReport.txt";

        var statInit = statObjByPath(objPath);
        var response = uploadFile(objPath);

        assertThat(response.versionId()).isNotEqualTo(statInit.versionId());
    }

    @SneakyThrows
    @Test
    void upload_VersionChange_Timeout() {
        String objPath = "judge/InspReport_3.txt";

        var initResponse = uploadFile(objPath);

        minioProxy.toxics().latency("latency", ToxicDirection.DOWNSTREAM, 16_000);
        Timer timer = new Timer();
        timer.schedule(new ToxicRemover(minioProxy, "latency"), 16_000);

        assertThatExceptionOfType(SocketTimeoutException.class)
                .isThrownBy(() -> uploadFile(objPath));

        var statAfter = statObjByPath(objPath);

        assertThat(statAfter.versionId()).isNotEqualTo(initResponse.versionId());
    }

    @SneakyThrows
    @Test
    void upload_CheckExist_Timeout() {
        String objPath = "judge/InspReport_4.txt";

        var initResponse = uploadFile(objPath);

        minioProxy.toxics().latency("latency", ToxicDirection.DOWNSTREAM, 16_000);
        Timer timer = new Timer();
        timer.schedule(new ToxicRemover(minioProxy, "latency"), 16_000);

        resilientMinIOClient.uploadObjectWithRetry(BUCKET_NAME, objPath, getFile());

        var statAfter = statObjByPath(objPath);

        assertThat(statAfter.versionId()).isEqualTo(initResponse.versionId());
    }

    private StatObjectResponse statObjByPath(String name) throws ErrorResponseException, InsufficientDataException, InternalException, InvalidKeyException, InvalidResponseException, IOException, NoSuchAlgorithmException, ServerException, XmlParserException {
        return minioClient.statObject(StatObjectArgs.builder()
                .bucket(BUCKET_NAME)
                .object(name)
                .build());
    }

    @SneakyThrows
    private ObjectWriteResponse uploadFile(String objPath) {
        File file = getFile();

        return minioClient.uploadObject(UploadObjectArgs
                .builder()
                .bucket("product")
                .object(objPath)
                .filename(file.getAbsolutePath())
                .build());
    }

    @NotNull
    private File getFile() {
        ClassLoader loader = getClass().getClassLoader();
        return new File(loader.getResource("InspReport.txt").getFile());
    }

}
