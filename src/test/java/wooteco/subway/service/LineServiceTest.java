package wooteco.subway.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dao.entity.LineEntity;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineEditRequest;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.line.DuplicateLineNameException;
import wooteco.subway.exception.line.NoSuchLineException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Sql("/testSchema.sql")
class LineServiceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private LineDao lineDao;

    @Autowired
    private StationDao stationDao;

    @DisplayName("노선을 저장한다")
    @Test
    void 노선_저장() {
        Station up = stationDao.save(new Station("서울역"));
        Station down = stationDao.save(new Station("용산역"));
        Section section = new Section(up, down, 3);
        Line line = new Line("1호선", "bg-darkblue-600", 0, new Sections(section));

        LineResponse result = lineService.save(new LineRequest("1호선", "bg-darkblue-600", up.getId(), down.getId(), 3, line.getExtraFare()));

        assertThat(result).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(LineResponse.from(line));
    }

    @DisplayName("중복된 이름의 노선을 저장할 경우 예외가 발생한다")
    @Test
    void 중복된_노선_예외발생() {
        Station up = stationDao.save(new Station("합정역"));
        Station down = stationDao.save(new Station("홍대입구역"));
        lineService.save(new LineRequest("1호선", "bg-darkblue-600", up.getId(), down.getId(), 3, 0));

        assertThatThrownBy(() -> lineService.save(new LineRequest("1호선", "bg-darkblue-600", up.getId(), down.getId(), 3, 0)))
                .isInstanceOf(DuplicateLineNameException.class);
    }

    @DisplayName("모든 노선을 조회한다")
    @Test
    void 모든_노선_조회() {
        Station up = stationDao.save(new Station("합정역"));
        Station down = stationDao.save(new Station("홍대입구역"));
        lineService.save(new LineRequest("1호선", "bg-darkblue-600", up.getId(), down.getId(), 1, 0));
        lineService.save(new LineRequest("2호선", "bg-green-600", up.getId(), down.getId(), 1, 0));

        List<LineResponse> result = lineService.findAll();

        assertAll(
                () -> assertThat(result.size()).isEqualTo(2),
                () -> assertThat(result.get(0).getStations().size()).isEqualTo(2)
        );
    }

    @DisplayName("노선을 조회한다")
    @Test
    void 노선_조회() {
        Station up = stationDao.save(new Station("합정역"));
        Station down = stationDao.save(new Station("홍대입구역"));
        Line line = new Line("3호선", "bg-orange-600", 0, new Sections(new Section(up, down, 3)));
        LineResponse savedLine = lineService.save(new LineRequest("3호선", "bg-orange-600", up.getId(), down.getId(), 3, line.getExtraFare()));

        LineResponse result = lineService.findById(savedLine.getId());

        assertThat(result).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(LineResponse.from(line));
    }

    @DisplayName("존재하지 않는 노선을 조회할 시 예외가 발생한다")
    @Test
    void 존재하지않는_노선_조회_예외발생() {
        assertThatThrownBy(() -> lineService.findById(1L))
                .isInstanceOf(NoSuchLineException.class);
    }

    @DisplayName("노선을 업데이트한다")
    @Test
    void 노선_업데이트() {
        Station up = stationDao.save(new Station("신용산역"));
        Station down = stationDao.save(new Station("삼각지역"));
        LineResponse savedLine = lineService.save(new LineRequest("4호선", "bg-purple-600", up.getId(), down.getId(), 3, 0));
        Line newLine = new Line("4호선", "bg-skyblue-600", 0, new Sections(new Section(up, down, 3)));

        lineService.update(savedLine.getId(), new LineEditRequest("4호선", "bg-skyblue-600"));

        assertThat(lineService.findById(savedLine.getId()))
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(LineResponse.from(newLine));
    }

    @DisplayName("중복된 노선 이름으로 업데이트 시 예외가 발생한다")
    @Test
    void 중복노선_업데이트_예외발생() {
        Station up = stationDao.save(new Station("신용산역"));
        Station down = stationDao.save(new Station("삼각지역"));
        lineDao.save(new LineEntity("2호선", "bg-green-400"));
        LineResponse savedLine = lineService.save(new LineRequest("4호선", "bg-purple-600", up.getId(), down.getId(), 3, 0));

        assertThatThrownBy(() -> lineService.update(savedLine.getId(), new LineEditRequest("2호선", "bg-brown-600")))
                .isInstanceOf(DuplicateLineNameException.class);
    }

    @DisplayName("노선 삭제")
    @Test
    void 노선_삭제() {
        Station up = stationDao.save(new Station("신용산역"));
        Station down = stationDao.save(new Station("삼각지역"));
        LineResponse line = lineService.save(new LineRequest("4호선", "bg-purple-600", up.getId(), down.getId(), 3, 0));

        lineService.deleteById(line.getId());

        assertThat(lineService.findAll().size()).isEqualTo(0);
    }
}