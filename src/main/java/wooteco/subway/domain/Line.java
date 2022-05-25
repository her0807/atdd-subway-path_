package wooteco.subway.domain;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Line {
    private final Long id;
    private String name;
    private String color;
    private int extraFare;
    private final Sections sections = new Sections();

    public Line(final Long id, final String name, final String color, final int extraFare) {
        validateRequiredArgument(name, color, extraFare);
        this.id = id;
        this.name = name;
        this.color = color;
        this.extraFare = extraFare;
    }

    public Line(final Long id, final String name, final String color) {
        this(id, name, color, 0);
    }

    public Line(final String name, final String color) {
        this(null, name, color);
    }

    public Line(final String name, final String color, final int extraFare) {
        this(null, name, color, extraFare);
    }

    public void update(final String name, final String color, final int extraFare) {
        validateRequiredArgument(name, color, extraFare);
        this.name = name;
        this.color = color;
        this.extraFare = extraFare;
    }

    public void addAllSections(final List<Section> sections) {
        for (Section section : sections) {
            this.sections.add(section);
        }
    }

    public void addSection(final Section section) {
        sections.add(section);
    }

    public Long removeStation(final Station station) {
        return sections.removeStation(station);
    }

    public List<Section> getSections() {
        return List.copyOf(sections.values());
    }

    public List<Station> getStations() {
        return Collections.unmodifiableList(sections.sortedStations());
    }

    private void validateRequiredArgument(final String name, final String color, final int extraFare) {
        validateNullOrBlank(name);
        validateNullOrBlank(color);
        validateNegative(extraFare);
    }

    private void validateNullOrBlank(final String string) {
        if (string == null || string.isBlank()) {
            throw new IllegalArgumentException("빈 값이 들어올 수 없습니다.");
        }
    }

    private void validateNegative(final int extraFare) {
        if (extraFare < 0) {
            throw new IllegalArgumentException("추가 요금은 0 이상이어야 합니다.");
        }
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
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Line line = (Line) o;
        return id.equals(line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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
