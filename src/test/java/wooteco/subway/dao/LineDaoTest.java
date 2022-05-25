package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@Import({
        LineDao.class,
        StationDao.class,
        SectionDao.class
})
@JdbcTest
class LineDaoTest {

    @Autowired
    private LineDao lineDao;
    @Autowired
    private StationDao stationDao;
    @Autowired
    private SectionDao sectionDao;

    @Test
    @DisplayName("노선을 등록할 수 있다.")
    void save() {
        // given
        final Line line = new Line("신분당선", "bg-red-600", 1000);

        // when
        final Long savedId = lineDao.save(line);

        // then
        final Line findLine = lineDao.findById(savedId);
        assertThat(findLine).extracting("name", "color", "extraFare")
                .contains("신분당선", "bg-red-600", 1000);
    }

    @Test
    @DisplayName("전체 노선을 조회할 수 있다.")
    void findAll() {
        // given
        Long 강남역_id = stationDao.save(new Station("강남역"));
        Long 역삼역_id = stationDao.save(new Station("역삼역"));
        Station 강남역 = stationDao.findById(강남역_id);
        Station 역삼역 = stationDao.findById(역삼역_id);

        Line 신분당선 = new Line("신분당선", "bg-red-600", 1000);
        Line 분당선 = new Line("분당선", "bg-green-600", 1000);
        Long 신분당선_id = lineDao.save(신분당선);
        Long 분당선_id = lineDao.save(분당선);

        Section 강남_역삼_신분당선 = new Section(신분당선_id, 강남역, 역삼역, 10);
        Section 강남_역삼_분당선 = new Section(분당선_id, 강남역, 역삼역, 10);
        sectionDao.save(강남_역삼_신분당선);
        sectionDao.save(강남_역삼_분당선);

        // when
        List<Line> lines = lineDao.findAll();

        // then
        assertThat(lines).hasSize(2)
                .extracting("name", "color", "extraFare")
                .containsExactlyInAnyOrder(
                        tuple("신분당선", "bg-red-600", 1000),
                        tuple("분당선", "bg-green-600", 1000));
    }

    @Test
    @DisplayName("단건 노선을 조회한다.")
    void findById() {
        // given
        Long 강남역_id = stationDao.save(new Station("강남역"));
        Long 역삼역_id = stationDao.save(new Station("역삼역"));
        Station 강남역 = stationDao.findById(강남역_id);
        Station 역삼역 = stationDao.findById(역삼역_id);

        Line 신분당선 = new Line("신분당선", "bg-red-600", 1000);
        Long 신분당선_id = lineDao.save(신분당선);
        sectionDao.save(new Section(신분당선_id, 강남역, 역삼역, 10));

        // when
        Line findLine = lineDao.findById(신분당선_id);

        // then
        assertThat(findLine).extracting("name", "color", "extraFare")
                .contains("신분당선", "bg-red-600", 1000);
    }

    @Test
    @DisplayName("기존 노선의 이름과 색상을 변경할 수 있다.")
    void updateById() {
        // given
        Line 신분당선 = new Line("신분당선", "bg-red-600", 1000);
        Long 신분당선_id = lineDao.save(신분당선);

        // when
        final Line updateLine = new Line(신분당선_id, "다른분당선", "bg-red-600", 1500);
        lineDao.updateByLine(updateLine);

        // then
        final Line findLine = lineDao.findById(신분당선_id);
        assertThat(findLine).extracting("name", "color", "extraFare")
                .contains("다른분당선", "bg-red-600", 1500);
    }

    @Test
    @DisplayName("라인 아이디들의 정보를 통해 가장 높은 추가 금액을 가진 라인을 조회할 수 있다.")
    void findMaxExtraFareByLineId() {
        // given
        Line 이호선 = new Line("2호선", "bg-green-600", 1000);
        Long 이호선_id = lineDao.save(이호선);
        Line 신분당선 = new Line("신분당선", "bg-red-600", 2000);
        Long 신분당선_id = lineDao.save(신분당선);

        List<Long> lineIds = List.of(이호선_id, 신분당선_id);

        // when
        int maxExtraFare = lineDao.findMaxExtraFareByLineId(lineIds);

        // then
        assertThat(maxExtraFare).isEqualTo(2000);
    }

    @Test
    @DisplayName("노선을 삭제할 수 있다.")
    void deleteById() {
        // given
        final Line line = new Line("신분당선", "bg-red-600", 1000);
        final Long savedId = lineDao.save(line);

        // when & then
        assertDoesNotThrow(() -> lineDao.deleteById(savedId));
    }
}
