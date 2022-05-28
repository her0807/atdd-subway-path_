package wooteco.subway.infra.path;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Path;
import wooteco.subway.domain.Station;

import java.util.List;

public interface PathFinder {
    Path findShortestPath(Station source, Station target, List<Line> lines);
}
