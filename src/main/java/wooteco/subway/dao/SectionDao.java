package wooteco.subway.dao;

import wooteco.subway.domain.Section;

import java.util.List;

public interface SectionDao {
    Section save(Long lineId, Section section);

    List<Section> findByLineId(long lineId);

    List<Section> findAll();

    void batchUpdate(Long lineId, List<Section> sections);

    void deleteSectionsByLineId(Long lineId);

    void deleteById(Long sectionId, Long lineId);
}
