package wooteco.subway.domain.section;

public class Distance {
    public static final int MINIMUM_DISTANCE = 1;

    private final int value;

    public Distance(int value) {
        validatePositive(value);
        this.value = value;
    }

    public Distance(double value) {
        this((int) value);
    }

    private void validatePositive(int value) {
        if (value < MINIMUM_DISTANCE) {
            throw new IllegalArgumentException("거리는 0이하일 수 없습니다.");
        }
    }

    public Distance add(Distance other) {
        return new Distance(value + other.value);
    }

    public Distance subtract(Distance other) {
        return new Distance(value - other.value);
    }

    public boolean isGreaterThanOrEqualTo(int distance) {
        return value >= distance;
    }

    public boolean isLessThanOrEqualTo(int distance) {
        return value <= distance;
    }

    public boolean isBetween(int smallDistance, int largeDistance) {
        return smallDistance <= value && value <= largeDistance;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Distance distance = (Distance) o;

        return value == distance.value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return "Distance{" +
                "value=" + value +
                '}';
    }
}
