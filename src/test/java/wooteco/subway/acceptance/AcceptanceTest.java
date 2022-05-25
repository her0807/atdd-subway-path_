package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import wooteco.subway.repository.dao.JdbcLineDao;
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.repository.entity.LineEntity;
import wooteco.subway.repository.entity.StationEntity;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    private StationDao stationDao;

    @Autowired
    private JdbcLineDao jdbcLineDao;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        clearAllStations();
        clearAllLines();
    }

    private void clearAllStations() {
        List<StationEntity> stationEntities = stationDao.findAll();
        List<Long> stationIds = stationEntities.stream()
            .map(StationEntity::getId)
            .collect(Collectors.toList());

        for (Long stationId : stationIds) {
            stationDao.deleteById(stationId);
        }
    }

    private void clearAllLines() {
        List<LineEntity> lineEntities = jdbcLineDao.findAll();
        List<Long> lineIds = lineEntities.stream()
            .map(LineEntity::getId)
            .collect(Collectors.toList());

        for (Long lineId : lineIds) {
            jdbcLineDao.deleteById(lineId);
        }
    }
}
