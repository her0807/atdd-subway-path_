package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.dto.line.LineRequest;
import wooteco.subway.dto.section.SectionRequest;
import wooteco.subway.dto.station.StationRequest;

@DisplayName("지하철 경로 관련 기능")
class PathAcceptanceTest extends AcceptanceTest {

    private static final StationRequest 강남역 = new StationRequest("강남역");

    private Long stationId1;
    private Long stationId2;
    private Long stationId3;
    private Long stationId4;
    private Long stationId5;

    private LineRequest 분당선;
    private LineRequest 다른분당선;
    private Long lineId1;
    private Long lineId2;

    @BeforeEach
    void beforeEach() {
        stationId1 = postStationId(강남역);
        stationId2 = postStationId(대흥역);
        stationId3 = postStationId(공덕역);
        stationId4 = postStationId(광흥창역);
        stationId5 = postStationId(상수역);

        분당선 = new LineRequest("분당선", "bg-green-600", stationId1, stationId2, 2, 500);
        다른분당선 = new LineRequest("다른분당선", "bg-red-600", stationId2, stationId5, 3, 900);
        lineId1 = postLineId(분당선);
        lineId2 = postLineId(다른분당선);
    }

    @DisplayName("추가 요금이 없는 지하철 경로를 조회한다.")
    @Test
    void findPath() {
        // given
        LineRequest 무료노선 = new LineRequest("무료노선", "bg-white-600", stationId3, stationId4, 50, 0);
        postLineId(무료노선);

        System.out.println("/paths?source=" + stationId3 + "&target=" + stationId4 + "&age=" + 20);
        // when
        ValidatableResponse validatableResponse = RestAssured.given()
                .log().all()
                .param("source", stationId3)
                .param("target", stationId4)
                .param("age", 20)
                .when()
                .get("/paths")
                .then().log().all();

        // then
        assertThat(validatableResponse.extract().statusCode()).isEqualTo(HttpStatus.OK.value());
        validatableResponse
                .body("stations.id", contains(stationId3.intValue(), stationId4.intValue()))
                .body("distance", equalTo(50))
                .body("fare", equalTo(2050));
    }

    @DisplayName("추가 요금이 있는 지하철 경로를 조회한다.")
    @Test
    void findPathWithExtraFare() {
        // given
        SectionRequest section1 = new SectionRequest(stationId2, stationId3, 2);
        SectionRequest section2 = new SectionRequest(stationId3, stationId4, 7);
        SectionRequest section3 = new SectionRequest(stationId5, stationId4, 4);

        postSectionResponse(lineId1, section1);
        postSectionResponse(lineId1, section2);
        postSectionResponse(lineId2, section3);

        // when
        ValidatableResponse validatableResponse = RestAssured.given()
                .log().all()
                .param("source", stationId1)
                .param("target", stationId4)
                .param("age", 20)
                .when()
                .get("/paths")
                .then().log().all();

        // then
        assertThat(validatableResponse.extract().statusCode()).isEqualTo(HttpStatus.OK.value());
        validatableResponse
                .body("stations.id", contains(stationId1.intValue(),
                        stationId2.intValue(),
                        stationId5.intValue(),
                        stationId4.intValue()))
                .body("distance", equalTo(9))
                .body("fare", equalTo(2150));
    }

    @DisplayName("존재하지 않는 지하철 경로를 조회한다.")
    @Test
    void findNotExistPath() {
        // given
        SectionRequest section1 = new SectionRequest(stationId2, stationId3, 2);
        SectionRequest section2 = new SectionRequest(stationId4, stationId5, 4);

        postSectionResponse(lineId1, section1);
        postSectionResponse(lineId2, section2);

        // when
        ValidatableResponse validatableResponse = RestAssured.given()
                .log().all()
                .param("source", stationId1)
                .param("target", stationId4)
                .param("age", 20)
                .when()
                .get("/paths")
                .then().log().all();

        // then
        assertThat(validatableResponse.extract().statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("존재하지 않는 역에 대한 경로를 조회하면 예외가 발생한다.")
    @Test
    void getNotExistStationPath() {
        /// given

        // when
        ValidatableResponse validatableResponse = RestAssured.given()
                .log().all()
                .param("source", 0)
                .param("target", stationId1)
                .param("age", 20)
                .when()
                .get("/paths")
                .then().log().all();

        // then
        assertThat(validatableResponse.extract().statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        validatableResponse.body("message", equalTo("존재하지 않는 역입니다."));
    }

    @DisplayName("출발역과 도착역이 같은 경로를 검색하면 예외가 발생한다.")
    @Test
    void equalSourceAndTargetStation() {
        // given

        // when
        ValidatableResponse validatableResponse = RestAssured.given()
                .log().all()
                .param("source", stationId1)
                .param("target", stationId1)
                .param("age", 20)
                .when()
                .get("/paths")
                .then().log().all();

        assertThat(validatableResponse.extract().statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        validatableResponse.body("message", equalTo("출발역과 도착역이 같을 수 없습니다."));
    }

    @Test
    @DisplayName("잘못된 uri로 요청하면 예외가 발생한다.")
    void invalidUrl() {
        // given

        // when
        ExtractableResponse<Response> extract = RestAssured.given().log().all()
                .when()
                .get("/path")
                .then().log().all()
                .extract();

        // then
        assertThat(extract.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
