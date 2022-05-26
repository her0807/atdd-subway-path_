package wooteco.subway.dao;

import javax.sql.DataSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

@Repository
public class SectionDao {

    private static final RowMapper<Section> SECTION_MAPPER = (resultSet, rowNum) -> {
        Long id = resultSet.getLong("id");
        Line line = new Line(resultSet.getLong("l_id"), resultSet.getString("l_name"), resultSet.getString("l_color"),
                resultSet.getInt("l_extra_fare"));
        Station upStation = new Station(resultSet.getLong("us_id"), resultSet.getString("us_name"));
        Station downStation = new Station(resultSet.getLong("ds_id"), resultSet.getString("ds_name"));
        int distance = resultSet.getInt("distance");
        return new Section(id, line, upStation, downStation, distance);
    };

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleInsert;

    public SectionDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("SECTION")
                .usingGeneratedKeyColumns("id");
    }

    public Section save(Section section) {
        Line line = section.getLine();
        SqlParameterSource parameters = new MapSqlParameterSource("line_id", line.getId())
                .addValue("up_station_id", section.getUpStation().getId())
                .addValue("down_station_id", section.getDownStation().getId())
                .addValue("distance", section.getDistance());
        Long id = simpleInsert.executeAndReturnKey(parameters).longValue();
        return new Section(id, line, section.getUpStation(), section.getDownStation(), section.getDistance());
    }

    public Sections findAll() {
        String sql = "SELECT s.id AS id, s.line_id AS line_id, s.distance AS distance, "
                + "l.id AS l_id, l.name AS l_name, l.color AS l_color, l.extra_fare AS l_extra_fare, "
                + "us.id AS us_id, us.name AS us_name, ds.id AS ds_id, ds.name AS ds_name "
                + "FROM SECTION AS s "
                + "INNER JOIN LINE AS l ON s.line_id = l.id "
                + "INNER JOIN STATION AS us ON s.up_station_id = us.id "
                + "INNER JOIN STATION AS ds ON s.down_station_id = ds.id ";
        return new Sections(jdbcTemplate.query(sql, SECTION_MAPPER));
    }

    public Sections findAllByLine(Line line) {
        String sql = "SELECT s.id AS id, s.line_id AS line_id, s.distance AS distance, "
                + "l.id AS l_id, l.name AS l_name, l.color AS l_color, l.extra_fare AS l_extra_fare, "
                + "us.id AS us_id, us.name AS us_name, ds.id AS ds_id, ds.name AS ds_name "
                + "FROM SECTION AS s "
                + "INNER JOIN LINE AS l ON s.line_id = l.id "
                + "INNER JOIN STATION AS us ON s.up_station_id = us.id "
                + "INNER JOIN STATION AS ds ON s.down_station_id = ds.id "
                + "WHERE s.line_id = :line_id";
        SqlParameterSource parameters = new MapSqlParameterSource("line_id", line.getId());
        return new Sections(jdbcTemplate.query(sql, parameters, SECTION_MAPPER));
    }

    public void deleteByLineAndSection(Line line, Section section) {
        String sql = "DELETE FROM SECTION WHERE line_id = :line_id AND id = :id";
        MapSqlParameterSource parameters = new MapSqlParameterSource("line_id", line.getId())
                .addValue("id", section.getId());
        jdbcTemplate.update(sql, parameters);
    }

    public void deleteByLineAndStation(Line line, Station station) {
        String sql = "DELETE FROM SECTION WHERE line_id = :line_id AND (up_station_id = :station_id OR down_station_id = :station_id)";
        MapSqlParameterSource parameters = new MapSqlParameterSource("line_id", line.getId())
                .addValue("station_id", station.getId());
        jdbcTemplate.update(sql, parameters);
    }

    public boolean exists(Line line, Section section) {
        String sql = "SELECT EXISTS(SELECT 1 FROM SECTION WHERE line_id = :line_id "
                + "AND up_station_id = :up_station_id AND down_station_id = :down_station_id)";
        SqlParameterSource parameters = new MapSqlParameterSource("line_id", line.getId())
                .addValue("up_station_id", section.getUpStation().getId())
                .addValue("down_station_id", section.getDownStation().getId());
        return Integer.valueOf(1).equals(jdbcTemplate.queryForObject(sql, parameters, Integer.class));
    }

}
