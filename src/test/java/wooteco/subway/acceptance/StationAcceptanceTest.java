package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.dto.controller.response.StationResponse;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");

        // when
        ExtractableResponse<Response> response = RequestFrame.post(BodyCreator.makeStationBodyForPost("강남역"),
                "/stations");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("역 저장 시 요청 객체의 요청 정보를 누락(공백, null)하면 에러를 응답한다.")
    @Test
    void createStationMissingParam() {
        // given

        // when
        ExtractableResponse<Response> response = RequestFrame.post(BodyCreator.makeStationBodyForPost(""),
                "/stations");

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().asString()).contains("name")
        );
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStationWithLongName() {
        // given

        // when
        ExtractableResponse<Response> response = RequestFrame.post(BodyCreator.makeStationBodyForPost("a".repeat(256)),
                "/stations");

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().asString()).contains("존재할 수 없는 이름입니다.")
        );
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.(400에러)")
    @Test
    void createStationWithDuplicateName() {
        // given
        RequestFrame.post(BodyCreator.makeStationBodyForPost("강남역"), "/stations");

        // when
        ExtractableResponse<Response> response = RequestFrame.post(BodyCreator.makeStationBodyForPost("강남역"),
                "/stations");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = RequestFrame.post(BodyCreator.makeStationBodyForPost("강남역"),
                "/stations");

        ExtractableResponse<Response> createResponse2 = RequestFrame.post(BodyCreator.makeStationBodyForPost("역삼역"),
                "/stations");

        // when
        ExtractableResponse<Response> response = RequestFrame.get("/stations");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedStationIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultStationIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultStationIds).containsAll(expectedStationIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = RequestFrame.post(BodyCreator.makeStationBodyForPost("강남역"),
                "/stations");
        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RequestFrame.delete(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 지하철역을 제거한다.(404에러)")
    @Test
    void deleteStationNotExists() {
        // given

        // when
        ExtractableResponse<Response> response = RequestFrame.delete("/stations/1");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
