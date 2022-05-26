package wooteco.subway.reopository;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.section.Station;
import wooteco.subway.reopository.dao.StationDao;
import wooteco.subway.reopository.entity.StationEntity;

@Repository
public class StationRepository {

    private final StationDao stationDao;

    public StationRepository(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Long save(Station station) {
        return stationDao.save(new StationEntity(station.getName()));
    }

    public Optional<Station> findById(Long id) {
        return stationDao.findById(id)
                .map(entity -> new Station(entity.getId(), entity.getName()));
    }

    public List<Station> findAll() {
        List<StationEntity> list = stationDao.findAll();
        return list.stream()
                .map(entity -> new Station(entity.getId(), entity.getName()))
                .collect(toList());
    }

    public void deleteById(Long id) {
        stationDao.deleteById(id);
    }

    public boolean existByName(String name) {
        return stationDao.existByName(name);
    }
}
