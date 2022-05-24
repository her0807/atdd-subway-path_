package wooteco.subway.dto.line;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import wooteco.subway.domain.Line;

public class LineUpdateRequest {

    @NotBlank(message = "line 이름은 공백 혹은 null이 들어올 수 없습니다.")
    private String name;

    @NotBlank(message = "line 색상은 공백 혹은 null이 들어올 수 없습니다.")
    private String color;

    @PositiveOrZero(message = "요금의 최소 금액은 0입니다.")
    private int extraFare;

    private LineUpdateRequest() {
    }

    public LineUpdateRequest(final String name, final String color, int extraFare) {
        this.name = name;
        this.color = color;
        this.extraFare = extraFare;
    }

    public Line toLineWithId(final Long lineId) {
        return new Line(lineId, name, color, extraFare);
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
}
