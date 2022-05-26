package wooteco.subway.domain.fare;

import java.util.List;
import java.util.NoSuchElementException;
import wooteco.subway.domain.fare.strategy.ExtraFareStrategy;
import wooteco.subway.domain.path.strategy.DiscountStrategy;
import wooteco.subway.domain.section.Line;

public class Fare {

    private static final int BASIC_FARE = 1250;

    private final ExtraFareStrategy fareStrategy;
    private final DiscountStrategy discountStrategy;

    public Fare(ExtraFareStrategy fareStrategy, DiscountStrategy discountStrategy) {
        this.fareStrategy = fareStrategy;
        this.discountStrategy = discountStrategy;
    }

    public int calculateFare(int distance, int extraFare) {
        int fare = BASIC_FARE + fareStrategy.calculate(distance);
        return discountStrategy.calculate(fare + extraFare);
    }

    public int calculateMaxLineExtraFare(List<Line> lines) {
        return lines.stream()
                .mapToInt(Line::getExtraFare)
                .max()
                .orElseThrow(NoSuchElementException::new);
    }
}
