package org.javanamba.javahw12datajdbc.repository;

import lombok.RequiredArgsConstructor;
import org.javanamba.javahw12datajdbc.dtos.PhoneUsageHistoryDTO;
import org.javanamba.javahw12datajdbc.entity.PhoneUsageHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PhoneUsageHistoryRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String BASE_SQL =
            "SELECT puh.id, o.first_name AS operatorFirstName, o.last_name AS operatorLastName, " +
                    "p.serial_number AS phoneSerial, p.phone_number AS phoneNumber, " +
                    "puh.usage_start AS startTime, puh.usage_end AS endTime " +
                    "FROM phone_usage_history puh " +
                    "JOIN operators o ON puh.operator_id = o.id " +
                    "JOIN work_phones p ON puh.phone_id = p.id ";

    private static final String COUNT_SQL =
            "SELECT COUNT(*) FROM phone_usage_history puh " +
                    "JOIN operators o ON puh.operator_id = o.id " +
                    "JOIN work_phones p ON puh.phone_id = p.id ";

    private static final Map<SearchField, String> FIELD_TO_SQL_MAP = Map.of(
            SearchField.OPERATOR_FIRST_NAME, "o.first_name",
            SearchField.OPERATOR_LAST_NAME, "o.last_name",
            SearchField.PHONE_SERIAL, "p.serial_number",
            SearchField.PHONE_NUMBER, "p.phone_number"
    );

    private enum SearchField {
        OPERATOR_FIRST_NAME, OPERATOR_LAST_NAME, PHONE_SERIAL, PHONE_NUMBER
    }

    private final RowMapper<PhoneUsageHistoryDTO> dtoMapper = (rs, rowNum) -> new PhoneUsageHistoryDTO(
            rs.getLong("id"),
            rs.getString("operatorFirstName"),
            rs.getString("operatorLastName"),
            rs.getString("phoneSerial"),
            rs.getString("phoneNumber"),
            rs.getTimestamp("startTime").toLocalDateTime(),
            Optional.ofNullable(rs.getTimestamp("endTime"))
                    .map(Timestamp::toLocalDateTime)
                    .orElse(null)
    );

    public Page<PhoneUsageHistoryDTO> findByFieldContaining(SearchField field, String value, Pageable pageable) {
        String fieldSql = FIELD_TO_SQL_MAP.get(field);
        if (fieldSql == null) throw new IllegalArgumentException("Invalid search field: " + field);

        String whereClause = "WHERE " + fieldSql + " LIKE ?";
        String querySql = BASE_SQL + whereClause + " ORDER BY puh.id LIMIT ? OFFSET ?";
        String countSql = COUNT_SQL + whereClause;

        String likeValue = "%" + value + "%";
        Object[] queryParams = {likeValue, pageable.getPageSize(), pageable.getOffset()};
        Object[] countParams = {likeValue};

        List<PhoneUsageHistoryDTO> list = jdbcTemplate.query(querySql, queryParams, dtoMapper);
        int total = jdbcTemplate.queryForObject(countSql, countParams, Integer.class);

        return new PageImpl<>(list, pageable, total);
    }

    public Page<PhoneUsageHistoryDTO> findByOperatorFirstNameContaining(String firstName, Pageable pageable) {
        return findByFieldContaining(SearchField.OPERATOR_FIRST_NAME, firstName, pageable);
    }

    public Page<PhoneUsageHistoryDTO> findByOperatorLastNameContaining(String lastName, Pageable pageable) {
        return findByFieldContaining(SearchField.OPERATOR_LAST_NAME, lastName, pageable);
    }

    public Page<PhoneUsageHistoryDTO> findByPhoneSerialContaining(String serial, Pageable pageable) {
        return findByFieldContaining(SearchField.PHONE_SERIAL, serial, pageable);
    }

    public Page<PhoneUsageHistoryDTO> findByPhoneNumberContaining(String phoneNumber, Pageable pageable) {
        return findByFieldContaining(SearchField.PHONE_NUMBER, phoneNumber, pageable);
    }

    public Page<PhoneUsageHistoryDTO> findAll(Pageable pageable) {
        String sql = BASE_SQL + " ORDER BY puh.id LIMIT ? OFFSET ?";
        List<PhoneUsageHistoryDTO> list = jdbcTemplate.query(
                sql,
                new Object[]{pageable.getPageSize(), pageable.getOffset()},
                dtoMapper
        );
        int total = jdbcTemplate.queryForObject(COUNT_SQL, Integer.class);
        return new PageImpl<>(list, pageable, total);
    }

    public Optional<PhoneUsageHistory> findHistoryByOperatorIdAndPhoneId(Long operatorId, Long phoneId) {
        String sql = "SELECT puh.id, puh.usage_start, puh.usage_end, puh.operator_id, puh.phone_id " +
                "FROM phone_usage_history puh " +
                "WHERE puh.operator_id = ? AND puh.phone_id = ? AND puh.usage_end IS NULL";

        List<PhoneUsageHistory> result = jdbcTemplate.query(sql, new Object[]{operatorId, phoneId}, (rs, rowNum) -> {
            PhoneUsageHistory ph = new PhoneUsageHistory();
            ph.setId(rs.getLong("id"));
            ph.setUsageStart(rs.getTimestamp("usage_start").toLocalDateTime());
            ph.setUsageEnd(null);
            ph.setOperatorId(rs.getLong("operator_id"));
            ph.setPhoneId(rs.getLong("phone_id"));
            return ph;
        });

        return result.stream().findFirst();
    }

    public void update(PhoneUsageHistory phoneUsageHistory) {
        String sql = "UPDATE phone_usage_history SET usage_start = ?, usage_end = ?, operator_id = ?, phone_id = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                phoneUsageHistory.getUsageStart(),
                phoneUsageHistory.getUsageEnd(),
                phoneUsageHistory.getOperatorId(),
                phoneUsageHistory.getPhoneId(),
                phoneUsageHistory.getId());
    }

    public void save(PhoneUsageHistory phoneUsageHistory) {
        String sql = "INSERT INTO phone_usage_history (usage_start, usage_end, operator_id, phone_id) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                phoneUsageHistory.getUsageStart(),
                phoneUsageHistory.getUsageEnd(),
                phoneUsageHistory.getOperatorId(),
                phoneUsageHistory.getPhoneId());
    }
}