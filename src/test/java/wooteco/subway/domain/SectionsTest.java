package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.DomainException;
import wooteco.subway.exception.ExceptionMessage;

class SectionsTest {

    Station 상계;
    Station 중계;
    Station 하계;
    Station 노원;

    Section 상계_중계;
    Section 중계_하계;
    Section 상계_노원;
    Sections sections;
    Sections invalidSection;

    Line line = new Line(1L, "7호선", "red", 100L);

    @BeforeEach
    void setUp() {
        상계 = new Station(1L, "상계");
        중계 = new Station(2L, "중계");
        하계 = new Station(3L, "하계");
        노원 = new Station(4L, "노원");

        중계_하계 = new Section(1L, line, 중계, 하계, 10);
        상계_중계 = new Section(2L, line, 상계, 중계, 10);
        상계_노원 = new Section(3L, line, 상계, 노원, 5);
        sections = new Sections(List.of(상계_중계, 중계_하계));
        invalidSection = new Sections(List.of(상계_노원, 중계_하계));
    }

    @Test
    @DisplayName("상계-중계-하계에 상계-노원 구간 추가하기")
    void add() {
        sections.add(상계_노원);

        assertThat(sections.getValue()).hasSize(3);
    }

    @Test
    @DisplayName("상계-중계-하계 순서대로 역 반환")
    void getSortedStation() {
        List<Station> sorted = sections.getSortedStation();

        assertThat(sorted).containsExactly(상계, 중계, 하계);
    }

    @Test
    @DisplayName("상계-노원, 중계-하계 에서 순서대로 역반환 시도시 예외")
    void getSortedStation_unconnected() {
        // given
        assertThatThrownBy(() -> invalidSection.getSortedStation())
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("상계-중계-하계에서 중계 제거")
    void deleteNearBy() {
        sections.deleteNearBy(중계);

        assertThat(sections.getValue()).hasSize(1);
        assertThat(sections.getValue().get(0).getDistance())
                .isEqualTo(중계_하계.getDistance() + 상계_중계.getDistance());
    }

    @Test
    @DisplayName("구간이 하나일 때 특정역에 따라 삭제할 구간 찾으려 하면 예외")
    void findNearByStationId_invalid() {
        // when
        Sections sections = new Sections(List.of(상계_노원));

        // then
        assertThatThrownBy(() -> sections.deleteNearBy(상계))
                .isInstanceOf(DomainException.class)
                .hasMessage(ExceptionMessage.SECTIONS_NOT_DELETABLE.getContent());
    }
}
