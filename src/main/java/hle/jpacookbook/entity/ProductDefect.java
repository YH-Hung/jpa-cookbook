package hle.jpacookbook.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "product_defect")
public class ProductDefect {
    @EmbeddedId
    private ProductDefectId id;

    @Column(name = "auto_code", length = 2)
    private String autoCode;

    @Column(name = "manual_code", length = 2)
    private String manualCode;

    @OneToMany(mappedBy = "defect")
    @SQLRestriction("type like 'MANUAL_JUDGE%'")
    private List<ProductDefectAttr> attrs;

}