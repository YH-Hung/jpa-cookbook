package hle.jpacookbook.model.report;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

        assertThat(result).isEqualTo("12.986343 mm, 78.38263 mm");
    }
}