package hle.jpacookbook.model.judge;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DefectView {
    private Integer productId;
    private LocalDateTime inspectDate;
    private Integer defectId;
    private String manualJudgeCode1st;
    private String manualJudgeCode2nd;
    private String manualJudgeCode3rd;
}
