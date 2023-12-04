package hle.jpacookbook.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SaveJudgeRequest {
    private Integer productId;
    private String inspectDate;
    private String judgeStage;
    private List<JudgeChange> judges;
}
