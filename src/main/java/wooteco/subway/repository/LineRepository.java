package wooteco.subway.repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.LineSection;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.IdNotFoundException;
import wooteco.subway.exception.NameDuplicatedException;

@Repository
public class LineRepository {

    private static final int NO_ROW = 0;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    private final RowMapper<LineSection> lineSectionMapper = (resultSet, rowNum) -> {
        long lineId = resultSet.getLong("line_id");
        String name = resultSet.getString("name");
        String color = resultSet.getString("color");
        Line line = new Line(lineId, name, color);
        long sectionId = resultSet.getLong("section_id");
        long upStationId = resultSet.getLong("up_station_id");
        long downStationId = resultSet.getLong("down_station_id");
        String upStationName = resultSet.getString("up_station_name");
        String downStationName = resultSet.getString("down_station_name");
        int distance = resultSet.getInt("distance");
        Section section = new Section(sectionId,
                lineId,
                new Station(upStationId, upStationName),
                new Station(downStationId, downStationName),
                distance);
        return new LineSection(line, section);
    };

    private final RowMapper<Line> lineMapper = (resultSet, rowNum) -> {
        long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String color = resultSet.getString("color");
        return new Line(id, name, color);
    };

    public LineRepository(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");
    }

    public Long save(final Line line) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", line.getName())
                .addValue("color", line.getColor());

        try {
            return simpleJdbcInsert.executeAndReturnKey(parameters)
                    .longValue();
        } catch (DuplicateKeyException e) {
            throw new NameDuplicatedException(line.getName());
        }
    }

    public List<Line> findAll() {
        String sql = "SELECT l.id AS line_id, l.name, l.color, "
                + "s.id AS section_id, "
                + "s.up_station_id, us.name AS up_station_name, "
                + "s.down_station_id, ds.name AS down_station_name, s.distance "
                + "FROM line AS l "
                + "LEFT JOIN section AS s ON s.line_id = l.id "
                + "LEFT JOIN station AS us ON us.id = s.up_station_id "
                + "LEFT JOIN station AS ds ON ds.id = s.down_station_id";

        List<LineSection> lineSections = namedParameterJdbcTemplate.query(sql, lineSectionMapper);
        Map<Line, List<LineSection>> groupByLine = lineSections.stream()
                .collect(Collectors.groupingBy(LineSection::getLine));
        return groupByLine.keySet()
                .stream()
                .map(key -> new Line(key.getId(), key.getName(), key.getColor(), toSections(groupByLine.get(key))))
                .collect(Collectors.toList());
    }

    public Line findById(final Long id) {
        String sql = "SELECT * FROM line WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, parameters, lineMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new IdNotFoundException(id);
        }
    }

    private Sections toSections(List<LineSection> lineSections) {
        return new Sections(lineSections.stream()
                .map(LineSection::getSection)
                .collect(Collectors.toList()));
    }

    public Boolean isNameExists(final String name) {
        String sql = "SELECT EXISTS (SELECT * FROM line WHERE name = :name)";
        SqlParameterSource parameters = new MapSqlParameterSource("name", name);
        return Boolean.TRUE.equals(namedParameterJdbcTemplate.queryForObject(sql, parameters, Boolean.class));
    }

    public void update(final Line line) {
        String sql = "UPDATE line SET name = :name, color = :color WHERE id = :id";
        SqlParameterSource nameParameters = new BeanPropertySqlParameterSource(line);
        try {
            namedParameterJdbcTemplate.update(sql, nameParameters);
        } catch (DuplicateKeyException e) {
            throw new NameDuplicatedException(line.getName());
        } catch (EmptyResultDataAccessException e) {
            throw new IdNotFoundException(line.getId());
        }
    }

    public void deleteById(final Long id) {
        String sql = "DELETE FROM line WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        int rowCounts = namedParameterJdbcTemplate.update(sql, parameters);
        if (rowCounts == NO_ROW) {
            throw new IdNotFoundException(id);
        }
    }
}