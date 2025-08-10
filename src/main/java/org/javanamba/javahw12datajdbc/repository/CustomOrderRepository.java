package org.javanamba.javahw12datajdbc.repository;

import lombok.RequiredArgsConstructor;
import org.javanamba.javahw12datajdbc.dtos.OrderDTO;
import org.javanamba.javahw12datajdbc.enums.OrderStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class CustomOrderRepository {

    private final JdbcTemplate jdbcTemplate;

    private final Set<String> ALLOWED_KEYS = Set.of(
            "op_cl", "open", "closed", "status",
            "operator_first_name", "operator_last_name",
            "client_first_name", "client_last_name"
    );

    private static final Map<String, String> WHERE_CONDITIONS = Map.of(
            "open", "o.closed IS NULL",
            "closed", "o.closed IS NOT NULL",
            "status", "o.status = ?",
            "operator_first_name", "ops.first_name LIKE ?",
            "operator_last_name", "ops.last_name LIKE ?",
            "client_first_name", "c.first_name LIKE ?",
            "client_last_name", "c.last_name LIKE ?"
    );

    private static final String SELECT_SQL = """
    SELECT
        o.*,
        ops.first_name AS operator_first_name,
        ops.last_name AS operator_last_name,
        c.first_name AS client_first_name,
        c.last_name AS client_last_name
    FROM orders o
    LEFT JOIN operators ops ON o.operator_id = ops.id
    LEFT JOIN clients c ON o.client_id = c.id
    """;

    private static final String COUNT_SQL = """
    SELECT COUNT(*)
    FROM orders o
    LEFT JOIN operators ops ON o.operator_id = ops.id
    LEFT JOIN clients c ON o.client_id = c.id
    """;

    private final RowMapper<OrderDTO> orderMapper = (rs, rowNum) -> {
        OrderDTO entity = new OrderDTO();
        entity.setId(rs.getLong("id"));
        entity.setDescription(rs.getString("description"));
        entity.setCreated(rs.getObject("created", LocalDateTime.class));
        entity.setClosed(rs.getObject("closed", LocalDateTime.class));
        entity.setStatus(OrderStatus.valueOf(rs.getString("status")));
        entity.setClientsComment(rs.getString("clients_comment"));
        entity.setOperatorsComment(rs.getString("operators_comment"));
        entity.setOperatorFirstName(rs.getString("operator_first_name"));
        entity.setOperatorLastName(rs.getString("operator_last_name"));
        entity.setOperatorId(rs.getLong("operator_id"));
        entity.setClientFirstName(rs.getString("client_first_name"));
        entity.setClientLastName(rs.getString("client_last_name"));
        entity.setClientId(rs.getLong("client_id"));
        return entity;
    };

    /**
     * Получает страницу всех заказов.
     */
    public Page<OrderDTO> findAll(Pageable pageable) throws DataAccessException {
        String sql = SELECT_SQL + "LIMIT ? OFFSET ?";
        List<OrderDTO> orders = jdbcTemplate.query(
                sql,
                ps -> {
                    ps.setInt(1, pageable.getPageSize());
                    ps.setInt(2, (int) pageable.getOffset());
                },
                orderMapper
        );

        int total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM orders", Integer.class);
        return new PageImpl<>(orders, pageable, total);
    }

    /**
     * Поиск заказов по ключу (статус/имя и т.д.)
     */
    public Page<OrderDTO> findOrdersByKey(String keyBy, String search, Pageable pageable) {
        if (!ALLOWED_KEYS.contains(keyBy)) {
            throw new IllegalArgumentException("Недопустимый ключ: " + keyBy);
        }

        String whereClause = WHERE_CONDITIONS.get(keyBy);
        if (whereClause == null) {
            throw new IllegalArgumentException("Условия фильтрации не указаны для ключа: " + keyBy);
        }

        String countSql = COUNT_SQL + " WHERE " + whereClause;
        String dataSql = SELECT_SQL + " WHERE " + whereClause + " LIMIT ? OFFSET ?";

        // Подготовка параметров
        Object[] args;
        if (keyBy.equals("open") || keyBy.equals("closed")) {
            args = new Object[]{pageable.getPageSize(), pageable.getOffset()};
        } else if (keyBy.equals("status")) {
            args = new Object[]{search, pageable.getPageSize(), pageable.getOffset()};
        } else {
            args = new Object[]{"%" + search + "%", pageable.getPageSize(), pageable.getOffset()};
        }

        Object[] argsCount;
        if (keyBy.equals("open") || keyBy.equals("closed")) {
            argsCount = new Object[]{};
        } else if (keyBy.equals("status")) {
            argsCount = new Object[]{search};
        } else {
            argsCount = new Object[]{"%" + search + "%"};
        }

        Integer totalCount = jdbcTemplate.queryForObject(countSql, argsCount, Integer.class);
        int total = totalCount != null ? totalCount : 0;

        List<OrderDTO> orders = jdbcTemplate.query(dataSql, args, orderMapper);
        return new PageImpl<>(orders, pageable, total);
    }

}