package wooteco.subway.reopository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import wooteco.subway.domain.section.Line;
import wooteco.subway.reopository.dao.LineDao;
import wooteco.subway.service.LineService;

@JdbcTest
@Import({LineRepository.class, LineDao.class})
public class LineDaoTest {

    @Autowired
    private LineRepository lineRepository;

    @Test
    @DisplayName("노선 저장")
    void save() {
        Line line = new Line("1호선", "blue", 0);
        Long id = lineRepository.save(line);
        assertThat(id).isNotNull();
    }

    @Test
    @DisplayName("지하철 역 이름 중복 여부 조회")
    void duplicateName() {
        Line line = new Line("1호선", "blue", 0);
        lineRepository.save(line);
        assertThat(lineRepository.existByNameAndColor("1호선", "blue")).isTrue();
    }

    @Test
    @DisplayName("id로 노선 조회")
    void findById() {
        Long lineId = lineRepository.save(new Line("1호선", "blue", 0));
        Line findLine = lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException(LineService.NOT_FOUNT_ID_ERROR_MESSAGE));
        assertThat(findLine.getId()).isNotNull();
        assertThat(findLine.getName()).isEqualTo("1호선");
    }

    @Test
    @DisplayName("노선 전체 조회")
    void findAll() {
        Line line1 = new Line("1호선", "blue", 0);
        Line line2 = new Line("2호선", "red", 0);
        lineRepository.save(line1);
        lineRepository.save(line2);
        List<Line> liens = lineRepository.findAll();
        assertThat(liens).hasSize(2);
    }

    @Test
    @DisplayName("id로 노선 수정")
    void modifyById() {
        Long id = lineRepository.save(new Line("1호선", "blue", 0));
        lineRepository.modifyById(id, new Line("2호선", "red", 0));
        Line updateLine = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("노선 못찾음"));
        assertThat(updateLine.getName()).isEqualTo("2호선");
        assertThat(updateLine.getColor()).isEqualTo("red");
    }

    @Test
    @DisplayName("id로 노선 삭제")
    void deleteById() {
        Long id = lineRepository.save(new Line("1호선", "blue", 0));
        lineRepository.deleteById(id);
        assertThat(lineRepository.findAll()).hasSize(0);
    }

}
