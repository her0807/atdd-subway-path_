package wooteco.subway.domain;

import java.util.List;
import java.util.Optional;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;
import wooteco.subway.exception.NoReachableStationException;
import wooteco.subway.exception.StationNotFoundException;

public class Path {

    private static final String NOT_EXIST_STATION = "출발지, 도착지 모두 존재해야 됩니다.";
    private static final String NO_REACHABLE = "출발지에서 도착지로 갈 수 없습니다.";

    private final WeightedMultigraph<Long, ShortestPathEdge> graph = new WeightedMultigraph(
            ShortestPathEdge.class);

    public Path(List<Section> sections) {
        for (Section section : sections) {
            graph.addVertex(section.getUpStationId());
            graph.addVertex(section.getDownStationId());
            graph.addEdge(section.getUpStationId(), section.getDownStationId(),
                    new ShortestPathEdge(section.getLine(),
                            section.getDistance()));
        }
    }

    public List<Long> calculateShortestPath(Long source, Long target) {
        Optional<GraphPath> path = makeGraphPath(source, target);

        return path.orElseThrow(() -> new NoReachableStationException(NO_REACHABLE)).getVertexList();
    }

    public int calculateShortestDistance(Long source, Long target) {
        Optional<GraphPath> path = makeGraphPath(source, target);

        return (int) path.orElseThrow(() -> new NoReachableStationException(NO_REACHABLE)).getWeight();
    }

    private Optional<GraphPath> makeGraphPath(Long source, Long target) {
        Optional<GraphPath> path;

        try {
            DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
            path = Optional.ofNullable(
                    dijkstraShortestPath.getPath(source, target));
        } catch (IllegalArgumentException exception) {
            throw new StationNotFoundException(NOT_EXIST_STATION);
        }

        return path;
    }

    public int calculateExtraFare(Long source, Long target) {
        List<ShortestPathEdge> edges = getEdges(source, target);

        return edges.stream()
                .distinct()
                .map(shortestPathEdge -> shortestPathEdge.getLine())
                .mapToInt(line -> line.getExtraFare())
                .max()
                .orElse(0);
    }

    private List<ShortestPathEdge> getEdges(Long source, Long target) {
        Optional<GraphPath> path = makeGraphPath(source, target);
        return path.orElseThrow(() -> new NoReachableStationException(NO_REACHABLE)).getEdgeList();
    }
}
