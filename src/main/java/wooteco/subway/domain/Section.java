package wooteco.subway.domain;

import java.util.Objects;

public class Section {

    private final Long id;
    private final Station upStation;
    private final Station downStation;
    private final int distance;
    private final int extraFare;

    public Section(final Station upStation, final Station downStation, final int distance) {
        this(null, upStation, downStation, distance, 0);
    }

    public Section(final Long id, final Station upStation, final Station downStation, final int distance,
                   final int extraFare) {
        validateSameStationId(upStation, downStation);
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.extraFare = extraFare;
    }

    private void validateSameStationId(final Station upStation, final Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException("상행, 하행 역은 서로 달라야합니다.");
        }
    }

    public boolean isUpperThan(final Section section) {
        return this.downStation.equals(section.upStation);
    }

    public boolean isLowerThan(final Section section) {
        return this.upStation.equals(section.downStation);
    }

    public boolean isSameOrLongerThan(final Section section) {
        return this.distance >= section.distance;
    }

    public Boolean hasSameUpStation(final Section section) {
        return this.upStation.equals(section.upStation);
    }

    public boolean hasSameDownStation(final Section section) {
        return this.downStation.equals(section.downStation);
    }

    public int plusDistance(final Section section) {
        return this.distance + section.distance;
    }

    public int minusDistance(final Section section) {
        return this.distance - section.distance;
    }

    public Long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public int getExtraFare() {
        return extraFare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(upStation, section.upStation) && Objects.equals(downStation,
                section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStation, downStation);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }
}
