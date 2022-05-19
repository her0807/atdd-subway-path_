package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.PathResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.service.fake.FakeLineDao;
import wooteco.subway.service.fake.FakeSectionDao;
import wooteco.subway.service.fake.FakeStationDao;

class PathServiceTest {

    private PathService pathService;
    private StationService stationService;
    private SectionService sectionService;
    private LineService lineService;

    @BeforeEach
    void setUp() {
        final FakeSectionDao sectionDao = new FakeSectionDao();
        final FakeStationDao stationDao = new FakeStationDao();
        final FakeLineDao lineDao = new FakeLineDao();

        pathService = new PathService(stationDao, sectionDao);
        stationService = new StationService(stationDao, sectionDao);
        sectionService = new SectionService(sectionDao);
        lineService = new LineService(lineDao, sectionDao, stationDao);
    }


    @DisplayName("하나의 line에서 경로를 조회할 수 있다.")
    @Test
    public void findPath() {
        // given
        final StationRequest a = new StationRequest("a");
        final StationRequest b = new StationRequest("b");
        final StationRequest c = new StationRequest("c");

        final StationResponse response1 = stationService.save(a);
        final StationResponse response2 = stationService.save(b);
        final StationResponse response3 = stationService.save(c);

        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", response1.getId(), response3.getId(), 10);
        final Long lineId = lineService.save(lineRequest).getId();

        final SectionRequest request = new SectionRequest(response1.getId(), response2.getId(), 4);

        sectionService.save(lineId, request);

        // when
        final PathResponse response = pathService.findPath(response1.getId(), response3.getId());

        // then
        assertThat(response).extracting("distance", "fare")
                .containsExactly(10, 1250);
        assertThat(response.getStations()).hasSize(3)
                .extracting("id", "name")
                .containsExactly(
                        tuple(response1.getId(), "a"),
                        tuple(response2.getId(), "b"),
                        tuple(response3.getId(), "c")
                );
    }

    @DisplayName("여러 개의 line에서 경로를 조회할 수 있다.")
    @Test
    public void findPath2() {
        // given
        final StationRequest a = new StationRequest("a");
        final StationRequest b = new StationRequest("b");
        final StationRequest c = new StationRequest("c");
        final StationRequest d = new StationRequest("d");
        final StationRequest e = new StationRequest("e");
        final StationRequest f = new StationRequest("f");
        final StationRequest g = new StationRequest("g");

        final StationResponse response1 = stationService.save(a);
        final StationResponse response2 = stationService.save(b);
        final StationResponse response3 = stationService.save(c);
        final StationResponse response4 = stationService.save(d);
        final StationResponse response5 = stationService.save(e);
        final StationResponse response6 = stationService.save(f);
        final StationResponse response7 = stationService.save(g);

        final LineRequest 신분당선 = new LineRequest("신분당선", "bg-red-600", response1.getId(), response3.getId(), 10);
        final Long 신분당선id = lineService.save(신분당선).getId();
        sectionService.save(신분당선id, new SectionRequest(response1.getId(), response2.getId(), 4));
        sectionService.save(신분당선id, new SectionRequest(response3.getId(), response7.getId(), 4));

        final LineRequest 경중선 = new LineRequest("경중선", "bg-blue-600", response2.getId(), response7.getId(), 21);
        final Long 경중선id = lineService.save(경중선).getId();
        sectionService.save(경중선id, new SectionRequest(response2.getId(), response4.getId(), 10));
        sectionService.save(경중선id, new SectionRequest(response4.getId(), response5.getId(), 1));
        sectionService.save(경중선id, new SectionRequest(response6.getId(), response7.getId(), 6));


        final LineRequest 분당선 = new LineRequest("분당선", "bg-yellow-600", response3.getId(), response5.getId(), 5);
        final Long 분당선id = lineService.save(분당선).getId();


        // when
        final PathResponse response = pathService.findPath(response1.getId(), response6.getId());

        // then
        assertThat(response).extracting("distance", "fare")
                .containsExactly(19, 1450);
        assertThat(response.getStations()).hasSize(5)
                .extracting("id", "name")
                .containsExactly(
                        tuple(response1.getId(), "a"),
                        tuple(response2.getId(), "b"),
                        tuple(response3.getId(), "c"),
                        tuple(response5.getId(), "e"),
                        tuple(response6.getId(), "f")
                );
    }
}