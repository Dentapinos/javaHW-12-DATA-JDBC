package org.javanamba.javahw12datajdbc.repository;

import lombok.RequiredArgsConstructor;
import org.javanamba.javahw12datajdbc.entity.Operator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class CustomOperatorRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final Set<String> ALLOWED_FIELDS = Set.of("first_name", "last_name", "email", "phone_number", "address", "status", "date_of_birth");

    public Page<Operator> searchOperators(String fieldName, String searchValue, Pageable pageable, boolean isUpdate) {
        if (!ALLOWED_FIELDS.contains(fieldName)) {
            throw new IllegalArgumentException("Invalid field name");
        }
        String countQuery;
        if (isUpdate) {
            countQuery = "SELECT COUNT(*) FROM operators WHERE " + fieldName + " LIKE :searchValue AND status=true";
        } else  {
            countQuery = "SELECT COUNT(*) FROM operators WHERE " + fieldName + " LIKE :searchValue";
        }

        Map<String, Object> countParams = new HashMap<>();
        countParams.put("searchValue", "%" + searchValue + "%");
        Integer total = namedParameterJdbcTemplate.queryForObject(countQuery, countParams, Integer.class);
        String dataQuery;
        if (isUpdate) {
            dataQuery = "SELECT id, first_name, last_name, email, phone_number, address, status, date_of_birth FROM operators WHERE " + fieldName + " LIKE :searchValue AND status=true LIMIT :limit OFFSET :offset";

        } else {
            dataQuery = "SELECT id, first_name, last_name, email, phone_number, address, status, date_of_birth FROM operators WHERE " + fieldName + " LIKE :searchValue LIMIT :limit OFFSET :offset";
        }
        Map<String, Object> dataParams = new HashMap<>();
        dataParams.put("searchValue", "%" + searchValue + "%");
        dataParams.put("limit", pageable.getPageSize());
        dataParams.put("offset", pageable.getOffset());

        List<Operator> operators = namedParameterJdbcTemplate.query(
                dataQuery,
                dataParams,
                new BeanPropertyRowMapper<>(Operator.class)
        );

        return new PageImpl<>(operators, pageable, total);
    }
}
