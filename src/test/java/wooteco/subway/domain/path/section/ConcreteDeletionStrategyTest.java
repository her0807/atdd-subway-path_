package wooteco.subway.domain.path.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.section.ConcreteDeletionStrategy;
import wooteco.subway.domain.section.Section;

class ConcreteDeletionStrategyTest {

    private final ConcreteDeletionStrategy concreteDeletionStrategy = new ConcreteDeletionStrategy();
    private List<Section> sections;

    private final Section _1L_2L_10 = new Section(1L, 1L, 1L, 2L, 10);

    @BeforeEach
    void setUp() {
        sections = new ArrayList<>(List.of(_1L_2L_10));
    }

    @Test
    @DisplayName("노선에 구간이 한개만 존재하면 예외 발생")
    void save() {
        assertThatThrownBy(() -> concreteDeletionStrategy.delete(sections, 1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("노선의 유일한 구간은 삭제할 수 없습니다.");
    }

    @Test
    @DisplayName("중간 구간 제거시 수정된 구간을 반환한다.")
    void fixDisconnectedSection() {
        Section _2L_3L_10 = new Section(2L, 1L, 2L, 3L, 10);
        sections.add(_2L_3L_10);
        Section fixedSection = concreteDeletionStrategy.fixDisconnectedSection(sections, 1L, 2L)
                .orElseThrow();

        assertThat(fixedSection.getUpStationId()).isEqualTo(1L);
        assertThat(fixedSection.getDownStationId()).isEqualTo(3L);
        assertThat(fixedSection.getDistance()).isEqualTo(20);
    }

    @Test
    @DisplayName("중간 구간이 아닐 시 수정할 구간은 Optional.empty를 반환")
    void fixDisconnectedSection_empty() {
        Section _2L_3L_10 = new Section(2L, 1L, 2L, 3L, 10);
        sections.add(_2L_3L_10);

        Optional<Section> fixedSection = concreteDeletionStrategy.fixDisconnectedSection(sections, 1L, 3L);

        assertThat(fixedSection.isPresent()).isFalse();
    }
}