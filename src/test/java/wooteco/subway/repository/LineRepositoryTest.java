package wooteco.subway.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dao.*;
import wooteco.subway.dao.entity.LineEntity;
import wooteco.subway.dao.entity.SectionEntity;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Sql("/testSchema.sql")
public class LineRepositoryTest {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    public LineRepositoryTest(JdbcTemplate jdbcTemplate) {
        this.lineDao = new JdbcLineDao(jdbcTemplate);
        this.sectionDao = new JdbcSectionDao(jdbcTemplate);
        this.stationDao = new JdbcStationDao(jdbcTemplate);
    }

    @DisplayName("노선 저장")
    @Test
    void 노선_저장() {
        Station A = stationDao.save(new Station("A"));
        Station B = stationDao.save(new Station("B"));
        Section section = new Section(A, B, 10);
        Line line = new Line("A호선", "yellow", 0, new Sections(section));

        Line savedLine = lineRepository.save(line);

        assertAll(
                () -> assertThat(savedLine.getName()).isEqualTo("A호선"),
                () -> assertThat(savedLine.getColor()).isEqualTo("yellow")
        );
    }

    @DisplayName("단일 노선 조회")
    @Test
    void 노선_조회() {
        Station A = stationDao.save(new Station("A"));
        Station B = stationDao.save(new Station("B"));
        Section section = new Section(A, B, 10);
        Line line = new Line("A호선", "yellow", 0, new Sections(section));

        LineEntity savedLine = lineDao.save(LineEntity.from(line));
        sectionDao.save(SectionEntity.of(section, savedLine.getId()));

        Line result = lineRepository.findById(savedLine.getId());

        assertAll(
                () -> assertThat(result.getName()).isEqualTo("A호선"),
                () -> assertThat(result.getColor()).isEqualTo("yellow"),
                () -> assertThat(result.getSections()).isEqualTo(line.getSections())
        );
    }
}
