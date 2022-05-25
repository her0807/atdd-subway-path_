package wooteco.subway.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import wooteco.subway.domain.property.Distance;
import wooteco.subway.domain.station.Station;

public class PathResponse {

    private final List<StationResponse> stations;
    private final Integer distance;
    private final Integer fare;

    public PathResponse(List<StationResponse> stations, Integer distance, Integer fare) {
        this.stations = stations;
        this.distance = distance;
        this.fare = fare;
    }

    public PathResponse() {
        this(null, null, null);
    }

    public static PathResponse of(List<Station> paths, Distance distance, int fare) {
        final List<StationResponse> stations = paths.stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
        return new PathResponse(stations, distance.getValue(), fare);
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public Integer getDistance() {
        return distance;
    }

    public Integer getFare() {
        return fare;
    }
}
