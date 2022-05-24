package wooteco.subway.acceptance.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.acceptance.Fixture.강남역;
import static wooteco.subway.acceptance.Fixture.청계산입구역;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

@JdbcTest
@Sql({"/schema.sql", "/test-data.sql"})
class LineDaoTest {

    private final LineDao lineDao;

    @Autowired
    public LineDaoTest(DataSource dataSource) {
        this.lineDao = new LineDao(dataSource);
    }

    @Test
    void save() {
        final Line createdLine = lineDao.save(Line.initialCreateWithoutId("8호선", "핑크", 강남역, 청계산입구역, 1, 0));

        assertThat(createdLine.getId()).isEqualTo(6L);
    }

    @Test
    @DisplayName("노선의 이름이 존재하면 true 반환")
    void existsByName() {
        final boolean result = lineDao.existsByName("경의중앙선");

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("노선의 이름이 존재하지 않으면 false 반환")
    void nonExistsName() {
        final boolean result = lineDao.existsByName("123호선");

        assertThat(result).isFalse();
    }

    @Test
    void findAll() {
        final List<Line> lines = lineDao.findAll();

        assertThat(lines.size()).isEqualTo(5);
    }

    @Test
    void findById() {
        Line line = Line.initialCreateWithoutId("12호선", "초록이", 강남역, 청계산입구역, 1, 0);
        final Line createdLine = lineDao.save(line);

        final Line foundLine = lineDao.findById(createdLine.getId());

        assertThat(foundLine.getColor()).isEqualTo(line.getColor());
    }

    @Test
    void updateLineById() {
        lineDao.updateLineById(1L, "12호선", "초록이이", 0);
        final Line line = lineDao.findById(1L);

        assertThat(line.getName()).isEqualTo("12호선");
    }

    @Test
    @DisplayName("존재하지 않는 id로 수정을 할 경우 예외 발생")
    void updateNonExistentLineId() {
        assertThatThrownBy(() -> lineDao.updateLineById(100L, "12호선", "초록이", 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteById() {
        lineDao.deleteById(1L);

        final List<Line> lines = lineDao.findAll();

        assertThat(lines.size()).isEqualTo(4);
    }

    @Test
    @DisplayName("존재하지 않는 id로 삭제할 경우 예외 발생")
    void deleteNonExistentLineId() {
        assertThatThrownBy(() -> lineDao.deleteById(100L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
