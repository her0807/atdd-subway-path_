package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;

import wooteco.subway.domain.Line;

public interface LineDao {

    Long save(Line line);

    List<Line> findAll();

    boolean deleteById(Long id);

    Optional<Line> findById(Long id);

    List<Line> findByIds(List<Long> lineId);

    boolean updateById(Line line);

    boolean existsByName(String name);
}
