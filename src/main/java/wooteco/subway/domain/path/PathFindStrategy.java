package wooteco.subway.domain.path;

import wooteco.subway.domain.Sections;

public interface PathFindStrategy {

    FindPathResult findPath(final Sections sections, final long sourceId, final long targetId);
}
