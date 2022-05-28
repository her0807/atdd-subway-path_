package wooteco.subway.dao.section;

import java.util.List;
import wooteco.subway.domain.section.Section;

public interface SectionDao {

    Long save(Section section);

    List<Section> findAllByLineId(Long lineId);

    List<Section> findAll();

    void update(Section section);

    void delete(Long id);

    boolean existSectionById(Long id);
}
