package wooteco.subway.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public class LineRequest {

    @NotBlank(message = "노선 이름은 빈 문자열일 수 없습니다.")
    @Length(max = 255, message = "노선 이름은 255자이하여야 합니다.")
    private String name;

    @NotBlank(message = "노선 색깔은 빈 문자열일 수 없습니다.")
    @Length(max = 20, message = "노선 색깔은 20자이하여야 합니다.")
    private String color;

    @NotNull(message = "상행역 ID가 필요합니다.")
    private Long upStationId;

    @NotNull(message = "하행역 ID가 필요합니다.")
    private Long downStationId;

    @Min(value = 1, message = "거리는 1이상이여야 합니다.")
    private int distance;

    @Min(value = 0, message = "추가 요금은 0이상이여야 합니다.")
    private int extraFare;

    public LineRequest() {
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId,
                       Integer distance, Integer extraFare) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.extraFare = extraFare;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Integer getDistance() {
        return distance;
    }

    public Integer getExtraFare() {
        return extraFare;
    }
}
