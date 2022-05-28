package wooteco.subway.dao.station;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.station.Station;

@Repository
public class JdbcStationDao implements StationDao {

    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) -> new Station(
            resultSet.getLong("id"),
            resultSet.getString("name")
    );

    private final JdbcTemplate jdbcTemplate;

    public JdbcStationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long save(Station station) {
        final String sql = "insert into STATION (name) values (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setString(1, station.getName());
            return preparedStatement;
        }, keyHolder);

        return Optional.ofNullable(keyHolder.getKey())
                .orElseThrow(() -> new DuplicateKeyException("데이터를 저장할 수 없습니다."))
                .longValue();
    }

    @Override
    public boolean existStationById(Long id) {
        final String sql = "select exists (select * from STATION where id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id));
    }

    @Override
    public boolean existStationByName(String name) {
        final String sql = "select exists (select * from STATION where name = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, name));
    }

    @Override
    public List<Station> findAll() {
        final String sql = "select id, name from STATION";
        return jdbcTemplate.query(sql, stationRowMapper);
    }

    @Override
    public void delete(Long id) {
        final String sql = "delete from STATION where id = ?";
        jdbcTemplate.update(sql, id);
    }
}
