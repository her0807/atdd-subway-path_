package wooteco.subway.ui.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final List<StationResponse> stations;

    private LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse from(Line line) {
        List<Station> stations = line.getStations();
        List<StationResponse> stationResponses = stations.stream()
            .map(StationResponse::from)
            .collect(Collectors.toList());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses);
    }

    public static LineResponse from(Long id, Line line) {
        return new LineResponse(id, line.getName(), line.getColor(), new ArrayList<>());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
