package hle.jpacookbook.model.stage;

import hle.jpacookbook.constant.CommonConstant;

public final class Manual1st implements JudgeStage {
    @Override
    public String getStageName() {
        return CommonConstant.MANUAL_JUDGE_1ST_STAGE_NAME;
    }

    @Override
    public Integer getStageNo() {
        return 1;
    }
}
