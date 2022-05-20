package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Fare;
import wooteco.subway.domain.Path;
import wooteco.subway.domain.Sections;
import wooteco.subway.service.dto.path.PathRequestDto;
import wooteco.subway.service.dto.path.PathResponseDto;
import wooteco.subway.service.dto.station.StationResponseDto;

@Service
public class PathService {

    private final StationService stationService;
    private final SectionService sectionService;

    public PathService(StationService stationService, SectionService sectionService) {
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    @Transactional(readOnly = true)
    public PathResponseDto getPath(PathRequestDto pathRequestDto) {
        List<Long> stationIds = stationService.findStations().stream()
                .map(StationResponseDto::getId)
                .collect(Collectors.toList());
        Sections sections = sectionService.findAll();
        Path path = new Path(pathRequestDto.getSource(), pathRequestDto.getTarget(), stationIds, sections);
        Fare fare = new Fare();
        List<StationResponseDto> stations = path.getShortestPath().stream()
                .map(stationService::findById)
                .map(StationResponseDto::new)
                .collect(Collectors.toList());
        return new PathResponseDto(stations, path.getTotalDistance(), fare.calculateFare(path.getTotalDistance()));
    }
}
