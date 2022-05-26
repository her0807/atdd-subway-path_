package wooteco.subway.domain.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FareStrategyTest {

    @DisplayName("기본 거리에 대한 운임을 계산한다.")
    @Test
    void calculateBasicDistance() {
        FareStrategy fareStrategy = new FareStrategy();

        int fare = fareStrategy.calculateFare(9, 0, 20);

        assertThat(fare).isEqualTo(1250);
    }

    @DisplayName("거리가 50이하일 경우 5km 마다 100원이 추가되어 계산할 수 있다.")
    @Test
    void calculate50UnderDistance() {
        FareStrategy fareStrategy = new FareStrategy();

        int fare = fareStrategy.calculateFare(12, 0, 20);

        assertThat(fare).isEqualTo(1350);
    }

    @DisplayName("거리가 50초과일 경우 8km 마다 100원이 추가되어 계산할 수 있다.")
    @Test
    void calculate50OverDistance() {
        FareStrategy fareStrategy = new FareStrategy();

        int fare = fareStrategy.calculateFare(58, 0, 20);

        assertThat(fare).isEqualTo(2150);
    }

    @DisplayName("추가 요금이 붙으면 합산해서 계산한다.")
    @Test
    void addExtraFare() {
        FareStrategy fareStrategy = new FareStrategy();

        int fare = fareStrategy.calculateFare(9, 900, 20);

        assertThat(fare).isEqualTo(2150);
    }

    @DisplayName("이용객의 연령에 따라 요금을 할인한다.")
    @ParameterizedTest
    @CsvSource(value = {"5,0", "12,800", "18,1070", "19,1250"})
    void babyFare(int age, int expectedFare) {
        FareStrategy fareStrategy = new FareStrategy();

        int fare = fareStrategy.calculateFare(1, 0, age);

        assertThat(fare).isEqualTo(expectedFare);
    }

}
