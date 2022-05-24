package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;

import wooteco.subway.acceptance.fixture.SimpleResponse;
import wooteco.subway.acceptance.fixture.SimpleRestAssured;
import wooteco.subway.dto.response.LineResponse;

public class LineAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void setUpStations() {
        Map<String, Object> stationParams1 = Map.of("name", "강남역");
        Map<String, Object> stationParams2 = Map.of("name", "역삼역");
        SimpleRestAssured.post("/stations", stationParams1);
        SimpleRestAssured.post("/stations", stationParams2);
    }

    @Test
    @DisplayName("노선을 생성한다.")
    void createLine() {
        // given
        Map<String, Object> params = mapParams("신분당선", "bg-red-600", 100);
        // when
        final SimpleResponse response = SimpleRestAssured.post("/lines", params);
        // then
        LineResponse lineResponse = response.getObject(".", LineResponse.class);

        Assertions.assertAll(
                () -> response.assertStatus(HttpStatus.CREATED),
                () -> assertThat(response.getHeader("Location")).isNotBlank(),
                () -> assertThat(lineResponse.getName()).isEqualTo("신분당선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("bg-red-600"),
                () -> assertThat(lineResponse.getExtraFare()).isEqualTo(100)
        );
    }

    @Test
    @DisplayName("입력값이 비어있는 경우 노선을 생성할 수 없다.")
    void createLine_throwsExceptionWithBlankInput() {
        // given
        Map<String, Object> params = mapParams("신분당선", "");
        // when
        final SimpleResponse response = SimpleRestAssured.post("/lines", params);
        // then
        Assertions.assertAll(
                () -> response.assertStatus(HttpStatus.BAD_REQUEST),
                () -> assertThat(response.containsExceptionMessage("필수 입력")).isTrue()
        );
    }

    @Test
    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성할 수 없다.")
    void createLine_throwsExceptionWithDuplicatedName() {
        // given
        Map<String, Object> params1 = mapParams("신분당선", "bg-red-600");
        SimpleRestAssured.post("/lines", params1);
        // when
        Map<String, Object> params2 = mapParams("신분당선", "bg-red-600");
        final SimpleResponse response = SimpleRestAssured.post("/lines", params2);
        // then
        Assertions.assertAll(
                () -> response.assertStatus(HttpStatus.BAD_REQUEST),
                () -> assertThat(response.containsExceptionMessage("이미 존재")).isTrue()
        );
    }

    @Test
    @DisplayName("전체 노선 목록을 조회한다.")
    void getLines() {
        /// given
        Map<String, Object> params1 = mapParams("신분당선", "bg-red-600");
        Map<String, Object> params2 = mapParams("경의중앙선", "bg-red-800");

        SimpleRestAssured.post("/lines", params1);
        SimpleRestAssured.post("/lines", params2);
        // when
        SimpleResponse response = SimpleRestAssured.get("/lines");
        // then
        List<LineResponse> lineResponses = response.getList(".", LineResponse.class);
        Assertions.assertAll(
                () -> response.assertStatus(HttpStatus.OK),
                () -> assertThat(lineResponses).hasSize(2)
        );
    }

    @Test
    @DisplayName("ID값으로 노선을 조회한다.")
    void getLine() {
        // given
        Map<String, Object> params1 = mapParams("신분당선", "bg-red-600");
        SimpleResponse createdResponse = SimpleRestAssured.post("/lines", params1);
        // when
        final String uri = createdResponse.getHeader("Location");
        final SimpleResponse foundResponse = SimpleRestAssured.get(uri);
        // then
        LineResponse lineResponse = foundResponse.getObject(".", LineResponse.class);
        Assertions.assertAll(
                () -> foundResponse.assertStatus(HttpStatus.OK),
                () -> assertThat(lineResponse.getName()).isEqualTo("신분당선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("bg-red-600")
        );
    }

    @Test
    @DisplayName("존재하지 않는 ID값으로 노선을 조회할 수 없다.")
    void getLine_throwExceptionWithInvalidId() {
        // given
        Map<String, Object> params = mapParams("신분당선", "bg-red-600");
        SimpleRestAssured.post("/lines", params);
        // when
        final SimpleResponse response = SimpleRestAssured.get("/lines/99");
        // then
        Assertions.assertAll(
                () -> response.assertStatus(HttpStatus.BAD_REQUEST),
                () -> assertThat(response.containsExceptionMessage("존재하지 않습니다")).isTrue()
        );
    }

    @Test
    @DisplayName("ID값으로 노선을 수정한다.")
    void modifyLine() {
        // given
        Map<String, Object> params = mapParams("신분당선", "bg-red-600");
        SimpleResponse createdResponse = SimpleRestAssured.post("/lines", params);
        // when
        final Map<String, Object> modificationParam = mapParams("구분당선", "bg-red-800");
        final String uri = createdResponse.getHeader("Location");
        final SimpleResponse modifiedResponse = SimpleRestAssured.put(uri, modificationParam);
        // then
        modifiedResponse.assertStatus(HttpStatus.OK);
    }

    @Test
    @DisplayName("존재하지 않는 ID값의 노선을 수정할 수 없다.")
    void modifyLine_throwExceptionWithInvalidId() {
        // given
        Map<String, Object> params = mapParams("신분당선", "bg-red-600");
        SimpleRestAssured.post("/lines", params);
        // when
        final Map<String, Object> modificationParam = mapParams("구분당선", "bg-red-600");
        final SimpleResponse response = SimpleRestAssured.put("/lines/99", modificationParam);
        // then
        Assertions.assertAll(
                () -> response.assertStatus(HttpStatus.BAD_REQUEST),
                () -> assertThat(response.containsExceptionMessage("존재하지 않습니다")).isTrue()
        );
    }

    @Test
    @DisplayName("ID값으로 노선을 제거한다.")
    void deleteLine() {
        // given
        Map<String, Object> params = mapParams("신분당선", "bg-red-600");
        SimpleResponse createdResponse = SimpleRestAssured.post("/lines", params);
        // when
        final String uri = createdResponse.getHeader("Location");
        final SimpleResponse deleteResponse = SimpleRestAssured.delete(uri);
        // then
        deleteResponse.assertStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("존재하지 않는 ID값의 노선을 제거할 수 없다.")
    void deleteLine_throwExceptionWithInvalidId() {
        // given
        Map<String, Object> params = mapParams("신분당선", "bg-red-600");
        SimpleRestAssured.post("/lines", params);
        // when
        final SimpleResponse response = SimpleRestAssured.delete("/lines/99");
        // then
        Assertions.assertAll(
                () -> response.assertStatus(HttpStatus.BAD_REQUEST),
                () -> assertThat(response.containsExceptionMessage("존재하지 않습니다")).isTrue()
        );
    }

    private Map<String, Object> mapParams(String name, String color, Integer extraFare) {
        return Map.of(
                "name", name,
                "color", color,
                "extraFare", extraFare,
                "upStationId", "1",
                "downStationId", "2",
                "distance", "10"
        );
    }

    private Map<String, Object> mapParams(String name, String color) {
        return Map.of(
                "name", name,
                "color", color,
                "upStationId", "1",
                "downStationId", "2",
                "distance", "10"
        );
    }
}
