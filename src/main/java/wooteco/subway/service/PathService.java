package wooteco.subway.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.line.LineInfo;
import wooteco.subway.domain.path.GraphGenerator;
import wooteco.subway.domain.path.Path;
import wooteco.subway.domain.path.PathManager;
import wooteco.subway.domain.path.cost.CostManager;
import wooteco.subway.domain.path.cost.DistanceSection;
import wooteco.subway.domain.station.Station;
import wooteco.subway.dto.response.PathResponse;
import wooteco.subway.repository.StationRepository;
import wooteco.subway.repository.SubwayRepository;

@Service
public class PathService {

    private final SubwayRepository subwayRepository;
    private final StationRepository stationRepository;
    private final CostManager costManager;

    public PathService(SubwayRepository subwayRepository, StationRepository stationRepository, CostManager costManager) {
        this.subwayRepository = subwayRepository;
        this.stationRepository = stationRepository;
        this.costManager = costManager;
    }

    public PathResponse findShortestPath(long sourceStationId, long targetStationId, int age) {
        PathManager pathManager = PathManager.of(
                GraphGenerator.toAdjacentPath(subwayRepository.findAllSections(), generateAllLinesCosts()));
        Station startStation = stationRepository.findExistingStation(sourceStationId);
        Station endStation = stationRepository.findExistingStation(targetStationId);
        Path optimalPath = pathManager.calculateOptimalPath(startStation, endStation);
        int fare = costManager.calculateFare(optimalPath.getTotalDistance(), optimalPath.getExtraFare(), age);

        return PathResponse.of(optimalPath, fare);
    }

    private Map<Long, Integer> generateAllLinesCosts() {
        List<LineInfo> lineInfos = subwayRepository.findAllLines();
        Map<Long, Integer> costs = new HashMap<>();
        for (LineInfo lineInfo : lineInfos) {
            costs.put(lineInfo.getId(), lineInfo.getExtraFare());
        }
        return costs;
    }
}
