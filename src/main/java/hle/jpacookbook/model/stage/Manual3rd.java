package hle.jpacookbook.model.stage;

import hle.jpacookbook.constant.CommonConstant;

public final class Manual3rd implements JudgeStage {
    @Override
    public String getStageName() {
        return CommonConstant.MANUAL_JUDGE_3RD_STATE_NAME;
    }

    @Override
    public Integer getStageNo() {
        return 3;
    }
}
