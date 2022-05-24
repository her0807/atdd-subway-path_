package wooteco.subway.domain.path;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.NotFoundException;

public class FindDijkstraShortestPathStrategy implements FindPathStrategy {

    @Override
    public Path findPath(final Station source, final Station target, final Sections sections) {
        sections.checkExistStations(source, target);

        WeightedMultigraph<Station, ShortestPathEdge> graph = new WeightedMultigraph<>(ShortestPathEdge.class);
        addVertexStation(sections, graph);
        addEdgeWeightStation(sections, graph);

        GraphPath<Station, ShortestPathEdge> shortestPath = getStationShortestPathEdgeGraphPath(source, target, graph);
        List<Line> lines = getLines(shortestPath);
        return new Path(shortestPath.getVertexList(), (int) shortestPath.getWeight(), lines);
    }

    private void addVertexStation(final Sections sections,
                                  final WeightedMultigraph<Station, ShortestPathEdge> graph) {
        List<Station> allStations = sections.getAllStations();
        for (Station station : allStations) {
            graph.addVertex(station);
        }
    }

    private void addEdgeWeightStation(final Sections sections,
                                      final WeightedMultigraph<Station, ShortestPathEdge> graph) {
        List<Section> allSections = sections.getSections();
        for (Section section : allSections) {
            graph.addEdge(section.getUpStation(), section.getDownStation(),
                    new ShortestPathEdge(section.getLine(), section.getDistance()));
        }
    }

    private GraphPath<Station, ShortestPathEdge> getStationShortestPathEdgeGraphPath(final Station source,
                                                                                     final Station target,
                                                                                     final WeightedMultigraph<Station, ShortestPathEdge> graph) {
        DijkstraShortestPath<Station, ShortestPathEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        return Optional.ofNullable(dijkstraShortestPath.getPath(source, target))
                .orElseThrow(() -> new NotFoundException("갈 수 있는 경로를 찾을 수 없습니다."));
    }

    private List<Line> getLines(final GraphPath<Station, ShortestPathEdge> shortestPath) {
        List<ShortestPathEdge> edges = shortestPath.getEdgeList();
        return edges.stream()
                .map(ShortestPathEdge::getLine)
                .collect(Collectors.toList());
    }
}
