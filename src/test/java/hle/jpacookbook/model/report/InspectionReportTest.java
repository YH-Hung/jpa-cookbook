package hle.jpacookbook.model.report;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class InspectionReportTest {

    @Test
    public void testParse() throws IOException {
        ClassLoader loader = getClass().getClassLoader();
        File file = new File(loader.getResource("InspReport.txt").getFile());
        List<String> data = FileUtils.readLines(file, StandardCharsets.UTF_8);

        var result = InspectionReport.parseTmp(data);

        assertThat(result).isEqualTo("12.986343,78.38263");
    }

    @Test
    public void testParseFromMinIO() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        // https://www.baeldung.com/minio
        MinioClient minioClient = MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials("minioRoot", "minioRoot")
                .build();

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