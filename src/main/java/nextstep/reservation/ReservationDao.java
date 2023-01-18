package nextstep.reservation;

import nextstep.error.ErrorCode;
import nextstep.error.exception.FailedRecordSaveException;
import nextstep.error.exception.RecordNotFoundException;
import nextstep.member.Member;
import nextstep.member.Role;
import nextstep.schedule.Schedule;
import nextstep.theme.Theme;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Component
public class ReservationDao implements ReservationRepository {

    public final JdbcTemplate jdbcTemplate;

    public ReservationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    String baseSelect = "SELECT reservation.id, schedule.id, schedule.theme_id, schedule.date, schedule.time, theme.id, theme.name, theme.desc, theme.price, member.id, member.username, member.password, member.name, member.phone, member.role " +
            "from reservation " +
            "inner join schedule on reservation.schedule_id = schedule.id " +
            "inner join theme on schedule.theme_id = theme.id " +
            "inner join member on reservation.member_id = member.id ";

    private final RowMapper<Reservation> rowMapper = (resultSet, rowNum) -> new Reservation(
            resultSet.getLong("reservation.id"),
            new Schedule(
                    resultSet.getLong("schedule.id"),
                    new Theme(
                            resultSet.getLong("theme.id"),
                            resultSet.getString("theme.name"),
                            resultSet.getString("theme.desc"),
                            resultSet.getInt("theme.price")
                    ),
                    resultSet.getDate("schedule.date").toLocalDate(),
                    resultSet.getTime("schedule.time").toLocalTime()
            ),
            new Member(
                    resultSet.getLong("member.id"),
                    resultSet.getString("member.username"),
                    resultSet.getString("member.password"),
                    resultSet.getString("member.name"),
                    resultSet.getString("member.phone"),
                    Role.map.get(resultSet.getString("member.role"))
            )
    );

    @Override
    public Long save(Reservation reservation) {
        String sql = "INSERT INTO reservation (schedule_id, member_id) VALUES (?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, reservation.getSchedule().getId());
            ps.setLong(2, reservation.getMember().getId());
            return ps;

        }, keyHolder);
        Number key = keyHolder.getKey();

        if (Objects.isNull(key)) throw new FailedRecordSaveException();

        return key.longValue();
    }

    @Override
    public List<Reservation> findAllByThemeIdAndDate(Long themeId, String date) {
        String sql = baseSelect + "where theme.id = ? and schedule.date = ?;";

        return jdbcTemplate.query(sql, rowMapper, themeId, Date.valueOf(date));
    }

    @Override
    public Reservation findById(Long id) {
        String sql = baseSelect + "where reservation.id = ?;";
        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, id);
        } catch (DataAccessException e) {
            throw new RecordNotFoundException(ErrorCode.RESERVATION_NOT_FOUND);
        }
    }

    @Override
    public List<Reservation> findByScheduleId(Long id) {
        String sql = baseSelect + "where schedule.id = ?;";

        return jdbcTemplate.query(sql, rowMapper, id);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM reservation where id = ?;";
        try {
            jdbcTemplate.update(sql, id);
        } catch (DataAccessException e) {
            throw new RecordNotFoundException(ErrorCode.RESERVATION_NOT_FOUND);
        }
    }
}
