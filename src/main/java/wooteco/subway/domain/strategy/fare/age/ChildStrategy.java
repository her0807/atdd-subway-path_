package wooteco.subway.domain.strategy.fare.age;

import static wooteco.subway.domain.strategy.fare.age.FareDiscountAgeConstant.DEDUCT_FARE;

public class ChildStrategy implements FareDiscountAgeStrategy {

    private static final double CHILD_DISCOUNT_PERCENT = 0.5;

    @Override
    public boolean isApplied(int age) {
        return age >= 6 && age < 13;
    }

    @Override
    public int calculateDiscount(int totalAmount) {
        return ((int) (Math.ceil(totalAmount - DEDUCT_FARE) * CHILD_DISCOUNT_PERCENT) / 10 * 10);
    }
}
