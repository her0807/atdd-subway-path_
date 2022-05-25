package wooteco.subway.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import wooteco.subway.domain.Line;

public class LineRequest {

    @NotBlank(message = "이름이 공백일 수 없습니다")
    private String name;

    @NotBlank(message = "색깔을 선택해야 합니다")
    private String color;

    @NotNull
    @Min(value = 0, message = "추가 요금은 0이상이여야 합니다.")
    private int extraFare;

    @NotNull
    @Min(value = 1, message = "상행역은 1 이상이어야 합니다")
    private Long upStationId;

    @NotNull
    @Min(value = 1, message = "하행역은 1 이상이어야 합니다")
    private Long downStationId;

    @NotNull
    @Min(value = 1, message = "거리는 1 이상이여야 합니다")
    private int distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color, int extraFare, Long upStationId,
        Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.extraFare = extraFare;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static Line toLine(LineRequest lineRequest) {
        return new Line(lineRequest.getName(), lineRequest.getColor(), lineRequest.getExtraFare());
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public int getExtraFare() {
        return extraFare;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
