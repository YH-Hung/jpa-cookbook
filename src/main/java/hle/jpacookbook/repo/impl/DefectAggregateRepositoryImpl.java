package hle.jpacookbook.repo.impl;

import hle.jpacookbook.constant.DefectAttrType;
import hle.jpacookbook.dao.ProductDefectRepository;
import hle.jpacookbook.entity.ProductDefect;
import hle.jpacookbook.entity.ProductDefectAttr;
import hle.jpacookbook.model.judge.DefectEdit;
import hle.jpacookbook.model.judge.DefectView;
import hle.jpacookbook.model.query.DefectQuery;
import hle.jpacookbook.model.query.InspectQuery;
import hle.jpacookbook.repo.DefectAggregateRepository;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DefectAggregateRepositoryImpl implements DefectAggregateRepository {

    public final ProductDefectRepository defectRepository;

    public DefectAggregateRepositoryImpl(ProductDefectRepository defectRepository) {
        this.defectRepository = defectRepository;
    }

    @Override
    public Option<DefectView> getRecordById(DefectQuery defectQuery) {
        return defectRepository.findById_ProductIdAndId_InspectDateAndId_DefectId(defectQuery.productId(), defectQuery.inspDateTime(), defectQuery.defectId())
                .map(this::entityToView);
    }

    @Override
    public List<DefectView> getRecordsByInsp(InspectQuery inspectQuery) {
        return defectRepository.findById_ProductIdAndId_InspectDate(inspectQuery.productId(), inspectQuery.inspDateTime())
                .stream().map(this::entityToView)
                .toList();
    }

    @Override
    public List<DefectEdit> getEditModels(InspectQuery inspectQuery) {
        return defectRepository.findById_ProductIdAndId_InspectDate(inspectQuery.productId(), inspectQuery.inspDateTime())
                .stream().map(this::entityToEdit)
                .toList();
    }

    private DefectView entityToView(ProductDefect entity) {
        var view = new DefectView();
        view.setProductId(entity.getId().getProductId());
        view.setInspectDate(entity.getId().getInspectDate());
        view.setDefectId(entity.getId().getDefectId());

        entity.getAttrs().forEach(attr -> Try.of(() -> DefectAttrType.valueOf(attr.getType()))
                .peek(attrType -> {
                    switch (attrType) {
                        case MANUAL_JUDGE_1ST -> view.setManualJudgeCode1st(attr.getVal());
                        case MANUAL_JUDGE_2ND -> view.setManualJudgeCode2nd(attr.getVal());
                        case MANUAL_JUDGE_3RD -> view.setManualJudgeCode3rd(attr.getVal());
                    }
                }));

        return view;
    }

    private DefectEdit entityToEdit(ProductDefect entity) {
        Map<Integer, ProductDefectAttr> attrMap = new HashMap<>();
        entity.getAttrs().forEach(attr -> Try.of(() -> DefectAttrType.valueOf(attr.getType()))
                .peek(attrType -> {
                    switch (attrType) {
                        case MANUAL_JUDGE_1ST -> attrMap.put(1, attr);
                        case MANUAL_JUDGE_2ND -> attrMap.put(2, attr);
                        case MANUAL_JUDGE_3RD -> attrMap.put(3, attr);
                    }
                }));

        return new DefectEdit(
                entity.getId().getProductId(),
                entity.getId().getInspectDate(),
                entity.getId().getDefectId(),
                Option.of(attrMap.getOrDefault(1, null)),
                Option.of(attrMap.getOrDefault(2, null)),
                Option.of(attrMap.getOrDefault(3, null))
        );
    }

}
