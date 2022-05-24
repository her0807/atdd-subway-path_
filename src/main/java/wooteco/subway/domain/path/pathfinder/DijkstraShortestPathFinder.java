package wooteco.subway.domain.path.pathfinder;

import java.util.List;
import java.util.stream.Collectors;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.path.Path;
import wooteco.subway.domain.section.Distance;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;

public class DijkstraShortestPathFinder {

    public Path getPath(List<Section> sections, Station source, Station target) {
        validateDepartureAndArrival(source, target);

        GraphPath<Station, SectionEdge> path = generatePath(sections, source, target);

        List<Station> stations = path.getVertexList();
        List<Line> lines = getLinesFromPath(path);
        Distance distance = new Distance(path.getWeight());

        return new Path(stations, lines, distance);
    }

    private void validateDepartureAndArrival(Station departure, Station arrival) {
        if (departure.equals(arrival)) {
            throw new IllegalArgumentException("경로의 출발역과 도착역이 같을 수 없습니다.");
        }
    }

    private GraphPath<Station, SectionEdge> generatePath(List<Section> sections, Station source, Station target) {
        WeightedMultigraph<Station, SectionEdge> graph = generateWeightedMultiGraph(sections);
        DijkstraShortestPath<Station, SectionEdge> shortestPath = new DijkstraShortestPath<>(graph);
        return shortestPath.getPath(source, target);
    }

    private WeightedMultigraph<Station, SectionEdge> generateWeightedMultiGraph(List<Section> sections) {
        WeightedMultigraph<Station, SectionEdge> graph = new WeightedMultigraph<>(SectionEdge.class);

        for (Section section : sections) {
            addVertices(graph, section);
            addEdges(graph, section);
        }

        return graph;
    }

    private void addVertices(WeightedMultigraph<Station, SectionEdge> graph, Section section) {
        graph.addVertex(section.getUpStation());
        graph.addVertex(section.getDownStation());
    }

    private void addEdges(WeightedMultigraph<Station, SectionEdge> graph, Section section) {
        SectionEdge edge = new SectionEdge(section);
        graph.addEdge(section.getUpStation(), section.getDownStation(), edge);
        graph.setEdgeWeight(edge, section.getDistance().getValue());
    }

    private List<Line> getLinesFromPath(GraphPath<Station, SectionEdge> path) {
        return path.getEdgeList()
                .stream()
                .map(SectionEdge::getLine)
                .collect(Collectors.toList());
    }
}
