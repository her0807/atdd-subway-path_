package wooteco.subway.domain.path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Lines;
import wooteco.subway.domain.Stations;

public class PathTest {

    @DisplayName("경로의 거리가 1보다 작을 경우 예외를 반환한다.")
    @Test
    void notAllowDistanceLessThan1() {
        assertThatThrownBy(() -> new Path(new Stations(List.of()), new Lines(List.of()), 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("경로 거리는 1보다 작을 수 없습니다.");
    }

    @DisplayName("거쳐간 노선 중 가장 추가요금이 비싼 노선의 추가요금을 반환한다.")
    @Test
    void findMostExpensiveExtraFare() {
        Line line = new Line("2호선", "초록색", 0);
        Line expensiveLine = new Line("신분당선", "빨간색", 500);

        Path path = new Path(new Stations(List.of()), new Lines(List.of(line, expensiveLine)), 1);

        assertThat(path.getMostExpensiveExtraFare()).isEqualTo(500);
    }

}
