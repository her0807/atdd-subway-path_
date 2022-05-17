package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.PathCalculator;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.response.PathResponse;
import wooteco.subway.repository.LineRepository;

@Service
public class PathService {

    private final LineRepository lineRepository;

    public PathService(final LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public PathResponse findShortestPath(final Station source, final Station target) {
        final List<Line> lines = lineRepository.findAll();

        final PathCalculator pathCalculator = new PathCalculator(lines);
        final List<Station> stations = pathCalculator.findShortestPath(source, target);

        return new PathResponse(stations);
    }
}
