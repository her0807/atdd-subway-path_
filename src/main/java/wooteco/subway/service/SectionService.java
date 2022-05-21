package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.LineSections;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionUpdateResult;
import wooteco.subway.dto.request.SectionRequest;

@Service
@Transactional
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void firstSave(Long lineId, SectionRequest sectionRequest) {
        sectionDao.save(
                Section.createWithoutId(
                        lineId,
                        sectionRequest.getUpStationId(),
                        sectionRequest.getDownStationId(),
                        sectionRequest.getDistance(),
                        1L
                )
        );
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void save(Long lineId, SectionRequest sectionReq) {
        long upStationId = sectionReq.getUpStationId();
        long downStationId = sectionReq.getDownStationId();
        int distance = sectionReq.getDistance();

        LineSections lineSections = new LineSections(sectionDao.findAllByLineId(lineId));
        lineSections.validateSection(upStationId, downStationId, distance);

        SectionUpdateResult targetSections = lineSections.findOverlapSection(upStationId, downStationId, distance);
        updateSections(targetSections);
    }

    private void updateSections(SectionUpdateResult sections) {
        updateSection(sections.getUpdatedSection());
        addSection(sections.getAddedSection());
    }

    private void updateSection(Section section) {
        sectionDao.deleteById(section.getId());
        sectionDao.save(section);
    }

    private void addSection(Section section) {
        sectionDao.updateLineOrderByInc(section.getLineId(), section.getLineOrder());
        sectionDao.save(section);
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<Long> findAllStationByLineId(long lineId) {
        LineSections lineSections = new LineSections(sectionDao.findAllByLineId(lineId));
        return lineSections.getStationIds();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void deleteByLineIdAndStationId(long lineId, long stationId) {
        LineSections lineSections = new LineSections(sectionDao.findByLineIdAndStationId(lineId, stationId));
        if (lineSections.hasTwoSection()) {
            Section upsideSection = lineSections.getUpsideSection();
            Section downsideSection = lineSections.getDownsideSection();

            deleteAndUnionTwoSection(lineId, upsideSection, downsideSection);
            return;
        }
        deleteSingleSection(lineId, lineSections);
    }

    private void deleteAndUnionTwoSection(long lineId, Section upsideSection,
                                          Section downsideSection) {
        sectionDao.deleteById(upsideSection.getId());
        sectionDao.deleteById(downsideSection.getId());
        sectionDao.save(new Section(null, lineId,
                upsideSection.getUpStationId(), downsideSection.getDownStationId(),
                upsideSection.getDistance() + downsideSection.getDistance(),
                upsideSection.getLineOrder()));

        sectionDao.updateLineOrderByDec(lineId, downsideSection.getLineOrder());
    }

    private void deleteSingleSection(long lineId, LineSections lineSections) {
        Section section = lineSections.getSingleDeleteSection();
        sectionDao.deleteById(section.getId());

        sectionDao.updateLineOrderByDec(lineId, section.getLineOrder());
    }
}
