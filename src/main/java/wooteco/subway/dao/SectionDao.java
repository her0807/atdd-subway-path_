package wooteco.subway.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

@Repository
public class SectionDao {
    private final SimpleJdbcInsert jdbcInsert;
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public SectionDao(DataSource dataSource) {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public void save(Section section, Long lineId) {
        save(section, lineId, 0);
    }

    private void save(Section section, Long lineId, int index) {
        Map<String, Object> param = new HashMap<>();
        param.put("line_id", lineId);
        param.put("up_station_id", section.getUpStationId());
        param.put("down_station_id", section.getDownStationId());
        param.put("distance", section.getDistance());
        param.put("index_num", index);
        jdbcInsert.execute(param);
    }

    public void save(Sections sections, Long lineId) {
        LinkedList<Section> values = sections.getSections();
        for (int i = 0; i < values.size(); i++) {
            Section section = values.get(i);
            update(section, lineId, i);
        }
    }

    private void update(Section section, Long lineId, int index) {
        if (Objects.isNull(section.getId())) {
            save(section, lineId, index);
            return;
        }
        String sql = "UPDATE section "
                + "SET up_station_id = :upStationId, down_station_id = :downStationId, distance = :distance, index_num = :indexNum "
                + "WHERE id = :id";

        SqlParameterSource paramSource = new MapSqlParameterSource("upStationId", section.getUpStationId())
                .addValue("downStationId", section.getDownStationId())
                .addValue("distance", section.getDistance())
                .addValue("indexNum", index)
                .addValue("id", section.getId());
        namedParameterJdbcTemplate.update(sql, paramSource);
    }

    public int delete(Section section) {
        String sql = "DELETE FROM section WHERE id = ?";
        return delete(sql, section.getId());
    }

    public int deleteByLine(Long lineId) {
        String sql = "DELETE FROM section WHERE line_id = ?";
        return delete(sql, lineId);
    }

    public List<Section> findAll() {
        String sql = "SELECT "
                + "sec.id, sec.distance, "
                + "sec.up_station_id, us.name up_station_name,"
                + "sec.down_station_id, ds.name down_station_name "
                + "FROM section AS sec "
                + "JOIN station AS us ON sec.up_station_id = us.id "
                + "JOIN station AS ds ON sec.down_station_id = ds.id ";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> mapToSection(resultSet));
    }

    private Section mapToSection(ResultSet resultSet) throws SQLException {
        Long upStationId = resultSet.getLong("up_station_id");
        String upStationName = resultSet.getString("up_station_name");

        Long downStationId = resultSet.getLong("down_station_id");
        String downStationName = resultSet.getString("down_station_name");

        return new Section(
                resultSet.getLong("id"),
                new Station(upStationId, upStationName),
                new Station(downStationId, downStationName),
                resultSet.getInt("distance")
        );
    }

    private int delete(String sql, Long id) {
        int deletedCount = jdbcTemplate.update(sql, id);
        validateRemoved(deletedCount);
        return deletedCount;
    }

    private void validateRemoved(int count) {
        if (count == 0) {
            throw new IllegalStateException("삭제할 구간이 존재하지 않습니다.");
        }
    }
}
