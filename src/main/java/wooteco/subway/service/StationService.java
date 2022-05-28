package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.service.dto.StationServiceResponse;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public StationServiceResponse save(String name) {
        Station station = new Station(name);
        Station savedStation = stationDao.save(station);
        return new StationServiceResponse(savedStation);
    }

    @Transactional(readOnly = true)
    public List<StationServiceResponse> findAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(StationServiceResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Station findById(long lindId) {
        return stationDao.findById(lindId);
    }

    @Transactional(readOnly = true)
    public List<Station> findAllByLineId(long lindId) {
        return stationDao.findAllByLineId(lindId);
    }

    @Transactional
    public void deleteById(long id) {
        stationDao.deleteById(id);
    }
}
