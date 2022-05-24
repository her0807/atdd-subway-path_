package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import wooteco.subway.ui.dto.LineRequest;
import wooteco.subway.ui.dto.PathResponse;
import wooteco.subway.ui.dto.SectionRequest;
import wooteco.subway.ui.dto.StationRequest;
import wooteco.subway.ui.dto.StationResponse;

@DisplayName("경로 조회 기능")
public class PathAcceptanceTest extends AcceptanceTest {

    private List<Long> stationIds;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        clearAllStations();
        clearAllLines();

        List<StationRequest> requests = new ArrayList<>();
        for (char c = 'a'; c <= 'k'; c++) {
            requests.add(new StationRequest(String.valueOf(c)));
        }

        stationIds = requests.stream()
            .map(request -> createStationRequest(request).extract())
            .map(this::getSavedStationIdByResponse)
            .collect(Collectors.toList());

        Map<SectionRequest, String> sectionRequests = new LinkedHashMap<>();
        createLine1(sectionRequests);
        createLine2(sectionRequests);
        createLine3(sectionRequests);
        createLine4();

        for (SectionRequest sectionRequest : sectionRequests.keySet()) {
            createSectionRequest(sectionRequest, sectionRequests.get(sectionRequest));
        }
    }

    private void createLine1(Map<SectionRequest, String> sectionRequests) {
        LineRequest lineRequest = new LineRequest("1", "red", stationIds.get(0), stationIds.get(1), 5, 100);
        ExtractableResponse<Response> createLineResponse1 = createLineRequest(lineRequest).extract();

        sectionRequests.put(new SectionRequest(stationIds.get(1), stationIds.get(2), 15),
            createLineResponse1.header("Location"));
        sectionRequests.put(new SectionRequest(stationIds.get(2), stationIds.get(3), 10),
            createLineResponse1.header("Location"));
    }

    private void createLine2(Map<SectionRequest, String> sectionRequests) {
        LineRequest lineRequest2 = new LineRequest("2", "green", stationIds.get(1), stationIds.get(4), 4, 500);
        ExtractableResponse<Response> createLineResponse2 = createLineRequest(lineRequest2).extract();

        sectionRequests.put(new SectionRequest(stationIds.get(4), stationIds.get(5), 7),
            createLineResponse2.header("Location"));
        sectionRequests.put(new SectionRequest(stationIds.get(5), stationIds.get(6), 4),
            createLineResponse2.header("Location"));
    }

    private void createLine3(Map<SectionRequest, String> sectionRequests) {
        LineRequest lineRequest3 = new LineRequest("3", "orange", stationIds.get(6), stationIds.get(2), 10, 300);
        ExtractableResponse<Response> createLineResponse3 = createLineRequest(lineRequest3).extract();

        sectionRequests.put(new SectionRequest(stationIds.get(2), stationIds.get(7), 15),
            createLineResponse3.header("Location"));
        sectionRequests.put(new SectionRequest(stationIds.get(7), stationIds.get(8), 23),
            createLineResponse3.header("Location"));
    }

    private void createLine4() {
        LineRequest lineRequest4 = new LineRequest("4", "blue", stationIds.get(9), stationIds.get(10), 10, 400);
        createLineRequest(lineRequest4);
    }

    @Test
    @DisplayName("최단경로를 조회하면 경로, 거리, 요금을 반환한다.")
    void findShortestPath() {

        Map<String, String> params = new HashMap<>();
        params.put("source", "1");
        params.put("target", "9");
        params.put("age", "15");

        ExtractableResponse<Response> response = requestShortestPath(params).extract();

        PathResponse pathResponse = objectMapper.convertValue(response.jsonPath().get("."), PathResponse.class);
        assertAll(
            () -> assertThat(pathResponse.getStations())
                .containsExactly(
                    new StationResponse(1L, "a"),
                    new StationResponse(2L, "b"),
                    new StationResponse(3L, "c"),
                    new StationResponse(8L, "h"),
                    new StationResponse(9L, "i")
                ),
            () -> assertThat(pathResponse.getDistance()).isEqualTo(58),
            () -> assertThat(pathResponse.getFare()).isEqualTo(1680)
        );
    }

    @ParameterizedTest(name = "{index} : {0}")
    @MethodSource("invalidInputs")
    @DisplayName("사용자 입력을 검증한다.")
    void findShortestInvalidInput(Map<String, String> params) {
        requestShortestPath(params)
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    private static Stream<Arguments> invalidInputs() {
        return Stream.of(
            Arguments.of(Named.of("출발역이 null", new HashMap<String, String>() {
                {
                    put("source", null);
                    put("target", "9");
                    put("age", "15");
                }
            })),
            Arguments.of(Named.of("도착역이 null", new HashMap<String, String>() {
                {
                    put("source", "1");
                    put("target", null);
                    put("age", "15");
                }
            })),
            Arguments.of(Named.of("나이가 null", new HashMap<String, String>() {
                {
                    put("source", "1");
                    put("target", "9");
                    put("age", null);
                }
            })),
            Arguments.of(Named.of("나이가 음수", new HashMap<String, String>() {
                {
                    put("source", "1");
                    put("target", "9");
                    put("age", "-15");
                }
            })));
    }

    @Test
    @DisplayName("출발역과 도착역이 연결되어있지 않은 경우 상태코드는 notFound 이어야 합니다.")
    void findInvalidPath() {
        Map<String, String> params = new HashMap<>();
        params.put("source", "1");
        params.put("target", "11");
        params.put("age", "15");

        requestShortestPath(params)
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    private ValidatableResponse requestShortestPath(Map<String, String> params) {
        return RestAssured.given().log().all()
            .params(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/paths")
            .then().log().all();
    }
}
