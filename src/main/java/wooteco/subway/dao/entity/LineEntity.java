package wooteco.subway.dao.entity;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Sections;

public class LineEntity {

    private final Long id;
    private final String name;
    private final String color;
    private final int extraFare;

    public LineEntity(final String name, final String color, final int extraFare) {
        this(null, name, color, extraFare);
    }

    public LineEntity(final Long id, final String name, final String color, final int extraFare) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.extraFare = extraFare;
    }

    public static LineEntity from(final Line line) {
        return new LineEntity(line.getId(), line.getName(), line.getColor(), line.getExtraFare());
    }

    public Line toLine(final Sections sections) {
        return new Line(id, name, color, extraFare, sections);
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
}
