package hle.jpacookbook.dto.report;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class PointLocation {
    private BigDecimal xMm;
    private BigDecimal yMm;
}
