package wooteco.subway.acceptance.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Path;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

class PathTest {
    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;
    private Station 삼성역;
    private Station 종합운동장역;
    private Station 잠실새내역;
    private Station 잠실역;

    private List<Section> sections;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        선릉역 = new Station("선릉역");
        삼성역 = new Station("삼성역");
        종합운동장역 = new Station("종합운동장역");
        잠실새내역 = new Station("잠실새내역");
        잠실역 = new Station("잠실역");

        Section 강남_역삼 = Section.createWithoutId(강남역, 역삼역, 5);
        Section 역삼_선릉 = Section.createWithoutId(역삼역, 선릉역, 5);
        Section 선릉_삼성 = Section.createWithoutId(선릉역, 삼성역, 5);
        Section 삼성_종합운동장 = Section.createWithoutId(삼성역, 종합운동장역, 5);
        Section 종합운동장_잠실새내 = Section.createWithoutId(종합운동장역, 잠실새내역, 5);
        Section 잠실새내_잠실 = Section.createWithoutId(잠실새내역, 잠실역, 5);

        sections = List.of(강남_역삼, 역삼_선릉, 선릉_삼성, 삼성_종합운동장, 종합운동장_잠실새내, 잠실새내_잠실);
    }

    @DisplayName("구간 리스트를 전달 받아 Path 객체를 생성한다.")
    @Test
    void constructor_withSectionList() {
        // given & when
        Path createdPath = Path.from(sections, 강남역, 잠실역);

        // then
        assertThat(createdPath).isNotNull();
    }

    @DisplayName("getStations 를 호출하면 생성자에서 계산한 강남역에서 잠실역까지의 최단경로 역의 목록은 7개이다.")
    @Test
    void shortest_path() {
        // given
        Path createdPath = Path.from(sections, 강남역, 잠실역);

        // when
        List<Station> actual = createdPath.getStations();

        // then
        assertThat(actual).containsExactly(강남역, 역삼역, 선릉역, 삼성역, 종합운동장역, 잠실새내역, 잠실역);
    }

    @DisplayName("getStations 를 호출하면 생성자에서 계산한 강남역에서 잠실새내역까지의 최단경로 역의 목록은 6개이다.")
    @Test
    void shortest_path_2() {
        // given
        Path createdPath = Path.from(sections, 강남역, 잠실새내역);

        // when
        List<Station> actual = createdPath.getStations();

        // then
        assertThat(actual).containsExactly(강남역, 역삼역, 선릉역, 삼성역, 종합운동장역, 잠실새내역);
    }

    @DisplayName("추가요금이 있는 여러 노선을 환승할 경우 가장 높은 추가요금을 받는다.")
    @Test
    void get_max_extra_fare() {
        //given
        final Line 신분당선 = Line.createWithoutSection(1L, "신분당선", "빨강", 500);
        final Line 호선9 = Line.createWithoutSection(2L, "9호선", "곤색", 700);
        final Path path = Path.from(List.of(Section.createWithLine(1L, 신분당선, 강남역, 역삼역, 5),
                Section.createWithLine(2L, 호선9, 역삼역, 삼성역, 5)), 강남역, 삼성역);
        //when
        final int maxExtraFare = path.getMaxExtraFare();
        //then
        assertThat(maxExtraFare).isEqualTo(700);
    }
}
