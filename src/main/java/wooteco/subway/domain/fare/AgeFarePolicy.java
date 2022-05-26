package wooteco.subway.domain.fare;

import java.util.Arrays;
import java.util.function.Predicate;

public enum AgeFarePolicy {
    INFANT(age -> age.lessThan(6)) {
        @Override
        public Fare apply(Fare fare) {
            return FREE;
        }
    },
    CHILDREN(age -> age.moreThan(6) && age.lessThan(13)) {
        @Override
        public Fare apply(Fare fare) {
            return fare.discount(350)
                    .discountPercent(50);
        }
    },
    TEENAGER(age -> age.moreThan(13) && age.lessThan(19)) {
        @Override
        public Fare apply(Fare fare) {
            return fare.discount(350)
                    .discountPercent(20);
        }
    },
    GENERAL(age -> age.moreThan(19) && age.lessThan(65)) {
        @Override
        public Fare apply(Fare fare) {
            return fare;
        }
    },
    SENIOR(age -> age.moreThan(65)) {
        @Override
        public Fare apply(Fare fare) {
            return FREE;
        }
    };

    private static final Fare FREE = new Fare(0);
    private final Predicate<Age> condition;

    AgeFarePolicy(Predicate<Age> condition) {
        this.condition = condition;
    }

    abstract public Fare apply(Fare fare);

    public static Fare getFare(Fare fare, Age age) {
        AgeFarePolicy farePolicy = findFarePolicy(age);
        return farePolicy.apply(fare);
    }

    private static AgeFarePolicy findFarePolicy(Age age) {
        return Arrays.stream(values())
                .filter(farePolicy -> farePolicy.condition.test(age))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("요금 정책을 찾을 수 없습니다."));
    }
}
