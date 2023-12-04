package hle.jpacookbook.model.query;

import java.time.LocalDateTime;

public record DefectQuery(
        Integer productId,
        LocalDateTime inspDateTime,
        Integer defectId
) {
}
