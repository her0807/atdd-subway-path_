package wooteco.subway.domain.age;

public class TeenagerDiscountPolicy implements DiscountByAgePolicy {

    @Override
    public int apply(int fare) {
        return (int) Math.ceil((fare - 350) * 0.8);
    }
}
