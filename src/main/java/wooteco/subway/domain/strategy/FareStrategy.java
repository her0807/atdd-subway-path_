package wooteco.subway.domain.strategy;

import wooteco.subway.domain.strategy.discount.FareDiscountStrategy;
import wooteco.subway.domain.strategy.discount.FareDiscountStrategyFactory;

public class FareStrategy {

    private static final int BASIC_FARE = 1250;
    private static final int ADDITIONAL_FARE = 100;

    private static final int BASIC_DISTANCE = 10;
    private static final int STEP_ONE_DISTANCE = 50;
    private static final double STEP_ONE_CHARGE_DISTANCE = 5.0;
    private static final double STEP_TWO_CHARGE_DISTANCE = 8.0;

    public int calculateFare(int distance, int extraFare, int age) {
        return applyDiscount(calculateFareByDistance(distance) + extraFare, age);
    }

    private int calculateFareByDistance(int distance) {
        if (distance < BASIC_DISTANCE) {
            return BASIC_FARE;
        }
        if (distance < STEP_ONE_DISTANCE) {
            return BASIC_FARE + calculateStepOne(distance);
        }
        return BASIC_FARE + calculateStepOne(distance) + calculateStepTwo(distance);
    }

    private int calculateStepOne(int distance) {
        return (int) Math.ceil(
                Math.min(distance - BASIC_DISTANCE, STEP_ONE_DISTANCE - BASIC_DISTANCE) / STEP_ONE_CHARGE_DISTANCE)
                * ADDITIONAL_FARE;
    }

    private int calculateStepTwo(int distance) {
        return (int) Math.ceil((distance - STEP_ONE_DISTANCE) / STEP_TWO_CHARGE_DISTANCE) * ADDITIONAL_FARE;
    }

    private int applyDiscount(int fare, int age) {
        FareDiscountStrategy fareDiscountStrategy = FareDiscountStrategyFactory.getFareDiscountStrategy(age);
        return fare - fareDiscountStrategy.calculateDiscount(fare);
    }

}
