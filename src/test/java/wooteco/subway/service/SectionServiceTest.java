package wooteco.subway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.SectionRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
class SectionServiceTest {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private LineDao lineDao;

    @Autowired
    private StationDao stationDao;

    @Autowired
    private SectionDao sectionDao;

    private Line savedLine;
    private Station downStation;
    private Section section;

    @BeforeEach
    void setup() {
        savedLine = lineDao.save(new Line("5호선", "bg-purple-600"));
        Station upStation = stationDao.save(new Station("아차산역"));
        downStation = stationDao.save(new Station("군자역"));
        section = sectionDao.save(new Section(upStation, downStation, 10, savedLine));
    }

    @DisplayName("(갈래길이 아닌 경우) 특정 노선에 구간을 추가한다.")
    @Test
    void addNotBranchedSection() {
        final Station newStation = stationDao.save(new Station("마장역"));
        final SectionRequest sectionRequest = new SectionRequest(downStation.getId(), newStation.getId(), 10);
        final Section savedSection = sectionService.create(savedLine.getId(), sectionRequest);

        assertAll(
                () -> assertThat(savedSection.getId()).isNotNull(),
                () -> assertThat(savedSection.getUpStation().getName()).isEqualTo("군자역"),
                () -> assertThat(savedSection.getDownStation().getName()).isEqualTo("마장역"),
                () -> assertThat(savedSection.getDistance()).isEqualTo(10)
        );
    }

    @DisplayName("(갈래길인 경우) 특정 노선에 구간을 추가한다.")
    @Test
    void addBranchedSection() {
        final Station newStation = stationDao.save(new Station("마장역"));
        final SectionRequest sectionRequest = new SectionRequest(newStation.getId(), downStation.getId(), 9);
        final Section savedSection = sectionService.create(savedLine.getId(), sectionRequest);

        final Optional<Section> foundSection = sectionDao.findAllByLineId(savedLine.getId())
                .stream()
                .filter(it -> it.getId().equals(section.getId()))
                .findAny();

        assert (foundSection.isPresent());

        assertAll(
                () -> assertThat(savedSection.getUpStation().getName()).isEqualTo("마장역"),
                () -> assertThat(savedSection.getDownStation().getName()).isEqualTo("군자역"),
                () -> assertThat(savedSection.getDistance()).isEqualTo(9),
                () -> assertThat(foundSection.get().getUpStation().getName()).isEqualTo("아차산역"),
                () -> assertThat(foundSection.get().getDownStation().getName()).isEqualTo("마장역"),
                () -> assertThat(foundSection.get().getDistance()).isEqualTo(1)
        );
    }

    @DisplayName("특정 노선의 중간 구간을 삭제한다.")
    @Test
    void delete() {
        final Station newStation = stationDao.save(new Station("마장역"));
        sectionDao.save(new Section(downStation, newStation, 10, savedLine));

        sectionService.remove(savedLine.getId(), downStation.getId());

        final List<Section> list = sectionDao.findAllByLineId(savedLine.getId());

        Optional<Section> foundSection = list.stream()
                .filter(section -> section.getDownStation().equals(newStation))
                .findAny();
        stationDao.findById(newStation.getId());

        assert (foundSection.isPresent());

        assertAll(
                () -> assertThat(foundSection.get().getUpStation().getName()).isEqualTo("아차산역"),
                () -> assertThat(foundSection.get().getDownStation().getName()).isEqualTo("마장역"),
                () -> assertThat(foundSection.get().getDistance()).isEqualTo(20)
        );
    }

    @DisplayName("특정 노선의 종점 구간을 삭제한다.")
    @Test
    void deleteLast() {
        final Station newStation = stationDao.save(new Station("마장역"));
        sectionDao.save(new Section(downStation, newStation, 10, savedLine));

        sectionService.remove(savedLine.getId(), newStation.getId());

        final List<Section> list = sectionDao.findAllByLineId(savedLine.getId());

        Optional<Section> foundSection = list.stream()
                .filter(section -> section.getDownStation().equals(downStation))
                .findAny();

        assert (foundSection.isPresent());

        assertAll(
                () -> assertThat(foundSection.get().getUpStation().getName()).isEqualTo("아차산역"),
                () -> assertThat(foundSection.get().getDownStation().getName()).isEqualTo("군자역"),
                () -> assertThat(foundSection.get().getDistance()).isEqualTo(10)
        );
    }
}
