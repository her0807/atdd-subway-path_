package wooteco.subway.common;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.dto.SectionRequest;

public class LineAddAndRequest extends Request {

    private final Long id;

    public LineAddAndRequest(TLine tLine, Long startStationId, Long endStationId, int distance) {
        this.id = tLine.노선을등록한다(new SectionRequest(startStationId, endStationId, distance)).getId();
    }

    public List<ExtractableResponse<Response>> 구간을등록한다(SectionRequest... sectionRequests) {
        return List.of(sectionRequests).stream()
                .map(this::구간을등록한다)
                .collect(Collectors.toList());
    }

    public ExtractableResponse<Response> 구간을등록한다(SectionRequest sectionRequest) {
        return post(sectionRequest, String.format("/lines/%d/sections", id));
    }

}
