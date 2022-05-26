package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.entity.LineEntity;

import java.util.List;
import java.util.Map;

@Repository
public class JdbcLineDao implements LineDao {

    public static final String TABLE_NAME = "LINE";
    public static final String KEY_NAME = "id";
    private final static RowMapper<LineEntity> generateMapper =
            (resultSet, rowNum) ->
                    new LineEntity(
                            resultSet.getLong("id"),
                            resultSet.getString("name"),
                            resultSet.getString("color"),
                            resultSet.getInt("fare")
                    );
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;

    public JdbcLineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        this.insertActor = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns(KEY_NAME);
    }

    @Override
    public LineEntity save(LineEntity lineEntity) {
        Long id = insertActor.executeAndReturnKey(
                Map.of("name", lineEntity.getName(), "color", lineEntity.getColor(), "fare", lineEntity.getFare())).longValue();
        return findById(id);
    }

    @Override
    public List<LineEntity> findAll() {
        String sql = "select * from LINE";
        return jdbcTemplate.query(sql, generateMapper);
    }

    @Override
    public LineEntity findById(Long id) {
        String sql = "select * from LINE where id = :id";
        return jdbcTemplate.queryForObject(sql, Map.of("id", id), generateMapper);
    }


    @Override
    public LineEntity update(LineEntity lineEntity) {
        String sql = "update LINE set name = :name, color = :color, fare = :fare where id = :id";
        jdbcTemplate.update(sql,
                Map.of("id", lineEntity.getId(),
                        "name", lineEntity.getName(),
                        "color", lineEntity.getColor(),
                        "fare", lineEntity.getFare()));

        return findById(lineEntity.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "delete from LINE where id = :id";
        jdbcTemplate.update(sql, Map.of("id", id));
    }
}
