package wooteco.subway.domain.path;

import java.util.List;

import wooteco.subway.domain.line.Lines;
import wooteco.subway.domain.station.Station;

public interface PathFindingStrategy {
    int getShortestDistance(Station source, Station target, Lines lines);

    List<Station> getShortestPath(Station source, Station target, Lines lines);

    List<Long> getLineIds(Station source, Station target, Lines lines);
}
