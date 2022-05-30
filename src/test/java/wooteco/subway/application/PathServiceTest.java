package wooteco.subway.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wooteco.subway.Infrastructure.line.LineDao;
import wooteco.subway.Infrastructure.line.MemoryLineDao;
import wooteco.subway.Infrastructure.section.MemorySectionDao;
import wooteco.subway.Infrastructure.section.SectionDao;
import wooteco.subway.Infrastructure.station.MemoryStationDao;
import wooteco.subway.Infrastructure.station.StationDao;
import wooteco.subway.domain.fare.FareCalculator;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;
import wooteco.subway.dto.response.PathResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
class PathServiceTest {

    private final StationDao stationDao;
    private final SectionDao sectionDao;
    private final LineDao lineDao;
    private final PathService pathService;
    private long 노선1_ID;
    private long 노선2_ID;
    private long 노선3_ID;

    private long 선릉역_ID;
    private long 선정릉역_ID;
    private long 한티역_ID;
    private long 모란역_ID;
    private long 기흥역_ID;
    private long 강남역_ID;

    public PathServiceTest() {
        stationDao = new MemoryStationDao();
        sectionDao = new MemorySectionDao();
        lineDao = new MemoryLineDao();
        pathService = new PathService(new FareCalculator(), sectionDao, stationDao, lineDao);

        setUpLines();
        setUpStations();
        setUpSections();
    }

    private void setUpLines() {
        this.노선1_ID = lineDao.save(new Line("노선_1", "red", 0));
        this.노선2_ID = lineDao.save(new Line("노선_2", "blue", 500));
        this.노선3_ID = lineDao.save(new Line("노선_3", "green", 900));
    }

    void setUpStations() {
        this.선릉역_ID = stationDao.save(new Station("선릉역"));
        this.선정릉역_ID = stationDao.save(new Station("선정릉역"));
        this.한티역_ID = stationDao.save(new Station("한티역"));
        this.모란역_ID = stationDao.save(new Station("모란역"));
        this.기흥역_ID = stationDao.save(new Station("기흥역"));
        this.강남역_ID = stationDao.save(new Station("강남역"));
    }

    void setUpSections() {
        List<Section> sections = List.of(
                new Section(선릉역_ID, 선정릉역_ID, 50, 노선1_ID),
                new Section(선정릉역_ID, 한티역_ID, 8, 노선1_ID),
                new Section(한티역_ID, 강남역_ID, 20, 노선1_ID),
                new Section(선정릉역_ID, 모란역_ID, 6, 노선2_ID),
                new Section(기흥역_ID, 모란역_ID, 10, 노선3_ID),
                new Section(모란역_ID, 강남역_ID, 5, 노선3_ID)
        );

        for (Section section : sections) {
            sectionDao.save(section);
        }
    }

    @DisplayName("경로를 조회한 결과가 순서, 거리, 운임에 맞게 출력되었는지 검증한다")
    @Nested
    class findPath {
        @DisplayName("노선 추가 운임: 0원")
        @Test
        void findPath_0won() {
            PathResponse result = pathService.findPath(선릉역_ID, 한티역_ID, 0);
            assertAll(
                    () -> assertThat(result.getStations().get(0).getId()).isEqualTo(선릉역_ID),
                    () -> assertThat(result.getStations().get(1).getId()).isEqualTo(선정릉역_ID),
                    () -> assertThat(result.getStations().get(2).getId()).isEqualTo(한티역_ID),
                    () -> assertThat(result.getDistance()).isEqualTo(58),
                    () -> assertThat(result.getFare()).isEqualTo(2150)
            );
        }

        @DisplayName("가장 높은 노선 추가 운임: 900원")
        @Test
        void findPath_900won() {
            PathResponse result = pathService.findPath(기흥역_ID, 한티역_ID, 0);
            assertAll(
                    () -> assertThat(result.getStations().get(0).getId()).isEqualTo(기흥역_ID),
                    () -> assertThat(result.getStations().get(1).getId()).isEqualTo(모란역_ID),
                    () -> assertThat(result.getStations().get(2).getId()).isEqualTo(선정릉역_ID),
                    () -> assertThat(result.getStations().get(3).getId()).isEqualTo(한티역_ID),
                    () -> assertThat(result.getDistance()).isEqualTo(24),
                    () -> assertThat(result.getFare()).isEqualTo(1550 + 900)
            );
        }

        @DisplayName("연령 할인 적용")
        @ParameterizedTest(name = "{0}일 경우")
        @CsvSource(value = {
                "어린이 - 7 - 900",
                "청소년 - 14 - 1440"
        }, delimiterString = " - ")
        void findPath_youth(String ageClassification, int age, int fare) {
            PathResponse result = pathService.findPath(선릉역_ID, 한티역_ID, age);
            assertAll(
                    () -> assertThat(result.getStations().get(0).getId()).isEqualTo(선릉역_ID),
                    () -> assertThat(result.getStations().get(1).getId()).isEqualTo(선정릉역_ID),
                    () -> assertThat(result.getStations().get(2).getId()).isEqualTo(한티역_ID),
                    () -> assertThat(result.getDistance()).isEqualTo(58),
                    () -> assertThat(result.getFare()).isEqualTo(fare)
            );
        }
    }
}
