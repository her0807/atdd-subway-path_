package wooteco.subway.service.dto.response;

import java.util.List;

import wooteco.subway.service.dto.StationDto;

public class PathServiceResponse {
    private final List<StationDto> stations;
    private final int distance;
    private final int fare;

    public PathServiceResponse(List<StationDto> stations, int distance, int fare) {
        this.stations = stations;
        this.distance = distance;
        this.fare = fare;
    }

    public List<StationDto> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }

    public int getFare() {
        return fare;
    }
}
