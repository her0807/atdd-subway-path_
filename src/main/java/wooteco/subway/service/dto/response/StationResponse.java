package wooteco.subway.service.dto.response;

public class StationResponse {

    private final long id;
    private final String name;

    public StationResponse(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
