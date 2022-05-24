package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Lines;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Stations;
import wooteco.subway.domain.path.SubwayPathFinder;
import wooteco.subway.dto.path.PathResponse;

@Service
public class PathService {

    private final StationDao stationDao;
    private final SectionDao sectionDao;
    private final LineDao lineDao;

    public PathService(StationDao stationDao, SectionDao sectionDao, LineDao lineDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
    }

    public PathResponse findPath(Long source, Long target, int age) {
        var path = new SubwayPathFinder(
                new Stations(stationDao.findAll()),
                new Sections(sectionDao.findAll()),
                new Lines(lineDao.findAll())
        );

        return new PathResponse(path.getPath(source, target, age));
    }
}
