package wooteco.subway.controller.converter;

import java.util.List;
import java.util.stream.Collectors;

import wooteco.subway.controller.dto.response.PathResponse;
import wooteco.subway.controller.dto.response.StationResponse;
import wooteco.subway.service.dto.request.PathServiceRequest;
import wooteco.subway.service.dto.response.PathServiceResponse;

public class PathConverter {
    public static PathServiceRequest toInfo(Long source, Long target, int age) {
        return new PathServiceRequest(source, target, age);
    }

    public static PathResponse toResponse(PathServiceResponse pathServiceResponse) {
        List<StationResponse> stationResponses = pathServiceResponse.getStations()
            .stream()
            .map(stationDto -> new StationResponse(stationDto.getId(), stationDto.getName()))
            .collect(Collectors.toList());
        return new PathResponse(stationResponses, pathServiceResponse.getDistance(), pathServiceResponse.getFare());
    }
}
