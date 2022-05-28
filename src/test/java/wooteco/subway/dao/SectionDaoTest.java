package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.Fixtures.부평역;
import static wooteco.subway.Fixtures.삼성역;
import static wooteco.subway.Fixtures.선릉역;
import static wooteco.subway.Fixtures.이호선;
import static wooteco.subway.Fixtures.잠실새내역;
import static wooteco.subway.Fixtures.잠실역;
import static wooteco.subway.Fixtures.종합운동장역;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.section.Distance;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;
import wooteco.subway.entity.SectionEntity;
import wooteco.subway.repository.JdbcLineRepository;
import wooteco.subway.repository.JdbcSectionRepository;
import wooteco.subway.repository.JdbcStationRepository;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.SectionRepository;
import wooteco.subway.repository.StationRepository;

@JdbcTest
class SectionDaoTest {

    private final SectionDao sectionDao;
    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;
    private final LineRepository lineRepository;

    private Station 저장된_잠실역;
    private Station 저장된_부평역;

    private Long 저장된_이호선_ID;

    @Autowired
    public SectionDaoTest(JdbcTemplate jdbcTemplate) {
        StationDao stationDao = new StationDao(jdbcTemplate);
        LineDao lineDao = new LineDao(jdbcTemplate);

        sectionDao = new SectionDao(jdbcTemplate);
        stationRepository = new JdbcStationRepository(stationDao);
        sectionRepository = new JdbcSectionRepository(sectionDao, stationRepository);
        lineRepository = new JdbcLineRepository(lineDao, sectionRepository);
    }

    @BeforeEach
    void setUp() {
        저장된_이호선_ID = lineRepository.save(이호선);

        Long 저장된_선릉역_ID = stationRepository.save(선릉역);
        Long 저장된_삼성역_ID = stationRepository.save(삼성역);
        Long 저장된_종합운동장역_ID = stationRepository.save(종합운동장역);
        Long 저장된_잠실새내역_ID = stationRepository.save(잠실새내역);
        Long 저장된_잠실역_ID = stationRepository.save(잠실역);
        Long 저장된_부평역_ID = stationRepository.save(부평역);

        Station 저장된_선릉역 = new Station(저장된_선릉역_ID, 선릉역.getName());
        Station 저장된_삼성역 = new Station(저장된_삼성역_ID, 삼성역.getName());
        Station 저장된_종합운동장역 = new Station(저장된_종합운동장역_ID, 종합운동장역.getName());
        Station 저장된_잠실새내역 = new Station(저장된_잠실새내역_ID, 잠실새내역.getName());
        저장된_잠실역 = new Station(저장된_잠실역_ID, 잠실역.getName());
        저장된_부평역 = new Station(저장된_부평역_ID, 부평역.getName());

        sectionRepository.save(저장된_이호선_ID, new Section(저장된_선릉역, 저장된_삼성역, new Distance(10)));
        sectionRepository.save(저장된_이호선_ID, new Section(저장된_삼성역, 저장된_종합운동장역, new Distance(10)));
        sectionRepository.save(저장된_이호선_ID, new Section(저장된_종합운동장역, 저장된_잠실새내역, new Distance(10)));
        sectionRepository.save(저장된_이호선_ID, new Section(저장된_잠실새내역, 저장된_잠실역, new Distance(10)));
    }

    @DisplayName("호선 ID를 통해 구간 목록을 가져온다.")
    @Test
    void findSectionsByLineId() {
        // when
        List<SectionEntity> actual = sectionDao.findSectionsByLineId(저장된_이호선_ID);

        // then
        assertThat(actual).hasSize(4);
    }

    @DisplayName("호선 ID와 구간 목록을 전달받아 구간 목록을 저장한다.")
    @Test
    void saveSections() {
        // given
        Section 새로운_구간 = new Section(저장된_잠실역, 저장된_부평역, new Distance(10));

        // when
        sectionDao.save(저장된_이호선_ID, 새로운_구간);
        List<SectionEntity> actual = sectionDao.findSectionsByLineId(저장된_이호선_ID);

        // then
        assertThat(actual).hasSize(5);
    }
}
