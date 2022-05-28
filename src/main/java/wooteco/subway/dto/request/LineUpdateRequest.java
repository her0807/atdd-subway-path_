package wooteco.subway.dto.request;

import javax.validation.constraints.NotBlank;

public class LineUpdateRequest {

    @NotBlank
    private String name;

    @NotBlank
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

    @Override
    public String toString() {
        return "LineUpdateRequest{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
