package wooteco.subway.dto;

import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.domain.Path;

public class PathResponse {
    private final List<StationResponse> stations;
    private final int distance;
    private final int fare;

    private PathResponse(List<StationResponse> stations, int distance, int fare) {
        this.stations = stations;
        this.distance = distance;
        this.fare = fare;
    }

    public static PathResponse of(Path path) {
        final List<StationResponse> stationResponses = path.getStations()
                .stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
        final int distance = path.getDistance();
        final int fare = path.getCost();
        return new PathResponse(stationResponses, distance, fare);
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }

    public int getFare() {
        return fare;
    }

    @Override
    public String toString() {
        return "PathResponse{" +
                "stations=" + stations +
                ", distance=" + distance +
                ", fare=" + fare +
                '}';
    }
}
