package wooteco.subway.domain.fare;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import wooteco.subway.domain.distance.Kilometer;

public class DistanceFarePolicyTest {

    @ParameterizedTest(name = "{0}일 때 요금은 {1}이다")
    @MethodSource("provideDistanceAndFare")
    void getFare(Kilometer distance, Fare expected) {
        assertThat(DistanceFarePolicy.getFare(distance)).isEqualTo(expected);
    }

    private static Stream<Arguments> provideDistanceAndFare() {
        return Stream.of(
                Arguments.of(Kilometer.from(10), new Fare(1250)),
                Arguments.of(Kilometer.from(11), new Fare(1350)),
                Arguments.of(Kilometer.from(15), new Fare(1350)),
                Arguments.of(Kilometer.from(16), new Fare(1450)),
                Arguments.of(Kilometer.from(50), new Fare(2050)),
                Arguments.of(Kilometer.from(51), new Fare(2150)),
                Arguments.of(Kilometer.from(58), new Fare(2150)),
                Arguments.of(Kilometer.from(59), new Fare(2250))
        );
    }
}
