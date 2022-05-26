package wooteco.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.controller.dto.path.PathRequest;
import wooteco.subway.service.dto.path.PathResponse;
import wooteco.subway.service.dto.station.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.acceptance.AcceptanceFixture.*;
import static wooteco.subway.acceptance.ResponseCreator.*;

@DisplayName("경로 관련 기능")
public class PathAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void init() {
        createPostStationResponse(낙성대);
        createPostStationResponse(사당);
        createPostStationResponse(방배);
        createPostStationResponse(서초);
        createPostStationResponse(서울대입구);
        createPostStationResponse(봉천);

        createPostLineResponse(이호선);

        createPostSectionResponse(2L, 사당_서초);
        createPostSectionResponse(2L, 사당_방배);
        createPostSectionResponse(2L, 봉천_낙성대);
    }

    @Test
    @DisplayName("중복된 경로가 있다면 가중치가 낮은 거리가 선택된다")
    void FindPathWithDuplicatedNodes() {
        //given
        createPostLineResponse(일호선);
        //when
        ExtractableResponse<Response> response = createGetPathResponse(new PathRequest(1L, 4L, 20));
        PathResponse 경로응답 = response.body().jsonPath().getObject(".", PathResponse.class);
        List<String> 실제경로 = 경로응답.getStations().stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(실제경로).containsExactly("낙성대", "사당", "방배", "서초"),
                () -> assertThat(경로응답.getDistance()).isEqualTo(35),
                () -> assertThat(경로응답.getFare()).isEqualTo(1750)
        );
    }

    @Test
    @DisplayName("없는 출발 역 또는 도착 역 Id 를 입력받으면 예외를 반환한다.")
    void FindPathWithNotExistsStationId() {
        //given
        //when
        ExtractableResponse<Response> response = createGetPathResponse(new PathRequest(30000L, 40000L, 15));
        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.asString()).isEqualTo("[ERROR] 역을 찾을 수 없습니다")
        );
    }

    @Test
    @DisplayName("경로를 찾을 수 없으면 예외을 반환한다.")
    void FindPathWithNotExistsPath() {
        //given
        createPostStationResponse(에덴);
        createPostStationResponse(제로);
        createPostLineResponse(삼호선);
        //when
        ExtractableResponse<Response> response = createGetPathResponse(new PathRequest(1L, 8L, 15));
        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.asString()).isEqualTo("[ERROR] 경로를 찾을 수 없습니다")
        );
    }
}
