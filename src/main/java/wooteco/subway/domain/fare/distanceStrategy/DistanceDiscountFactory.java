package wooteco.subway.domain.fare.distanceStrategy;

import java.util.Arrays;
import java.util.function.Predicate;

public enum DistanceDiscountFactory {
    BELOW_MINIMUM (discount -> discount < 10, new FreeDiscountPolicy()),
    BELOW_MAXIMUM (discount -> discount >= 10 && discount < 50, new NormalDistanceDiscountPolicy()),
    OVER_MAXIMUM (discount -> discount >= 50, new ExtraDiscountPolicy())
    ;

    private final Predicate<Integer> discountCondition;
    private final DistanceDiscountPolicy distanceDiscountPolicy;

    DistanceDiscountFactory(Predicate<Integer> discountCondition,
        DistanceDiscountPolicy distanceDiscountPolicy) {
        this.discountCondition = discountCondition;
        this.distanceDiscountPolicy = distanceDiscountPolicy;
    }

    public static DistanceDiscountPolicy from(int rawDistance) {
        return Arrays.stream(DistanceDiscountFactory.values())
            .filter(distance -> distance.discountCondition.test(rawDistance))
            .map(distance -> distance.distanceDiscountPolicy)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("거리가 잘못 입력되었습니다."));
    }
}
