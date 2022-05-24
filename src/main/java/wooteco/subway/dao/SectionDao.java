package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void save(List<Section> sections, Long lineId) {
        batchInsert(sections, lineId);
    }

    public List<Section> findAll() {
        final String sql =
                "select s.id sid, s.line_id slid, s.distance sdistance, us.id usid, us.name usname, ds.id dsid, ds.name dsname, l.id lid, l.name lname, l.color lcolor, l.extra_fare lfare " +
                        "from sections s " +
                        "join line l on s.line_id = l.id " +
                        "join station us on s.up_station_id = us.id " +
                        "join station ds on s.down_station_id = ds.id";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> {
            return Section.createWithLine(rs.getLong("sid"),
                    Line.createWithoutSection(rs.getLong("lid"), rs.getString("lname"), rs.getString("lcolor"), rs.getInt("lfare")),
                    new Station(rs.getLong("usid"), rs.getString("usname")),
                    new Station(rs.getLong("dsid"), rs.getString("dsname")), rs.getInt("sdistance"));
        }));
    }

    private int[] batchInsert(List<Section> sections, Long lineId) {
        return this.jdbcTemplate.batchUpdate(
                "insert into sections (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, lineId);
                        ps.setLong(2, sections.get(i).getUpStation().getId());
                        ps.setLong(3, sections.get(i).getDownStation().getId());
                        ps.setInt(4, sections.get(i).getDistance());
                    }

                    @Override
                    public int getBatchSize() {
                        return sections.size();
                    }
                }
        );
    }

    public void deleteByLineId(Long lineId) {
        final String sql = "delete from sections where line_id = ?";
        jdbcTemplate.update(sql, lineId);
    }
}
