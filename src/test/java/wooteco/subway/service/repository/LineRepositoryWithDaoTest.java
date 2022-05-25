package wooteco.subway.service.repository;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;
import wooteco.subway.service.FakeLineDao;
import wooteco.subway.service.FakeSectionDao;
import wooteco.subway.service.FakeStationDao;

public class LineRepositoryWithDaoTest {
    private LineDao lineDao;
    private StationDao stationDao;
    private SectionDao sectionDao;
    private LineRepositoryWithDao lineRepositoryWithDao;

    @BeforeEach
    void setUp() {
        lineDao = new FakeLineDao();
        sectionDao = new FakeSectionDao();
        stationDao = new FakeStationDao();
        lineRepositoryWithDao = new LineRepositoryWithDao(lineDao, sectionDao, stationDao);

        stationDao.save(new Station("강남역"));
        stationDao.save(new Station("선릉역"));
        lineDao.save(new Line("2호선", "green", 900));
        sectionDao.save(1L, new Section(new Station(1L, "강남역"), new Station(2L, "선릉역"), 10));
    }

    @DisplayName("정상적으로 Line을 만드는지 테스트")
    @Test
    void createLine() {
        Line line = lineRepositoryWithDao.find(1L);
        Sections sections = line.getSections();

        assertThat(line.getId()).isEqualTo(1L);
        assertThat(line.getName()).isEqualTo("2호선");
        assertThat(line.getColor()).isEqualTo("green");

        assertThat(sections.size()).isEqualTo(1);

        List<Station> stations = sections.getStations();
        assertThat(stations.contains(new Station(1L, "강남역"))).isTrue();
        assertThat(stations.contains(new Station(2L, "선릉역"))).isTrue();
    }
}
