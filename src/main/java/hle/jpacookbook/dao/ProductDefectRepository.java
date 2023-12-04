package hle.jpacookbook.dao;

import hle.jpacookbook.entity.ProductDefect;
import hle.jpacookbook.entity.ProductDefectId;
import io.vavr.control.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductDefectRepository extends JpaRepository<ProductDefect, ProductDefectId> {
    Option<ProductDefect> findById_ProductIdAndId_InspectDateAndId_DefectId(Integer productId, LocalDateTime inspectDate, Integer defectId);

    List<ProductDefect> findById_ProductIdAndId_InspectDate(Integer productId, LocalDateTime inspectDate);
}