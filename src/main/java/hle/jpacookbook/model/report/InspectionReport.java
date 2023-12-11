package hle.jpacookbook.model.report;

import hle.jpacookbook.dto.report.ChromeDefect;
import hle.jpacookbook.dto.report.PointLocation;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@ToString
public class InspectionReport {
    private String type;
    private Integer orientation;
    private PointLocation bottomLeftCorner;
    private PointLocation TopLeftCorner;
    private List<PointLocation> diePoints;
    private List<ChromeDefect> defectList;

    private static final String KEY_INSPECTION_TYPE = "Inspection:";
    private static final String KEY_BOTTOM_LEFT  = "Bottom Left Corner";
    private static final String KEY_TOP_RIGHT = "Top Right Corner";
    private static final String KEY_ORIENTATION = "Orientation:";
    private static final String KEY_DIE_POINT = "DiePoint";
    private static final String KEY_DEFECT = "Location:";

    private static final Pattern CORNER_PATTERN = Pattern.compile("(\\d+.\\d+) mm, (\\d+.\\d+) mm");

    public static InspectionReport parse(List<String> lines) {
        InspectionReport report = new InspectionReport();

        return report;
    }

    public static String parseTmp(List<String> lines) {
        for (String line : lines) {
            if (line.contains(KEY_BOTTOM_LEFT)) {

                Matcher matcher = CORNER_PATTERN.matcher(line);
                if (matcher.find()) {
                    return String.format("%s,%s", matcher.group(1), matcher.group(2));
                }
            }
        }

        return null;
    }
}
