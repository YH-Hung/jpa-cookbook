package hle.jpacookbook.model.query;

import java.time.LocalDateTime;

public record InspectQuery(
        Integer productId,
        LocalDateTime inspDateTime
) {
}
