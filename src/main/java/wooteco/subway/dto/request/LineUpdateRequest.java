package wooteco.subway.dto.request;

public class LineUpdateRequest {

    private String name;
    private String color;

    private LineUpdateRequest() {
    }

    public LineUpdateRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
