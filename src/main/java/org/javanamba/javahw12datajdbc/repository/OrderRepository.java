package org.javanamba.javahw12datajdbc.repository;

import org.javanamba.javahw12datajdbc.entity.Order;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
public interface OrderRepository extends PagingAndSortingRepository<Order, Long>,CrudRepository<Order, Long> {

    // Для того чтобы вернуть кастомный DTO, используем native SQL
    @Query(value = """
            SELECT
                o.*,
                op.first_name AS operator_first_name,
                op.last_name AS operator_last_name,
                c.first_name AS client_first_name,
                c.last_name AS client_last_name
            FROM orders o
            LEFT JOIN operators op ON o.operator_id = op.id
            LEFT JOIN clients c ON o.client_id = c.id
            WHERE o.id = :id
            """)
    Optional<Order.OrderDto> findDtoById(@Param("id") Long id);

    // Удаление всех заявок по id оператора
    @Modifying
    @Transactional
    @Query("DELETE FROM orders o WHERE o.operator_id = :operatorId")
    void deleteAllByOperatorId(@Param("operatorId") Long operatorId);
}
