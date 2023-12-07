package hle.jpacookbook.controller;

import hle.jpacookbook.dao.ProductDefectRepository;
import hle.jpacookbook.entity.ProductDefect;
import hle.jpacookbook.model.judge.DefectView;
import hle.jpacookbook.model.query.InspectQuery;
import hle.jpacookbook.repo.DefectAggregateRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/defects")
public class DefectController {

    private final ProductDefectRepository defectRepository;
    private final DefectAggregateRepository defectAggregateRepository;

    public DefectController(ProductDefectRepository defectRepository, DefectAggregateRepository defectAggregateRepository) {
        this.defectRepository = defectRepository;
        this.defectAggregateRepository = defectAggregateRepository;
    }

    @GetMapping("/entities")
    public List<ProductDefect> getDefects() {
        return defectRepository.findAll();
    }

    @GetMapping("/views")
    public List<DefectView> getViews(@RequestParam Integer productId, @RequestParam String inspDt) {
        var inspect = new InspectQuery(productId, LocalDateTime.parse(inspDt));
        return defectAggregateRepository.getRecordsByInsp(inspect);
    }


}
