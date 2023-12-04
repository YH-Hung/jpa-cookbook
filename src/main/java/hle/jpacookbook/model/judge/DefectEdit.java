package hle.jpacookbook.model.judge;

import hle.jpacookbook.entity.ProductDefectAttr;
import io.vavr.control.Option;

import java.time.LocalDateTime;

public record DefectEdit(
        Integer productId,
        LocalDateTime inspectDate,
        Integer defectId,
        Option<ProductDefectAttr> manualJudge1stEntity,
        Option<ProductDefectAttr> manualJudge2ndEntity,
        Option<ProductDefectAttr> manualJudge3rdEntity
) {
}
