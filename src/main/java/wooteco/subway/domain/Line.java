package wooteco.subway.domain;

import java.util.List;
import java.util.Objects;

public class Line {

    private Long id;
    private String name;
    private String color;
    private int extraFare;
    private Sections sections;

    private Line() {
    }

    public Line(String name, String color, int extraFare) {
        validateNameNotEmpty(name);
        validateColorNotEmpty(color);
        this.name = name;
        this.color = color;
        this.extraFare = extraFare;
    }

    public Line(Long id, String name, String color, int extraFare) {
        this(name, color, extraFare);
        this.id = id;
    }

    public Line(String name, String color, int extraFare, Sections sections) {
        this(name, color, extraFare);
        this.sections = sections;
    }

    public Line(Long id, String name, String color, int extraFare, Sections sections) {
        this(name, color, extraFare, sections);
        this.id = id;
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void add(Section section) {
        sections.add(section);
    }

    public void delete(Station station) {
        sections.delete(station);
    }

    private void validateNameNotEmpty(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("이름은 비워둘 수 없습니다.");
        }
    }

    private void validateColorNotEmpty(String color) {
        if (color.isBlank()) {
            throw new IllegalArgumentException("색상은 비워둘 수 없습니다.");
        }
    }

    public void updateNameAndColor(String name, String color) {
        validateNameNotEmpty(name);
        validateColorNotEmpty(color);
        this.name = name;
        this.color = color;
    }

    public boolean isSectionExisted(Section section) {
        return sections.getSections()
                .stream()
                .anyMatch(lineSection -> lineSection.equals(section));
    }

    public List<Section> getSections() {
        return sections.getSections();
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

    public int getExtraFare() {
        return extraFare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(name, line.name) && Objects.equals(color, line.color)
                && Objects.equals(sections, line.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color, sections);
    }
}
