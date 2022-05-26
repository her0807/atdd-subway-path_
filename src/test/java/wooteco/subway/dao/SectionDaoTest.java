package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

@JdbcTest
public class SectionDaoTest {

    @Autowired
    private DataSource dataSource;

    private LineDao lineDao;
    private StationDao stationDao;
    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(dataSource);
        stationDao = new StationDao(dataSource);
        sectionDao = new SectionDao(dataSource);
    }

    @DisplayName("노선과 구간 정보를 받아서 저장한다.")
    @Test
    void save() {
        Line line = lineDao.save(new Line("2호선", "초록색", 0));
        Station upStation = stationDao.save(new Station("강남역"));
        Station downStation = stationDao.save(new Station("역삼역"));

        Section section = new Section(null, line, upStation, downStation, 1);

        Section persistSection = sectionDao.save(section);

        assertThat(persistSection).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(section);
    }

    @Test
    @DisplayName("등록된 전체 구간을 조회한다.")
    void findAll() {
        Station gangnam = stationDao.save(new Station("강남역"));
        Station yeoksam = stationDao.save(new Station("역삼역"));
        Station seolleung = stationDao.save(new Station("선릉역"));

        Line line = lineDao.save(new Line("2호선", "초록색", 0));
        Section gangnam_yeoksam = sectionDao.save(new Section(line, gangnam, yeoksam, 1));
        Section yeoksam_seolleung = sectionDao.save(new Section(line, yeoksam, seolleung, 1));

        Sections sections = sectionDao.findAll();

        assertThat(sections.getValues()).containsOnly(gangnam_yeoksam, yeoksam_seolleung);
    }

    @DisplayName("노선을 받아서 구간을 조회한다.")
    @Test
    void findAllByLine() {
        Station gangnam = stationDao.save(new Station("강남역"));
        Station yeoksam = stationDao.save(new Station("역삼역"));
        Station seolleung = stationDao.save(new Station("선릉역"));

        Line line = lineDao.save(new Line("2호선", "초록색", 0));
        Section gangnam_yeoksam = sectionDao.save(new Section(line, gangnam, yeoksam, 1));
        Section yeoksam_seolleung = sectionDao.save(new Section(line, yeoksam, seolleung, 1));

        Line ignoredLine = lineDao.save(new Line("1호선", "군청색", 0));
        Section ignoredSection = sectionDao.save(new Section(ignoredLine, gangnam, yeoksam, 1));

        Sections sections = sectionDao.findAllByLine(line);

        assertThat(sections.getValues()).doesNotContain(ignoredSection)
                .containsOnly(gangnam_yeoksam, yeoksam_seolleung);
    }

    @DisplayName("노선 구간 중 해당하는 역을 포함하는 구간을 모두 제거한다.")
    @Test
    void delete() {
        Line line = lineDao.save(new Line("2호선", "초록색", 0));
        Station station1 = stationDao.save(new Station("강남역"));
        Station station2 = stationDao.save(new Station("역삼역"));
        Station station3 = stationDao.save(new Station("선릉역"));

        Section section1 = sectionDao.save(new Section(line, station1, station2, 1));
        Section section2 = sectionDao.save(new Section(line, station2, station3, 1));

        sectionDao.deleteByLineAndStation(line, station3);

        Sections sections = sectionDao.findAllByLine(line);

        assertThat(sections.getValues()).doesNotContain(section2)
                .containsOnly(section1);
    }

    @DisplayName("노선에 이미 등록되어 있는 구간인지 확인한다.")
    @Test
    void checkExist() {
        Line line = lineDao.save(new Line("2호선", "초록색", 0));
        Station upStation = stationDao.save(new Station("강남역"));
        Station downStation = stationDao.save(new Station("역삼역"));
        Section section = new Section(line, upStation, downStation, 1);
        sectionDao.save(section);

        boolean actual = sectionDao.exists(line, section);

        assertThat(actual).isTrue();
    }

}
