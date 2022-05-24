package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.ui.dto.ExceptionResponse;
import wooteco.subway.ui.dto.LineRequest;
import wooteco.subway.ui.dto.LineResponse;
import wooteco.subway.ui.dto.PathResponse;
import wooteco.subway.ui.dto.SectionRequest;
import wooteco.subway.ui.dto.StationRequest;
import wooteco.subway.ui.dto.StationResponse;

class PathAcceptanceTest extends AcceptanceTest {

    private Long stationId1;
    private Long stationId2;
    private Long stationId3;
    private Long stationId4;
    private Long lineId;

    @BeforeEach
    void createLine() {
        stationId1 = createStation(new StationRequest("강남역")).as(StationResponse.class)
                .getId();
        stationId2 = createStation(new StationRequest("선릉역")).as(StationResponse.class)
                .getId();
        stationId3 = createStation(new StationRequest("수서역")).as(StationResponse.class)
                .getId();
        stationId4 = createStation(new StationRequest("천호역")).as(StationResponse.class)
                .getId();
        lineId = createLine(
                new LineRequest("2호선", "green", stationId1, stationId2, 2, 200))
                .as(LineResponse.class)
                .getId();
        createSection(lineId, new SectionRequest(stationId2, stationId3, 4));
    }

    @DisplayName("최단 경로의 지하철 역들과 거리, 운임 비용을 응답한다.")
    @Test
    void findShortestPath() {
        // given
        createLine(new LineRequest("3호선", "orange", stationId2, stationId4, 2, 500));

        // when
        ExtractableResponse<Response> response = findShortestPath(stationId1, stationId4, 5);
        PathResponse pathResponse = response.jsonPath()
                .getObject(".", PathResponse.class);
        List<StationResponse> stationResponses = pathResponse.getStations();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(stationResponses.get(0).getId()).isEqualTo(stationId1),
                () -> assertThat(stationResponses.get(0).getName()).isEqualTo("강남역"),
                () -> assertThat(stationResponses.get(1).getId()).isEqualTo(stationId2),
                () -> assertThat(stationResponses.get(1).getName()).isEqualTo("선릉역"),
                () -> assertThat(stationResponses.get(2).getId()).isEqualTo(stationId4),
                () -> assertThat(stationResponses.get(2).getName()).isEqualTo("천호역"),
                () -> assertThat(pathResponse.getDistance()).isEqualTo(4),
                () -> assertThat(pathResponse.getFare()).isEqualTo(700)
        );
    }

    @DisplayName("구간에 등록되지않은 지하철역으로 최단 경로 조회시 badRequest를 응답한다.")
    @Test
    void findShortestPath_exceptionNotSavedInSection() {
        // given
        Long stationId5 = createStation(new StationRequest("가락시장역")).as(StationResponse.class)
                .getId();
        createLine(new LineRequest("3호선", "orange", stationId2, stationId4, 2, 500));

        // when
        ExtractableResponse<Response> response = findShortestPath(stationId1, stationId5, 5);
        ExceptionResponse exceptionResponse = response.as(ExceptionResponse.class);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(exceptionResponse.getMessage()).isEqualTo("구간에 등록 되지 않은 역입니다.")
        );
    }

    @DisplayName("연결되지 않은 구간의 최단 경로 조회시 badRequest를 응답한다.")
    @Test
    void findShortestPath_exceptionInvalidPath() {
        // given
        Long stationId5 = createStation(new StationRequest("가락시장역")).as(StationResponse.class)
                .getId();
        createLine(new LineRequest("3호선", "orange", stationId4, stationId5, 2, 500));

        // when
        ExtractableResponse<Response> response = findShortestPath(stationId1, stationId5, 5);
        ExceptionResponse exceptionResponse = response.as(ExceptionResponse.class);

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(exceptionResponse.getMessage()).isEqualTo("연결되지 않은 구간입니다.")
        );
    }

    private ExtractableResponse<Response> findShortestPath(Long departureId, Long arrivalId, int age) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/paths?source=" + departureId + "&target=" + arrivalId + "&age=" + age)
                .then().log().all()
                .extract();
    }
}
