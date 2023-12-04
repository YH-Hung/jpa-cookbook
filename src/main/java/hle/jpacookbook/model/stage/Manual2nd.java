package hle.jpacookbook.model.stage;

import hle.jpacookbook.constant.CommonConstant;

public final class Manual2nd implements JudgeStage {
    @Override
    public String getStageName() {
        return CommonConstant.MANUAL_JUDGE_2ND_STAGE_NAME;
    }

    @Override
    public Integer getStageNo() {
        return 2;
    }
}
