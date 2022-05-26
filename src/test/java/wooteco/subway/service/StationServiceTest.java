package wooteco.subway.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.station.DuplicateStationNameException;
import wooteco.subway.exception.station.NoSuchStationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Sql("/testSchema.sql")
class StationServiceTest {

    @Autowired
    private StationService stationService;

    @DisplayName("역을 저장한다")
    @Test
    void 역_저장() {
        StationRequest stationRequest = new StationRequest("홍대입구역");

        StationResponse savedStation = stationService.save(stationRequest);

        assertThat(savedStation.getName()).isEqualTo(stationRequest.getName());
    }

    @DisplayName("존재하는 이름의 역 저장 시 예외가 발생한다")
    @Test
    void 존재하는_이름의_역_저장_예외발생() {
        StationRequest station = new StationRequest("선릉역");
        StationRequest duplicatedNameStation = new StationRequest("선릉역");

        stationService.save(station);

        assertThatThrownBy(() -> stationService.save(duplicatedNameStation))
                .isInstanceOf(DuplicateStationNameException.class);
    }

    @DisplayName("역을 조회한다")
    @Test
    void 역_조회() {
        StationResponse savedStation = stationService.save(new StationRequest("서울역"));

        StationResponse foundStation = stationService.findById(savedStation.getId());

        assertThat(foundStation.getName()).isEqualTo(savedStation.getName());
    }

    @DisplayName("존재하지 않는 역 조회 시 예외가 발생한다")
    @Test
    void 존재하지_않는_역_조회_예외발생() {
        assertThatThrownBy(() -> stationService.findById(1L))
                .isInstanceOf(NoSuchStationException.class);
    }

    @DisplayName("모든 역을 조회한다")
    @Test
    void 모든_역_조회() {
        stationService.save(new StationRequest("용산역"));
        stationService.save(new StationRequest("잠실역"));
        stationService.save(new StationRequest("강남역"));

        assertThat(stationService.findAll().size()).isEqualTo(3);
    }

    @DisplayName("역을 삭제한다")
    @Test
    void 역_삭제() {
        StationResponse station = stationService.save(new StationRequest("양재역"));

        stationService.deleteById(station.getId());

        assertThat(stationService.findAll().size()).isEqualTo(0);
    }
}