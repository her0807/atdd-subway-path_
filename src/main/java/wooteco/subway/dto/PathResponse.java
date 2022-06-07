package wooteco.subway.dto;

import java.util.List;
import wooteco.subway.domain.Station;

public class PathResponse {
    private List<Station> stations;
    private int distance;
    private int fare;

    public PathResponse() {
    }

    public PathResponse(List<Station> stations, int distance, int fare) {
        this.stations = stations;
        this.distance = distance;
        this.fare = fare;
    }

    public List<Station> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }

    public int getFare() {
        return fare;
    }
}
