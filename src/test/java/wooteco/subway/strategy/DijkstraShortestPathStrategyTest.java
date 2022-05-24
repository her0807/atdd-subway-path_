package wooteco.subway.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Lines;
import wooteco.subway.domain.Path;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

class DijkstraShortestPathStrategyTest {

    @DisplayName("최단 거리를 구한다.")
    @Test
    void getShortestDistance() {
        // given
        Station station1 = new Station(1L, "신림역");
        Station station2 = new Station(2L, "선릉역");
        Station station3 = new Station(3L, "잠실역");
        Section section1 = new Section(station1, station2, 10);
        Section section2 = new Section(station2, station3, 10);
        Sections sections = new Sections(List.of(section1, section2));
        Lines lines = new Lines(List.of(new Line(1L, "2호선", "green", 300, sections)));

        // when
        Path path = new DijkstraShortestPathStrategy().getPath(
                lines,
                station1,
                station2
        );

        // then
        assertThat(path.getDistance()).isEqualTo(10);
    }

    @DisplayName("최단 경로를 구한다.")
    @Test
    void getPath() {
        // given
        Station station1 = new Station(1L, "신림역");
        Station station2 = new Station(2L, "선릉역");
        Station station3 = new Station(3L, "잠실역");
        Section section1 = new Section(station1, station2, 10);
        Section section2 = new Section(station2, station3, 10);
        Sections sections = new Sections(List.of(section1, section2));
        Lines lines = new Lines(List.of(new Line(1L, "2호선", "green", 300, sections)));

        // when
        Path path = new DijkstraShortestPathStrategy().getPath(
                lines,
                station1,
                station3
        );

        // then
        assertThat(path.getStations())
                .hasSize(3)
                .containsExactly(station1, station2, station3);
    }

    @DisplayName("최단 거리가 같다면 역이 더 적은 경로로 최단 경로를 구한다.")
    @Test
    void getPathSameDistance() {
        // given
        Station station1 = new Station(1L, "선릉역");
        Station station2 = new Station(2L, "삼성역");
        Station station3 = new Station(3L, "선정릉역");
        Station station4 = new Station(4L, "봉은사역");
        Station station5 = new Station(5L, "종합운동장역");
        Section section1 = new Section(station1, station2, 5);
        Section section2 = new Section(station2, station5, 5);
        Section section3 = new Section(station1, station3, 3);
        Section section4 = new Section(station3, station4, 3);
        Section section5 = new Section(station4, station5, 4);
        Sections sections1 = new Sections(List.of(section1, section2));
        Sections sections2 = new Sections(List.of(section3, section4, section5));
        List<Line> lines = List.of(
                new Line(1L, "2호선", "green", 300, sections1),
                new Line(2L, "수인분당선", "yellow", 500, sections2)
        );

        // when
        Path path = new DijkstraShortestPathStrategy().getPath(
                new Lines(lines),
                station1,
                station5
        );

        // then
        assertThat(path.getStations())
                .hasSize(3)
                .containsExactly(station1, station2, station5);
    }

    @DisplayName("두 역이 이어져있지 않으면 에러가 발생한다.")
    @Test
    void getPathNotConnect() {
        // given
        Station station1 = new Station(1L, "선릉역");
        Station station2 = new Station(2L, "삼성역");
        Station station3 = new Station(3L, "선정릉역");
        Station station4 = new Station(4L, "봉은사역");
        Station station5 = new Station(5L, "종합운동장역");
        Section section1 = new Section(station1, station2, 5);
        Section section2 = new Section(station2, station3, 5);
        Section section3 = new Section(station4, station5, 3);
        Sections sections = new Sections(List.of(section1, section2, section3));
        Lines lines = new Lines(List.of(new Line(1L, "2호선", "green", 300, sections)));

        // when
        // then
        assertThatThrownBy(() ->
                new DijkstraShortestPathStrategy().getPath(
                        lines,
                        station1,
                        station5
                ))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("이동할 수 있는 경로가 없습니다.");
    }

    @DisplayName("구간이 등록되지 않은 역을 탐색하면 에러가 발생한다.")
    @Test
    void getPathNotHasSection() {
        // given
        Station station1 = new Station(1L, "신림역");
        Station station2 = new Station(2L, "선릉역");
        Station station3 = new Station(3L, "잠실역");
        Station station4 = new Station(4L, "왕십리역");
        Section section1 = new Section(station1, station2, 10);
        Section section2 = new Section(station2, station3, 10);
        Section section3 = new Section(station1, station3, 10);
        Sections sections = new Sections(List.of(section1, section2, section3));
        Lines lines = new Lines(List.of(new Line(1L, "2호선", "green", 300, sections)));

        // when
        // then
        assertThatThrownBy(() -> new DijkstraShortestPathStrategy().getPath(
                lines,
                station1,
                station4
        ))
                .isInstanceOf(NoSuchElementException.class);
    }
}
