package hle.jpacookbook.controller;

import hle.jpacookbook.model.report.InspectionReport;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/report")
public class MinioTester {

    @Autowired
    MinioClient minioClient;

    @SneakyThrows
    @GetMapping("/insp")
    public String getReport() {
        InputStream is = minioClient.getObject(GetObjectArgs
                .builder()
                .bucket("product")
                .object("judge/InspReport.txt")
                .build());

        List<String> data = IOUtils.readLines(is, StandardCharsets.UTF_8);
        var result = InspectionReport.parseTmp(data);

        return result;
    }
}
