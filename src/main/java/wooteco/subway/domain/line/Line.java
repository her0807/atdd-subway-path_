package wooteco.subway.domain.line;

import java.util.List;
import wooteco.subway.domain.fare.Fare;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;


public class Line {
    private Long id;
    private String name;
    private String color;
    private Fare extraFare;
    private Sections sections;

    private Line() {
    }

    public Line(Long id, String name, String color, Fare extraFare, Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.extraFare = extraFare;
        this.sections = sections;
    }

    public Line(Long id, String name, String color, Fare extraFare) {
        this(id, name, color, extraFare, null);
    }

    public Line(String name, String color, Fare extraFare, Section section) {
        this(null, name, color, extraFare, new Sections(List.of(section)));
    }

    public Line(String name, String color, Fare extraFare) {
        this(null, name, color, extraFare, null);
    }

    public void addSection(Section section) {
        sections.addSection(section);
    }

    public void deleteStation(Station station) {
        sections.deleteStation(station);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Fare getExtraFare() {
        return extraFare;
    }

    public Sections getSections() {
        return sections;
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", extraFare=" + extraFare +
                ", sections=" + sections +
                '}';
    }
}
