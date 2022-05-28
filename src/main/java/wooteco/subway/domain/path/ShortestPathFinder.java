package wooteco.subway.domain.path;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;
import wooteco.subway.domain.section.Section;
import wooteco.subway.exception.SubwayException;

@Component
public class ShortestPathFinder implements PathFinder {

    @Override
    public Path find(List<Long> stationIds, List<Section> sections, Long source, Long target) {
        DijkstraShortestPath<Long, ShortestPathEdge> shortestPath = initializePathGraph(stationIds, sections);
        GraphPath<Long, ShortestPathEdge> graphPath = Optional.ofNullable(shortestPath.getPath(source, target))
                .orElseThrow(() -> new SubwayException("경로가 존재하지 않습니다."));

        long distance = Double.valueOf(graphPath.getWeight()).longValue();
        return new Path(distance, getLineIds(graphPath), getStations(graphPath));
    }

    private DijkstraShortestPath<Long, ShortestPathEdge> initializePathGraph(List<Long> stationIds,
                                                                             List<Section> sections) {
        Graph<Long, ShortestPathEdge> graph = new WeightedMultigraph<>(ShortestPathEdge.class);
        addVertexes(stationIds, graph);
        setEdgeWeights(sections, graph);
        return new DijkstraShortestPath<>(graph);
    }

    private static void addVertexes(List<Long> stationIds, Graph<Long, ShortestPathEdge> graph) {
        for (Long stationId : stationIds) {
            graph.addVertex(stationId);
        }
    }

    private static void setEdgeWeights(List<Section> sections, Graph<Long, ShortestPathEdge> graph) {
        for (Section section : sections) {
            graph.addEdge(section.getUpStationId(), section.getDownStationId(),
                    new ShortestPathEdge(section.getLineId(), section.getDistance()));
        }
    }

    private Set<Long> getLineIds(GraphPath<Long, ShortestPathEdge> graphPath) {
        return graphPath.getEdgeList()
                .stream()
                .map(ShortestPathEdge::getLineId)
                .collect(Collectors.toUnmodifiableSet());
    }

    private Set<Long> getStations(GraphPath<Long, ShortestPathEdge> graphPath) {
        return new LinkedHashSet<>(graphPath.getVertexList());
    }
}
