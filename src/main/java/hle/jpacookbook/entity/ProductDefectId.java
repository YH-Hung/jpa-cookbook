package hle.jpacookbook.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class ProductDefectId implements Serializable {
    @Serial
    private static final long serialVersionUID = 2482981761874250727L;
    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(name = "inspect_date", nullable = false)
    private LocalDateTime inspectDate;

    @Column(name = "defect_id", nullable = false)
    private Integer defectId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProductDefectId entity = (ProductDefectId) o;
        return Objects.equals(this.productId, entity.productId) &&
                Objects.equals(this.inspectDate, entity.inspectDate) &&
                Objects.equals(this.defectId, entity.defectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, inspectDate, defectId);
    }

}