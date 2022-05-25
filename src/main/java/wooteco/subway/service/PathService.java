package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Path;
import wooteco.subway.domain.PathFinder;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.policy.FarePolicies;
import wooteco.subway.dto.PathRequest;
import wooteco.subway.dto.PathResponse;

@Service
@Transactional(readOnly = true)
public class PathService {

    private final LineDao lineDao;
    private final StationDao stationDao;

    public PathService(LineDao lineDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    public PathResponse findShortestPath(PathRequest pathRequest) {
        List<Line> lines = lineDao.findAll();
        PathFinder pathGraph = PathFinder.from(lines);

        Station sourceStation = findStationById(pathRequest.getSource());
        Station targetStation = findStationById(pathRequest.getTarget());
        Path path = pathGraph.findShortestPath(sourceStation, targetStation);
        int fare = FarePolicies.of(path, pathRequest.getAge()).calculate();

        return PathResponse.of(path, fare);
    }

    private Station findStationById(Long id) {
        if (!stationDao.existById(id)) {
            throw new IllegalArgumentException("존재하지 않는 id입니다.");
        }
        return stationDao.findById(id);
    }
}
