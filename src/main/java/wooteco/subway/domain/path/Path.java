package wooteco.subway.domain.path;

import java.util.List;
import wooteco.subway.domain.AgeFarePolicy;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;

public class Path {

    private static final int DEFAULT_FARE = 1250;
    private static final int UNIT_OF_ADDITIONAL_FARE = 100;

    private static final int DISTANCE_OF_DEFAULT_FARE = 10;
    private static final int DISTANCE_OF_FIRST_ADDITIONAL_UNIT = 5;
    private static final int DISTANCE_OF_OVER_ADDITIONAL_UNIT = 8;
    private static final int DISTANCE_OF_OVER_ADDITIONAL_FARE = 50;
    private static final int NUMBER_TO_MATCH_THE_UNITS = 1;

    private final List<Station> stations;
    private final int distance;
    private final List<Line> lines;

    public Path(final List<Station> stations, final int distance, final List<Line> lines) {
        validateEmptyStations(stations);
        validatePositiveDistance(distance);
        validateEmptyLines(lines);
        this.stations = stations;
        this.distance = distance;
        this.lines = lines;
    }

    public void validateEmptyStations(final List<Station> stations) {
        if (stations.isEmpty()) {
            throw new IllegalArgumentException("경로는 비어서는 안됩니다.");
        }
    }

    public void validatePositiveDistance(final int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("거리는 0보다 커야합니다.");
        }
    }

    private void validateEmptyLines(final List<Line> lines) {
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("이용한 노선은 비어서는 안됩니다.");
        }
    }

    public int calculateFinalFare(final int age) {
        AgeFarePolicy ageFarePolicy = AgeFarePolicy.from(age);
        return ageFarePolicy.calculateFare(calculateGeneralFare());
    }

    private int calculateGeneralFare() {
        if (distance <= DISTANCE_OF_DEFAULT_FARE) {
            return DEFAULT_FARE + getMaxFareInLines();
        }
        if (distance <= DISTANCE_OF_OVER_ADDITIONAL_FARE) {
            return DEFAULT_FARE + calculateFirstAdditionalFare() + getMaxFareInLines();
        }
        return DEFAULT_FARE + calculateFirstAdditionalMaxFare() + calculateOverAdditionalFare()
                + getMaxFareInLines();
    }

    private int calculateFirstAdditionalFare() {
        return calculateOverFare(distance - DISTANCE_OF_DEFAULT_FARE, DISTANCE_OF_FIRST_ADDITIONAL_UNIT);
    }

    private int calculateOverFare(final int distance, final int unitDistance) {
        return (((distance - NUMBER_TO_MATCH_THE_UNITS) / unitDistance) * UNIT_OF_ADDITIONAL_FARE) + UNIT_OF_ADDITIONAL_FARE;
    }

    private int calculateFirstAdditionalMaxFare() {
        return calculateOverFare(DISTANCE_OF_OVER_ADDITIONAL_FARE - DISTANCE_OF_DEFAULT_FARE,
                DISTANCE_OF_FIRST_ADDITIONAL_UNIT);
    }

    private int calculateOverAdditionalFare() {
        return calculateOverFare(distance - DISTANCE_OF_OVER_ADDITIONAL_FARE, DISTANCE_OF_OVER_ADDITIONAL_UNIT);
    }

    private int getMaxFareInLines() {
        return lines.stream()
                .map(Line::getExtraFare)
                .mapToInt(fare -> fare)
                .max()
                .orElseThrow(() -> new IllegalArgumentException("존재하는 노선 요금이 없습니다."));
    }

    public List<Station> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
