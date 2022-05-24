package wooteco.subway.domain.fare;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FareTest {

	@DisplayName("거리에 따른 요금 정책에 따라 요금을 계산한다.")
	@ParameterizedTest
	@CsvSource(
		value = {"10:1250", "11:1350", "16:1450","40:1950",  "50:2050", "58:2150"},
		delimiter = ':')
	void fare(int distance, int result) {
		// when
		Fare fare = Fare.of(distance, 0);
		// then
		assertThat(fare.getValue()).isEqualTo(result);
	}

	@DisplayName("거리에 따른 요금 정책에 따라 요금을 계산한다.")
	@ParameterizedTest
	@CsvSource(
		value = {"10:1250", "11:1350", "16:1450","40:1950",  "50:2050", "58:2150"},
		delimiter = ':')
	void extraFare(int distance, int result) {
		// when
		Fare fare = Fare.of(distance, 1000);
		// then
		assertThat(fare.getValue()).isEqualTo(result + 1000);
	}

}
