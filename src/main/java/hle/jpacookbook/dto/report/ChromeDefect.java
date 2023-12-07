package hle.jpacookbook.dto.report;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChromeDefect {
    private Integer id;
    private PointLocation location;
}
