package wooteco.subway.domain.subwaymap;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;
import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.IllegalInputException;
import wooteco.subway.exception.path.NoSuchPathException;

public class SubwayMap {

    private final List<Line> lines;

    public SubwayMap(final List<Line> lines) {
        this.lines = lines;
    }

    public List<Station> searchPath(final Station sourceStation, final Station targetStation) {
        final GraphPath<Station, ShortestPathEdge> shortestPath = searchShortestPath(sourceStation, targetStation);
        return shortestPath.getVertexList();
    }

    private GraphPath<Station, ShortestPathEdge> searchShortestPath(final Station sourceStation,
                                                                    final Station targetStation) {
        validateStations(sourceStation, targetStation);

        final GraphPath<Station, ShortestPathEdge> shortestPath = toShortestPath()
                .getPath(sourceStation, targetStation);

        if (shortestPath == null) {
            throw new NoSuchPathException();
        }
        return shortestPath;
    }

    private void validateStations(final Station sourceStation, final Station targetStation) {
        if (sourceStation.equals(targetStation)) {
            throw new IllegalInputException("출발역과 도착역이 동일합니다.");
        }
    }

    private DijkstraShortestPath<Station, ShortestPathEdge> toShortestPath() {
        WeightedMultigraph<Station, ShortestPathEdge> graph = new WeightedMultigraph<>(ShortestPathEdge.class);

        for (Section section : toSections()) {
            graph.addVertex(section.getUpStation());
            graph.addVertex(section.getDownStation());
            graph.addEdge(
                    section.getUpStation(),
                    section.getDownStation(),
                    new ShortestPathEdge(section.getLineId(), section.getDistance())
            );
        }

        return new DijkstraShortestPath<>(graph);
    }

    private List<Section> toSections() {
        return lines.stream()
                .map(Line::getSections)
                .map(Sections::getValues)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public Distance searchDistance(final Station sourceStation, final Station targetStation) {
        final GraphPath<Station, ShortestPathEdge> shortestPath = searchShortestPath(sourceStation, targetStation);
        return new Distance((int) shortestPath.getWeight());
    }

    public int calculateMaxExtraFare(final Station sourceStation, final Station targetStation) {
        return searchShortestPath(sourceStation, targetStation)
                .getEdgeList()
                .stream()
                .map(ShortestPathEdge::getLineId)
                .map(this::findById)
                .mapToInt(Line::getExtraFare)
                .max()
                .orElse(0);
    }

    private Line findById(final Long id) {
        return lines.stream()
                .filter(it -> it.getId().equals(id))
                .findFirst()
                .orElseThrow();
    }
}
