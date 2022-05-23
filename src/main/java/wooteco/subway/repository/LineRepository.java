package wooteco.subway.repository;

import java.util.List;

import wooteco.subway.domain.line.Line;

public interface LineRepository {
	Long save(Line line);

	List<Line> findAll();

	Line findById(Long id);

	void update(Line line);

	void remove(Long id);

	Boolean existsByName(String name);
}
