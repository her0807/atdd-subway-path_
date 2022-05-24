package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

@Repository
public class LineDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final RowMapper<Line> resultMapper = (rs, rowNum) -> new Line(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("color"),
            rs.getInt("extra_fare")
    );

    public LineDao(final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public Line save(final Line line) {
        final String sql = "insert into LINE (name, color, extra_fare) values (:name, :color, :extra_fare)";

        final Map<String, Object> params = new HashMap<>();
        params.put("name", line.getName());
        params.put("color", line.getColor());
        params.put("extra_fare", line.getExtraFare());

        final KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);
        return new Line(Objects.requireNonNull(keyHolder.getKey()).longValue(), line.getName(), line.getColor(),
                line.getExtraFare());
    }

    public List<Line> findAll() {
        final String sql = "select id, name, color, extra_fare from LINE";

        return namedParameterJdbcTemplate.query(sql,
                (rs, rowNum) -> new Line(rs.getLong("id"), rs.getString("name"), rs.getString("color"),
                        rs.getInt("extra_fare")));
    }

    public Optional<Line> findById(final Long id) {
        final String sql = "select id, name, color, extra_fare from LINE where id=:id";

        final Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        final List<Line> queryResult = namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource(params),
                resultMapper);
        return Optional.ofNullable(DataAccessUtils.singleResult(queryResult));
    }

    public Optional<Line> findByName(final String name) {
        final String sql = "select id, name, color, extra_fare from LINE where name=:name";

        final Map<String, Object> params = new HashMap<>();
        params.put("name", name);

        final List<Line> queryResult = namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource(params),
                resultMapper);
        return Optional.ofNullable(DataAccessUtils.singleResult(queryResult));
    }

    public Optional<Integer> findMaxFareByLineIds(final List<Long> ids) {
        final String sql = "select max(extra_fare) from LINE where id in (:ids)";
        MapSqlParameterSource params = new MapSqlParameterSource("ids", ids);
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public int getMaxFareByLineIds(final List<Long> ids) {
        return findMaxFareByLineIds(ids)
                .orElseThrow(() -> new IllegalArgumentException("해당 lineId 에서 최대 추가 요금을 찾을 수 없습니다."));
    }

    public int update(final Long id, final Line newLine) {
        final String sql = "update LINE set name=:name, color=:color where id=:id";

        final Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("name", newLine.getName());
        params.put("color", newLine.getColor());

        return namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params));
    }

    public int deleteById(final Long id) {
        final String sql = "delete from LINE where id = :id";

        final Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        return namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params));
    }
}
