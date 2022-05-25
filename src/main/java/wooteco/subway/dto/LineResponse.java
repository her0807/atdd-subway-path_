package wooteco.subway.dto;

import java.util.List;
import wooteco.subway.domain.Line;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;
    private int extraFare;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, List<StationResponse> stations, int extraFare) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
        this.extraFare = extraFare;
    }

    public LineResponse(Line line, List<StationResponse> stations) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
        this.stations = stations;
        this.extraFare = line.getExtraFare();
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

    public int getExtraFare() {
        return extraFare;
    }
}
