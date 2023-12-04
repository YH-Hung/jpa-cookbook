package hle.jpacookbook.model.stage;

public sealed interface JudgeStage permits Manual1st, Manual2nd, Manual3rd {
    String getStageName();
    Integer getStageNo();
}
