package wooteco.subway.controller.dto;

import java.util.List;
import java.util.stream.Collectors;

import wooteco.subway.domain.PathInfo;
import wooteco.subway.domain.Station;

public class PathResponse {

	private List<StationResponse> stations;
	private int distance;
	private int fare;

	private PathResponse() {
	}

	public PathResponse(List<Station> stations, int distance, int fare) {
		this.stations = stations.stream()
			.map(StationResponse::from)
			.collect(Collectors.toList());
		this.distance = distance;
		this.fare = fare;
	}

	public static PathResponse from(PathInfo pathInfo) {
		return new PathResponse(pathInfo.getStations(), pathInfo.getDistance(), pathInfo.getFare());
	}

	public List<StationResponse> getStations() {
		return stations;
	}

	public int getDistance() {
		return distance;
	}

	public int getFare() {
		return fare;
	}
}
