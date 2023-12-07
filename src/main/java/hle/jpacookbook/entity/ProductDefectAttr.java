package hle.jpacookbook.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "product_defect_attr")
public class ProductDefectAttr {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", nullable = false)
    private Integer id;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "inspect_date")
    private LocalDateTime inspectDate;

    @Column(name = "defect_id")
    private Integer defectId;

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "val", length = 20)
    private String val;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "product_id", referencedColumnName = "product_id", insertable = false, updatable = false),
            @JoinColumn(name = "inspect_date", referencedColumnName = "inspect_date", insertable = false, updatable = false),
            @JoinColumn(name = "defect_id", referencedColumnName = "defect_id", insertable = false, updatable = false)
    })
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    private ProductDefect defect;
}