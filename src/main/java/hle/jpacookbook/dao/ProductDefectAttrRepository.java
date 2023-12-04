package hle.jpacookbook.dao;

import hle.jpacookbook.entity.ProductDefectAttr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductDefectAttrRepository extends JpaRepository<ProductDefectAttr, Integer> {

}