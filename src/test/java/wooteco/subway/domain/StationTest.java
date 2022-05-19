package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StationTest {

    @Test
    @DisplayName("이름이 같은지 확인한다.")
    public void hasSameNameWith() {
        // given
        Station station = new Station("청구역");
        // when
        boolean hasSameName = station.hasSameNameWith(new Station("청구역"));
        // then
        assertThat(hasSameName).isTrue();
    }
}
