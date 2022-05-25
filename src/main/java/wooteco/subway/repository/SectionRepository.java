package wooteco.subway.repository;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.entity.SectionEntity;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

@Repository
public class SectionRepository {

    private final SectionDao sectionDao;
    private final LineDao lineDao;

    private final StationRepository stationRepository;

    public SectionRepository(final SectionDao sectionDao, final LineDao lineDao,
                             final StationRepository stationRepository) {
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
        this.stationRepository = stationRepository;
    }

    public Long save(final Long lineId, final Section section) {
        return sectionDao.save(SectionEntity.of(lineId, section));
    }

    public void batchSave(final Long lineId, final List<Section> sections) {
        final List<SectionEntity> entities = sections.stream()
                .map(s -> SectionEntity.of(lineId, s))
                .collect(Collectors.toList());
        sectionDao.batchSave(entities);
    }

    public List<Section> findAll() {
        final List<SectionEntity> entities = sectionDao.findAll();
        return entities.stream()
                .map(e -> {
                    final Station upStation = stationRepository.getById(e.getUpStationId());
                    final Station downStation = stationRepository.getById(e.getDownStationId());
                    int extraFare = lineDao.find(e.getLineId()).getExtraFare();
                    return new Section(e.getId(), upStation, downStation, e.getDistance(), extraFare);
                }).collect(Collectors.toList());
    }

    public Sections findAllByLineId(final Long id) {
        final List<SectionEntity> entities = sectionDao.findAllByLineId(id);
        return new Sections(entities.stream()
                .map(e -> {
                    final Station upStation = stationRepository.getById(e.getUpStationId());
                    final Station downStation = stationRepository.getById(e.getDownStationId());
                    int extraFare = lineDao.find(e.getLineId()).getExtraFare();
                    return new Section(e.getId(), upStation, downStation, e.getDistance(), extraFare);
                }).collect(Collectors.toList()));
    }

    public void deleteById(final Long id) {
        sectionDao.deleteById(id);
    }

    public void batchDelete(final Long lineId, final List<Section> sections) {
        final List<SectionEntity> entities = sections.stream()
                .map(s -> SectionEntity.of(lineId, s))
                .collect(Collectors.toList());
        sectionDao.batchDelete(entities);
    }
}
